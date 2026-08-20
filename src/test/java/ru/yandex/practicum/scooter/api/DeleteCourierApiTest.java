package ru.yandex.practicum.scooter.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.scooter.api.client.CourierRestClient;
import ru.yandex.practicum.scooter.api.dto.Courier;
import ru.yandex.practicum.scooter.api.dto.CourierCredentials;
import ru.yandex.practicum.scooter.api.utils.CourierDataGenerator;

import java.util.Random;

import static org.hamcrest.Matchers.*;

public class DeleteCourierApiTest {
    private CourierRestClient courierRestClient;
    private int nonExistentCourierId;

    @BeforeEach
    public void deleteCourierSetUp() {
        courierRestClient = new CourierRestClient();
        Random random = new Random();
        nonExistentCourierId = Integer.MAX_VALUE - random.nextInt(100000);
    }

    @Test
    @DisplayName("Успешное удаление курьера (200), возвращает ok: true")
    public void deleteCourierByValidIdReturnsSuccessTest() {
        Courier courier = CourierDataGenerator.generateCourier();
        courierRestClient.createCourier(courier);

        Integer courierId = courierRestClient.getCourierIdAfterLogin(new CourierCredentials(courier.getLogin(), courier.getPassword()));

        Response deleteResponse = courierRestClient.deleteCourier(courierId);
        deleteResponse.then().statusCode(200).body("ok", is(true));
    }

    @Test
    @DisplayName("Ошибка (400) удаления курьера без id")
    public void deleteCourierWithoutIdReturnsErrorTest() {
        Response response = courierRestClient.deleteCourier(null);
        response.then().statusCode(400).body("message", containsString("Недостаточно данных для удаления курьера"));
    }

    @Test
    @DisplayName("Ошибка (404) удаления курьера с несуществующим id")
    public void deleteCourierWithNonExistentIdReturnsErrorTest() {
        Response response = courierRestClient.deleteCourier(nonExistentCourierId);
        response.then().statusCode(404).body("message", containsString("Курьера с таким id нет"));
    }
}
