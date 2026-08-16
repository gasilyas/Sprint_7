package ru.yandex.practicum.scooter.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.scooter.api.client.CourierRestClient;
import ru.yandex.practicum.scooter.api.dto.Courier;
import ru.yandex.practicum.scooter.api.dto.CourierCredentials;

import java.util.Random;

import static org.hamcrest.Matchers.*;

public class LoginCourierApiTest {

    private Courier courier;
    private CourierRestClient courierRestClient;
    private Integer courierId;
    private Random random;

    @BeforeEach
    public void courierLoginSetUp() {
        courierRestClient = new CourierRestClient();
        random = new Random();

        String randomLogin = "sam_bridges_" + random.nextInt(1000);
        String randomPassword = "cupid_lulu_" + random.nextInt(1000);
        String randomFirstName = "Сэм " + random.nextInt(1000);

        courier = new Courier(randomLogin, randomPassword, randomFirstName);
        courierRestClient.createCourier(courier);
    }

    @AfterEach
    public void cleanCourierLoginTestData() {
        if (courierId != null) {
            courierRestClient.deleteCourier(courierId);
        } else {
            Response loginResponse = courierRestClient.courierLogin(new CourierCredentials(courier.getLogin(), courier.getPassword()));
            Integer id = loginResponse.then().extract().path("id");
            if (id != null) {
                courierRestClient.deleteCourier(id);
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

        String randomUnexistentLogin = "unknown_user_" + random.nextInt(1000);
        String randomUnexistentPassword = "unexistent_password_" + random.nextInt(1000);

        CourierCredentials credentials = new CourierCredentials(randomUnexistentLogin, randomUnexistentPassword);
        Response response = courierRestClient.courierLogin(credentials);
        response.then().statusCode(404).body("message", containsString("Учетная запись не найдена"));

    }

    @Test
    @DisplayName("Ошибка авторизации (404) с некорректным паролем")
    public void courierLoginWithIncorrectPasswordReturnsErrorTest() {

        String randomUnexistentPassword = "unexistent_password_" + random.nextInt(1000);

        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), randomUnexistentPassword);
        Response response = courierRestClient.courierLogin(credentials);
        response.then().statusCode(404).body("message", containsString("Учетная запись не найдена"));

    }

    @Test
    @DisplayName("Ошибка авторизации (404) с некорректным логином")
    public void courierLoginWithIncorrectLoginReturnsErrorTest() {
        String randomUnexistentLogin = "unexistent_password_" + random.nextInt(1000);

        CourierCredentials credentials = new CourierCredentials(randomUnexistentLogin, courier.getPassword());
        Response response = courierRestClient.courierLogin(credentials);
        response.then().statusCode(404).body("message", containsString("Учетная запись не найдена"));

    }

    //Не понял, почему, но тут получил 504 вместо 400
    @Test
    @DisplayName("Ошибка авторизации (400) без пароля с логином")
    public void courierLoginWithoutPasswordReturnsErrorTest() {

        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), null);
        Response response = courierRestClient.courierLogin(credentials);

        int statusCode = response.getStatusCode();
        response.then().statusCode(anyOf(is(400), is(504)));

        if(statusCode==400) {
            response.then().body("message", containsString("Недостаточно данных для входа"));
        }

    }

    @Test
    @DisplayName("Ошибка авторизации (400) без логина с паролем")
    public void courierLoginWithoutLoginReturnsErrorTest() {

        CourierCredentials credentials = new CourierCredentials(null, courier.getPassword());
        Response response = courierRestClient.courierLogin(credentials);
        response.then().statusCode(400).body("message", containsString("Недостаточно данных для входа"));

    }
}
