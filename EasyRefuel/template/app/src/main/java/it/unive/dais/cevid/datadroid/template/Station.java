package it.unive.dais.cevid.datadroid.template;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

import it.unive.dais.cevid.datadroid.lib.util.MapItem;

/**
 * Created by Sara on 26/12/2017.
 */

public class Station implements MapItem {

    String ID;
    String Nome;
    String Provincia;
    String Comune;
    String Indirizzo;
    String Gestore;
    HashMap<String,Double> Carburanti_costo;
    String Bandiera;
    LatLng position;

    public Station(String ID, String Nome, String Provincia, String Comune, String Indirizzo, String Gestore, String Bandiera, LatLng position,HashMap<String,Double> Carburanti_costo){
        this.ID = ID;
        this.Nome = Nome;
        this.Provincia = Provincia;
        this.Comune = Comune;
        this.Indirizzo = Indirizzo;
        this.Gestore = Gestore;
        this.Bandiera = Bandiera;
        this.position = position;
        this.Carburanti_costo = Carburanti_costo;
    }

    public String getID(){return ID;}
    public String getTitle(){return Nome;}
    public String getProvincia(){return Provincia;}
    public String getComune(){return  Comune;}
    public  String getIndirizzo(){return Indirizzo;}
    public String getGestore(){return Gestore;}
    public HashMap<String,Double> getCarburantiCosto(){return Carburanti_costo;}
    public String getBandiera(){return Bandiera;}
    public LatLng getPosition(){return position;}
    public String getDescription(){return getTitle()+" "+getBandiera()+" "+getGestore()+" "+"indirizzo:"+getIndirizzo()+" "+getComune()+" "+getProvincia();}//+" "+getCarburanti()+getCosto();}
    }

