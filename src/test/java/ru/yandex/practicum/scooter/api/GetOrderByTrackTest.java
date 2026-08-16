package ru.yandex.practicum.scooter.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.scooter.api.client.OrderRestClient;
import ru.yandex.practicum.scooter.api.dto.Order;

import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class GetOrderByTrackTest {
    private OrderRestClient orderRestClient;
    private Integer trackNumber;
    private Random random;

    @BeforeEach
    public void getOrderByTrackSetUp() {
        orderRestClient = new OrderRestClient();
        random = new Random();

        String randomString = "qwerty_" + random.nextInt(1000);
        Order order = new Order(randomString, randomString, randomString, "1", "111", (byte)1, "2026-11-11", "", List.of());
        Response response = orderRestClient.createOrder(order);
        trackNumber = response.jsonPath().getInt("track");
    }

    @Test
    @DisplayName("Успешный запрос получения заказа по его номеру возвращает объект с заказом")
    public void getOrderByValidTrackNumberReturnsCorrectOrderStructureTest() {
        Response response = orderRestClient.getOrderDetailsByTrackNumber(trackNumber);

        response.then().statusCode(200);

        Order receivedOrder = response.jsonPath().getObject("order", Order.class);

        assertNotNull(receivedOrder.getFirstName(), "Поле firstName не может быть пустым");
        assertNotNull(receivedOrder.getLastName(), "Поле lastName не может быть пустым");
        assertNotNull(receivedOrder.getAddress(), "Поле address не может быть пустым");
        assertNotNull(receivedOrder.getMetroStation(), "Поле metroStation не может быть пустым");
        assertNotNull(receivedOrder.getPhone(), "Поле phone не может быть пустым");
        assertNotNull(receivedOrder.getDeliveryDate(), "Поле deliveryDate не может быть пустым");
        assertTrue(receivedOrder.getRentTime() > 0, "Срок аренды не может быть меньше 1");
    }

    @Test
    @DisplayName("Ошибка (400) получения заказа без номера")
    public void getOrderWithoutTrackNumberReturnsErrorTest() {
        Response response = orderRestClient.getOrderDetailsByTrackNumber(null);
        response.then().statusCode(400).body("message", containsString("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Ошибка (404) получения заказа несуществующим номером (track)")
    public void getOrderWithNonExistingTrackNumberReturnsErrorTest() {
        random = new Random();
        Integer nonExistingTrack = Integer.MAX_VALUE - random.nextInt(100000);
        Response response = orderRestClient.getOrderDetailsByTrackNumber(nonExistingTrack);
        response.then().statusCode(404).body("message", containsString("Заказ не найден"));
    }
}
