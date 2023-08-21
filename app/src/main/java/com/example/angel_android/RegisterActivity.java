package com.example.angel_android;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;
import android.util.Log;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.angel_android.databinding.ActivityRegisterBinding;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    // Enlace de la vista de registro
    private ActivityRegisterBinding binding;

    // Diálogo de progreso para el proceso de registro
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configurar el diálogo de progreso
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        // Manejar el clic en el botón de registro
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validar los datos ingresados
                validateData();
            }
        });
    }

    // Inicializamos variables
    private String email = "", password = "", username = "", confirmPassword = "";

    private void validateData() {
        // Obtener los datos ingresados
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();
        username = binding.usernameEt.getText().toString().trim();
        confirmPassword = binding.confirmPasswordEt.getText().toString().trim();

        // Validar los datos ingresados
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Username is required...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email is required...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password is required...", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match...", Toast.LENGTH_SHORT).show();
        } else {
            // Si todos los datos son válidos, comenzar a crear la cuenta
            createUserAccount();
        }
    }

    private void createUserAccount() {
        // Mostrar el diálogo de progreso
        progressDialog.setMessage("Creating account...");
        progressDialog.show();

        // Crear la solicitud POST para registrar al usuario
        RequestQueue queue = Volley.newRequestQueue(this, new CustomHurlStack());
        String url = Constants.API_URL + "/auth/register";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Procesar la respuesta del servidor
                        if (response.isEmpty()) {
                            // Si la respuesta está vacía, el registro falló
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();

                        } else {
                            // Si la respuesta no está vacía, el registro fue exitoso
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Account created..", Toast.LENGTH_SHORT).show();
                            // Iniciar la actividad de inicio de sesión
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar los errores de la solicitud
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    // Preparar el cuerpo de la solicitud
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("username", username);
                    jsonBody.put("email", email);
                    jsonBody.put("password", password);
                    jsonBody.put("confirmPassword", confirmPassword);
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
        };
        // Enviar la solicitud
        queue.add(postRequest);
    }
}