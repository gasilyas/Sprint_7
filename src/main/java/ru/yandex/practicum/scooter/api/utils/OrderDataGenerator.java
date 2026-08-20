package ru.yandex.practicum.scooter.api.utils;

import ru.yandex.practicum.scooter.api.dto.Order;

import java.util.List;
import java.util.Random;

public class OrderDataGenerator {
    private static final Random RANDOM = new Random();

    public static Order generateOrderData(List<String> color) {

        String firstName = "Иван_" + RANDOM.nextInt(1000);
        String lastName = "Иванов_" + RANDOM.nextInt(1000);
        String address = "Москва, ул. Пушкина, д. " + RANDOM.nextInt(100);
        String metroStation = String.valueOf((RANDOM.nextInt(10) + 1));
        String phone = "+7916" + (RANDOM.nextInt(9000000) + 1000000);
        byte rentTime = (byte) (RANDOM.nextInt(7) + 1);
        String deliveryDate = "2026-08-" + (RANDOM.nextInt(18) + 10);
        String comment = "Комментарий_" + RANDOM.nextInt(100);

        return new Order(firstName, lastName, address, metroStation,
                phone, rentTime, deliveryDate, comment, color);
    }
}
