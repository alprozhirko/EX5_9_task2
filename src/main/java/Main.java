import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {
    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();
        HttpGet request = new HttpGet(
                "https://api.nasa.gov/planetary/apod?api_key=UcNEwP1nDXToccaKjbKfMSc3qCQSzbdNEqpmgS7j");
        CloseableHttpResponse response = httpClient.execute(request);
        // чтение тела ответа
        String body = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
        AnswerNASA answerNASA = mapper.readValue(body, new TypeReference<>() {});
        System.out.println(answerNASA);

        String[] urlSplit = answerNASA.getUrl().split("/");
        String fileName = urlSplit[urlSplit.length - 1];

        HttpGet requestURL = new HttpGet(answerNASA.getUrl());
        CloseableHttpResponse responseURL = httpClient.execute(requestURL);

        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(responseURL.getEntity().getContent().readAllBytes());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}