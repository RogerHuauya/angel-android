package com.example.angel_android;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import java.util.logging.Logger;
public class OverallAverage extends AppCompatActivity{
    String enrollmentId, courseId;
    private List<Exams> gradesList = new ArrayList<>();  // Lista para almacenar los libros obtenidos de la API
    private LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_average);

        // Retrieve the course ID from the intent
        Intent intent = getIntent();
        enrollmentId = intent.getStringExtra("enrollmentId");
        courseId = intent.getStringExtra("courseId");
        getStudentAverages();

        Button buttonMain = findViewById(R.id.buttonMain);

        Button buttonCourse = findViewById(R.id.buttonCourse);

        buttonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OverallAverage.this, MainActivity2.class);
                startActivity(intent);
            }
        });

        buttonCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OverallAverage.this, ViewCourseActivity.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });
    }


    private void getStudentAverages(){
        RequestQueue queue = Volley.newRequestQueue(this, new CustomHurlStack());
        String url = Constants.API_URL+"/grades/average/all/" +  enrollmentId;
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

                                String id = courseObj.getString("enrollment_id");
                                String name = courseObj.getString("student_name");
                                String grade = courseObj.getString("student_average");
                                Exams exams = new Exams(id, name, grade);
                                gradesList.add(exams);
                            }

                            // Limpia el LinearLayout para actualizarlo con los nuevos libros obtenidos
                            linearLayout = findViewById(R.id.linearLayout);
                            //linearLayout.removeAllViews();

                            // Ciclo que recorre cada libro en la lista y añade un nuevo TextView al LinearLayout para cada libro
                            for (Exams exams : gradesList) {
                                String name = exams.getName();
                                String grade = exams.getExamGrade();

                                LinearLayout linearLayoutItem = new LinearLayout(OverallAverage.this);
                                linearLayoutItem.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));
                                linearLayoutItem.setOrientation(LinearLayout.HORIZONTAL);

                                TextView textview1 = new TextView(OverallAverage.this);
                                textview1.setLayoutParams(new LinearLayout.LayoutParams(
                                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                                textview1.setText(name);
                                textview1.setTextSize(17);
                                textview1.setTypeface(null, Typeface.BOLD);


                                TextView textview2 = new TextView(OverallAverage.this);
                                textview2.setLayoutParams(new LinearLayout.LayoutParams(
                                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                                textview2.setText(grade);
                                textview2.setTextSize(17);
                                textview2.setTypeface(null, Typeface.BOLD);


                                linearLayoutItem.addView(textview1);
                                linearLayoutItem.addView(textview2);
                                linearLayout.addView(linearLayoutItem);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(OverallAverage.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(OverallAverage.this, error.toString(), Toast.LENGTH_SHORT).show();
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
