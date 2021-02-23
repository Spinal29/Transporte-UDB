package proyecto.transporte.udb;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import proyecto.transporte.udb.keepLogin.PreferenceUtils;

public class GPS_Service extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private FirebaseDatabase database;
    private DatabaseReference unidades;
    private DatabaseReference usuarios;
    private LocationListener listener;
    private LocationManager locationManager;
    private String motorista;
    private String placa;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        database = FirebaseDatabase.getInstance();
        unidades = database.getReference("Unidades");
        usuarios = database.getReference("Usuarios");

        //Se toma el usuario del motorista de la sesion
        motorista = PreferenceUtils.getUser(this);

        //Se obtiene la placa de la unidad de transporte a partir del motorista
        usuarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                placa = dataSnapshot.child(motorista).child("Unidad").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Se actualiza la ubicacion de la unidad de transporte cada 3 segundos
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //Envio de la nueva ubicacion
                unidades.child(placa).child("Ubicacion").child("Latitud").setValue(location.getLatitude());
                unidades.child(placa).child("Ubicacion").child("Longitud").setValue(location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        //Aqui se especifica cada cuanto tiempo y cada cuanta distancia se actualiza la ubicacion
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000,0,listener);
    }



    //Metodo que se utiliza para dejar de transmitir la ubicacion
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            locationManager.removeUpdates(listener);
        }
    }

}
