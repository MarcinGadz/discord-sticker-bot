package com.zzpj.dc.app;

import com.zzpj.dc.app.model.Image;
import com.zzpj.dc.app.util.EnvironmentUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AppIntegrationTests {

    private final String keyHeader = "x-api-key";
    private final String firstOwner = "johndoe";
    private final String otherOwner = "jankowalski";

    private final Image[] INIT_DATA = {
            new Image("first", "https://localhost:1234/first", new byte[]{1, 2, 3}, firstOwner, 1654284602228L),
            new Image("sec", "https://localhost:1234/sec", new byte[]{9, 9, 3}, otherOwner, 1654284578628L),
            new Image("third", "https://localhost:1234/third", new byte[]{5, 5, 2}, firstOwner, 1654284987234L)};

    private final ParameterizedTypeReference<List<Image>> typeReference = new ParameterizedTypeReference<>() {
    };

    private String basePath;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EnvironmentUtils environmentUtils;

    @PostConstruct
    private void setup() {
        basePath = "http://localhost:" + port + "/image/";
    }

    @Test
    public void addGetPhotoSuccessful() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(keyHeader, environmentUtils.getApiKey());
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        String filename = "image.png";
        String stickerName = "image";
        var file = new ClassPathResource(filename);
        var map = new LinkedMultiValueMap<String, Object>();
        map.add("image", file);

        HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(map, httpHeaders);
        ResponseEntity<Object> response = restTemplate.exchange(basePath + firstOwner + "/" + stickerName,
                HttpMethod.POST,
                entity,
                Object.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());


        httpHeaders.setContentType(null);

        ResponseEntity<Image> res = restTemplate.exchange(basePath + firstOwner + "/" + stickerName,
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                Image.class);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals(res.getBody().getOwner(), firstOwner);
        assertEquals(res.getBody().getName(), stickerName);
    }

    @Test
    public void addPhotoNonPNG() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(keyHeader, environmentUtils.getApiKey());
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        String stickerName = "image";
        var file = new ClassPathResource("randomfile.txt");
        var map = new LinkedMultiValueMap<String, Object>();
        map.add("image", file);

        HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(map, httpHeaders);
        ResponseEntity<Object> response = restTemplate.exchange(basePath + firstOwner + "/" + stickerName,
                HttpMethod.POST,
                entity,
                Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Map.of("message", "File is not valid PNG"), response.getBody());
    }

    @Test
    public void addPhotoExceededHourlyLimit() {

    }

    @Test
    public void addPhotoExceededDailyLimit() {

    }

    @Test
    public void addToLargeFIle() {

    }

    @Test
    public void getWithoutApiKey() {
        ResponseEntity<List<Image>> response = restTemplate.exchange(basePath + firstOwner,
                HttpMethod.GET,
                null,
                typeReference);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void getWithWrongApiKey() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(keyHeader, "wrong-value");

        ResponseEntity<List<Image>> response = restTemplate.exchange(basePath + firstOwner,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                typeReference);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void getAllForOwnerSuccessful() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(keyHeader, environmentUtils.getApiKey());

        ResponseEntity<List<Image>> response = restTemplate.exchange(basePath + firstOwner,
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                typeReference);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Image> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.size() >= 2);
        assertEquals(INIT_DATA[0], body.get(0));
        assertNull(body.get(0).getContent());
        assertEquals(INIT_DATA[2], body.get(1));
        assertNull(body.get(1).getContent());
    }

    @Test
    public void getAllForNonExistingOwner() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(keyHeader, environmentUtils.getApiKey());

        ResponseEntity<List<Image>> response = restTemplate.exchange(basePath + "i-do-not-exists",
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                typeReference);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Image> body = response.getBody();
        assertNotNull(body);
        assertEquals(0, body.size());
    }
}
