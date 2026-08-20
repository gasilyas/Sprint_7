package ru.yandex.practicum.scooter.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.scooter.api.client.OrderRestClient;
import ru.yandex.practicum.scooter.api.dto.Order;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GetOrderListApiTest {
    private OrderRestClient orderRestClient;

    @BeforeEach
    public void getOrderListSetUp() {
        orderRestClient = new OrderRestClient();
    }

    @Test
    @DisplayName("Получение списка заказов (массива с json-объектами) в теле ответа")
    public void getOrderListReturnsNotEmptyTest() {
        Response response = orderRestClient.getOrderList();

        response.then().statusCode(200);

        List<Order> orders = response.jsonPath().getList("orders", Order.class);

        assertNotNull(orders, "В ответе отсутствует массив заказов");
        assertFalse(orders.isEmpty(), "Массив заказов не должен быть пустым");
    }
}