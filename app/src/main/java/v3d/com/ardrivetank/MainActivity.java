package v3d.com.ardrivetank;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
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

    ImageButton imgbtn_bt, imgbtn_btClose; //dichiaro il bottone-immagine
    ListView list__bt;//dichiaro la listview dove vedrò tutti i dispositivi bt abilitati
    ImageButton imgbtn_forward, imgbtn_back, imgbtn_right, imgbtn_left;//bottoni avanti,indietro,destra,sinistra
    ImageButton led_vuoto, led_rosso, led_verde;//immagini led
    ImageButton fari_off, fari_on;
    TextView label_nome_dispositivo_connesso;//testo del dispositivo a cui è connesso

    private BluetoothAdapter bt_adapter = BluetoothAdapter.getDefaultAdapter(); //cerca i dispositivi nelle vicinanze con cui comunicare

    private ArrayAdapter adapter = null;//permette di gestire dei dati
                                        //memorizzati sotto forma di array
                                        //Gestisce quello che si è trovato

    Set<BluetoothDevice> pairedDevices = bt_adapter.getBondedDevices();//prendo l'insieme dei dispositivi già accoppiati

    //gestione nuovi device BT non accoppiati in passato
    BroadcastReceiver receiver;
    String NuovodeviceName;
    String NuovodeviceAddress;
    //int NuovodeviceLegame;
    //String StatoLegameDevice;

    int counter = 0;
    boolean connesso = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//è un numero di identificazione unico assegnato a un hardware (numero che viene assegnato all'HC-05)
    BluetoothSocket btsocket = null; //creo una variabile di tipo socket per la trasmissione e ricezione dati

    OutputStream outStream;//si occupa dell'invio dei comandi all'HC-05

    //dichiarazioni variabili per lo streaming video
    WebView browser;
    Button btn_connetti_streaming;
    EditText text_url;
    String url;

    //LANGUAGE
    String language;

    EnLanguageClass en = new EnLanguageClass();
    PtLanguageClass pt = new PtLanguageClass();
    BrLanguageClass br = new BrLanguageClass();

    public boolean StatoFari = false;

    //fine dichiarazione variabili

