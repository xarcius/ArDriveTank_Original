package v3d.com.ardrivetank;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

//fine Import

//_________________________________________________________________________________________________

public class MainActivity extends AppCompatActivity {

    ImageButton imgbtn_bt; //dichiaro il bottone-immagine
    ListView list__bt;//dichiaro la listview dove vedrò tutti i dispositivi bt abilitati
    ImageButton imgbtn_up, imgbtn_back, imgbtn_right, imgbtn_left;
    ImageButton led_vuoto, led_rosso, led_verde;
    ImageButton setting;
    TextView label_nome_dispositivo_connesso;

    private BluetoothAdapter bt_adapter = BluetoothAdapter.getDefaultAdapter(); //cerca i dispositivi nelle vicinanze con cui comunicare

    private ArrayAdapter adapter = null;//permette di gestire dei dati
                                        // memorizzati sotto forma di array
                                        //Gestire quello che si è trovato

    Set<BluetoothDevice> pairedDevices = bt_adapter.getBondedDevices();//prendo l'insieme dei dispositivi già accoppiati

    //gestione nuovi device BT non accoppiati in passato
    BroadcastReceiver receiver; //Trasmissione
    String NuovodeviceName;
    String NuovodeviceAddress;
    int NuovodeviceLegame;
    String StatoLegameDevice;

    int counter = 0;
    boolean connesso = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//è un numero di identificazione unico assegnato a un hardware (numero che viene assegnato all'HC-05)
    BluetoothSocket btsocket = null; //creo una variabile di tipo socket per la trasmissione e ricezione dati

    OutputStream outStream;

    //dichiarazioni var per lo streaming video

    WebView browser;
    Button btn_connetti_streaming;
    EditText text_url;
    String url;

