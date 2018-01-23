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
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import static android.database.sqlite.SQLiteDatabase.OPEN_READONLY;
import static android.database.sqlite.SQLiteDatabase.openDatabase;

public class DBHelper extends SQLiteOpenHelper {

        //The Android's default system path of your application database.
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
                if (!f.exists()) {
                f.mkdirs();
                f.createNewFile();
                CopyDB(myContext.getAssets().open("db"), new FileOutputStream(destPath + DB_NAME));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            SQLiteDatabase db = openDatabase(destPath,null,OPEN_READONLY);

        }

        @Override
        public synchronized void close() {
            if (myDataBase != null)
                myDataBase.close();
            super.close();
        }

        /*private List<Station> getStationfromFuel(String fuel,SQLiteDatabase db){
            String mQuery = "SELECT idImpianto FROM carburanti where descCarburante LIKE ?";
            Cursor c = db.rawQuery(mQuery,new String[]{'%'+fuel+'%'});
            List<String> ids = new ArrayList<>();
            if (c != null ) {
                if  (c.moveToFirst()) {
                    do {
                        ids.add(c.getString(0));
                    }while (c.moveToNext());
                }
            }
            List<Station> stations = new ArrayList<>();
            for(String str: ids){
                String query =  "SELECT * FROM distributori where idImpianto ="+str;

                String ID = str;
                String gestore = c.getString(1);
                String bandiera = c.getString(2);
                String nome = c.getString(4);
                String indirizzo = c.getString(5);
                String comune = c.getString(6);
                String provincia = c.getString(7);
                String lat = c.getString(8);
                String lon = c.getString(9);

                LatLng latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));

                Station s = new Station(ID, nome, provincia, comune, indirizzo, gestore, bandiera, latlng);
                stations.add(s);
            }
            return stations;
        }*/

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
