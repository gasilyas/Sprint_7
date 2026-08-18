package ru.yandex.practicum.scooter.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.scooter.api.client.OrderRestClient;
import ru.yandex.practicum.scooter.api.dto.Order;
import ru.yandex.practicum.scooter.api.utils.OrderDataGenerator;

import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class GetOrderByTrackTest {
    private OrderRestClient orderRestClient;
    private Integer trackNumber;

    private String expectedFirstName;
    private String expectedLastName;
    private String expectedAddress;
    private String expectedMetroStation;
    private String expectedPhone;
    private String expectedDeliveryDate;
    private byte expectedRentTime;
    private String expectedComment;
    private List<String> expectedColors;

    @BeforeEach
    public void getOrderByTrackSetUp() {
        orderRestClient = new OrderRestClient();

        Order order = OrderDataGenerator.generateOrderData(List.of());

        expectedFirstName = order.getFirstName();
        expectedLastName = order.getLastName();
        expectedAddress = order.getAddress();
        expectedMetroStation = order.getMetroStation();
        expectedPhone = order.getPhone();
        expectedDeliveryDate = order.getDeliveryDate();
        expectedRentTime = order.getRentTime();
        expectedComment = order.getComment();
        expectedColors = order.getColor();

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
        assertTrue(receivedOrder.getDeliveryDate().contains(expectedDeliveryDate), "Дата доставки не содержит ожидаемое значение. Получено:" + receivedOrder.getDeliveryDate());
        assertEquals(expectedRentTime, receivedOrder.getRentTime(), "Срок аренды не совпадает");
        assertEquals(expectedComment, receivedOrder.getComment(), "Комментарий не совпадает");
        assertEquals(expectedColors, receivedOrder.getColor(), "Цвета не совпадают");
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
        Random random = new Random();
        Integer nonExistingTrack = Integer.MAX_VALUE - random.nextInt(100000);
        Response response = orderRestClient.getOrderDetailsByTrackNumber(nonExistingTrack);
        response.then().statusCode(404).body("message", containsString("Заказ не найден"));
    }
}
