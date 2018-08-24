package br.com.californiamobile.organizze.helper;

import java.text.SimpleDateFormat;

public class DateCustom {


    public static String daraAtual() {

        Long data = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dataString = sdf.format(data);
    return dataString;
    }

    public static String mesAnoEscolhido(String data){
        String[] retornoData = data.split("/");
        String dia = retornoData[0];
        String mes = retornoData[1];
        String ano = retornoData[2];
        String mesAno = mes + ano;

    return mesAno;


    }
}
