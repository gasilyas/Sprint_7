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
import ru.yandex.practicum.scooter.api.utils.CourierDataGenerator;
import ru.yandex.practicum.scooter.api.utils.OrderDataGenerator;

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

        wrongCourierId = Integer.MAX_VALUE - random.nextInt(100000);
        wrongOrderId = Integer.MAX_VALUE - random.nextInt(100000);

        Courier courier = CourierDataGenerator.generateCourier();
        courierRestClient.createCourier(courier);

        courierId = courierRestClient.getCourierIdAfterLogin(new CourierCredentials(courier.getLogin(), courier.getPassword()));

        Order order = OrderDataGenerator.generateOrderData(List.of("BLACK"));
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

    @Test
    @DisplayName("Ошибка (400) принятия заказа с пустым id курьера")
    public void acceptOrderWithoutCourierIdReturnsErrorTest() {
        Response response = orderRestClient.acceptOrderByCourier(orderId, null);
        response.then().statusCode(400).body("message", containsString("Недостаточно данных для поиска"));

    }

    @Test
    @DisplayName("Ошибка (400) принятия заказа с пустым id заказа")
    public void acceptOrderWithoutOrderIdReturnsErrorTest() {
        Response response = orderRestClient.acceptOrderByCourier(null, courierId);
        response.then().statusCode(400).body("message", containsString("Недостаточно данных для поиска"));
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
