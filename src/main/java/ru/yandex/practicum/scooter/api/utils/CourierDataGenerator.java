package ru.yandex.practicum.scooter.api.utils;

import ru.yandex.practicum.scooter.api.dto.Courier;

import java.util.Random;

public class CourierDataGenerator {
    private static final Random RANDOM = new Random();

    public static Courier generateCourier() {
        String courierLogin = "sam_bridges_" + RANDOM.nextInt(1000);
        String courierPassword = "cupid_lulu_" + RANDOM.nextInt(1000);
        String courierFirstName = "Сэм " + RANDOM.nextInt(1000);

        return new Courier(courierLogin, courierPassword, courierFirstName);
    }


}
