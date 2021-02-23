package proyecto.transporte.udb;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class mant_unidades extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference unidades, zonas, motoristas, propietarios, itinerarios;
    private TextInputEditText placa;
    private AutoCompleteTextView tipo, propietario, motorista, zona, itinerario;
    private String[] TIPOS = {"Microbus","Bus"};
    private String[] PLACAS, ZONAS, PROPIETARIOS, MOTORISTAS, ITINERARIOS;
    private String[][] ramasPROPIETARIOS, ramasMOTORISTAS, ramasITINERARIOS;
    private int totPlacas, totPropietarios, totMotoristas, totZonas, totItinerarios;
    private int totRamasPropietarios, totRamasMotoristas, totRamasItinerarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mantunidades);

        database = FirebaseDatabase.getInstance();
        unidades = database.getReference("Unidades");
        zonas = database.getReference("Zonas");
        motoristas = database.getReference("Motoristas");
        propietarios = database.getReference("Propietarios");
        itinerarios = database.getReference("Itinerarios");

        placa = findViewById(R.id.txtinp_placa);
        tipo = findViewById(R.id.filled_tipoU);
        propietario = findViewById(R.id.dd_prop);
        motorista = findViewById(R.id.dd_moto);
        zona = findViewById(R.id.filled_zonas);
        itinerario = findViewById(R.id.itinerario);

        Toolbar toolbar = findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        cargarInformacion();
    }

    //Llenado de los dropdown con informacion de firebase
    private void cargarInformacion() {
        unidades.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot conteoPlacas : dataSnapshot.getChildren()) {
                    totPlacas++;
                }
                PLACAS = new String[totPlacas];
                for (DataSnapshot conteoPlacas : dataSnapshot.getChildren()) {
                    PLACAS[i] = conteoPlacas.getKey();
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        propietarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                int j = 0;
                for (DataSnapshot conteoPropietarios : dataSnapshot.getChildren()) {
                    for(DataSnapshot ramas : conteoPropietarios.getChildren()){
                        totRamasPropietarios++;
                    }
                    totPropietarios++;
                }
                totRamasPropietarios++;
                PROPIETARIOS = new String[totPropietarios];
                ramasPROPIETARIOS = new String[totPropietarios][totRamasPropietarios];
                for (DataSnapshot conteoPropietarios : dataSnapshot.getChildren()) {
                    PROPIETARIOS[i] = conteoPropietarios.child("Nombre").getValue(String.class);
                    ramasPROPIETARIOS [i][0] =  conteoPropietarios.getKey();
                    for(DataSnapshot ramas : conteoPropietarios.getChildren()){
                        ramasPROPIETARIOS [i][j+1] = ramas.getValue(String.class);
                        j++;
                    }
                    j = 0;
                    i++;
                }
                ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, PROPIETARIOS);
                propietario.setAdapter(adapter1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        motoristas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                int j = 0;
                for (DataSnapshot conteoMotoristas : dataSnapshot.getChildren()) {
                    for(DataSnapshot ramas : conteoMotoristas.getChildren()){
                        totRamasMotoristas++;
                    }
                    totMotoristas++;
                }
                totRamasMotoristas++;
                MOTORISTAS = new String[totMotoristas];
                ramasMOTORISTAS = new String[totMotoristas][totRamasMotoristas];
                for (DataSnapshot conteoMotoristas : dataSnapshot.getChildren()) {
                    MOTORISTAS[i] = conteoMotoristas.child("Nombre").getValue(String.class);
                    ramasMOTORISTAS[i][0] = conteoMotoristas.getKey();
                    for(DataSnapshot ramas : conteoMotoristas.getChildren()){
                        ramasMOTORISTAS[i][j+1] = ramas.getValue(String.class);
                        j++;
                    }
                    j = 0;
                    i++;
                }
                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, MOTORISTAS);
                motorista.setAdapter(adapter2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        zonas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot conteoZonas : dataSnapshot.getChildren()) {
                    totZonas++;
                }
                ZONAS = new String[totZonas];
                for (DataSnapshot conteoZonas : dataSnapshot.getChildren()) {
                    ZONAS[i] = conteoZonas.getValue(String.class);
                    i++;
                }
                ArrayAdapter<String> adapter3 = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, ZONAS);
                zona.setAdapter(adapter3);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        itinerarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                int j = 0;
                for (DataSnapshot conteoItinerarios : dataSnapshot.getChildren()) {
                    for(DataSnapshot ramas : conteoItinerarios.getChildren()){
                        totRamasItinerarios++;
                    }
                    totItinerarios++;
                }
                totRamasItinerarios++;
                ITINERARIOS = new String[totItinerarios];
                ramasITINERARIOS = new String[totItinerarios][totRamasItinerarios];
                for (DataSnapshot conteoItinerarios : dataSnapshot.getChildren()) {
                    ITINERARIOS[i] = conteoItinerarios.getKey();
                    ramasITINERARIOS [i][0] = conteoItinerarios.getKey();
                    for(DataSnapshot ramas : conteoItinerarios.getChildren()){
                        ramasITINERARIOS[i][j+1] = ramas.getValue(String.class);
                        j++;
                    }
                    j = 0;
                    i++;
                }
                ArrayAdapter<String> adapter4 = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, ITINERARIOS);
                itinerario.setAdapter(adapter4);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ArrayAdapter<String> adapter5 = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, TIPOS);
        tipo.setAdapter(adapter5);
    }

    public void buscarUnidad(View view){
        final String placaProv = placa.getText().toString().toUpperCase();

        if ( placaProv.equals("")){
            Toast toast = Toast.makeText(getApplicationContext(),"Error, no dejar campos vacios", Toast.LENGTH_SHORT);
            toast.show();
        }else{
            unidades.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    DataSnapshot unidad = dataSnapshot.child(placaProv);
                    tipo.setText(unidad.child("Tipo").getValue(String.class));
                    propietario.setText(unidad.child("Propietario").child("Nombre").getValue(String.class));
                    motorista.setText(unidad.child("Motorista").child("Nombre").getValue(String.class));
                    zona.setText(unidad.child("Zona").getValue(String.class));
                    itinerario.setText(unidad.child("Itinerario").child("ID").getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void agregarUnidad (View view){
        if (validacion(placa.getText().toString(),tipo.getText().toString(),propietario.getText().toString(),motorista.getText().toString(),zona.getText().toString(), itinerario.getText().toString())){
            Toast toast = Toast.makeText(getApplicationContext(),"Error, no dejar campos vacios", Toast.LENGTH_SHORT);
            toast.show();
        }else{
            String placaNueva = placa.getText().toString().toUpperCase();
            String tipoNuevo =  tipo.getText().toString();
            String propietarioNuevo = propietario.getText().toString();
            String motoristaNuevo = motorista.getText().toString();
            String zonaNueva = zona.getText().toString();
            String itinerarioNuevo = itinerario.getText().toString();
            if (unidadExistente(placaNueva)){
                Toast toast = Toast.makeText(getApplicationContext(),"Unidad existente", Toast.LENGTH_SHORT);
                toast.show();
            }else{
                //llenado de unidad nueva
                unidades.child(placaNueva).child("Estado").setValue("Fuera de viaje");
                unidades.child(placaNueva).child("Foto").setValue("cadena");

                unidades.child(placaNueva).child("Itinerario").child("ID").setValue(itinerarioNuevo);
                unidades.child(placaNueva).child("Itinerario").child("Viaje actual").setValue("nada");
                for(int i = 0; i < totItinerarios; i++){
                    if(itinerarioNuevo.equals(ramasITINERARIOS[i][0])){
                        unidades.child(placaNueva).child("Itinerario").child("Entrada").setValue(ramasITINERARIOS[i][1]);
                        unidades.child(placaNueva).child("Itinerario").child("Salida").setValue(ramasITINERARIOS[i][2]);
                        Log.d("--------ID",ramasITINERARIOS[i][0]);
                        Log.d("--------ENTRADAS",ramasITINERARIOS[i][1]);
                        //Log.d("--------SALIDAS",ramasITINERARIOS[i][2]);
                        break;
                    }
                }

                unidades.child(placaNueva).child("Motorista").child("Nombre").setValue(motoristaNuevo);
                unidades.child(placaNueva).child("Motorista").child("Foto").setValue("Enlace");
                for(int i = 0; i < totMotoristas; i++){
                    if(motoristaNuevo.equals(ramasMOTORISTAS[i][1])){
                        unidades.child(placaNueva).child("Motorista").child("ID").setValue(ramasMOTORISTAS[i][0]);
                        unidades.child(placaNueva).child("Motorista").child("Telefono").setValue(ramasMOTORISTAS[i][2]);
                        break;
                    }
                }

                unidades.child(placaNueva).child("Propietario").child("Nombre").setValue(propietarioNuevo);
                unidades.child(placaNueva).child("Propietario").child("Foto").setValue("Enlace");
                for(int i = 0; i < totPropietarios; i++){
                    if(propietarioNuevo.equals(ramasPROPIETARIOS[i][1])){
                        unidades.child(placaNueva).child("Propietario").child("ID").setValue(ramasPROPIETARIOS[i][0]);
                        unidades.child(placaNueva).child("Propietario").child("Telefono").setValue(ramasPROPIETARIOS[i][2]);
                        break;
                    }
                }

                unidades.child(placaNueva).child("Tipo").setValue(tipoNuevo);
                unidades.child(placaNueva).child("Ubicacion").child("Latitud").setValue("13.6");
                unidades.child(placaNueva).child("Ubicacion").child("Longitud").setValue("-89.3");
                unidades.child(placaNueva).child("Zona").setValue(zonaNueva);

                Toast toast = Toast.makeText(getApplicationContext(),"Unidad agregada", Toast.LENGTH_SHORT);
                toast.show();
                limpiarCampos();
            }
        }
    }

    public void eliminarUnidad(View view){
        String eliminarUnidad = placa.getText().toString();
        if(eliminarUnidad.equals("")){
            Toast toast = Toast.makeText(getApplicationContext(),"Error, no dejar placa vacia", Toast.LENGTH_SHORT);
            toast.show();
        }else{
            if(unidadExistente(eliminarUnidad)){
                unidades.child(eliminarUnidad).removeValue();
                Toast toast = Toast.makeText(getApplicationContext(),"Unidad eliminada", Toast.LENGTH_SHORT);
                toast.show();
                limpiarCampos();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"Unidad no existente", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void limpiarCampos(){
        placa.setText("");
        tipo.setText("");
        propietario.setText("");
        motorista.setText("");
        zona.setText("");
        itinerario.setText("");
    }

    private Boolean validacion(String pl, String tp, String pr, String mt, String zn, String it){
        if (pl.equals("") || tp.equals("") || pr.equals("") || mt.equals("") || zn.equals("") || it.equals("")){
            return true;
        }else {
            return false;
        }
    }

    private boolean unidadExistente(String valor){
        for (String placa : PLACAS) {
            if (valor.equals(placa)){
                return true;
            }
        }
        return false;
    }
}
