/**
 * Questo package contiene le componenti Android riusabili.
 * Si tratta di classi che contengono già funzionalità base e possono essere riusate apportandovi modifiche
 */
package it.unive.dais.cevid.datadroid.template;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuAdapter;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unive.dais.cevid.datadroid.lib.parser.AsyncParser;
import it.unive.dais.cevid.datadroid.lib.parser.CsvRowParser;
import it.unive.dais.cevid.datadroid.lib.parser.RecoverableParseException;
import it.unive.dais.cevid.datadroid.lib.util.MapItem;
import okhttp3.OkHttpClient;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Questa classe è la componente principale del toolkit: fornisce servizi primari per un'app basata su Google Maps, tra cui localizzazione, pulsanti
 * di navigazione, preferenze ed altro. Essa rappresenta un template che è una buona pratica riusabile per la scrittura di app, fungendo da base
 * solida, robusta e mantenibile.
 * Vengono rispettate le convenzioni e gli standard di qualità di Google per la scrittura di app Android; ogni pulsante, componente,
 * menu ecc. è definito in modo che sia facile riprodurne degli altri con caratteristiche diverse.
 * All'interno del codice ci sono dei commenti che aiutano il programmatore ad estendere questa app in maniera corretta, rispettando le convenzioni
 * e gli standard qualitativi.
 * Per scrivere una propria app è necessario modificare questa classe, aggiungendo campi, metodi e codice che svolge le funzionalità richieste.
 *
 * @author Alvise Spanò, Università Ca' Foscari
 */
