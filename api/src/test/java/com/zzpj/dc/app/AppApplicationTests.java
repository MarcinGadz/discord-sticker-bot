package com.zzpj.dc.app;

import com.zzpj.dc.app.dao.ImageDAO;
import com.zzpj.dc.app.exceptions.*;
import com.zzpj.dc.app.model.Image;
import com.zzpj.dc.app.service.ImageService;
import com.zzpj.dc.app.util.EnvironmentUtils;
import com.zzpj.dc.app.util.TimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;


@RunWith(SpringRunner.class)
public class AppApplicationTests {

    @TestConfiguration
    static class AppApplicationTestsContextConfiguration {

        @Bean
        public ImageService imageService() {
            return new ImageService(new EnvironmentUtils(
                    1,
                    3,
                    2,
                    "13cf17e6-0929-475b-bad0-1b7ab1bdca80"
            ));
        }

    }

    @Autowired
    private ImageService imageService;

    @MockBean
    private TimeUtils timeUtils;

    @MockBean
    private ImageDAO imageDao;

    private static final String TEST_OWNER = "test_owner";
    private static final String FILENAME = "some_filename";
    private static final long HOUR_IN_MILLIS = 60 * 60 * 1000;
    private static final long DAY_IN_MILLIS = 24 * HOUR_IN_MILLIS;

    private List<MockMultipartFile> INIT_DATA;

