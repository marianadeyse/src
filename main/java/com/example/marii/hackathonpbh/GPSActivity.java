package com.example.marii.hackathonpbh;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GPSActivity extends Activity {

    private GPSFacade facade = null;
    private TextView textViewLocations = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        facade = GPSFacade.getInstance(this);
        textViewLocations = (TextView) findViewById(R.id.textViewLocations);
    }

    public void eventGPS(View v) {
        if(facade.isCapturingGPSStarted()) {
            Toast.makeText(this, "Serviço de captura já foi solicitado!", Toast.LENGTH_SHORT).show();
        } else if(facade.isGPSEnable()) {
            facade.startCaptureLocation();
        } else {
            buildAlertMessageNoGps();
        }
    }

    public void eventListLocation(View v) {
        List<Location> locations = facade.getListLocation();
        if(locations.isEmpty()) {
            textViewLocations.setText("Sem localizações!");
        } else {
            StringBuilder builder = new  StringBuilder();
            for(Location location : locations) {
                String text = getLocationInString(location);
                builder.append(text);
            }
            textViewLocations.setText(builder.toString());
        }

    }

    public void eventCheckLocation(View v) {
        Location location = facade.getLastLocation();
        if(location != null) {
            String locationStr = getLocationInString(location);
            Log.i("INFO", locationStr);
            showInMap(location);
        }

    }

    private void showInMap(Location location) {
        String url = "http://maps.google.com/maps?daddr=" + location.getLatitude() + "," + location.getLongitude();

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    private String getLocationInString(Location location) {
        StringBuilder builder = new StringBuilder();

        builder.append(" Precisão: " );
        builder.append(location.getAccuracy());

        builder.append(" Provider: " );
        builder.append(location.getProvider());

        builder.append("  Latitude: " );
        builder.append(location.getLatitude());

        builder.append("  Longitude: " );
        builder.append(location.getLongitude());

        builder.append("  Tempo: " );

        Date date = new Date(location.getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String dateString = DateFormat.getDateInstance(DateFormat.SHORT).format(date);

        builder.append(dateString + " - " + hours + ":" + minute + ":" + second);
        builder.append("\n");

        return builder.toString();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        facade.stopCaptureLocation();
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Seu GPS esta desabilitado, deseja habilita-lo?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
