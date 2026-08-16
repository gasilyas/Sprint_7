package ru.yandex.practicum.scooter.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.scooter.api.client.OrderRestClient;
import ru.yandex.practicum.scooter.api.dto.Order;

import java.util.List;
import java.util.Random;
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
        Random random = new Random();
        String firstName = "Иван_" + random.nextInt(1000);
        String lastName = "Иванов_" + random.nextInt(1000);
        String address = "Москва, ул. Пушкина, д. " + random.nextInt(100);
        String metroStation = String.valueOf((random.nextInt(10) + 1));
        String phone = "+7916" + (random.nextInt(9000000) + 1000000);
        byte rentTime = (byte) (random.nextInt(7) + 1);
        String deliveryDate = "2026-08-" + (random.nextInt(18) + 10);
        String comment = "Комментарий_" + random.nextInt(100);

        Order order = new Order(firstName, lastName, address, metroStation,
                phone, rentTime, deliveryDate, comment, scooterColors);

        Response response = orderRestClient.createOrder(order);
        response.then().statusCode(201).body("track", notNullValue());
    }
}
