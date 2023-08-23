package com.example.angel_android;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;

import android.widget.Button;
import android.graphics.Typeface;
import android.content.SharedPreferences;

import android.app.ProgressDialog;
import java.util.ArrayList;
import java.util.List;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Toast;
import com.android.volley.AuthFailureError;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.angel_android.databinding.ActivityMain2Binding;
import java.util.Map;
import java.util.HashMap;

public class GradesActivity extends AppCompatActivity {
    private String enrollmentId, courseId;

    private List<Exams> gradesList = new ArrayList<>();  // Lista para almacenar los libros obtenidos de la API
    private LinearLayout linearLayout;
    private Button saveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades);

        // Retrieve the course ID from the intent
        Intent intent = getIntent();
        enrollmentId = intent.getStringExtra("enrollmentId"); // -1 is a default value if not found
        courseId = intent.getStringExtra("courseId");
        getExams();
        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGrades();
                Intent intent = new Intent(GradesActivity.this, SummaryGradesActivity.class);
                intent.putExtra("enrollmentId", enrollmentId);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });
    }
    private void getExams(){

        RequestQueue queue = Volley.newRequestQueue(this, new CustomHurlStack());
        String url = Constants.API_URL+"/exams/" +  enrollmentId;
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            // Limpia lista de cursos
                            gradesList.clear();

                            // Ciclo que recorre cada objeto JSON en la respuesta y crea un nuevo libro con los datos del objeto
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject courseObj = response.getJSONObject(i);

                                String id = courseObj.getString("id");
                                String name = courseObj.getString("name");

                                Exams exams = new Exams(id, name);
                                gradesList.add(exams);
                            }

                            // Limpia el LinearLayout para actualizarlo con los nuevos libros obtenidos
                            linearLayout = findViewById(R.id.linearLayout);
                            //linearLayout.removeAllViews();

                            // Ciclo que recorre cada libro en la lista y añade un nuevo TextView al LinearLayout para cada libro
                            for (Exams exams : gradesList) {
                                String name = exams.getName();

                                LinearLayout linearLayoutItem = new LinearLayout(GradesActivity.this);
                                linearLayoutItem.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));
                                linearLayoutItem.setOrientation(LinearLayout.HORIZONTAL);

                                TextView textview1 = new TextView(GradesActivity.this);
                                textview1.setLayoutParams(new LinearLayout.LayoutParams(
                                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                                textview1.setText(name);
                                textview1.setTextSize(17);
                                textview1.setTypeface(null, Typeface.BOLD);


                                EditText editText = new EditText(GradesActivity.this);
                                editText.setLayoutParams(new LinearLayout.LayoutParams(
                                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                                editText.setHint("Enter grade here");  // Set an appropriate hint
                                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                                editText.setFilters(new InputFilter[]
                                        {
                                                new InputFilterMinMax(0, 20),
                                        });

                                linearLayoutItem.addView(textview1);
                                linearLayoutItem.addView(editText);
                                linearLayout.addView(linearLayoutItem);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(GradesActivity.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GradesActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            // Sirve para personalizar los headers de una solicitud HTTP que se va a enviar a algún servidor
            public Map<String, String> getHeaders() {
                // Crea un nuevo mapa HashMap vacío. Este mapa tiene claves y valores String. Se va a usar para almacenar las cabeceras.
                Map<String, String> headers = new HashMap<>();
                // Accede a un almacenamiento de preferencias compartidas llamado "MyPref" que se va a usar para almacenar datos
                SharedPreferences sharedPreferences = getSharedPreferences("MyPref", 0);
                // Obtiene una cadena llamada "cookie" de las preferencias compartidas
                String cookie = sharedPreferences.getString("cookie", "");
                // Añade una cabecera a la solicitud
                headers.put("Cookie", cookie);
                // Añade una cabecera a la solicitud, la solicitud va a contener datos JSON y que esos datos están codificados en UTF-8
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        queue.add(getRequest);
    }


    private void saveGrades() {
        // Crear una cola de solicitudes y establecer la URL del servidor
        RequestQueue queue = Volley.newRequestQueue(this, new CustomHurlStack());  // Usar CustomHurlStack para gestionar las cookies
        String url = Constants.API_URL + "/grades/" + enrollmentId + "/" ;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Respuesta exitosa del servidor
                        Toast.makeText(GradesActivity.this, "Notas registradas exitosamente", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Respuesta de error del servidor
                        Toast.makeText(GradesActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    // Convertir los datos del libro a JSON
                    JSONArray gradesArray = new JSONArray();
                    Log.e("debug", "raaaa");
                    Log.e("raa", String.valueOf(linearLayout.getChildCount()));
                    for (int i = 1; i < linearLayout.getChildCount(); i++) {
                        LinearLayout linearLayoutItem = (LinearLayout) linearLayout.getChildAt(i);
                        EditText editText = (EditText) linearLayoutItem.getChildAt(1);

                        String grade = editText.getText().toString();

                        try {
                            JSONObject gradeObject = new JSONObject();
                            gradeObject.put("exam_id", gradesList.get(i-1).getId());
                            gradeObject.put("exam_grade", grade);
                            gradesArray.put(gradeObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    String gradesJson = gradesArray.toString();
                    Log.e("debug", gradesJson);
                    return gradesJson.toString().getBytes("utf-8");
                } catch (Exception e) {
                    Log.e("getBody", "Failed to convert JSON to bytes", e);
                    return null;
                }
            }

            @Override
            // Sirve para personalizar los headers de una solicitud HTTP que se va a enviar a algún servidor
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Crea un nuevo mapa HashMap vacío. Este mapa tiene claves y valores String. Se va a usar para almacenar las cabeceras.
                Map<String, String> headers = new HashMap<>();
                // Accede a un almacenamiento de preferencias compartidas llamado "MyPref" que se va a usar para almacenar datos
                SharedPreferences sharedPreferences = getSharedPreferences("MyPref", 0);
                // Obtiene una cadena llamada "cookie" de las preferencias compartidas
                String cookie = sharedPreferences.getString("cookie", "");
                // Añade una cabecera a la solicitud
                headers.put("Cookie", cookie);
                // Añade una cabecera a la solicitud, la solicitud va a contener datos JSON y que esos datos están codificados en UTF-8
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        // Agregar la solicitud a la cola de solicitudes
        queue.add(postRequest);
    }
}