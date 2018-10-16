package mx.edu.ittepic.sergiobenigno.tpdm_u3_ejercicio1;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText nocontrol, nombre, domicilio,telefono;
    Button insertar;
    DatabaseReference basedatos;
    ListView lista;
    List<Map> alumnosLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nocontrol = findViewById(R.id.nocontrol);
        nombre = findViewById(R.id.nombre);
        domicilio = findViewById(R.id.domicilio);
        telefono = findViewById(R.id.telefono);
        lista = findViewById(R.id.lista);

        insertar = findViewById(R.id.insertar);
        basedatos = FirebaseDatabase.getInstance().getReference();

        basedatos.child("alumno").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()<=0){
                    Toast.makeText(MainActivity.this, "NO HAY DATOS A MOSTRAR", Toast.LENGTH_LONG).show();
                    return;
                }
                alumnosLocal = new ArrayList<>();

                Log.e("DATASNAP",dataSnapshot.getValue().toString());
                for(final DataSnapshot otro:dataSnapshot.getChildren()){
                    basedatos.child("alumno").child(otro.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Alumno alu = dataSnapshot.getValue(Alumno.class);
                            if(alu!=null){
                                Map<String, Object> xx = new HashMap<>();

                                xx.put("nocontrol",otro.getKey());
                                xx.put("nombre",alu.getNombre());
                                xx.put("domicilio",alu.getDomicilio());
                                xx.put("telefonocel",alu.getTelefonocel());
                                alumnosLocal.add(xx);
                                cargarlista();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> data = new HashMap<>();

                data.put("nombre", nombre.getText().toString());
                data.put("domicilio", domicilio.getText().toString());
                data.put("telefonocel", telefono.getText().toString());

                basedatos.child("alumno").child(nocontrol.getText().toString()).setValue(data);
                Toast.makeText(MainActivity.this, "Se insertó", Toast.LENGTH_LONG).show();
                nocontrol.setText("");
                nombre.setText("");
                domicilio.setText("");
                telefono.setText("");
            }
        });

    }

    private void cargarlista(){
        String[] vector = new String[alumnosLocal.size()];

        for(int i=0; i<vector.length; i++){
            Map<String,Object> ww = new HashMap<>();

            ww = alumnosLocal.get(i);
            vector[i] = ww.get("nombre").toString();
        }

        ArrayAdapter<String> arr = new ArrayAdapter<String>
                  (this,android.R.layout.simple_list_item_1,vector);
        lista.setAdapter(arr);
    }
}