    private Image getTestImage(String filename, String owner, long timestamp) {
        try {
            return new Image(
                    filename,
                    "https://localhost:1234/" + filename,
                    INIT_DATA.get(0).getBytes(),
                    owner,
                    timestamp
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Image getTestImage(String filename, long timestamp) {
        return getTestImage(filename, TEST_OWNER, timestamp);
    }

    @Before
    public void init() throws IOException, ImageContentEmptyException {
        INIT_DATA = List.of(
                new MockMultipartFile[]{
                        new MockMultipartFile(
                                FILENAME,
                                FILENAME,
                                MediaType.IMAGE_PNG_VALUE,
                                new ClassPathResource("image.png" ).getInputStream().readAllBytes()
                        ),
                        new MockMultipartFile(
                                FILENAME,
                                FILENAME,
                                MediaType.TEXT_PLAIN_VALUE,
                                new ClassPathResource("randomfile.txt" ).getInputStream().readAllBytes()
                        ),
                        new MockMultipartFile(
                                FILENAME,
                                FILENAME,
                                MediaType.TEXT_PLAIN_VALUE,
                                (byte[]) null
                        )
                }
        );
        Mockito.doNothing().when(imageDao).addImage(Mockito.any());
        Mockito.doReturn(System.currentTimeMillis()).when(timeUtils).getCurrentMilis();
        Mockito.doReturn(LocalDate.now()).when(timeUtils).getCurrentDay();
    }


    @Test
    public void whenUploadingCorrectImage_proceedWithoutError() throws ImageContentEmptyException, IOException, ImageAlreadyExistsException, UserLimitExceededException, WrongFileTypeException, ImageDoesntExistException {
        Mockito.doThrow(new ImageDoesntExistException())
                        .when(imageDao).getImageByName(FILENAME, TEST_OWNER);
        imageService.addImage(INIT_DATA.get(0), FILENAME, TEST_OWNER);
    }

    @Test
    public void whenUploadingIncorrectImage_throwWrongFileTypeException() {
        assertThrows(
                WrongFileTypeException.class,
                () -> imageService.addImage(INIT_DATA.get(1), FILENAME, TEST_OWNER)
        );
    }

    @Test
    public void whenUploadingNullImage_throwWrongFileTypeException() {
        assertThrows(
                WrongFileTypeException.class,
                () -> imageService.addImage(INIT_DATA.get(2), FILENAME, TEST_OWNER)
        );
    }

    @Test
    public void whenWithinAccountRateLimits_proceedWithoutError()
            throws IOException, ImageContentEmptyException, ImageAlreadyExistsException, UserLimitExceededException, WrongFileTypeException, ImageDoesntExistException {
        String first_correct_limits = "first_correct_limits";
        String second_correct_limits = "second_correct_limits";
        Mockito.doReturn(
                Arrays.asList(
                        getTestImage(
                                first_correct_limits,
                                timeUtils.getCurrentMilis() - HOUR_IN_MILLIS
                        ),
                        getTestImage(
                                second_correct_limits,
                                timeUtils.getCurrentMilis() - DAY_IN_MILLIS
                        )
                )
        ).when(imageDao).getImagesForOwner(TEST_OWNER);

        Mockito.doThrow(new ImageDoesntExistException()).when(
                imageDao).getImageByName(FILENAME, TEST_OWNER);

        imageService.addImage(INIT_DATA.get(0), FILENAME, TEST_OWNER);
    }

    @Test
    public void whenTotalAccountHourlyLimitExceeded_throwUserLimitExceededException() {
        Mockito.doReturn(
                List.of(
                        getTestImage(
                                "hourly_exceed",
                                timeUtils.getCurrentMilis()
                        )
                )
        ).when(imageDao).getImagesForOwner(TEST_OWNER);

        assertThrows(
                UserLimitExceededException.class,
                () -> imageService.addImage(INIT_DATA.get(0), FILENAME, TEST_OWNER)
        );
    }

    @Test
    public void whenTotalAccountDailyLimitExceeded_throwUserLimitExceededException() throws ImageDoesntExistException {
        Mockito.doReturn(5*HOUR_IN_MILLIS).when(timeUtils).getCurrentMilis();
        Mockito.doReturn(LocalDate.ofEpochDay(0)).when(timeUtils).getCurrentDay();
        Mockito.doReturn(
                Arrays.asList(
                        getTestImage(
                                "first_daily_exceed",
                                timeUtils.getCurrentMilis() - HOUR_IN_MILLIS
                        ),
                        getTestImage(
                                "second_daily_exceed",
                                timeUtils.getCurrentMilis() - HOUR_IN_MILLIS
                        )
                )
        ).when(imageDao).getImagesForOwner(TEST_OWNER);
        Mockito.doThrow(new ImageDoesntExistException())
                        .when(imageDao).getImageByName(FILENAME, TEST_OWNER);
        assertThrows(
                UserLimitExceededException.class,
                () -> imageService.addImage(INIT_DATA.get(0), FILENAME, TEST_OWNER)
        );
    }

    @Test
    public void whenTotalAccountUploadLimitExceeded_throwUserLimitExceededException() {
        Mockito.doReturn(
                Arrays.asList(
                        getTestImage(
                                "first_total_exceed",
                                timeUtils.getCurrentMilis() - 3 * DAY_IN_MILLIS
                        ),
                        getTestImage(
                                "second_total_exceed",
                                timeUtils.getCurrentMilis() - 2 * DAY_IN_MILLIS
                        ),
                        getTestImage(
                                "third_total_exceed",
                                timeUtils.getCurrentMilis() - DAY_IN_MILLIS
                        )
                )
        ).when(imageDao).getImagesForOwner(TEST_OWNER);

        assertThrows(
                UserLimitExceededException.class,
                () -> imageService.addImage(INIT_DATA.get(0), FILENAME, TEST_OWNER)
        );
    }

    @Test
    public void whenGettingImageByName_returnImagesOfGivenName() throws ImageDoesntExistException {
        String owner = "some_owner";
        String name = "name";
        Image testImage = getTestImage(
                name,
                owner,
                timeUtils.getCurrentMilis() - 3 * DAY_IN_MILLIS
        );
        Mockito.doReturn(testImage).when(imageDao).getImageByName(name, owner);

        assertEquals(imageService.getImageByName(name, owner), testImage);
    }

    @Test
    public void whenGettingImagesForOwner_returnImagesOfGivenOwner() {
        String owner = "some_owner";
        List<Image> testImages = Arrays.asList(
                getTestImage(
                        "first_some_owner_image",
                        owner,
                        timeUtils.getCurrentMilis() - HOUR_IN_MILLIS
                ),
                getTestImage(
                        "second_some_owner_image",
                        owner,
                        timeUtils.getCurrentMilis() - 2 * DAY_IN_MILLIS - HOUR_IN_MILLIS
                )
        );
        Mockito.doReturn(testImages).when(imageDao).getImagesForOwner(owner, 1000, "");

        assertEquals(testImages, imageService.getForOwner(owner, 1000, ""));
    }
}
