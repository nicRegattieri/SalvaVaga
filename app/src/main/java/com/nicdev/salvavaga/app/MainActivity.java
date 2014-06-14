package com.nicdev.salvavaga.app;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.File;


public class MainActivity extends ActionBarActivity implements LocationListener {
    private String localArquivoFoto;
    private static final int TIRA_FOTO = 123;
    public Intent intent;
    MarkerDataSource data;
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        setupWidgets();
        verifiqueGps();


    }


    private void verifiqueGps() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if (status != ConnectionResult.SUCCESS) {
            // Google Play Services esta disponivel
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else {

            // Checa gps e network atraves de LocationManager
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                // Mostra um alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("O serviço de Localização não está ativo");
                builder.setMessage("Por Favor ative os serviços de Localização e o GPS depois volte ao aplicativo");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Mostra opcoes de configuracao de localizacao
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
                Dialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }

        }
    }

    public void setupWidgets() {

        final CircularProgressButton btnWithIcons2 = (CircularProgressButton) findViewById(R.id.btnWithIcons2);
        btnWithIcons2.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (btnWithIcons2.getProgress() == 0) {
                    simulateSuccessProgress(btnWithIcons2);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            Log.i("run", "ENTROU NO RUN");

                            gps = new GPSTracker(MainActivity.this);

                            // check if GPS enabled
                            if (gps.canGetLocation()) {

                                double latitude = gps.getLatitude();
                                double longitude = gps.getLongitude();
                                String latLongString = latitude + " " + longitude;
                                addMarker(latLongString);

                                // \n is for new line
                                Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                            } else {
                                // can't get location
                                // GPS or Network is not enabled
                                // Ask user to enable GPS/network in settings
                                gps.showSettingsAlert();
                            }


                            try {
                                data.deleteAll();
                            } catch (Exception e) {

                            }

                            localArquivoFoto = Environment.getExternalStorageDirectory() + "/" + System.currentTimeMillis() + ".jpg";
                            File arquivo = new File(localArquivoFoto);
                            Uri localFoto = Uri.fromFile(arquivo);
                            Intent irParaCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            irParaCamera.putExtra(MediaStore.EXTRA_OUTPUT, localFoto);
                            startActivityForResult(irParaCamera, TIRA_FOTO);
                            btnWithIcons2.setTag(0);
                        }
                    }, 7000);

                } else {
                    btnWithIcons2.setProgress(0);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void getTheLocation() {
//        LocationManager locationManager;
//        String svcName = Context.LOCATION_SERVICE;
//        locationManager = (LocationManager) getSystemService(svcName);
//        String provider = LocationManager.GPS_PROVIDER;
//
//        l = locationManager.getLastKnownLocation(provider);
//
//        double lat = l.getLatitude();
//        double lng = l.getLongitude();
//        String latLongString = lat + " " + lng;
//
//
//        Toast.makeText(this, latLongString, Toast.LENGTH_SHORT).show();
//
//
//        addMarker(latLongString);
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TIRA_FOTO) {


            if (resultCode == Activity.RESULT_OK) {
                intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);

            } else {
                this.localArquivoFoto = null;

            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    private void simulateSuccessProgress(final CircularProgressButton button) {

        ValueAnimator widthAnimation = ValueAnimator.ofInt(1, 100);
        widthAnimation.setDuration(5000);
        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                button.setProgress(value);
            }
        });
        widthAnimation.start();
    }

    private void simulateErrorProgress(final CircularProgressButton button) {
        ValueAnimator widthAnimation = ValueAnimator.ofInt(1, 99);
        widthAnimation.setDuration(1500);
        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                button.setProgress(value);
                if (value == 99) {
                    button.setProgress(-1);
                }
            }
        });
        widthAnimation.start();
    }

    public void addMarker(String loc) {
        Log.d("LatLong", loc);
        MarkerDataSource data = new MarkerDataSource(getApplicationContext());

        try {
            data.open();

        } catch (Exception e) {
            Log.i("hello", "hello");
        }

        data.addMarker(new MyMarkerObj("Vaga Salva", "navegue com o MAPS", loc));


        data.close();
        Toast.makeText(this, loc, Toast.LENGTH_LONG).show();
    }

}
