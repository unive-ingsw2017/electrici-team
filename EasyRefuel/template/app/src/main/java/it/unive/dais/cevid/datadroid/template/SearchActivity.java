package it.unive.dais.cevid.datadroid.template;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Sara on 07/01/2018.
 */

public class SearchActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            /*helper per il db*/
            DBHelper db = new DBHelper(this);
            db.openDataBase();
            List<Station> stations = db.getStationFromPlace(query);

            CustomAdapter adapter = new CustomAdapter(this,stations);
            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(adapter);

           listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                        LatLng latlng = stations.get(position).getPosition();

                        String ID = stations.get(position).getID();
                        String carburante = stations.get(position).getCarburante();
                        String bandiera = stations.get(position).getBandiera();
                        String gestore = stations.get(position).getGestore();
                        String nomeImpianto = stations.get(position).getTitle();
                        String indirizzo = stations.get(position).getIndirizzo();
                        String comune = stations.get(position).getComune();
                        String provincia = stations.get(position).getProvincia();
                        String latitudine = stations.get(position).getLatitudine();
                        String longitudine = stations.get(position).getLongitudine();

                        Station s = new Station(ID, nomeImpianto, provincia, comune, indirizzo, gestore, bandiera,latitudine,longitudine,carburante);

                        /*manda un intent con la stazione appena creata alla maps activity per far comparire il marker*/
                        Intent i = new Intent(SearchActivity.this, MapsActivity.class);
                        i.putExtra("search_result", (Parcelable) s);
                        startActivity(i);
                }
            });

        }
    }
}
