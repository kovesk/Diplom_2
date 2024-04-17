package order;

import static config.Requests.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import io.qameta.allure.Step;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import config.Requests;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import userreg.UserReg;
import userreg.LogSpecs;
import java.util.ArrayList;

public class OrderSpec {

    static ObjectMapper mapper = new ObjectMapper();

    @Step("Запрос ингредиентов")
    public static ValidatableResponse getIngredients() throws JsonProcessingException {
        return RestAssured.given().log().all()
                .baseUri(Requests.BASE_URL)
                .get(INGREDIENTS)
                .then().log().all()
                .statusCode(200);
    }

    @Step("Создание заказа")
    public static ValidatableResponse getCreateOrder(Order order, String userAccessToken,
                                                     int statusCode) throws JsonProcessingException {
        String jsonString = mapper.writeValueAsString(order);
        return RestAssured.given().log().all()
                .headers("Authorization", userAccessToken, "Content-Type", "application/json")
                .baseUri(Requests.BASE_URL)
                .body(jsonString)
                .when()
                .post(ORDERS)
                .then().log().all()
                .statusCode(statusCode);
    }

    @Step("Список валидных хешей ингредиентов")
    public static ArrayList<String> createListOfIngredients() throws JsonProcessingException {
        return new ArrayList<>(OrderSpec.getIngredients()
                .extract()
                .path("data._id"));
    }

    @Step("Создание списка заказов пользователя")
    public static void createListOfOrders(UserReg userreg, int numberOfOrders) throws JsonProcessingException {
        // получаем список валидных хешей
        ArrayList<String> ingredientsHash = createListOfIngredients();
        // массив ингредиентов для заказа
        String[] ingredients = new String[]{ingredientsHash.get(0), ingredientsHash.get(ingredientsHash.size() - 1)};
        Order order = new Order(ingredients);
        // запрос на авторизацию пользователя
        LogSpecs response = LogSpecs.getResponseUserAuthorization(userreg, 200);
        // создание numberOfOrders кол-во заказов
        for (int i = 0; i < numberOfOrders; i++){
            // запрос на создание заказа
            OrderSpec.getCreateOrder(order, LogSpecs.accessToken, 200)
                    .assertThat()
                    .body("order.number",notNullValue());
        }
        // выход из учетной записи пользователя
        LogSpecs.getResponseLogoutUser(LogSpecs.refreshToken, 200);
    }

    @Step("Получение списка заказов")
    public static ValidatableResponse getOrderList(String userAccessToken, int statusCode) {
        return RestAssured.given().log().all()
                .header("Authorization", userAccessToken)
                .baseUri(Requests.BASE_URL)
                .when()
                .get(ORDERS)
                .then().log().all()
                .statusCode(statusCode);
    }
}
