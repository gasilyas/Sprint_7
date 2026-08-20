package ru.yandex.practicum.scooter.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.scooter.api.client.CourierRestClient;
import ru.yandex.practicum.scooter.api.dto.Courier;
import ru.yandex.practicum.scooter.api.dto.CourierCredentials;
import ru.yandex.practicum.scooter.api.utils.CourierDataGenerator;

import java.util.Random;

import static org.hamcrest.Matchers.*;

public class LoginCourierApiTest {

    private Courier courier;
    private CourierRestClient courierRestClient;
    private Integer courierId;
    private String unexistentLogin;
    private String unexistentPassword;

    @BeforeEach
    public void courierLoginSetUp() {
        courierRestClient = new CourierRestClient();
        courier = CourierDataGenerator.generateCourier();
        courierRestClient.createCourier(courier);

        Random random = new Random();
        unexistentLogin = "dummy_login_" + random.nextInt(100000);
        unexistentPassword = "dummy_password_" + random.nextInt(10000);
    }

    @AfterEach
    public void cleanCourierLoginTestData() {
        if (courierId != null) {
            courierRestClient.deleteCourier(courierId);
        } else {
            Integer courierId = courierRestClient.getCourierIdAfterLogin(new CourierCredentials(courier.getLogin(), courier.getPassword()));
            if (courierId != null) {
                courierRestClient.deleteCourier(courierId);
            }
        }
    }

    @Test
    @DisplayName("Успешная авторизация (200) с полными данными возвращает id")
    public void courierLoginWithFullDataReturnsSuccessTest() {

        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        Response response = courierRestClient.courierLogin(credentials);

        response.then().statusCode(200).body("id", notNullValue());
        courierId = response.then().extract().path("id");
    }

    @Test
    @DisplayName("Ошибка авторизации (404) по несуществующим паролю и логину")
    public void courierLoginWithUnexistentDataReturnsErrorTest() {

        CourierCredentials credentials = new CourierCredentials(unexistentLogin, unexistentPassword);
        Response response = courierRestClient.courierLogin(credentials);
        response.then().statusCode(404).body("message", containsString("Учетная запись не найдена"));

    }

    @Test
    @DisplayName("Ошибка авторизации (404) с некорректным паролем")
    public void courierLoginWithIncorrectPasswordReturnsErrorTest() {

        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), unexistentPassword);
        Response response = courierRestClient.courierLogin(credentials);
        response.then().statusCode(404).body("message", containsString("Учетная запись не найдена"));

    }

    @Test
    @DisplayName("Ошибка авторизации (404) с некорректным логином")
    public void courierLoginWithIncorrectLoginReturnsErrorTest() {

        CourierCredentials credentials = new CourierCredentials(unexistentLogin, courier.getPassword());
        Response response = courierRestClient.courierLogin(credentials);
        response.then().statusCode(404).body("message", containsString("Учетная запись не найдена"));

    }

    //Не понял, почему, но тут получил 504 вместо 400
    @Test
    @DisplayName("Ошибка авторизации (400) без пароля с логином")
    public void courierLoginWithoutPasswordReturnsErrorTest() {

        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), null);
        Response response = courierRestClient.courierLogin(credentials);

        response.then().statusCode(400).body("message", containsString("Недостаточно данных для входа"));

    }

    @Test
    @DisplayName("Ошибка авторизации (400) без логина с паролем")
    public void courierLoginWithoutLoginReturnsErrorTest() {

        CourierCredentials credentials = new CourierCredentials(null, courier.getPassword());
        Response response = courierRestClient.courierLogin(credentials);
        response.then().statusCode(400).body("message", containsString("Недостаточно данных для входа"));

    }
}
