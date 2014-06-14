package com.nicdev.salvavaga.app;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements View.OnClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    Context context = this;
    MarkerDataSource data;
    SupportMapFragment fm;
    Button navega;
    static Double destLat;
    static Double destLng;
    protected static final String CATEGORIA = "salva";


    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.d(CATEGORIA, getLocalClassName() + ".onCreate() chamado: " + icicle);
        setContentView(R.layout.activity_maps);
        navega = (Button) findViewById(R.id.bNavigation);
        navega.setOnClickListener(this);
        navega.setTag(1);
        navega.setText("Navegue com o Maps");

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        // Getting Google Play availability status
        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else {

            // Get Location Manager and check for GPS & Network location services
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                // Build the alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("O serviço de Localização não está ativo");
                builder.setMessage("Pro Favor ative os serviços de Localização e o GPS e volte ao aplicativo");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Show location settings when the user acknowledges the alert dialog
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
                Dialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
            // Google Play Services are available

        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(CATEGORIA, getLocalClassName() + ".onResume() chamado: ");
        fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        setupMap(location);

        data = new MarkerDataSource(context);
        try {
            data.open();

        } catch (Exception e) {
            Log.i("hello", "hello");
        }

        List<MyMarkerObj> m = data.getMyMarkers();
        for (int i = 0; i < m.size(); i++) {
            String[] slatlng = m.get(i).getPosition().split(" ");
            destLat = Double.valueOf(slatlng[0]);
            destLng = Double.valueOf(slatlng[1]);
            LatLng lat = new LatLng(destLat, destLng);


            mMap.addMarker(new MarkerOptions()
                            .title(m.get(i).getTitle())
                            .snippet(m.get(i).getSnippet())
                            .position(lat)
            );

        }


    }

    private void setupMap(Location location) {
        mMap = fm.getMap();
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(18).bearing(0).tilt(90).build();
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);


        //map.moveCamera(update);
        mMap.animateCamera(update, 3000, new GoogleMap.CancelableCallback() {
            @Override
            public void onCancel() {
                Log.i("Script", "CancelableCallback.onCancel()");
            }

            @Override
            public void onFinish() {
                Log.i("Script", "CancelableCallback.onFinish()");
            }
        });

    }

    @Override
    public void onClick(View v) {
        String url = "http://maps.google.com/maps?f=d&daddr=" + destLat + "," + destLng + "&dirflg=w";
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }
}



