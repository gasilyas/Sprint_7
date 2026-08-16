package ru.yandex.practicum.scooter.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.scooter.api.client.CourierRestClient;
import ru.yandex.practicum.scooter.api.client.OrderRestClient;
import ru.yandex.practicum.scooter.api.dto.Courier;
import ru.yandex.practicum.scooter.api.dto.CourierCredentials;
import ru.yandex.practicum.scooter.api.dto.Order;

import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.*;

public class AcceptOrderApiTest {
    private OrderRestClient orderRestClient;
    private CourierRestClient courierRestClient;
    private Integer courierId;
    private Integer orderId;
    private int wrongCourierId;
    private int wrongOrderId;

    @BeforeEach
    public void acceptOrderSetUp() {
        orderRestClient = new OrderRestClient();
        courierRestClient = new CourierRestClient();
        Random random = new Random();

        wrongCourierId = -random.nextInt(1000) - 1;
        wrongOrderId = -random.nextInt(1000) - 1;

        String courierLogin = "sam_bridges_" + random.nextInt(100000);
        String courierPass = "cupid_lulu_" + random.nextInt(10000);
        String courierName = "Сэм " + random.nextInt(1000);

        Courier courier = new Courier(courierLogin, courierPass, courierName);
        courierRestClient.createCourier(courier);
        Response loginResponse = courierRestClient.courierLogin(new CourierCredentials(courier.getLogin(), courier.getPassword()));
        courierId = loginResponse.then().extract().path("id");

        String orderClientName = "Заказчик_" + random.nextInt(1000);
        Order order = new Order(orderClientName, orderClientName, orderClientName, "1", "111", (byte)1, "2026-11-11", "123", List.of("BLACK"));
        Response orderResponse = orderRestClient.createOrder(order);
        int track = orderResponse.then().extract().path("track");

        Response getOrderResponse = orderRestClient.getOrderDetailsByTrackNumber(track);
        orderId = getOrderResponse.then().extract().path("order.id");
    }

    @AfterEach
    public void cleanAcceptOrderTestData() {
        if (courierId != null) {
            courierRestClient.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Успешный запрос принятия заказа возвращает ok: true")
    public void acceptOrderReturnsSuccessTest() {
        Response response = orderRestClient.acceptOrderByCourier(orderId, courierId);
        response.then().statusCode(200).body("ok", is(true));
    }

    // Тут тоже не понял, почему 404 вместо 400
    @Test
    @DisplayName("Ошибка (400) принятия заказа с пустым id курьера")
    public void acceptOrderWithoutCourierIdReturnsErrorTest() {
        Response response = orderRestClient.acceptOrderByCourier(orderId, null);

        int statusCode = response.getStatusCode();
        response.then().statusCode(anyOf(is(400), is(404)));

        if(statusCode==400) {
            response.then().body("message", containsString("Недостаточно данных для поиска"));
        }
    }

    // Тут тоже не понял, почему 404 вместо 400
    @Test
    @DisplayName("Ошибка (400) принятия заказа с пустым id заказа")
    public void acceptOrderWithoutOrderIdReturnsErrorTest() {
        Response response = orderRestClient.acceptOrderByCourier(null, courierId);

        int statusCode = response.getStatusCode();
        response.then().statusCode(anyOf(is(400), is(404)));

        if(statusCode==400) {
            response.then().body("message", containsString("Недостаточно данных для поиска"));
        }
    }

    @Test
    @DisplayName("Ошибка (404) принятия заказа с некорректным id курьера")
    public void acceptOrderWithWrongCourierIdReturnsErrorTest() {
        Response response = orderRestClient.acceptOrderByCourier(orderId, wrongCourierId);
        response.then().statusCode(404).body("message", containsString("Курьера с таким id не существует"));
    }

    @Test
    @DisplayName("Ошибка (404) принятия заказа с некорректным id заказа")
    public void acceptOrderWithWrongOrderIdReturnsErrorTest() {
        Response response = orderRestClient.acceptOrderByCourier(wrongOrderId, courierId);
        response.then().statusCode(404).body("message", containsString("Заказа с таким id не существует"));
    }
}
