package ru.yandex.practicum.scooter.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.scooter.api.client.OrderRestClient;
import ru.yandex.practicum.scooter.api.dto.Order;
import ru.yandex.practicum.scooter.api.utils.OrderDataGenerator;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;

public class CreateOrderApiTest {

    private OrderRestClient orderRestClient;

    @BeforeEach
    public void createOrderSetUp() {
        orderRestClient =  new OrderRestClient();
    }

    public static Stream<Arguments> scooterColorProvider() {
    return Stream.of(
            Arguments.of(List.of("BLACK")),
            Arguments.of(List.of("GREY")),
            Arguments.of(List.of("BLACK", "GREY")),
            Arguments.of(List.of())
    );
    }

    @ParameterizedTest(name = "Тест создания заказа с цветом/цветами: {0}")
    @MethodSource("scooterColorProvider")
    @DisplayName("Создание заказа с разными вводными данными цветов возвращает track != null")
    public void createScooterOrderWithDifferentColorsReturnsSuccessTest(List<String> scooterColors) {

        Order order = OrderDataGenerator.generateOrderData(scooterColors);

        Response response = orderRestClient.createOrder(order);
        response.then().statusCode(201).body("track", notNullValue());
    }
}
