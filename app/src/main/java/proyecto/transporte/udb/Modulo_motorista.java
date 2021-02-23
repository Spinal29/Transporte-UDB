package proyecto.transporte.udb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import proyecto.transporte.udb.keepLogin.PreferenceUtils;

public class Modulo_motorista extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference referencia;
    private Button iniciar;
    private Button finalizar;
    private Button desperfectos;
    private TextView Nombre;
    private ImageView FotoMotorista;
    private TextView UnidadRuta;
    public  String Unidad, UserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Inicializacion de variables
        myDialog = new Dialog(this);
        UserName = PreferenceUtils.getUser(this);
        database = FirebaseDatabase.getInstance();
        referencia = database.getReference();
        setContentView(R.layout.activity_modulo_motorista);
        iniciar = (Button) findViewById(R.id.transmitir_ubi);
        finalizar = (Button) findViewById(R.id.dejar_transmitir);
        desperfectos = (Button) findViewById(R.id.desperfectos);
        Nombre = (TextView) findViewById(R.id.nomb_motorista);
        UnidadRuta = (TextView) findViewById(R.id.unidad_ruta);
        FotoMotorista = (ImageView) findViewById(R.id.img_motorista);

        //Métodos para asignar textos/imagen
        ObtUnidad(UserName);

        //Revisa si los permisos de la aplicacion han sido otorgados para activar los botones de transmitir
        //y finalizar transmision
        if (!runtime_permissions()){
            //enable_buttons();
        }

        ImageButton imageButton = (ImageButton) findViewById(R.id.closed_session);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateDialog();
            }
        });

        //Cambiando el título
        View tt = findViewById(R.id.toolbar_main);
        TextView title = (TextView) tt.findViewById(R.id.toolTitle);
        title.setText("Pantalla de motorista");

    }

    //Activa los botones
    public void enable_buttons() {
        IniciarTransmision(iniciar);
        FinalizarTransmision(finalizar);
        //ReportarDesperfecto(desperfectos);
    }

    //Metodo para el boton de despefectos mecanicos
    public void ReportarDesperfecto(View view) {
        Intent i = new Intent(this, GPS_Service.class);
        stopService(i);
        referencia.child("Unidades").child(Unidad).child("Estado").setValue("Desperfectos mecanicos");
    }

    //Metodo para el boton de finalizar transmision
    public void FinalizarTransmision(View view) {
        Intent i = new Intent(this, GPS_Service.class);
        stopService(i);
        referencia.child("Unidades").child(Unidad).child("Estado").setValue("Fuera de viaje");
    }

    //Metodo para el boton de iniciar transmision
    public void IniciarTransmision(View view) {
        Intent i = new Intent(this, GPS_Service.class);
        startService(i);
        referencia.child("Unidades").child(Unidad).child("Estado").setValue("Realizando viaje");
    }

    //Metodo para obtener Unidad:
    public void ObtUnidad(final String uID){

        referencia.child("Usuarios").child(uID).addListenerForSingleValueEvent(
                new ValueEventListener () {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Lo ENCUENTRA
                        Unidad = dataSnapshot.child("Unidad").getValue(String.class);
                        InfoCampos(Unidad);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Por si falla
                    }
                });
    }

    //Metodo para llenar info de Unidad:
    public void InfoCampos(final String uID){

        referencia.child("Unidades").child(Unidad).addListenerForSingleValueEvent(
                new ValueEventListener () {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Lo ENCUENTRA
                        Nombre.setText(dataSnapshot.child("Motorista").child("Nombre").getValue(String.class));
                        String tipo = dataSnapshot.child("Zona").getValue(String.class);
                        String UnidadrutaS = Unidad + " - " + tipo;
                        UnidadRuta.setText(UnidadrutaS);
                        Picasso.get().load(dataSnapshot.child("Motorista").child("Foto").getValue(String.class)).into(FotoMotorista);

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Por si falla
                    }
                });
    }

    //Este método revisa si los permisos necesarios para acceder a la ubicacion se encuentran aceptados, sino solicita acepatarlos
    private boolean runtime_permissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},100);
            return true;
        }
        return false;
    }

    //Este metodo se encarga del manejo de recibir los resultados de la solicitud de permisos de parte del usuario
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                //Si los permisos son aceptados se activan los botones
                enable_buttons();
            }else{
                //Si los permisos son denegados se vuelven a solicitar
                runtime_permissions();
            }
        }
    }


    //***Salir*** (cuando se cierra la sesion)

    public void onBackPressed()
    {
        CreateDialog();
    }

    private void CreateDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Desea cerrar la sesión?");
        builder.setCancelable(false);

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceUtils.Clear(Modulo_motorista.this);
                FinalizarTransmision(finalizar);
                Modulo_motorista.super.onBackPressed();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.create().show();
    }

    public void CrearDialogo(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    Dialog myDialog;
    public void ShowPopup(View v) {
        TextView Closetxt;
        myDialog.setContentView(R.layout.popupmotorista);
        Closetxt = (TextView) myDialog.findViewById(R.id.Closetxt);
        Closetxt.setText("X");
        Closetxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
}
