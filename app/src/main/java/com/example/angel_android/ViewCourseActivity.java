package com.example.angel_android;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
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
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.angel_android.databinding.ActivityMain2Binding;
import java.util.Map;
import java.util.HashMap;

public class ViewCourseActivity extends AppCompatActivity {
    private String courseId;
    private List<Student> studentList = new ArrayList<>();  // Lista para almacenar los libros obtenidos de la API

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_course);

        // Retrieve the course ID from the intent
        Intent intent = getIntent();
        courseId = intent.getStringExtra("courseId"); // -1 is a default value if not found
        getStudents();
    }

    private void getStudents(){

        RequestQueue queue = Volley.newRequestQueue(this, new CustomHurlStack());
        String url = Constants.API_URL+"/subject/" + courseId + "/students" ;
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            // Limpia lista de cursos
                            studentList.clear();

                            // Ciclo que recorre cada objeto JSON en la respuesta y crea un nuevo libro con los datos del objeto
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject courseObj = response.getJSONObject(i);

                                String id = courseObj.getString("id");
                                String name = courseObj.getString("name");
                                String enrollment_id = courseObj.getString("enrollment_id");

                                Student student = new Student(id, name, enrollment_id);
                                studentList.add(student);
                            }

                            // Limpia el LinearLayout para actualizarlo con los nuevos libros obtenidos
                            LinearLayout linearLayout = findViewById(R.id.linearLayout);
                            //linearLayout.removeAllViews();

                            // Ciclo que recorre cada libro en la lista y añade un nuevo TextView al LinearLayout para cada libro
                            for (Student student : studentList) {
                                String name = student.getName();

                                LinearLayout linearLayoutItem = new LinearLayout(ViewCourseActivity.this);
                                linearLayoutItem.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));
                                linearLayoutItem.setOrientation(LinearLayout.HORIZONTAL);

                                Button button = new Button(ViewCourseActivity.this);
                                button.setLayoutParams(new LinearLayout.LayoutParams(
                                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                                button.setText(name);
                                button.setTextSize(17);
                                button.setTypeface(null, Typeface.BOLD);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Launch the activity for the selected course
                                        Intent intent = new Intent(ViewCourseActivity.this, GradesActivity.class);
                                        intent.putExtra("enrollmentId", student.getEnrollmentId());
                                        intent.putExtra("courseId", courseId);

                                        startActivity(intent);
                                    }
                                });
                                linearLayoutItem.addView(button);
                                linearLayout.addView(linearLayoutItem);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ViewCourseActivity.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ViewCourseActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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

}
