package com.example.angel_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LastActivity extends AppCompatActivity {
    private String enrollmentId="", courseId, average="";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last);

        // Retrieve the course ID from the intent
        Intent intent = getIntent();
        enrollmentId = intent.getStringExtra("enrollmentId");
        courseId = intent.getStringExtra("courseId");
        averageGrades();

        Button buttonMain = findViewById(R.id.buttonMain);

        Button buttonCourse = findViewById(R.id.buttonCourse);

        Button buttonAverage = findViewById(R.id.buttonAverage);

        buttonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LastActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });

        buttonCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LastActivity.this, ViewCourseActivity.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });

        buttonAverage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LastActivity.this, OverallAverage.class);
                intent.putExtra("courseId", courseId);
                intent.putExtra("enrollmentId", enrollmentId);
                startActivity(intent);
            }
        });

    }

    private void averageGrades(){
        RequestQueue queue = Volley.newRequestQueue(this, new CustomHurlStack());
        String url = Constants.API_URL+"/grades/average/" +  enrollmentId;
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            average = response.getString("average");
                            String studentName = response.getString("student_name");
                            String courseName = response.getString("course_name");
                            TextView textView1 = findViewById(R.id.averageField);
                            TextView textView2 = findViewById(R.id.paragraphTextView);

                            textView1.setText(average);
                            String messageParagraph = "El promedio del alumno " + studentName +" en el curso " + courseName + " es:";
                            textView2.setText(messageParagraph);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LastActivity.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LastActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
