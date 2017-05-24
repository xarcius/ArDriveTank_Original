package v3d.com.ardrivetank;

/**
 * Created by nick- on 24/05/2017.
 */

public class BrLanguageClass {

    public BrLanguageClass(){

    }
            //1
    public String getAtivado(){
        String s = "Bt Ativado";
        return s;
    }

    //2
    public String getDispositivosEmparelhadosNãoEncontrados(){
        String s = "Dispositivos emparelhados não encontrados";
        return s;
    }

    //3
    public String getBtNãoSuportado(){
        String s = "Bt não suportado";
        return s;
    }


            //4
    public String getInserirUmaUrl(){
        String s = "Inserir uma URL";
        return s;
    }

            //5
    public String getNãoConectado(){
        String s = "Não conectando - Bt desativado";
        return s;
    }

            //6
    public String getConectando(){
        String s = "Conectando...";
        return s;
    }

        //7
    public String getConectadoA(){
        String s = "Conectado a: ";
        return s;
    }

        //8
    public String getConexãoFalhada(){
        String s = "Conexão falhada";
        return s;
    }

            //9
    public String getNãoConectadoBtAtivado(){
        String s = "Não conectado - Bt ativado. Procurando outros dispositivos Bt";
        return s;
    }
}
