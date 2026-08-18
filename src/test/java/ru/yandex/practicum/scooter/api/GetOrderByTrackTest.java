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

    private String expectedFirstName;
    private String expectedLastName;
    private String expectedAddress;
    private String expectedMetroStation;
    private String expectedPhone;
    private String expectedDeliveryDate;
    private byte expectedRentTime;
    private String expectedComment;

    @BeforeEach
    public void getOrderByTrackSetUp() {
        orderRestClient = new OrderRestClient();
        random = new Random();

        expectedFirstName = "qwerty_" + random.nextInt(1000);
        expectedLastName = "qwerty_" + random.nextInt(1000);
        expectedAddress = "qwerty_" + random.nextInt(1000);
        expectedMetroStation = "1";
        expectedPhone = "+7999" + (random.nextInt(9000000) + 1000000);
        expectedDeliveryDate = "2026-11-11";
        expectedRentTime = (byte)1;
        expectedComment = "";

        Order order = new Order(expectedFirstName, expectedLastName, expectedAddress, expectedMetroStation, expectedPhone, expectedRentTime, expectedDeliveryDate, expectedComment, List.of());
        Response response = orderRestClient.createOrder(order);
        trackNumber = response.jsonPath().getInt("track");
    }

    @Test
    @DisplayName("Успешный запрос получения заказа по его номеру возвращает объект с заказом")
    public void getOrderByValidTrackNumberReturnsCorrectOrderStructureTest() {
        Response response = orderRestClient.getOrderDetailsByTrackNumber(trackNumber);

        response.then().statusCode(200);

        Order receivedOrder = response.jsonPath().getObject("order", Order.class);

        assertEquals(expectedFirstName, receivedOrder.getFirstName(), "Имя пользователя не совпадает");
        assertEquals(expectedLastName, receivedOrder.getLastName(), "Фамилия пользователя не совпадает");
        assertEquals(expectedAddress, receivedOrder.getAddress(), "Адрес заказа не совпадает");
        assertEquals(expectedMetroStation, receivedOrder.getMetroStation(), "Станция метро в заказе не совпадает");
        assertEquals(expectedPhone, receivedOrder.getPhone(), "Номер телефона не совпадает");
        assertEquals(expectedDeliveryDate, receivedOrder.getDeliveryDate(), "Дата доставки не совпадает");
        assertEquals(expectedRentTime, receivedOrder.getRentTime(), "Срок аренды не совпадает");
        assertEquals(expectedComment, receivedOrder.getComment(), "Комментарий не совпадает");
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