public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnMarkerClickListener{

    protected static final int REQUEST_CHECK_SETTINGS = 500;
    protected static final int PERMISSIONS_REQUEST_ACCESS_BOTH_LOCATION = 501;
    // alcune costanti
    private static final String TAG = "MapsActivity";
    /**
     * Questo oggetto è la mappa di Google Maps. Viene inizializzato asincronamente dal metodo {@code onMapsReady}.
     */
    protected GoogleMap gMap;
    /**
     * Pulsanti in sovraimpressione gestiti da questa app. Da non confondere con i pulsanti che GoogleMaps mette in sovraimpressione e che non
     * fanno parte degli oggetti gestiti manualmente dal codice.
     */
    protected ImageButton button_here,button_maps,button_nav,button_ibrida,button_satellite,button_rilievo,button_normale;
    protected Button button_confirm,button_go,button_credits,button_privacy;
    /**
     * API per i servizi di localizzazione.
     */
    protected FusedLocationProviderClient fusedLocationClient;
    /**
     * Posizione corrente. Potrebbe essere null prima di essere calcolata la prima volta.
     */
    @Nullable
    protected LatLng currentPosition = null;
    /**
     * Il marker che viene creato premendo il pulsante button_here (cioè quello dell'app, non quello di Google Maps).
     * E' utile avere un campo d'istanza che tiene il puntatore a questo marker perché così è possibile rimuoverlo se necessario.
     * E' null quando non è stato creato il marker, cioè prima che venga premuto il pulsante HERE la prima volta.
     */
    @Nullable
    protected Marker hereMarker = null;

    /*variabili per il drawer*/
    private NavigationView navigationView;
    private MenuAdapter mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    /*bottoni del drawer*/
    protected Switch GPL,diesel,benzina,metano,elettrico;

    /*progress bar e Async Task*/
    private ProgressBar mProgressBar;
    private AsyncTask<HashMap<Integer,Station>, Integer,HashMap<Integer,Station>> mMyTask;
    private TextView mTextView;

    HashMap<Integer,Station> station = new HashMap();
    private Collection<Marker> markers;


    /**
     * Questo metodo viene invocato quando viene inizializzata questa activity.
     * Si tratta di una sorta di "main" dell'intera activity.
     * Inizializza i campi d'istanza, imposta alcuni listener e svolge gran parte delle operazioni "globali" dell'activity.
     *
     * @param savedInstanceState bundle con lo stato dell'activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // inizializza le preferenze
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // trova gli oggetti che rappresentano i bottoni e li salva come campi d'istanza
        button_here = (ImageButton) findViewById(R.id.button_here);
        button_maps = (ImageButton) findViewById(R.id.button_maps);
        button_nav = (ImageButton) findViewById(R.id.button_nav);


        /*drawer*/
        navigationView = (NavigationView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        /*progress bar*/
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mTextView = (TextView) findViewById(R.id.text_view);

        /*aggiungici l'azione*/
        button_confirm = (Button) findViewById(R.id.Conferma);
        button_go = (Button) findViewById(R.id.Parti);

        button_credits = (Button) findViewById(R.id.Crediti);
        button_privacy = (Button) findViewById(R.id.Privacy);


        /*switch*/
        GPL = (Switch) findViewById(R.id.GPL);
        diesel = (Switch) findViewById(R.id.Diesel);
        metano = (Switch) findViewById(R.id.Metano);
        elettrico = (Switch) findViewById(R.id.Elettrico);
        benzina = (Switch) findViewById(R.id.Benzina);

        /*setup drawer*/
        //addDrawerItems();
        setupDrawer();

        /*fai partire l'async task e la progress bar*/
        mMyTask = new DownloadURL().execute(station);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        /*fine drawer*/

        // API per i servizi di localizzazione
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // inizializza la mappa asincronamente
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // quando viene premito il pulsante HERE viene eseguito questo codice
        button_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "here button clicked");
                gpsCheck();
                updateCurrentPosition();
                if (hereMarker != null) hereMarker.remove();
                if (currentPosition != null) {
                    MarkerOptions opts = new MarkerOptions();
                    opts.position(currentPosition);
                    opts.title(getString(R.string.marker_title));
                    opts.snippet(String.format("lat: %g\nlng: %g", currentPosition.latitude, currentPosition.longitude));
                    hereMarker = gMap.addMarker(opts);
                    if (gMap != null)
                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, getResources().getInteger(R.integer.zoomFactor_button_here)));
                } else
                    Log.d(TAG, "no current position available");
            }
        });

        button_credits.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "credits button clicked");
                // Start InfoActivity.class
                Intent myIntent = new Intent(MapsActivity.this,
                        InfoActivity.class);
                startActivity(myIntent);
            }
        });
        button_privacy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "privacy button clicked");
                // Start Privacy.class
                Intent myIntent = new Intent(MapsActivity.this,
                        PrivacyActivity.class);
                startActivity(myIntent);
            }
        });
        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GPL.isChecked()){

                }
                if(benzina.isChecked()){

                }
                if(diesel.isChecked()){

                }
                if(metano.isChecked()){

                }
                if(elettrico.isChecked()){

                }
                try {
                    /*pulisci la mappa dai marker,almeno credo*/
                    gMap.clear();
                    station = mMyTask.get();
                    /*filtra*/
                    markers = putMarkersFromMapItems(station);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });


       button_maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPopupClick(v);
            }
        });

       button_nav.setClickable(false);
    }

    // ciclo di vita della app
    //

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Applica le impostazioni (preferenze) della mappa ad ogni chiamata.
     */
    @Override
    protected void onResume() {
        super.onResume();
        applyMapSettings();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Pulisce la mappa quando l'app viene distrutta.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        gMap.clear();
    }


    public void onPopupClick(View view) {
        // get a reference to the already created main layout
        //FrameLayout mainLayout = (FrameLayout) findViewById(R.id.map);

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        // create the popup window
        int width = 1220;
        int height = 520;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        popupWindow.showAtLocation(view, Gravity.CENTER_VERTICAL, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    /*per drawer*/
    /*private void addDrawerItems() {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        toglilo perchè non c'è più un menu
                        switch (menuItem.getItemId()) {
                            case R.id.parti:
                                final EditText partenza = (EditText)findViewById(R.id.partenza);
                                final EditText destinazione = (EditText)findViewById(R.id.destinazione);
                                String part = partenza.getText().toString();
                                String dest = destinazione.getText().toString();
                                chiama il navigatore di google maps in qualche modo
                                //break;

                            case R.id.Conferma:

                                if(navigationView.getMenu().getItem(R.id.Benzina).isChecked()){

                                }
                                if(navigationView.getMenu().getItem(R.id.Metano).isChecked()){

                                }
                                if(navigationView.getMenu().getItem(R.id.GPL).isChecked()){

                                }
                                if(navigationView.getMenu().getItem(R.id.Elettrico).isChecked()){

                                }
                                if(navigationView.getMenu().getItem(R.id.Diesel).isChecked()){

                                }
                                try {
                                    station = mMyTask.get();

                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                }
                                markers = putMarkersFromMapItems(station);
                                break;
                        }

                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }*/

    private void setupDrawer() {

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /*fine per drawer*/
    /**
     * Quando arriva un Intent viene eseguito questo metodo.
     * Può essere esteso e modificato secondo le necessità.
     *
     * @see Activity#onActivityResult(int, int, Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(intent);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // inserire codice qui
                        break;
                    case Activity.RESULT_CANCELED:
                        // o qui
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    /**
     * Questo metodo viene chiamato quando viene richiesto un permesso.
     * Si tratta di un pattern asincrono che va gestito come qui impostato: per gestire nuovi permessi, qualora dovesse essere necessario,
     * è possibile riprodurre un comportamento simile a quello già implementato.
     *
     * @param requestCode  codice di richiesta.
     * @param permissions  array con i permessi richiesti.
     * @param grantResults array con l'azione da intraprende per ciascun dei permessi richiesti.
     * @see Activity#onRequestPermissionsResult(int, String[], int[])
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_BOTH_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "permissions granted: ACCESS_FINE_LOCATION + ACCESS_COARSE_LOCATION");
                } else {
                    Log.e(TAG, "permissions not granted: ACCESS_FINE_LOCATION + ACCESS_COARSE_LOCATION");
                    Snackbar.make(this.findViewById(R.id.map), R.string.msg_permissions_not_granted, Snackbar.LENGTH_LONG);
                }
            }
        }
    }

    /**
     * Invocato quando viene creato il menu delle impostazioni.
     *
     * @param menu l'oggetto menu.
     * @return ritornare true per visualizzare il menu.
     * @see Activity#onCreateOptionsMenu(Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps_with_options, menu);

        /*ricerca*/
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
               .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));

        return true;
    }

    /**
     * Invocato quando viene cliccata una voce nel menu delle impostazioni.
     *
     * @param item la voce di menu cliccata.
     * @return ritorna true per continuare a chiamare altre callback; false altrimenti.
     * @see Activity#onOptionsItemSelected(MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.MENU_SETTINGS:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.MENU_INFO:
                startActivity(new Intent(this, InfoActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // onConnection callbacks
    //
    //

    /**
     * Viene chiamata quando i servizi di localizzazione sono attivi.
     * Aggiungere qui eventuale codice da eseguire in tal caso.
     *
     * @param bundle il bundle passato da Android.
     * @see com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks#onConnected(Bundle)
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "location service connected");
    }

    /**
     * Viene chiamata quando i servizi di localizzazione sono sospesi.
     * Aggiungere qui eventuale codice da eseguire in tal caso.
     *
     * @param i un intero che rappresenta il codice della causa della sospenzione.
     * @see com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks#onConnectionSuspended(int)
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "location service connection suspended");
        Toast.makeText(this, R.string.conn_suspended, Toast.LENGTH_LONG).show();
    }

    /**
     * Viene chiamata quando la connessione ai servizi di localizzazione viene persa.
     * Aggiungere qui eventuale codice da eseguire in tal caso.
     *
     * @param connectionResult oggetto che rappresenta il risultato della connessione, con le cause della disconnessione ed altre informazioni.
     * @see com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener#onConnectionFailed(ConnectionResult)
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "location service connection lost");
        Toast.makeText(this, R.string.conn_failed, Toast.LENGTH_LONG).show();
    }

    // maps callbacks
    //
    //

    /**
     * Chiamare questo metodo per aggiornare la posizione corrente del GPS.
     * Si tratta di un metodo proprietario, che non ridefinisce alcun metodo della superclasse né implementa alcuna interfaccia: un metodo
     * di utilità per aggiornare asincronamente in modo robusto e sicuro la posizione corrente del dispositivo mobile.
     */
    public void updateCurrentPosition() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "requiring permission");
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_BOTH_LOCATION);
        } else {
            Log.d(TAG, "permission granted");
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location loc) {
                            if (loc != null) {
                                currentPosition = new LatLng(loc.getLatitude(), loc.getLongitude());
                                Log.i(TAG, "current position updated");
                            }
                        }
                    });
        }
    }

    /**
     * Viene chiamato quando si clicca sulla mappa.
     * Aggiungere qui codice che si vuole eseguire quando l'utente clicca sulla mappa.
     *
     * @param latLng la posizione del click.
     */
    @Override
    public void onMapClick(LatLng latLng) {
        // nascondi il pulsante della navigazione (non quello di google maps, ma il nostro pulsante custom)
        //button_nav.setVisibility(View.INVISIBLE);
    }

    /**
     * Viene chiamato quando si clicca a lungo sulla mappa (long click).
     * Aggiungere qui codice che si vuole eseguire quando l'utente clicca a lungo sulla mappa.
     *
     * @param latLng la posizione del click.
     */
    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    /**
     * Viene chiamato quando si muove la camera.
     * Attenzione: solamente al momento in cui la camera comincia a muoversi, non durante tutta la durata del movimento.
     *
     * @param reason
     */
    @Override
    public void onCameraMoveStarted(int reason) {
        setHereButtonVisibility();
    }

    /**
     * Metodo proprietario che imposta la visibilità del pulsante HERE.
     * Si occupa di nascondere o mostrare il pulsante HERE in base allo zoom attuale, confrontandolo con la soglia di zoom
     * impostanta nelle preferenze.
     * Questo comportamento è dimostrativo e non è necessario tenerlo quando si sviluppa un'applicazione modificando questo template.
     */
    public void setHereButtonVisibility() {
        if (gMap != null) {
            if (gMap.getCameraPosition().zoom < SettingsActivity.getZoomThreshold(this)) {
                button_here.setVisibility(View.INVISIBLE);
            } else {
                button_here.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Questo metodo è molto importante: esso viene invocato dal sistema quando la mappa è pronta.
     * Il parametro è l'oggetto di tipo GoogleMap pronto all'uso, che viene immediatamente assegnato ad un campo interno della
     * classe.
     * La natura asincrona di questo metodo, e quindi dell'inizializzazione del campo gMap, implica che tutte le
     * operazioni che coinvolgono la mappa e che vanno eseguite appena essa diventa disponibile, vanno messe in questo metodo.
     * Ciò non significa che tutte le operazioni che coinvolgono la mappa vanno eseguite qui: è naturale aver bisogno di accedere al campo
     * gMap in altri metodi, per eseguire operazioni sulla mappa in vari momenti, ma è necessario tenere a mente che tale campo potrebbe
     * essere ancora non inizializzato e va pertanto verificata la nullness.
     *
     * @param googleMap oggetto di tipo GoogleMap.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_BOTH_LOCATION);
        } else {
            gMap.setMyLocationEnabled(true);
        }

        gMap.setOnMapClickListener(this);
        gMap.setOnMapLongClickListener(this);
        gMap.setOnCameraMoveStartedListener(this);
        gMap.setOnMarkerClickListener(this);

        UiSettings uis = gMap.getUiSettings();
        /*messo false per nascondere i bottoni*/
        uis.setZoomGesturesEnabled(true);
        uis.setMyLocationButtonEnabled(false);
        gMap.setOnMyLocationButtonClickListener(
                new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        gpsCheck();
                        return false;
                    }
                });
        uis.setCompassEnabled(true);
        uis.setZoomControlsEnabled(false);
        uis.setMapToolbarEnabled(true);

        applyMapSettings();

        //demo();
        //parser non funzionante
        //parseStation();
    }

    /**
    /**
     * Metodo proprietario che forza l'applicazione le impostazioni (o preferenze) che riguardano la mappa.
     */
    protected void applyMapSettings() {
        if (gMap != null) {
            Log.d(TAG, "applying map settings");
            gMap.setMapType(SettingsActivity.getMapStyle(this));
        }
        setHereButtonVisibility();
    }

    /**
     * Naviga dalla posizione from alla posizione to chiamando il navigatore di Google.
     *
     * @param from posizione iniziale.
     * @param to   posizione finale.
     */
    protected void navigate(@NonNull LatLng from, @NonNull LatLng to) {
        Intent navigation = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + from.latitude + "," + from.longitude + "&daddr=" + to.latitude + "," + to.longitude + ""));
        navigation.setPackage("com.google.android.apps.maps");
        Log.i(TAG, String.format("starting navigation from %s to %s", from, to));
        startActivity(navigation);
    }

    // marker stuff
    //
    //

    /**
     * Callback che viene invocata quando viene cliccato un marker.
     * Questo metodo viene invocato al click di QUALUNQUE marker nella mappa; pertanto, se è necessario
     * eseguire comportamenti specifici per un certo marker o gruppo di marker, va modificato questo metodo
     * con codice che si occupa di discernere tra un marker e l'altro in qualche modo.
     *
     * @param marker il marker che è stato cliccato.
     * @return ritorna true per continuare a chiamare altre callback nella catena di callback per i marker; false altrimenti.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        marker.showInfoWindow();
        //button_nav.setVisibility(View.VISIBLE);
        button_nav.setClickable(true);
        button_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, R.string.msg_button_car, Snackbar.LENGTH_SHORT);
                if (currentPosition != null) {
                    navigate(currentPosition, marker.getPosition());
                }
            }
        });
        return false;
    }

    /**
     * Metodo di utilità che permette di posizionare rapidamente sulla mappa una lista di MapItem.
     * Attenzione: l'oggetto gMap deve essere inizializzato, questo metodo va pertanto chiamato preferibilmente dalla
     * callback onMapReady().
     * @param l la lista di oggetti di tipo I tale che I sia sottotipo di MapItem.
     * @param <I> sottotipo di MapItem.
     * @return ritorna la collection di oggetti Marker aggiunti alla mappa.
     */


    @NonNull
    protected <I extends MapItem> Collection<Marker> putMarkersFromMapItems(List<I> l) {
        Collection<Marker> r = new ArrayList<>();
        for (MapItem i : l) {
            MarkerOptions opts = new MarkerOptions().title(i.getTitle()).position(i.getPosition()).snippet(i.getDescription());
            r.add(gMap.addMarker(opts));
        }
        return r;
    }
    /*putMarkers from Item che usa le HashMap invece che le liste*/
    @NonNull
    protected <K,V extends MapItem> Collection<Marker> putMarkersFromMapItems(HashMap<K,V> l) {
        Collection<Marker> r = new ArrayList<>();
        for (MapItem i : l.values()) {
            MarkerOptions opts = new MarkerOptions().title(i.getTitle()).position(i.getPosition()).snippet(i.getDescription());
            r.add(gMap.addMarker(opts));
        }
        return r;
    }

    /**
     * Metodo proprietario di utilità per popolare la mappa con i dati provenienti da un parser.
     * Si tratta di un metodo che può essere usato direttamente oppure può fungere da esempio per come
     * utilizzare i parser con informazioni geolocalizzate.
     *
     * @param parser un parser che produca sottotipi di MapItem, con qualunque generic Progress o Input
     * @param <I>    parametro di tipo che estende MapItem.
     * @return ritorna una collection di marker se tutto va bene; null altrimenti.
     */
    @Nullable
    protected <I extends MapItem> Collection<Marker> putMarkersFromData(@NonNull AsyncParser<I, ?> parser) {
        try {
            List<I> l = parser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
            Log.i(TAG, String.format("parsed %d lines", l.size()));
            return putMarkersFromMapItems(l);
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, String.format("exception caught while parsing: %s", e));
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Controlla lo stato del GPS e dei servizi di localizzazione, comportandosi di conseguenza.
     * Viene usata durante l'inizializzazione ed in altri casi speciali.
     */
    protected void gpsCheck() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(MapsActivity.this).addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    /*void parseStation(){
        try {
            togliglo e mettilo nel drawer!
            station = new DownloadURL().execute(station).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
       markers = putMarkersFromMapItems(station);
    }*/
public class DownloadURL extends AsyncTask<HashMap<Integer,Station>, Integer,HashMap<Integer,Station>> {

        public static final int TIMEOUT = 1000000000;
        int tab;

        @Override
        protected HashMap<Integer,Station> doInBackground(HashMap<Integer,Station>[] station) {

            /*------------------------PROVA QUESTO CODICE PER GLI ERRORI---------------------------*/
            /*OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://www.sviluppoeconomico.gov.it/images/exportCSV/anagrafica_impianti_attivi.csv")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                InputStream is = response.body().byteStream();
                Reader reader1 = new InputStreamReader(is);
                BufferedReader br1 = new BufferedReader(reader1);
                br1.readLine();
                CsvRowParser parser_benz = new CsvRowParser(br1, true, ";", null);
                List<CsvRowParser.Row> rows1 = parser_benz.parse();
                int i=0;
                int count = rows1.size();

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

                    publishProgress((int) (((i+1) / (float) count) * 100));
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            */
            /*-------------------------------------------------------------------------------------*/
            /*--------------------------------------PARSER LOCALE----------------------------------*/
            /*InputStream is = getResources().openRawResource(R.raw.anagrafica_impianti_attivi);
            Reader reader1 = new InputStreamReader(is);
            BufferedReader br1 = new BufferedReader(reader1);
            try {
                br1.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            CsvRowParser parser_benz = new CsvRowParser(br1, true, ";", null);
            List<CsvRowParser.Row> rows1 = null;


            try {
                rows1 = parser_benz.parse();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int count = rows1.size(),i = 0;

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

                publishProgress((int) (((i+1) / (float) count) * 100));
            }*/
            /*-------------------------------------------------------------------------------------*/


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
            c1.setReadTimeout(TIMEOUT);
            try {
                c1.connect();
                InputStream is1 = c1.getInputStream();
                Reader reader1 = new InputStreamReader(is1);
                BufferedReader br1 = new BufferedReader(reader1);
                /*elimina la prima riga della tabella*/
                br1.readLine();
                CsvRowParser parser_benz = new CsvRowParser(br1, true, ";", null);
                int i = 0;
               // mTextView.setText("Scaricamento dati...");
                List<CsvRowParser.Row> rows1 = parser_benz.parse();
                c1.disconnect();

                /*numero delle righe da parsare*/
                int count = rows1.size();
                tab = 1;
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
                 /*avanzamento progress bar*/
                 publishProgress((int) (((i+1) / (float) count) * 100));
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
               // mTextView.setText("Scaricamento dati...");
                List<CsvRowParser.Row> rows2 = parser_costi.parse();
                c2.disconnect();
                /*fa riconnettere c2 finchè non ottengo i dati, ma non funziona*/
                while(rows2.size() == 0) {
                    c2.connect();
                    rows2 = parser_costi.parse();
                    c2.disconnect();
                }
                tab = 2;
                count = rows2.size();
                i = 0;
                /*parsa la seconda tabella*/
                for(CsvRowParser.Row row : rows2) {
                    int id = 0;
                    try {
                        id = Integer.parseInt(row.get("idImpianto"));
                        double prezzo = Double.parseDouble(row.get("prezzo"));
                        //cerco la stazione con l'id corretto e la rimuovo dall'hashMap
                        Station temp = station[0].remove(id);
                        //controllo se l'id corrisponde ad una stazione esistente
                        if(temp != null) {
                            //aggiungo alla hashMAp dei carburanti il carburante e il prezzo appena trovati
                            temp.getCarburantiCosto().put(row.get("descCarburante"), prezzo);
                            //rimetto la stazione nella HashMap
                            station[0].put(id, temp);
                            Log.d(TAG, "onMapReady;" + i + Arrays.toString(row.getValues()));
                            i++;
                        }
                    } catch (RecoverableParseException e) {
                        e.printStackTrace();
                    }
                    publishProgress((int) (((i+1) / (float) count) * 100));
                }
            } catch (Exception  e) {
                e.printStackTrace();
            }
            return station[0];
        }
        //ProgressDialog asyncDialog = new ProgressDialog(MapsActivity.this);

        @Override
        protected void onPreExecute() {
            /*
            asyncDialog.setMessage("Please wait until parsing finished");
            asyncDialog.show();*/
            mTextView.setText("Inizio caricamento...");
        }
        //dopo aver parsato una riga
        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Display the progress on text view
            mTextView.setText("Parsing "+tab+"/2..."+ progress[0]+"%");
            // Update the progress bar
            mProgressBar.setProgress(progress[0]);
        }
        @Override
        protected void onPostExecute(HashMap<Integer,Station> stations) {
            if(stations.size()==0 )
                mTextView.setText("Connessione Fallita, riavviare l'app.");
            mTextView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
        }
    }


}
