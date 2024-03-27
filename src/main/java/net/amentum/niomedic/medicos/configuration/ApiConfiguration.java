package net.amentum.niomedic.medicos.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ApiConfiguration {
    @Value("${url}")
    private String urlProperti;
    private ObjectMapper mapp = new ObjectMapper();
    private Date fechaExpiraTocken;
    String token = "";

    public Map<String, Object> obtenerToken() throws Exception {
        try {
            log.info("obtenerToken() - Solicitando token de acceso a: {} ", urlProperti);
            String params = "client_id=auth.testing&client_secret=auth.testing&username=sysAdmin&password=5ae23bbbb73b35ef9f4a624e656b8240641dc48e005b55482def92901253389f&grant_type=password";
            URL url = new URL(urlProperti + "auth/oauth/token");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            OutputStream os = conn.getOutputStream();
            os.write(params.getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println(conn.getResponseMessage() + "  " + conn.getResponseCode());
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder response = new StringBuilder();
                String currentLine;
                while ((currentLine = br.readLine()) != null) {
                    response.append(currentLine);
                }
                br.close();
                response.toString();
                log.info("obtenerToken() - Ocurrio un error al obtenero el token - error: {}", response.toString());
                throw new Exception("No fue posible obtener token");
            } else {
                log.info("obtenerToken() - El token se generó exitosamente");
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String currentLine;
                while ((currentLine = br.readLine()) != null) {
                    response.append(currentLine);
                }
                br.close();
                Map<String, Object> JsonResponse = mapp.readValue(response.toString(), Map.class);
                Map<String, Object> infoToken = new HashMap<String, Object>();
                Integer expiraEn = (Integer) JsonResponse.get("expires_in");
                infoToken.put("expires_in", expiraEn);
                infoToken.put("access_token", (String) JsonResponse.get("access_token"));
                infoToken.put("hier_token", (String) JsonResponse.get("hier_token"));
                final Long one_second_in_millis = 1000L;
                Calendar date = Calendar.getInstance();
                long t = date.getTimeInMillis();
                Date afterAdding = new Date(t + (expiraEn * one_second_in_millis));
                fechaExpiraTocken = afterAdding;
                return infoToken;
            }
        } catch (Exception e) {
            log.error("Ocurrio un error inesperado al obtener el token para los reportes - error: ", e);
            throw new Exception("Ocurrio un error al obtener el token: ", e);
        }
    }

    public Long getEventoId() throws Exception {
        try {
            log.info("Obtener id: {} ", urlProperti);
            URL url = new URL(urlProperti + "eventos/getValue");
            Integer contador = 0;
            Boolean ciclo = true;
            do {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("GET");

                Map<String, Object> infoTocken = obtenerToken();
                token = "bearer " + (String) infoTocken.get("access_token");
                conn.setRequestProperty("Authorization", token);
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    StringBuilder response = new StringBuilder();
                    String currentLine;
                    while ((currentLine = br.readLine()) != null) {
                        response.append(currentLine);
                    }
                    br.close();
                    response.toString();
                    contador++;
                    ciclo = true;
                    if (contador > 3) {
                        log.info("getPacieteByid() - Ocurrio un error al obtener el id del evento - error: {}", response);
                        throw new Exception("Ocurrio un error al obtener el id del evento");
                    }
                } else {
                    log.info("getPacieteByid() - Se obtuvieron los detalles exitosamente");
                    ciclo = false;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String currentLine;
                    while ((currentLine = br.readLine()) != null) {
                        response.append(currentLine);
                    }
                    br.close();
                    return Long.parseLong(response.toString());
                }
            } while (ciclo);
            return null;
        } catch (Exception e) {
            log.error("Ocurrio un error inesperado al obtener los detalles del paciente para los reportes - error: ", e);
            throw new Exception("Ocurrio un error al obtener el id del evento", e);
        }
    }

}