//__________________________________________________________________________________________________

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);//Quando l'app si avvia,
                                                                                  // andrà in LANDSCAPE inverso
        //si occupa di prendere la scelta della LINGUUA
        Intent myintent = new Intent();
        //language = myintent.getExtras().getString("LANGUAGE");
        language = myintent.getStringExtra("LANGUAGE");


        assegnazioni();

        //disattivo il led verde e rosso
        led_rosso.setVisibility(View.GONE);//disattivazione
        led_verde.setVisibility(View.GONE);//disattivazione
        led_vuoto.setVisibility(View.VISIBLE);//attivazione
        label_nome_dispositivo_connesso.setText(R.string.no_connessione_bt_disabilitato_IT);//testo

        configurazione_grafica_lstView();

        led_on_off();

        click_BT_button();

        click_item_listView();

        comandi();

        avvio_streaming();

        icona_faro_on();

        icona_faro_off();

        CloseConnessioneBt();

        back();

        forward();

        left();

        right();

    }//fine OnCreate

    //assegnazione delle variabili ai widget
    public void assegnazioni(){
        //ASSEGNAZIONE
        imgbtn_bt = (ImageButton) findViewById(R.id.id_imBtn_bt); //assegno la variabile al widget
        imgbtn_btClose = (ImageButton) findViewById(R.id.id_btClose);
        list__bt = (ListView) findViewById(R.id.id_list_bt);

        imgbtn_forward = (ImageButton) findViewById(R.id.id_forward_button);
        imgbtn_back = (ImageButton) findViewById(R.id.id_back_button);
        imgbtn_right = (ImageButton) findViewById(R.id.id_right_button);
        imgbtn_left = (ImageButton) findViewById(R.id.id_left_button);

        led_rosso = (ImageButton) findViewById(R.id.id_led_ardrivetank_rosso);
        led_verde = (ImageButton) findViewById(R.id.id_led_ardrivetank_verde);
        led_vuoto = (ImageButton) findViewById(R.id.id_led_ardrivetank_vuoto);

        label_nome_dispositivo_connesso = (TextView) findViewById(R.id.id_text_nome_dispositivo_connesso);

        browser = (WebView) findViewById(R.id.id_browser);
        btn_connetti_streaming = (Button) findViewById(R.id.id_connetti_stream);
        text_url = (EditText) findViewById(R.id.id_url);

        fari_off = (ImageButton) findViewById(R.id.id_fari_off);
        fari_on = (ImageButton) findViewById(R.id.id_fari_on);
    }

    //lampeggiamento led
    public void led_on_off(){
        //faccio lampeggiare i led
        //poichè il dispositivo non è connesso
        //a nessun altro dispositivo
        if(connesso == false) {
            //x    //y
            new CountDownTimer(300000, 1500) {
                                //x  ,  y
                //esegui X volte, ogni Y millisecondi
                //1000 = ogni secondo [1000 millisecondi = 1 secondo]
                public void onTick(long millisUntilFinished) {
                    counter++;
                    if (counter % 2 == 0) { //pari
                        led_rosso.setVisibility(View.GONE);//faccio sparire il led rosso
                        led_vuoto.setVisibility(View.VISIBLE);//faccio apparire il led vuoto
                    } else {//dispari
                        led_rosso.setVisibility(View.VISIBLE);//faccio apparire il led rosso
                        led_vuoto.setVisibility(View.GONE);//sparire il led vuoto
                    }
                }

                public void onFinish() {
                    //quando il timer finisce, fai qualcosa
                }
            }.start();
        }//FINE [if connesso == true]
    }

    //configurazione grafica della LISTVIEW
    public void configurazione_grafica_lstView(){
        //CONFIGURAZIONE BT
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1);//configurazione grafica che avrà la listview (font, ecc..)
        list__bt.setAdapter(adapter);//setto la grafica della listview
    }

    //si occupa di ciò che accade quando viene premuto il tasto del BT
    public void click_BT_button(){
        //gestisce il click sul pulsante-imageBtn_bt
        imgbtn_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan_bt();
            }
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
                //lg_mess_connessione();//si occupa dell'apparizione del testo nella lingua selezionata per questo messaggio
                mess_language(language, 6);//messaggio "CONNESSIONE"
                bt_adapter.cancelDiscovery();//termina la ricerca
                final String info = ((TextView) view).getText().toString();//prendo l'elemento cliccato ossia "dalla view cliccata prendi il testo"
                String address = info.substring(info.length()-17);//recupero l'indirizzo MAC
                BluetoothDevice connect_device = bt_adapter.getRemoteDevice(address);
                try{
                    btsocket = connect_device.createRfcommSocketToServiceRecord(myUUID);//chiamo il protocollo RFCOMM. "createRfcommSocketToServiceRecord" si occuperà di stabilire una connessione sicura
                    connesso = true;//fermo il CountDownTimer
                    btsocket.connect();//avvio la connessione
                    writeData("C");
                    led_rosso.setVisibility(View.GONE);
                    led_vuoto.setVisibility(View.GONE);
                    led_verde.setVisibility(View.VISIBLE);
                    label_nome_dispositivo_connesso.setText(R.string.connesso_a_IT + " " + address);//faccio apparire a chi è connesso
                }catch (IOException e){
                    e.printStackTrace();
                    resetConnection();
                    //label_nome_dispositivo_connesso.setText(R.string.connessione_fallita_IT);
                    mess_language(language, 8);
                }
            }
        });
    }

    //la void SCAN_BT gestisce il click del pulsante
    public void scan_bt() {
        adapter.clear();
        if (bt_adapter == null) {
            Toast.makeText(getApplicationContext(), R.string.bt_non_supportato_IT, Toast.LENGTH_LONG).show();
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
                label_nome_dispositivo_connesso.setText(R.string.non_connesso_bt_disabilitato_ricerca_altri_dispositivi_bt_IT);
                Toast.makeText(getApplicationContext(), R.string.mess_btAttivo_IT, Toast.LENGTH_LONG).show();//messaggino che dice che bt attivo
            }
        }
    }

    @Override
    //si occupa della gestione della risposta di Attivazione del BT
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            lista_dispositivi_accoppiati();
            lista_nuovi_dispositivi();
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
            adapter.add(R.string.device_accoppiati_non_trovati_IT);//fai apparire sto mess
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
                     //String _new_device = NuovodeviceName + "\n" + NuovodeviceAddress;//uniscili
                     /*
                     switch (NuovodeviceLegame){
                         case 10: StatoLegameDevice = "Non connesso"; break;
                         case 11: StatoLegameDevice = "In connessione..."; break;
                         case 12: StatoLegameDevice = "Connesso"; break;
                         default: StatoLegameDevice = "Errore"; break;
                     }
                     adapter.add(_new_device + "\n" + "Stato: " + StatoLegameDevice);
                     */
                     adapter.add(NuovodeviceName);
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
                writeData("D");
                led_on_off();
                bt_adapter = null;
                outStream.close();
                unregisterReceiver(receiver);
                btsocket.close();//chiude la connessione
                led_verde.setVisibility(View.INVISIBLE);
            }catch (Exception e){

            }
        }
    }

    public void CloseConnessioneBt(){
        imgbtn_btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetConnection();//tenta di chiudere la connessione
            }
        });
    }

    //contiene la chiamata delle void: forward/back/left/right
    public void comandi(){
        if(connesso == true) {

            //FORWARD
            forward();

            //BACK
            back();


            //RIGHT
            right();


            //LEFT
            left();
        }
    }

    public void forward(){
        imgbtn_forward.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event){
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    try{
                        writeData("F");
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
        if(connesso == true) {
            try {
                outStream = btsocket.getOutputStream();//si occupa dell'invio del comando
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Error_1: " + e.toString(), Toast.LENGTH_LONG).show();
            }

            String mess = data;
            byte[] msgBuffer = mess.getBytes();//trasformo in Bytes il comando

            try {
                outStream.write(msgBuffer);//invia il comando
                //Toast.makeText(getApplicationContext(), "Send: " + mess.toString(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Error_2: " + e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void avvio_streaming(){
        btn_connetti_streaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text_url.getText() != null) {
                    browser.getSettings().setJavaScriptEnabled(true);//attivo Javascript per la WEBVIEW
                    url = text_url.getText().toString();//prendo l'url dalla EditText
                    //se l'utente non inserisce http://
                    if (!url.contains("http://")) {
                        browser.loadUrl("http://" + url + "/video");//avvio lo streaming video-diretto
                    } else if (url.contains("http://")) {//altrimenti se lo inserisce
                        url.replace("http://", "");//glielo cancello
                        browser.loadUrl("http://" + url + "/video");//avvio lo streaming video-diretto
                    }
                }else if(text_url.getText() == null)
                {
                    Toast.makeText(getApplicationContext(), R.string.inserisci_url_IT, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void mess_language(String lang, int numero_messaggio){
        if(lang == "en"){
            if(numero_messaggio == 1){
                en.getBtOn();
            }else if(numero_messaggio == 2){
                en.getPairedDevicesNotFound();
            }else if(numero_messaggio == 3){
                en.getBtNotSupported();
            }else if(numero_messaggio == 4){
                en.getInsertUrl();
            }else if(numero_messaggio == 5){
                en.getNotConnected();
            }else if(numero_messaggio == 6){
                en.getConnecting();
            }else if(numero_messaggio == 7){
                en.getConnected();
            }else if(numero_messaggio == 8){
                en.getConnectionFailed();
            }else if(numero_messaggio == 9){
                en.getNotConnectedBtEnabled();
            }
        }

        else if(lang == "pt"){
            if(numero_messaggio == 1){
                pt.getBtAtivado();
            }else if(numero_messaggio == 2){
                pt.getDispositivosEmparelhadosNãoEncontrados();
            }else if(numero_messaggio == 3){
                pt.getBtNãoSuportado();
            }else if(numero_messaggio == 4){
                pt.getInserirUmURL();
            }else if(numero_messaggio == 5){
                pt.getNãoLigando();
            }else if(numero_messaggio == 6){
                pt.getALigar();
            }else if(numero_messaggio == 7){
                pt.getLigando();
            }else if(numero_messaggio == 8){
                pt.getLigaçãoFalhada();
            }else if(numero_messaggio == 9){
                pt.getNãoLigandoBtAtivado();
            }
        }

        else if(lang == "br"){
            if(numero_messaggio == 1){
                br.getAtivado();
            }else if(numero_messaggio == 2){
                br.getDispositivosEmparelhadosNãoEncontrados();
            }else if(numero_messaggio == 3){
                br.getBtNãoSuportado();
            }else if(numero_messaggio == 4){
                br.getInserirUmaUrl();
            }else if(numero_messaggio == 5){
                br.getNãoConectado();
            }else if(numero_messaggio == 6){
                br.getConectando();
            }else if(numero_messaggio == 7){
                br.getConectadoA();
            }else if(numero_messaggio == 8){
                br.getConexãoFalhada();
            }else if(numero_messaggio == 9){
                br.getNãoConectadoBtAtivado();
            }
        }
    }

    public void icona_faro_on(){
        fari_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connesso == true) {
                    if (StatoFari == true) {
                        writeData("H");
                        StatoFari = false;
                        fari_on.setVisibility(View.INVISIBLE);
                        fari_off.setVisibility(View.VISIBLE);
                        fari_on.setEnabled(false);
                        fari_off.setEnabled(true);
                    }
                }
            }
        });
    }

    public void icona_faro_off(){
        fari_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connesso == true) {
                    if (StatoFari == false) {
                        writeData("H");
                        StatoFari = true;
                        fari_on.setVisibility(View.VISIBLE);
                        fari_off.setVisibility(View.INVISIBLE);
                        fari_on.setEnabled(true);
                        fari_off.setEnabled(false);
                    }
                }
            }
        });
    }


}//fine MainActivity
