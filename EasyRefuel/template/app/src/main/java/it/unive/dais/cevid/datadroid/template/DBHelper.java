package it.unive.dais.cevid.datadroid.template;

/**
 * Created by Sara on 23/01/2018.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static android.database.sqlite.SQLiteDatabase.OPEN_READONLY;
import static android.database.sqlite.SQLiteDatabase.openDatabase;

public class DBHelper extends SQLiteOpenHelper {

        private static String DB_PATH = "/data/data/" + "it.unive.dais.cevid.datadroid.template" + "/databases";
        private static String DB_NAME = "db";
        private SQLiteDatabase myDataBase;
        private final Context myContext;


        public DBHelper(Context context) {
            super(context, DB_NAME, null, 1);
            this.myContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {}

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

        public void openDataBase() {
            /*apre il db*/
            String destPath = DB_PATH + DB_NAME;
            try {
                File f = new File(destPath);
               //if (!f.exists()) {
                    f.mkdirs();
                    f.createNewFile();
                    CopyDB(myContext.getAssets().open("db"), new FileOutputStream(destPath+DB_NAME));
                //}
            } catch (IOException e) {
                e.printStackTrace();
            }
            myDataBase = openDatabase(destPath+"db",null,OPEN_READONLY);
        }

        @Override
        public synchronized void close() {
            if (myDataBase != null)
                myDataBase.close();
            super.close();
        }

    public List<Station> getStationFromFuel (String fuel){

        String mQuery = " SELECT ID, descCarburante, Bandiera, Gestore, [Nome Impianto], Indirizzo, Comune, Provincia, Latitudine, Longitudine " +
                "FROM carburanti, distributori ON ID = idImpianto WHERE descCarburante LIKE ? GROUP BY ID ";

        Cursor cursor = myDataBase.rawQuery(mQuery, new String[]{'%'+fuel+'%'});
        List<Station> stations = new ArrayList<>();

        if (cursor.getCount()>0){
            if (cursor.moveToFirst()){
                do {
                    String ID = cursor.getString(0);
                    String carburante = cursor.getString(1);
                    String bandiera = cursor.getString(2);
                    String gestore = cursor.getString(3);
                    String nomeImpianto = cursor.getString(4);
                    String indirizzo = cursor.getString(5);
                    String comune = cursor.getString(6);
                    String provincia = cursor.getString(7);
                    String lat = cursor.getString(8);
                    String lon = cursor.getString(9);

                    //LatLng latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                    Station s = new Station(ID, nomeImpianto, provincia, comune, indirizzo, gestore, bandiera,lat,lon,carburante);
                    stations.add(s);
                } while (cursor.moveToNext());
            }
        }
        return stations;
    }
    public List<Station> getStationFromPlace(String place){

            String mQuery = " SELECT idImpianto, Bandiera, Gestore, [Nome Impianto], Indirizzo, Comune, Provincia, Latitudine, Longitudine " +
                    "FROM distributori WHERE comune LIKE ? OR indirizzo LIKE ? OR provincia LIKE ? ";

            Cursor cursor = myDataBase.rawQuery(mQuery, new String[]{'%'+place+'%','%'+place+'%','%'+place+'%'});
            List<Station> stations = new ArrayList<>();

            if (cursor.getCount()>0){
                if (cursor.moveToFirst()){
                    do {
                        String ID = cursor.getString(0);
                        String gestore = cursor.getString(1);
                        String bandiera = cursor.getString(2);
                        String nomeImpianto = cursor.getString(3);
                        String indirizzo = cursor.getString(4);
                        String comune = cursor.getString(5);
                        String provincia = cursor.getString(6);
                        String lat = cursor.getString(7);
                        String lon = cursor.getString(8);

                        String carburante = "";

                        //LatLng latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                        Station s = new Station(ID, nomeImpianto, provincia, comune, indirizzo, gestore, bandiera,lat,lon,carburante);
                        stations.add(s);
                    } while (cursor.moveToNext());
                }
            }
            return stations;
    }



    /*Definisco un metodo per la copia del DB*/
    public void CopyDB(InputStream inputStream,OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }








}
