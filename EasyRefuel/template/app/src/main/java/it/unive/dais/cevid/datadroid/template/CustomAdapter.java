package it.unive.dais.cevid.datadroid.template;

import android.widget.ArrayAdapter;

/**
 * Created by Sara on 24/01/2018.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Station>{

        private final Context context;
        private final List<Station> stations;

        public CustomAdapter(Context context, List<Station> stations) {
            super(context, -1, stations);
            this.context = context;
            this.stations = stations;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.station_list, parent, false);
            TextView città = (TextView) rowView.findViewById(R.id.citta);
            TextView indirizzo = (TextView) rowView.findViewById(R.id.indirizzo);
            TextView bandiera = (TextView) rowView.findViewById(R.id.bandiera);
            TextView provincia = (TextView) rowView.findViewById(R.id.provincia);

            città.setText(stations.get(position).getComune());
            indirizzo.setText(stations.get(position).getIndirizzo());
            bandiera.setText(stations.get(position).getBandiera());
            provincia.setText(stations.get(position).getProvincia());

            return rowView;
        }
}



