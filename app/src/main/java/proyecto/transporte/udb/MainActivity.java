package proyecto.transporte.udb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import proyecto.transporte.udb.keepLogin.PreferenceUtils;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference unidades;
    private String unidadT;
    private RecyclerView recyclerView;
    private ArrayList<itemModel> arrayList;
    private int icons[] = {R.drawable.ic_bus};
    private String ruta, tipo, estado;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDialog = new Dialog(this);

        database = FirebaseDatabase.getInstance();
        unidades = database.getReference("Unidades");
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_Main);
        arrayList = new ArrayList<>();

        //Metodo que genera las tarjetas con la informacion de las unidades
        //Genera tantas tarjetas como unidades hay en el firebase registradas
        addRoute();

        ImageButton imageButton = (ImageButton) findViewById(R.id.closed_session);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateDialog();
            }
        });

        //Título de la pantalla
        View tt = findViewById(R.id.toolbar_main);
        TextView title = (TextView) tt.findViewById(R.id.toolTitle);
        title.setText("Pantalla principal");
    }

    //Metodo para el boton que muestra la informacion de cada unidad
    public void showInfo(View view) {
        Intent intent = new Intent(view.getContext(), show_info.class);
        startActivity(intent);
    }


    public void addRoute() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        //Se obtiene la informacion del firebase de cada unidad y se es cargada en el recycler view
        unidades.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot placas : dataSnapshot.getChildren()) {
                    unidadT = placas.getKey();
                    ruta = dataSnapshot.child(unidadT).child("Zona").getValue(String.class) + " - " + unidadT;
                    tipo = dataSnapshot.child(unidadT).child("Tipo").getValue(String.class);
                    estado = dataSnapshot.child(unidadT).child("Estado").getValue(String.class);

                    //No entiendo por que solo la info permanece dentro de este método
                    itemModel itemModel = new itemModel();
                    itemModel.setImage(icons[0]);
                    itemModel.setRouteN(ruta);
                    itemModel.setType(tipo);
                    itemModel.setStatus(estado);
                    arrayList.add(itemModel);
                }
                routeAdapter adapter = new routeAdapter(getApplicationContext(), arrayList);

                //Se manda la lista de unidades que se utilizara en el adapter para llenar el recycler view
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //***Salir*** (Cerrar sesion)

    public void onBackPressed() {
        CreateDialog();
    }

    private void CreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Desea cerrar la sesión?");
        builder.setCancelable(false);

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceUtils.Clear(MainActivity.this);
                MainActivity.super.onBackPressed();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.create().show();
    }

    Dialog myDialog;
       public void ShowPopup(View v) {
            TextView Closetxt;
            myDialog.setContentView(R.layout.custompopup);
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
