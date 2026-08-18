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

import static org.hamcrest.Matchers.*;


public class CreateCourierApiTest {
    private CourierRestClient courierRestClient;
    private Integer courierId;
    private Courier courier;

    @BeforeEach
    public void createCourierSetUp() {
        courierRestClient = new CourierRestClient();
        courier = CourierDataGenerator.generateCourier();
    }

    @AfterEach
    public void cleanCreateCourierTestData() {
        if (courierId != null) {
            courierRestClient.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Успешное создание курьера со всеми обязательными атрибутами возвращает ok : true")
    public void createCourierWithAllDataReturnsSuccessTest() {

        Response createCourierResponse = courierRestClient.createCourier(courier);
        createCourierResponse.then().statusCode(201).body("ok", is(true));

        Response loginResponse = courierRestClient.courierLogin(new CourierCredentials(courier.getLogin(), courier.getPassword()));
        courierId = loginResponse.then().extract().path("id");
    }

    @Test
    @DisplayName("Ошибка (409) создания двух одинаковых курьеров")
    public void createCourierDuplicateReturnsErrorTest() {

        courierRestClient.createCourier(courier);

        courierId = courierRestClient.getCourierIdAfterLogin(new CourierCredentials(courier.getLogin(), courier.getPassword()));

        Courier duplicateCourier = CourierDataGenerator.generateCourier();
        duplicateCourier.setLogin(courier.getLogin());
        duplicateCourier.setPassword(courier.getPassword());

        Response createCourierResponse = courierRestClient.createCourier(duplicateCourier);
        createCourierResponse.then().statusCode(409).body("message", containsString("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Ошибка (400) создания курьера без логина")
    public void createCourierWithoutLoginReturnsErrorTest() {

        courier.setLogin(null);

        Response createCourierResponse = courierRestClient.createCourier(courier);
        createCourierResponse.then().statusCode(400).body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Ошибка (400) создания курьера без пароля")
    public void createCourierWithoutPasswordReturnsErrorTest() {

        courier.setPassword(null);

        Response createCourierResponse = courierRestClient.createCourier(courier);
        createCourierResponse.then().statusCode(400).body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Успешное создание курьера без имени возвращает ok : true")
    public void createCourierWithoutFirstNameReturnsSuccessTest() {

        courier.setFirstName(null);

        Response createCourierResponse = courierRestClient.createCourier(courier);
        createCourierResponse.then().statusCode(201).body("ok", is(true));

        Response loginResponse = courierRestClient.courierLogin(new CourierCredentials(courier.getLogin(), courier.getPassword()));
        courierId = loginResponse.then().extract().path("id");
    }
}
