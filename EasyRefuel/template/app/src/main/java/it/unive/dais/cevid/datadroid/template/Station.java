package it.unive.dais.cevid.datadroid.template;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import it.unive.dais.cevid.datadroid.lib.util.MapItem;

/**
 * Created by Sara on 26/12/2017.
 */

public class Station implements MapItem, Parcelable,Serializable {

    String ID;
    String Nome;
    String Provincia;
    String Comune;
    String Indirizzo;
    String Gestore;
    String Carburante;
    String Bandiera;
    String latitudine;
    String longitudine;

    public Station(String ID, String Nome, String Provincia, String Comune, String Indirizzo, String Gestore, String Bandiera, String latitudine,String logitudine,String Carburante){
        this.ID = ID;
        this.Nome = Nome;
        this.Provincia = Provincia;
        this.Comune = Comune;
        this.Indirizzo = Indirizzo;
        this.Gestore = Gestore;
        this.Bandiera = Bandiera;
        this.latitudine = latitudine;
        this.longitudine = logitudine;
        this.Carburante = Carburante;
    }

    protected Station(Parcel in) {
        ID = in.readString();
        Nome = in.readString();
        Provincia = in.readString();
        Comune = in.readString();
        Indirizzo = in.readString();
        Gestore = in.readString();
        Bandiera = in.readString();
        latitudine = in.readString();
        longitudine = in.readString();
        Carburante = in.readString();
    }

    public String getID(){return ID;}
    public String getTitle(){return Nome;}
    public String getProvincia(){return Provincia;}
    public String getComune(){return  Comune;}
    public String getIndirizzo(){return Indirizzo;}
    public String getGestore(){return Gestore;}
    public String getCarburante(){return Carburante;}
    public String getBandiera(){return Bandiera;}
    public LatLng getPosition(){
        LatLng position = new LatLng(Double.parseDouble(latitudine), Double.parseDouble(longitudine));
        return position;
    }
    public String getLatitudine(){return latitudine;}
    public String getLongitudine(){return longitudine;}
    public String getDescription(){return getIndirizzo()+ ", "+getComune() + ", "+getProvincia();}


    public static final Creator<Station> CREATOR = new Creator<Station>() {
        @Override
        public Station createFromParcel(Parcel in) {
            return new Station(in);
        }

        @Override
        public Station[] newArray(int size) {
            return new Station[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(Nome);
        dest.writeString(Provincia);
        dest.writeString(Comune);
        dest.writeString(Indirizzo);
        dest.writeString(Gestore);
        dest.writeString(Bandiera);
        dest.writeString(latitudine);
        dest.writeString(longitudine);
        dest.writeString(Carburante);
    }
    private void readFromParcel(Parcel in) {
        ID = in.readString();
        Nome = in.readString();
        Provincia = in.readString();
        Comune = in.readString();
        Indirizzo = in.readString();
        Gestore = in.readString();
        Bandiera = in.readString();
        latitudine = in.readString();
        longitudine = in.readString();
        Carburante = in.readString();
    }
}

