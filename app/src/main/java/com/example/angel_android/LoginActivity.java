package com.example.angel_android;


import com.android.volley.NetworkResponse;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.angel_android.databinding.ActivityLoginBinding;
import java.util.HashMap;
import java.util.Map;
import android.content.Intent;
import android.util.Log;
import org.json.JSONObject;
import android.content.SharedPreferences;

public class LoginActivity extends AppCompatActivity {

    // Permite acceder a los elementos de la interfaz de usuario definidos en el archivo XML correspondiente utilizando ViewBinding
    private ActivityLoginBinding binding;

    //Muestra un cuadro de diálogo de progreso durante el inicio de sesión.
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Se configura el cuadro de diálogo de progreso
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        // Se configura un click listener para el botón de inicio de sesión
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private String username = "", password = "";

    // Valida los datos ingresados por el usuario
    private void validateData() {

        // Obtenemos los valores
        username = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();

        // Toast indicando que el correo electrónico o la contraseña son inválidos
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Invalid email...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter password...", Toast.LENGTH_SHORT).show();
        } else {
            // Si ambos campos están llenos, se llama al método loginUser() para realizar el inicio de sesión
            loginUser();
        }
    }

    //  Realiza la solicitud de inicio de sesión al servidor
    private void loginUser() {
        // Cuadro de diálogo de progreso durante el proceso de inicio de sesión.
        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        // Cola de solicitudes y se establece la URL del servidor donde se enviarán los datos de inicio de sesión
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.API_URL + "/login";

        // Crear una solicitud de tipo POST con la URL, los oyentes de respuesta y error
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        // Si la respuesta está vacía, se muestra un mensaje Toast indicando que el inicio de sesión falló
                        if (response.isEmpty()) {
                            Toast.makeText(LoginActivity.this, "Login failed.", Toast.LENGTH_SHORT).show();
                        } else {

                            // Si la respuesta no está vacía, se inicia la actividad MainActivity2
                            Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                            startActivity(intent);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar los errores de la solicitud
                        progressDialog.dismiss();
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String errorResponse = new String(error.networkResponse.data);
                            Log.e("Error", errorResponse);
                        }
                        Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    // Preparar el cuerpo de la solicitud como un objeto JSON
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("username", username);
                    jsonBody.put("password", password);
                    return jsonBody.toString().getBytes("utf-8");
                } catch (Exception e) {
                    Log.e("getBody", "Failed to convert JSON to bytes", e);
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Preparar las cabeceras de la solicitud
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // Captura la cookie del encabezado "Set-Cookie" de la respuesta de la red.
                String cookie = response.headers.get("Set-Cookie");
                // Accede a las preferencias compartidas de la aplicación llamadas "MyPref" en modo privado (0).
                SharedPreferences sharedPreferences = getSharedPreferences("MyPref", 0);
                // Obtiene el editor de las preferencias compartidas para poder modificarlas.
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // Agrega la cookie a las preferencias compartidas con la clave "cookie".
                editor.putString("cookie", cookie);
                // Aplica (guarda) los cambios hechos al editor de las preferencias compartidas.
                editor.apply();

                // Llama al método original parseNetworkResponse de la superclase y devuelve su resultado.
                return super.parseNetworkResponse(response);
            }
        };
        // Enviar la solicitud
        queue.add(postRequest);
    }
}