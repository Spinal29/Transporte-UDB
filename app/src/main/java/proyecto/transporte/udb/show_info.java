package proyecto.transporte.udb;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class show_info extends AppCompatActivity {

    int rotationAngle = 0;
    private ChipGroup chipGroup1, chipGroup2;
    private FirebaseDatabase database;
    private DatabaseReference unidades;
    private String [] provicional;
    private String entradasProvicional, salidasProvicional;
    public static String placa;
    public Calendar hora = Calendar.getInstance();
    int hora_actual;
    int minuto_actual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);

        database = FirebaseDatabase.getInstance();
        unidades = database.getReference("Unidades");

        //Referencia de objetos del layout
        final TextView ruta = (TextView) findViewById(R.id.txtRuta);
        final ImageView imagenUnidad = (ImageView) findViewById(R.id.imagenUnidad);
        final TextView txtNombreMotorista = (TextView) findViewById(R.id.txtNombreMotorista);
        final TextView txtTelefonoMotorista = (TextView) findViewById(R.id.txtTelefonoMotorista);

        //obteniendo placa
        Bundle extras = getIntent().getExtras();
        provicional = extras.getString("RUTA").split(" - ");
        placa = provicional[1];

        ruta.setText(extras.getString("RUTA"));

        //Flecha para regresar
        Toolbar toolbar = findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        final View expandable = findViewById(R.id.expandable);
        final ImageButton b = findViewById(R.id.expand_button);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expandable.getHeight() == 0){
                    expand(expandable, 500,200);
                    //Función para que gire
                    rotationAngle = rotationAngle == 0 ? 180 : 0;  //toggle
                    b.animate().rotation(rotationAngle).setDuration(500).start();
                }else if(expandable.getHeight() > 0){
                    collapse(expandable, 500, 0);
                    //Función para que gire
                    rotationAngle = rotationAngle == 0 ? 180 : 0;  //toggle
                    b.animate().rotation(rotationAngle).setDuration(500).start();
                }
            }
        });

        //Llenado de los elementos del layout
        unidades.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txtNombreMotorista.setText(dataSnapshot.child(placa).child("Motorista").child("Nombre").getValue(String.class));
                txtTelefonoMotorista.setText(dataSnapshot.child(placa).child("Motorista").child("Telefono").getValue(String.class));
                Picasso.get().load(dataSnapshot.child(placa).child("Foto").getValue(String.class)).into(imagenUnidad);
                entradasProvicional = dataSnapshot.child(placa).child("Itinerario").child("Entrada").getValue(String.class);
                salidasProvicional = dataSnapshot.child(placa).child("Itinerario").child("Salida").getValue(String.class);
                llenadoItinerario(entradasProvicional,salidasProvicional);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //Creación de los chips (Llenado de las rutas)
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void llenadoItinerario(String entradasProv, String salidasProv){
        LayoutInflater inflater = LayoutInflater.from(show_info.this);
        chipGroup1 = (ChipGroup) findViewById(R.id.cgIn);
        chipGroup2 = (ChipGroup) findViewById(R.id.cgOut);
        String[] entradas = entradasProv.split("/");
        String[] salidas = salidasProv.split("/");
        for (String entrada : entradas){
            Chip chip = (Chip)inflater.inflate(R.layout.added_chip, null, false);
            chip.setText(entrada);

            if (ComprobarHora(entrada, 10, 30))
            {
                //Aquí pone como le querés cambiar el color :$
                chip.setChipStrokeColor(getColorStateList(R.color.yellow));
            }

            chipGroup1.addView(chip);
        }

        for (String salida : salidas){
            Chip chip = (Chip)inflater.inflate(R.layout.added_chip, null, false);
            chip.setText(salida);

            if (ComprobarHora(salida, 10,30))
            {
                //Aquí pone como le querés cambiar el color :$
                chip.setChipStrokeColor(getColorStateList(R.color.yellow));
                chip.setChipStrokeWidth(4.0f);
            }

            chipGroup2.addView(chip);
        }
    }

    //Animación para expandir
    public static void expand(final View v, int duration, int targetHeight) {

        int prevHeight  = v.getHeight();

        v.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    //Animación para retraer
    public static void collapse(final View v, int duration, int targetHeight) {
        int prevHeight  = v.getHeight();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public  void showMapa(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);

    }

    //**************************MANEJO DE HORAS DE AQUI EN ADELANTE****************88
    public boolean ComprobarHora(String hora_og, int s_mins, int r_mins)
    {
        String[] hora_str;
        String hora_temp;
        int hora_int;
        int minuto_int;
        boolean rangoMin = false;
        boolean rangoMax = false;

        //*** Comparación de horas
        hora_actual = hora.get(Calendar.HOUR_OF_DAY);
        minuto_actual = hora.get(Calendar.MINUTE);

        //*** Comprobar Min
        hora_temp = hora_og;
        hora_temp = restarMins_n(hora_temp, r_mins);

        hora_str = hora_temp.split(":");
        //Llenado hora
        hora_int = Integer.parseInt(hora_str[0]); //**

        //Llenado minuto
        minuto_int = Integer.parseInt(hora_str[1]); //**

        //Comprobación final
        if(hora_actual >= hora_int)
        {
            rangoMin = true;
            if(hora_actual == hora_int && minuto_actual < minuto_int) rangoMin = false;
        }


        //*** Comprobar Max
        hora_temp = hora_og;
        hora_temp = sumarMins_n(hora_temp, s_mins);

        hora_str = hora_temp.split(":");
        //Llenado hora
        hora_int = Integer.parseInt(hora_str[0]); //**

        //Llenado minuto
        minuto_int = Integer.parseInt(hora_str[1]); //**

        //Comprobación final
        if(hora_actual <= hora_int)
        {
            rangoMax = true;
            if(hora_actual == hora_int && minuto_actual > minuto_int) rangoMax = false;
        }


        //Retornar true si está en el rango
        return (rangoMin && rangoMax);
    }

    public String sumarMins_n(String hora_og, int nmins)
    {
        //Llenado hora
        String[] hora_partes = hora_og.split(":");
        int hora = Integer.parseInt(hora_partes[0]); //**

        //Llenado minuto
        String[] minuto_partes = hora_partes[1].split(" ");
        int min = Integer.parseInt(minuto_partes[0]); //**

        //Comprobación am o pm
        if (minuto_partes[1].equals("p.m."))
        {
            hora = hora + 12;
        }

        //Suma
        min = min + nmins;
        while (min > 60)
        {
            hora = hora + 1;
            min = min - 60;
        }
        if(min == 60)
        {
            hora = hora + 1;
            min = 0;
        }

        //Hora construida
        if (min < 10) hora_og = Integer.toString(hora) + ":0" + Integer.toString(min);
        else hora_og = Integer.toString(hora) + ":" + Integer.toString(min);
        return hora_og;
    }

    public String restarMins_n(String hora_og, int nmins)
    {
        //Llenado hora
        String[] hora_partes = hora_og.split(":");
        int hora = Integer.parseInt(hora_partes[0]); //**

        //Llenado minuto
        String[] minuto_partes = hora_partes[1].split(" ");
        int min = Integer.parseInt(minuto_partes[0]); //**

        //Comprobación am o pm
        if (minuto_partes[1].equals("p.m."))
        {
            hora = hora + 12;
        }

        //Resta
        min = min - nmins;
        while (min < 0)
        {
            hora = hora - 1;
            min = min + 60;
        }
        if(min == 60)
        {
            hora = hora + 1;
            min = 0;
        }

        //Hora construida
        if (min < 10) hora_og = Integer.toString(hora) + ":0" + Integer.toString(min);
        else hora_og = Integer.toString(hora) + ":" + Integer.toString(min);
        return hora_og;
    }
}