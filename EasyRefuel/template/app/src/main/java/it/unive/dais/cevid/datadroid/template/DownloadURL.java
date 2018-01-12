package it.unive.dais.cevid.datadroid.template;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unive.dais.cevid.datadroid.lib.parser.CsvRowParser;
import it.unive.dais.cevid.datadroid.lib.parser.RecoverableParseException;

import static android.content.ContentValues.TAG;

/**
 * Created by Sara on 08/01/2018.
 */

public class DownloadURL extends AsyncTask<HashMap<Integer,Station>, Void,HashMap<Integer,Station>> {

    public static final int TIMEOUT = 1000000000;

    protected HashMap<Integer,Station> doInBackground(HashMap<Integer,Station>[] station) {
        URL url_benz = null;
        URL url_costi = null;
        try {
            url_benz = new URL("http://www.sviluppoeconomico.gov.it/images/exportCSV/anagrafica_impianti_attivi.csv");
            url_costi = new URL("http://www.sviluppoeconomico.gov.it/images/exportCSV/prezzo_alle_8.csv");
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
        HttpURLConnection c1;
        HttpURLConnection c2;
        c1 = null;
        c2 = null;
        try {
            c1 = (HttpURLConnection) url_benz.openConnection();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        try {
            c1.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        //c1.setConnectTimeout(TIMEOUT);
        c1.setReadTimeout(TIMEOUT);
        try {
            c1.connect();
            InputStream is1 = c1.getInputStream();
            Reader reader1 = new InputStreamReader(is1);
            BufferedReader br1 = new BufferedReader(reader1);
            /*dovrebbe eliminare la prima riga delle tabelle*/
            br1.readLine();
            CsvRowParser parser_benz = new CsvRowParser(br1, true, ";", null);
            int i = 0;
            List<CsvRowParser.Row> rows1 = parser_benz.parse();
            c1.disconnect();

            /*parsa la prima tabella*/
            for (CsvRowParser.Row row : rows1) {
                try {
                    i++;
                    String ID = row.get("idImpianto");
                    String lat = row.get("Latitudine");
                    String lon = row.get("Longitudine");
                    String provincia = row.get("Provincia");
                    String comune = row.get("Comune");
                    String indirizzo = row.get("Indirizzo");
                    String nome = row.get("Nome Impianto");
                    String bandiera = row.get("Bandiera");
                    String gestore = row.get("Gestore");

                    HashMap<String,Double> carburanti_costo = new HashMap<>();

                    LatLng latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                    int id = Integer.parseInt(ID);
                    station[0].put(id,new Station(ID, nome, provincia, comune, indirizzo, gestore, bandiera, latlng,carburanti_costo));
                    Log.d(TAG, "onMapReady;" + nome + i + Arrays.toString(row.getValues()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            try {
                c2 = (HttpURLConnection) url_costi.openConnection();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            try {
                c2.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
           // c2.setConnectTimeout(TIMEOUT);
            c2.setReadTimeout(TIMEOUT);
            c2.connect();
            InputStream is2 = c2.getInputStream();
            Reader reader2 = new InputStreamReader(is2);
            BufferedReader br2 = new BufferedReader(reader2);
            br2.readLine();
            CsvRowParser parser_costi = new CsvRowParser(br2, true, ";", null);
            List<CsvRowParser.Row> rows2 = parser_costi.parse();
            c2.disconnect();
            i = 0;
            /*parsa la seconda tabella*/
            for(CsvRowParser.Row row : rows2) {
              int id = 0;
                try {
                    id = Integer.parseInt(row.get("idImpianto"));
                    double prezzo = Double.parseDouble(row.get("prezzo"));
                    /*cerco la stazione con l'id corretto e la rimuovo dall'hashMap*/
                    Station temp = station[0].remove(id);
                    /*controllo se l'id corrisponde ad una stazione esistente*/
                    if(temp != null) {
                        /*aggiungo alla hashMAp dei carburanti il carburante e il prezzo appena trovati*/
                        temp.getCarburantiCosto().put(row.get("descCarburante"), prezzo);
                        /*rimetto la stazione nella HashMap*/
                        station[0].put(id, temp);
                        Log.d(TAG, "onMapReady;" + i + Arrays.toString(row.getValues()));
                        i++;
                    }
                } catch (RecoverableParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException  e) {
            e.printStackTrace();
        }
        return station[0];
    }
        /*protected void onProgressUpdate(Integer... progress) {
            setProgressPercent(progress[0]);
        }
        protected void onPostExecute(List<Station> stations) {
            return stations[0];
        }*/
}
