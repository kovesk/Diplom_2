package userreg;

import static config.Requests.*;
import static io.restassured.RestAssured.given;

import config.Requests;
import io.qameta.allure.Step;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import java.util.HashMap;
import java.util.Map;

public class LogSpecs {

    private static String jsonString;
    public static String message;
    public static boolean success;
    public static String accessToken;
    public static String refreshToken;

    public LogSpecs(boolean success, String message, String accessToken, String refreshToken) {
        this.success = success;
        this.message = message;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    static ObjectMapper mapper = new ObjectMapper();


    @Step("Создание УЗ")
    public static LogSpecs getResponseCreateUser(UserReg userreg, int statusCode) throws JsonProcessingException {
        String jsonString = mapper.writeValueAsString(userreg);
        Response response = given().log().all()
                .header("Content-Type", "application/json")
                .baseUri(Requests.BASE_URL) // Использование переменной окружения
                .body(jsonString)
                .when()
                .post(REGISTER)
                .then().log().all()
                .statusCode(statusCode)
                .assertThat()
                .extract()
                .response();
        return new LogSpecs(response.path("success"), response.path("message"), response.path("accessToken"), response.path("refreshToken"));
    }


    @Step("Авторизация УЗ")
    public static LogSpecs getResponseUserAuthorization(UserReg userreg, int statusCode) throws JsonProcessingException {
        String jsonString = mapper.writeValueAsString(userreg);
        Response response = given().log().all()
                .header("Content-Type", "application/json")
                .baseUri(Requests.BASE_URL)
                .body(jsonString)
                .when()
                .post(LOGIN)
                .then().log().all()
                .statusCode(statusCode)
                .assertThat()
                .extract()
                .response();
        return new LogSpecs(response.path("success"), response.path("message"), response.path("accessToken"), response.path("refreshToken"));
    }

    @Step("Выход из УЗ")
    public static ValidatableResponse getResponseLogoutUser(String userRefreshToken, int statusCode) {
        // Создание Map для тела запроса
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("token", userRefreshToken);

        // Использование Map в качестве тела запроса
        return given().log().all()
                .header("Content-Type", "application/json")
                .baseUri(Requests.BASE_URL)
                .body(requestBody) // Передача Map в качестве тела запроса
                .when()
                .post(LOGOUT)
                .then().log().all()
                .statusCode(statusCode);
    }

    @Step("Удаление пользователя")
    public static ValidatableResponse getResponseUserDeleted(String userAccessToken, int statusCode) {
        return given().log().all()
                .header("Authorization", userAccessToken)
                .baseUri(Requests.BASE_URL)
                .when()
                .delete(UPDATE)
                .then().log().all()
                .statusCode(statusCode);
    }

    @Step("Обновление данных")
    public static ValidatableResponse getResponseUpdateUserData(UserReg userreg,
                                                                String userAccessToken,
                                                                int statusCode) throws JsonProcessingException {
        jsonString = mapper.writeValueAsString(userreg);
        return given().log().all()
                .headers("Authorization", userAccessToken, "Content-Type", "application/json")
                .baseUri(Requests.BASE_URL)
                .body(jsonString)
                .when()
                .patch(UPDATE)
                .then().log().all()
                .statusCode(statusCode);
    }

}