    //fine dichiarazione variabili

//__________________________________________________________________________________________________

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);//Quando l'app si avvia,
                                                                                  // andrà in LANDSCAPE inverso



        assegnazioni();

        //disattivo il led verde
        led_rosso.setVisibility(View.GONE);//disattivazione
        led_verde.setVisibility(View.GONE);//disattivazione
        led_vuoto.setVisibility(View.VISIBLE);//attivazione
        label_nome_dispositivo_connesso.setText("Non Connesso - BT disattivato");//testo


        configurazione_grafica_lstView();

        led_on_off();

        click_BT_button();

        click_item_listView();

        comandi();

        avvio_streaming();


    }//fine OnCreate

    //assegnazione delle variabili ai widgete
    public void assegnazioni(){
        //ASSEGNAZIONE
        imgbtn_bt = (ImageButton) findViewById(R.id.imBtn_bt); //assegno la variabile al widget
        list__bt = (ListView) findViewById(R.id.list_bt);

        imgbtn_up = (ImageButton) findViewById(R.id.up_button);
        imgbtn_back = (ImageButton) findViewById(R.id.back_button);
        imgbtn_right = (ImageButton) findViewById(R.id.right_button);
        imgbtn_left = (ImageButton) findViewById(R.id.left_button);

        led_rosso = (ImageButton) findViewById(R.id.led_ardrivetank_rosso);
        led_verde = (ImageButton) findViewById(R.id.led_ardrivetank_verde);
        led_vuoto = (ImageButton) findViewById(R.id.led_ardrivetank_vuoto);

        label_nome_dispositivo_connesso = (TextView) findViewById(R.id.text_nome_dispositivo_connesso);

        browser = (WebView) findViewById(R.id.id_browser);
        btn_connetti_streaming = (Button) findViewById(R.id.id_connetti_stream);
        text_url = (EditText) findViewById(R.id.id_url);



    }

    //lampeggiamento led
    public void led_on_off(){
        //faccio lampeggiare i led
        //poichè il dispositivo non è connesso
        //a nessun altro dispositivo
        if(connesso == false) {
            //x    //y
            new CountDownTimer(300000, 1500) {
                //esegui X volte, ogni Y millisecondi
                //1000 = ogni secondo [1000 millisecondi = 1 secondo]
                public void onTick(long millisUntilFinished) {
                    counter++;
                    if (counter % 2 == 0) { //pari
                        led_rosso.setVisibility(View.GONE);//faccio sparire il led rosso
                        led_vuoto.setVisibility(View.VISIBLE);//faccio apparire il led vuoto
                    } else {//dispari
                        led_rosso.setVisibility(View.VISIBLE);
                        led_vuoto.setVisibility(View.GONE);
                    }
                }

                public void onFinish() {
                    //quando il timer finisce, fai qualcosa
                }
            }.start();
        }//FINE [if connesso == true]_______________________________________________________________
    }

    //configurazione grafica della LISTVIEW
    public void configurazione_grafica_lstView(){
        //CONFIGURAZIONE BT
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1);//configurazione grafica che avrà la listview (font, ecc..)
        list__bt.setAdapter(adapter);//setto la listview in modo che riceva la lista di altri dispositi con BT attivo
    }

    //si occupa di ciò che accade quando viene premuto il tasto del BT
    public void click_BT_button(){
        //gestisce il click sul pulsante-imageBtn_bt
        imgbtn_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan_bt();
            }
            //fine setOnClickListener
        });
    }

    //si occupa di connettere il cell all'elemento cliccato nella listview
    public void click_item_listView(){
        list__bt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                resetConnection();
                led_rosso.setVisibility(View.VISIBLE);
                led_verde.setVisibility(View.GONE);
                label_nome_dispositivo_connesso.setText("Connessione...");
                bt_adapter.cancelDiscovery();//termina la ricerca
                final String info = ((TextView) view).getText().toString();//prendo l'elemento cliccato
                //Toast.makeText(getApplicationContext(), info.toString(), Toast.LENGTH_LONG).show();
                String address = info.substring(info.length()-17);//recupero l'indirizzo MAC
                BluetoothDevice connect_device = bt_adapter.getRemoteDevice(address);
                try{
                    btsocket = connect_device.createRfcommSocketToServiceRecord(myUUID);//chiamo il protocollo RFCOMM. "createRfcommSocketToServiceRecord" si occuperà di stabilire una connessione sicura
                    connesso = true;//fermo il CountDownTimer
                    btsocket.connect();//avvio la connessione
                    led_rosso.setVisibility(View.GONE);
                    led_vuoto.setVisibility(View.GONE);
                    led_verde.setVisibility(View.VISIBLE);
                    label_nome_dispositivo_connesso.setText("Connesso a: " + address);//faccio apparire a chi è connesso
                }catch (IOException e){
                    e.printStackTrace();
                    resetConnection();
                    label_nome_dispositivo_connesso.setText("Connessione fallita!");
                }
            }
        });
    }

    protected void OnDestroy() {
        super.onDestroy();
        if (bt_adapter != null) {
            bt_adapter.cancelDiscovery();
            unregisterReceiver(receiver);//elimino la registrazione della trasmissione
        }
    }

    //la void SCAN_BT gestisce il click del pulsante
    public void scan_bt() {
        adapter.clear();
        if (bt_adapter == null) {
            Toast.makeText(getApplicationContext(), R.string.bt_non_supportato, Toast.LENGTH_LONG).show();
        } else {
            if (!bt_adapter.isEnabled()) {//se il BT è disabilitato
                Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);//chiede di attivare il chip BT
                startActivityForResult(turnOn, 0);//startActivityForResult = permette di recuperare un dato
                                                 //in questo caso si aprirà una finestrella che chiederà di
                                                //abilitare il BT
                                               //Il risultato verrà trasferito al "onActivityResult"
                                              //In questo caso si occuperà di trasferimento il comando
                                             //di ACCESSO o NEGAZIONE all'attivazione del BT
                //Intent => messaggi che il sistema manda ad un'applicazione quando aspetta qualche azione dall'app
            } else {//se invece il BT è già attivo
                lista_dispositivi_accoppiati();//lista dispositivi accoppiati in passato
                lista_nuovi_dispositivi();//lista nuovi dispositivi
                label_nome_dispositivo_connesso.setText("Non Connesso - Bt abilitato. Ricerca altri dispositivi BT");
                Toast.makeText(getApplicationContext(), R.string.mess_btAttivo, Toast.LENGTH_LONG).show();//messaggino che dice che bt attivo
            }
        }
    }

    @Override
    //si occupa della gestione della risposta di Attivazione del BT
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            lista_dispositivi_accoppiati();
        }
    }

    //si occupa di far apparire la lista dei dispositivi BT già accoppiati (indipendentemente se siano attivi attualmente)
    private void lista_dispositivi_accoppiati(){
        adapter.clear();//pulisco la listview da precedenti ricerche
        if(pairedDevices.size() > 0){//se la lunghezza del nome dei dispositivi accoppiati in passato è > 0
            for(BluetoothDevice device : pairedDevices){//per ogni dispositivo associato in passato
                adapter.add(device.getName() + "\n" + device.getAddress());//aggiungi alla listview Nome e Indirizzo
            }
        }else {//altrimenti
            adapter.add(R.string.device_accoppiati_non_trovati);//fai apparire sto mess
        }
    }


    //si occupa di trovare nuovi dispositivi BT attivi
    private void lista_nuovi_dispositivi(){
        if(!bt_adapter.isDiscovering()){//se il dispositivo non è già alla ricerca di altri dispositivi
            bt_adapter.startDiscovery();//avvio il processo di rilevamento di altri dispositivi del dispositivo per 120 secondi
        }

        receiver = new BroadcastReceiver() {//creo un nuovo oggetto "Trasmissione"
                     //BroadcastReceiver => è un ascoltatore di eventi di sistema (Intent)
            @Override
            public void onReceive(Context context, Intent intent) {//Il metodo onReceive è un metodo che si occupa della ricezione di un messaggio da parte dei broadcast
                String action = intent.getAction();
                 if(BluetoothDevice.ACTION_FOUND.equals(action)){//quando trovi un dispositivo
                     //getParcelableExtra => recupero informazioni dal nuovo dispositivo
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);//creo un oggetto "device" che mi fornirà i dati del nuovo dispositivo trovato, trasferitemi da "getParcelableExtra"
                     //if(device.getBondState() != BluetoothDevice.BOND_BONDED)//se il dispositivo non è collegato a nessun altro dispositivo
                     NuovodeviceName = device.getName();//prendi il nome del device trovato
                     NuovodeviceAddress = device.getAddress();//prendine l'indirizzo
                     //NuovodeviceLegame = device.getBondState();//prendine lo stato di connessione
                     String _new_device = NuovodeviceName + "\n" + NuovodeviceAddress;//uniscili
                     /*
                     switch (NuovodeviceLegame){
                         case 10: StatoLegameDevice = "Non connesso"; break;
                         case 11: StatoLegameDevice = "In connessione..."; break;
                         case 12: StatoLegameDevice = "Connesso"; break;
                         default: StatoLegameDevice = "Errore"; break;
                     }
                     adapter.add(_new_device + "\n" + "Stato: " + StatoLegameDevice);
                     */
                     adapter.add(_new_device);
                }
            }//fine onReceive
                    //fine new BroadcastReceiver
        };//________________________________________________________________________________________

            //registro la "trasmissione"
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }


    //si occupa di chiudere la connessione
    private void resetConnection(){
        if(btsocket != null){//se la connessione è stabilita
            try{
                btsocket.close();//chiude la connessione
            }catch (Exception e){

            }
        }
    }

    //contiene la chiamata delle void: forward/back/left/right
    public void comandi(){
        //FORWARD
        forward();

        //BACK
        back();



        //RIGHT
        right();


        //LEFT
        left();
    }

    public void forward(){
        imgbtn_up.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event){
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    try{
                        writeData("F");
                    }catch (Exception ex){
                        //Toast.makeText(getApplicationContext(), "ERR: " + ex.toString(), Toast.LENGTH_SHORT).show();
                    }
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    try{
                        writeData("S");
                    }catch (Exception ex){

                    }
                }
                return false;
            }

        });
    }

    public void back(){
        imgbtn_back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    try{
                        writeData("B");
                    }catch (Exception ex){

                    }
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    try{
                        writeData("S");
                    }catch (Exception ex){

                    }
                }
                return false;
            }
        });
    }

    public void right(){
        imgbtn_right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    try{
                        writeData("R");
                    }catch (Exception ex){

                    }
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    try{
                        writeData("S");
                    }catch (Exception ex){

                    }
                }
                return false;
            }
        });
    }

    public void left(){
        imgbtn_left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    try{
                        writeData("L");
                    }catch (Exception ex){

                    }
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    try{
                        writeData("S");
                    }catch (Exception ex){

                    }
                }
                return false;
            }
        });
    }

    //si occupa d'inviare comandi al BT di ARDUINO
    public void writeData(String data) {
        //do {
            try {
                outStream = btsocket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Erroe_1: " + e.toString(), Toast.LENGTH_LONG).show();
            }

            String mess = data;
            byte[] msgBuffer = mess.getBytes();

            try {
                outStream.write(msgBuffer);
                //Toast.makeText(getApplicationContext(), "Send: " + mess.toString(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Erroe_2: " + e.toString(), Toast.LENGTH_LONG).show();
            }
        // }while(send_message == false);
    }

    public void avvio_streaming(){
        btn_connetti_streaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browser.getSettings().setJavaScriptEnabled(true);//attivo Javascript per la WEBVIEW
                url = text_url.getText().toString().trim();
                //Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
                browser.loadUrl("http://192.168.1.5:8080");
            }
        });
    }


}//fine MainActivity
