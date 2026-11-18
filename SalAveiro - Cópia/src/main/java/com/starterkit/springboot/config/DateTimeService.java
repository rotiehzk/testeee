package com.starterkit.springboot.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DateTimeService {

    private static final Logger log = LoggerFactory.getLogger(DateTimeService.class);

    public LocalDateTime getCurrentDateTime() {
        try {
            URL url = new URL("https://worldtimeapi.org/api/timezone/Europe/Lisbon");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                String json = response.toString();
                int datetimeIndex = json.indexOf("\"datetime\":\"");
                if (datetimeIndex != -1) {
                    int start = datetimeIndex + 12;
                    int end = json.indexOf("\"", start);
                    String datetimeStr = json.substring(start, end);
                    datetimeStr = datetimeStr.substring(0, 19);
                    return LocalDateTime.parse(datetimeStr);
                }
            }
        } catch (Exception e) {
            log.warn("Erro ao obter hora do servidor: {}", e.getMessage());
        }
        return LocalDateTime.now();
    }
}