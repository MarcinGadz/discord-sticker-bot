package com.zzpj.dc.app;

import com.zzpj.dc.app.model.Image;
import com.zzpj.dc.app.service.ImageService;
import com.zzpj.dc.app.util.EnvironmentUtils;
import com.zzpj.dc.app.util.TimeUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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
    private final HttpHeaders headers = new HttpHeaders();
    private String basePath;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EnvironmentUtils environmentUtils;

    @Autowired
    private ImageService imageService;

    @PostConstruct
    private void setup() {
        basePath = "http://localhost:" + port + "/image/";
        headers.set(keyHeader, environmentUtils.getApiKey());
    }

    @Test
    public void addGetRemovePhotoSuccessful() {
        // add
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        String filename = "image.png";
        String stickerName = "image";
        var file = new ClassPathResource(filename);
        var map = new LinkedMultiValueMap<String, Object>();
        map.add("image", file);

        HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<Object> response = restTemplate.exchange(basePath + firstOwner + "/" + stickerName,
                HttpMethod.POST,
                entity,
                Object.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // get
        headers.setContentType(null);

        ResponseEntity<Image> res = restTemplate.exchange(basePath + firstOwner + "/" + stickerName,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Image.class);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals(res.getBody().getOwner(), firstOwner);
        assertEquals(res.getBody().getName(), stickerName);

        // remove
        response = restTemplate.exchange(basePath + firstOwner + "/" + stickerName,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Object.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // verify removing
        res = restTemplate.exchange(basePath + firstOwner + "/" + stickerName,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Image.class);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    public void addPhotoNonPNG() {
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        String stickerName = "image";
        var file = new ClassPathResource("randomfile.txt");
        var map = new LinkedMultiValueMap<String, Object>();
        map.add("image", file);

        HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<Object> response = restTemplate.exchange(basePath + "nonPngOwner" + "/" + stickerName,
                HttpMethod.POST,
                entity,
                Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Map.of("message", "File is not valid PNG"), response.getBody());

        headers.setContentType(null);
    }

    @Test
    public void addPhotoExceededHourlyLimit() {
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        int limit = environmentUtils.getUserAddPerHourLimit();
        String filename = "image.png";
        String randomowner = "randomowner";

        var file = new ClassPathResource(filename);
        var map = new LinkedMultiValueMap<String, Object>();
        map.add("image", file);
        HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);

        for (int i = 0; i < limit; i++) {
            String stickerName = MessageFormat.format("image-{0}", i);
            ResponseEntity<Object> response = restTemplate.exchange(basePath + randomowner + "/" + stickerName,
                    HttpMethod.POST,
                    entity,
                    Object.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        String stickerName = "image-n";
        ResponseEntity<Object> response = restTemplate.exchange(basePath + randomowner + "/" + stickerName,
                HttpMethod.POST,
                entity,
                Object.class);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());


        headers.setContentType(null);
    }

    @Test
    public void addPhotoExceededDailyLimit() {
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        int hourlyLimit = environmentUtils.getUserAddPerHourLimit();
        int dailyLimit = environmentUtils.getGetUserAddPerDayLimit();
        String filename = "image.png";
        String yetAnotherOwner = "yetanother";

        var file = new ClassPathResource(filename);
        var map = new LinkedMultiValueMap<String, Object>();
        map.add("image", file);
        HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);

        TimeUtils mock = Mockito.mock(TimeUtils.class);
        imageService.setTimeUtils(mock);
        when(mock.getCurrentDay()).thenReturn(LocalDate.ofEpochDay(0));

        int i;
        for (i = 0; i < hourlyLimit; i++) {
            when(mock.getCurrentMilis()).thenReturn(200L * i);
            String stickerName = MessageFormat.format("image-{0}", i);
            ResponseEntity<Object> response = restTemplate.exchange(basePath + yetAnotherOwner + "/" + stickerName,
                    HttpMethod.POST,
                    entity,
                    Object.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        for (; i < dailyLimit; i++) {
            when(mock.getCurrentMilis()).thenReturn(200L * i + 7200*1000);
            String stickerName = MessageFormat.format("image-{0}", i);

            ResponseEntity<Object> response = restTemplate.exchange(basePath + yetAnotherOwner + "/" + stickerName,
                    HttpMethod.POST,
                    entity,
                    Object.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        String stickerName = "image-n";
        ResponseEntity<Object> response = restTemplate.exchange(basePath + yetAnotherOwner + "/" + stickerName,
                HttpMethod.POST,
                entity,
                Object.class);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());

        headers.setContentType(null);
    }

    @Test
    public void addToLargeFIle() {
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        String stickerName = "notok";
        var file = new ClassPathResource("toolarge");
        var map = new LinkedMultiValueMap<String, Object>();
        map.add("image", file);

        HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<Object> response = restTemplate.exchange(basePath + firstOwner + "/" + stickerName,
                HttpMethod.POST,
                entity,
                Object.class);
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());

        headers.setContentType(null);
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
        ResponseEntity<List<Image>> response = restTemplate.exchange(basePath + firstOwner,
                HttpMethod.GET,
                new HttpEntity<>(headers),
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

        ResponseEntity<List<Image>> response = restTemplate.exchange(basePath + "i-do-not-exists",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                typeReference);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Image> body = response.getBody();
        assertNotNull(body);
        assertEquals(0, body.size());
    }

    @Test
    public void getNonExistingImage() {

        ResponseEntity<Image> response = restTemplate.exchange(basePath + "i-do-not-exists/same",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Image.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void addDuplicate() {
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        String duplicateOwner = "duplicate";
        String filename = "image.png";
        String stickerName = "image";
        var file = new ClassPathResource(filename);
        var map = new LinkedMultiValueMap<String, Object>();
        map.add("image", file);

        HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<Object> response = restTemplate.exchange(basePath + duplicateOwner + "/" + stickerName,
                HttpMethod.POST,
                entity,
                Object.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = restTemplate.exchange(basePath + duplicateOwner + "/" + stickerName,
                HttpMethod.POST,
                entity,
                Object.class);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void addEmptyFile() {
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        String stickerName = "image";
        var map = new LinkedMultiValueMap<String, Object>();
        map.add("image", null);

        HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<Object> response = restTemplate.exchange(basePath + firstOwner + "/" + stickerName,
                HttpMethod.POST,
                entity,
                Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    public void addEmptyContentFile() {
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        String filename = "empty";
        String stickerName = "image";
        var map = new LinkedMultiValueMap<String, Object>();
        map.add("image", filename);

        HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<Object> response = restTemplate.exchange(basePath + firstOwner + "/" + stickerName,
                HttpMethod.POST,
                entity,
                Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
