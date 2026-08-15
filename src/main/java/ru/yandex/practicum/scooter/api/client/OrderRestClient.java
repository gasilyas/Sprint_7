package ru.yandex.practicum.scooter.api.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.practicum.scooter.api.dto.Order;

import static io.restassured.RestAssured.given;

public class OrderRestClient extends BaseRestClient{
    private static final String ORDER_V1_PATH = "/api/v1/order";

    @Step("Создание заказа для клиента {order.firstName} {order.lastName")
    public Response createOrder(Order order) {
        return given()
                .spec(getBaseSpecification())
                .body(order)
                .when()
                .post(ORDER_V1_PATH);
    }

    @Step("Получение общего списка заказов")
    public Response getOrderList(Order order) {
        return given()
                .spec(getBaseSpecification())
                .when()
                .get(ORDER_V1_PATH);
    }

    @Step("Принятие заказа {orderId} курьером {courierId}")
    public Response acceptOrderByCourier(Integer orderId, Integer courierId) {
        var acceptOrderRequest = given().spec(getBaseSpecification());
        if(courierId != null) {
            acceptOrderRequest.queryParam("courierId", courierId);
        }
        String finalAcceptOrderPath;
        if(orderId != null) {
            finalAcceptOrderPath = ORDER_V1_PATH + "/accept/" + orderId;
        } else {
            finalAcceptOrderPath = ORDER_V1_PATH + "/accept";
        }
        return acceptOrderRequest
                .when()
                .put(finalAcceptOrderPath);
    }

    @Step("Получение информации о заказе по номеру отслеживания {trackNumber}")
    public Response getOrderDetailsByTrackNumber(Integer trackNumber) {
        var getOrderRequest = given().spec(getBaseSpecification());
        if(trackNumber != null) {
            getOrderRequest.queryParam("t", trackNumber);
        }
        return  getOrderRequest
                .when()
                .get(ORDER_V1_PATH + "/track");
    }
}
