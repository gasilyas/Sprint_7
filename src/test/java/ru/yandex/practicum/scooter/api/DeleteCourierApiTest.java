package ru.yandex.practicum.scooter.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.scooter.api.client.CourierRestClient;
import ru.yandex.practicum.scooter.api.dto.Courier;
import ru.yandex.practicum.scooter.api.dto.CourierCredentials;

import java.util.Random;

import static org.hamcrest.Matchers.*;

public class DeleteCourierApiTest {
    private CourierRestClient courierRestClient;
    private String randomLogin;
    private String randomPassword;
    private String randomFirstName;
    private int nonExistentCourierId;

    @BeforeEach
    public void deleteCourierSetUp() {
        courierRestClient = new CourierRestClient();
        Random random = new Random();
        randomLogin = "sam_bridges_" + random.nextInt(1000);
        randomPassword = "cupid_lulu_" + random.nextInt(1000);
        randomFirstName = "Сэм " + random.nextInt(1000);
        nonExistentCourierId = Integer.MAX_VALUE - random.nextInt(100000);
    }

    @Test
    @DisplayName("Успешное удаление курьера (200), возвращает ok: true")
    public void deleteCourierByValidIdReturnsSuccessTest() {
        Courier courier = new Courier(randomLogin, randomPassword, randomFirstName);
        courierRestClient.createCourier(courier);

        Response loginResponse = courierRestClient.courierLogin(new CourierCredentials(courier.getLogin(), courier.getPassword()));
        int id = loginResponse.then().extract().path("id");

        Response deleteResponse = courierRestClient.deleteCourier(id);
        deleteResponse.then().statusCode(200).body("ok", is(true));
    }

    // Тут тоже не понял, почему 404 вместо 400
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
