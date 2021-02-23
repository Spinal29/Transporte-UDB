package proyecto.transporte.udb;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText; 

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import proyecto.transporte.udb.keepLogin.PreferenceUtils;


public class Login extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference referencia;
    public String username;
    public String password;
    public String tipo;
    private EditText Usuario;
    private EditText Password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Usuario = findViewById(R.id.carnet);
        Password = findViewById(R.id.pass);
        database = FirebaseDatabase.getInstance();
        referencia = database.getReference();

        //Prueba mantener sesion
        if (PreferenceUtils.getUser(this) != null ) /*Revisar que esté almacenado*/
        {//Identify(PreferenceUtils.getUser(this), PreferenceUtils.getPassword(this)); //<---- Método inicial
            tipo = PreferenceUtils.getType(this);
            //Redireccionar según tipo
            switch (tipo){
                case "Usuario":
                    ActividadSig();
                    break;
                case "Motorista":
                    ActividadMotorista();
                    break;
                case "Administrador":
                    ActividadAdministrador();
                default:
                    break;
            }
        }
    }

    //Metodo que identifica a que pantalla sera dirigido el usuario, segun su tipo
    public void Identify(final String uID, final String uPASS){
        referencia.child("Usuarios").child(uID).addListenerForSingleValueEvent(
                new ValueEventListener () {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Lo ENCUENTRA
                        String TruePassword = dataSnapshot.child("Pass").getValue(String.class);
                        tipo = dataSnapshot.child("Tipo").getValue(String.class);
                        if (uPASS.equals(TruePassword))
                        {
                            //Almacenar datos de sesion
                            PreferenceUtils.saveUser(uID, Login.this);
                            PreferenceUtils.savePassword(TruePassword, Login.this);
                            PreferenceUtils.saveType(tipo, Login.this); //<----- Probar si no sirve

                            //Revisar tipo
                            switch (tipo){
                                case "Usuario":
                                    ActividadSig();
                                    break;
                                case "Motorista":
                                    ActividadMotorista();
                                    break;
                                case "Administrador":
                                    ActividadAdministrador();
                                default:
                                        break;
                            }

                            //Limpiar campos
                            Usuario.setText("");
                            Password.setText("");
                            Password.clearFocus();
                        }
                        else
                        {
                            CrearDialogo("Error", "Usuario o password incorrecto");

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Por si falla
                        CrearDialogo("Error", "Usuario o password incorrecto");
                    }
                });
    }

    //En respuesta al botón
    public void nextActivity(View view) {
      username = Usuario.getText().toString();
      password = Password.getText().toString();

      Identify(username, password);
    }

    //Crea un dialogo al iniciar sesion
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


    //Metodo que dirigie a la pantalla donde se muestran las unidades
    public void ActividadSig()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //Metodo que dirige a la pantalla del motorista
    public void ActividadMotorista(){
        Intent intent = new Intent(this, Modulo_motorista.class);
        intent.putExtra("USUARIO",username);
        sendBroadcast(intent);
        startActivity(intent);
    }

    //Metodo que dirige a la pantalla del administrador
    public void ActividadAdministrador(){
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }
}