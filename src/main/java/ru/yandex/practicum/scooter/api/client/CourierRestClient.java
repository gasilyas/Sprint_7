package ru.yandex.practicum.scooter.api.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.practicum.scooter.api.dto.Courier;
import ru.yandex.practicum.scooter.api.dto.CourierCredentials;
import static io.restassured.RestAssured.given;

public class CourierRestClient extends BaseRestClient{
    private static final String COURIER_V1_PATH = "/api/v1/courier";
    private static final String COURIER_LOGIN_V1_PATH = "/api/v1/courier/login";

    @Step("Создание курьера {courier.login}")
    public Response createCourier(Courier courier) {
        return given()
                .spec(getBaseSpecification())
                .body(courier)
                .when()
                .post(COURIER_V1_PATH);
    }

    @Step("Удаление курьера по id {courierId} (при наличии)")
    public Response deleteCourier(Integer courierId) {
        var deleteCourierRequest = given().spec(getBaseSpecification());
        String finalDeleteCourierPath;
        if (courierId != null) {
            finalDeleteCourierPath = COURIER_V1_PATH + "/" + courierId;
        } else  {
            finalDeleteCourierPath = COURIER_V1_PATH;
        }
        return deleteCourierRequest
                .when()
                .delete(finalDeleteCourierPath);
    }

    @Step("Авторизация курьера с логином {credentials.login}")
    public Response courierLogin(CourierCredentials credentials) {
        return given()
                .spec(getBaseSpecification())
                .body(credentials)
                .when()
                .post(COURIER_LOGIN_V1_PATH);
    }

    @Step("Вспомогательный: получение id курьера после логина")
    public Integer getCourierIdAfterLogin(CourierCredentials credentials) {
        return courierLogin(credentials)
                .then()
                .statusCode(200)
                .extract().path("id");
    }

}
