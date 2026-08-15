package ru.yandex.practicum.scooter.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.scooter.api.client.CourierRestClient;
import ru.yandex.practicum.scooter.api.dto.Courier;
import ru.yandex.practicum.scooter.api.dto.CourierCredentials;

import static org.hamcrest.Matchers.*;

import java.util.Random;

public class CreateCourierApiTest {
    private CourierRestClient courierRestClient;
    private Integer courierId;
    private String randomLogin;
    private String randomPassword;
    private String randomFirstName;

    @BeforeEach
    public void courierSetUp()
    {
        courierRestClient = new CourierRestClient();
        Random random = new Random();
        randomLogin = "sam_bridges_" + random.nextInt(1000);
        randomPassword = "cupid_lulu_" + random.nextInt(1000);
        randomFirstName = "Сэм " + random.nextInt(1000);
    }

    @AfterEach
    public void tearDown() {
        if (courierId != null) {
            courierRestClient.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Успешное создание курьера со всеми обязательными атрибутами")
    public void createCourierWithValidDataTest() {
        Courier courier = new Courier(randomLogin, randomPassword, randomFirstName);
        Response createCourierResponse = courierRestClient.createCourier(courier);
        createCourierResponse.then().statusCode(201).body("ok", is(true));

        Response loginResponse = courierRestClient.courierLogin(new CourierCredentials(courier.getLogin(), courier.getPassword()));
        courierId = loginResponse.then().extract().path("id");
    }

    @Test
    @DisplayName("Ошибка (409) создания двух одинаковых курьеров")
    public void createCourierDuplicateTest() {
        Courier courier = new Courier(randomLogin, randomPassword, randomFirstName);

        courierRestClient.createCourier(courier);

        Response loginResponse = courierRestClient.courierLogin(new CourierCredentials(courier.getLogin(), courier.getPassword()));
        courierId = loginResponse.then().extract().path("id");

        Response createCourierResponse = courierRestClient.createCourier(courier);
        createCourierResponse.then().statusCode(409).body("message", containsString("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Ошибка (400) создания курьера без логина")
    public void createCourierWithoutLoginTest() {
        Courier courier = new Courier(null, randomPassword, randomFirstName);
        Response createCourierResponse = courierRestClient.createCourier(courier);
        createCourierResponse.then().statusCode(400).body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Ошибка (400) создания курьера без пароля")
    public void createCourierWithouthPasswordTest() {
        Courier courier = new Courier(randomLogin, null, randomFirstName);
        Response createCourierResponse = courierRestClient.createCourier(courier);
        createCourierResponse.then().statusCode(400).body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Успешное создание курьера без имени")
    public void createCourierWithoutFirstNameTest() {
        Courier courier = new Courier(randomLogin, randomPassword, null);
        Response createCourierResponse = courierRestClient.createCourier(courier);
        createCourierResponse.then().statusCode(201).body("ok", is(true));

        Response loginResponse = courierRestClient.courierLogin(new CourierCredentials(courier.getLogin(), courier.getPassword()));
        courierId = loginResponse.then().extract().path("id");
    }
}
