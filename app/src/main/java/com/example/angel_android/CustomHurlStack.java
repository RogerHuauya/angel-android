package com.example.angel_android;

import android.text.TextUtils;
import com.android.volley.toolbox.HurlStack;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;


/* Es una clase personalizada que maneja las conexiones HTTP,
   y particularmente gestiona las cookies a través de las solicitudes
   y respuestas, lo cual es útil para mantener la sesión del usuario.*/
public class CustomHurlStack extends HurlStack {
    private final CookieManager cookieManager; // Define una instancia de CookieManager para almacenar y manejar las cookies.

    public CustomHurlStack() {
        super();
        // Se crea una instancia de CookieManager para administrar las cookies HTTP a través de múltiples solicitudes y respuestas.
        cookieManager = new CookieManager();
        // Establece este CookieManager como el gestor de cookies predeterminado para todas las conexiones HTTP.
        CookieHandler.setDefault(cookieManager);
    }

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        // Se crea una conexión HTTP utilizando la superclase HurlStack.
        HttpURLConnection connection = super.createConnection(url);
        // Se habilita el uso de caché en la conexión para mejorar el rendimiento de las solicitudes repetidas.
        connection.setUseCaches(true);
        // Se establece la propiedad "Cookie" en el encabezado de la solicitud.
        // La propiedad "Cookie" incluye todas las cookies almacenadas en el CookieManager, son concatenadas con un ";".
        connection.setRequestProperty("Cookie", TextUtils.join(";", cookieManager.getCookieStore().getCookies()));
        return connection;
    }
}
