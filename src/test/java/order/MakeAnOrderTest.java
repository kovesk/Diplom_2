package order;

import userreg.UserReg;
import userreg.LogSpecs;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class MakeAnOrderTest {

    private String userAccessToken;
    private ArrayList<String> ingredientsHash;
    private String[] ingredients;

    UserReg user = new UserReg();

    @Before
    public void tearUp() throws Exception {
        //создание пользователя
        user = UserReg.getRandomUser();
        //создание УЗ
        LogSpecs.getResponseCreateUser(user, 200);
        userAccessToken = LogSpecs.accessToken;
        //валидные хеши ингредиентов
        ingredientsHash = OrderSpec.createListOfIngredients();
    }

    @After //удаление УЗ
    public void tearDown() throws Exception {
        LogSpecs.getResponseUserDeleted(userAccessToken, 202);
    }

    @Test
    @DisplayName("Успешное создание заказа с авторизацией и двумя ингредиентами")
    public void successfullyCreatingAnOrderWithAuthorizationAndTwoIngredients() throws JsonProcessingException {
        //авторизация пользователя
        LogSpecs.getResponseUserAuthorization(user, 200);
        userAccessToken = LogSpecs.accessToken;
        //ингредиенты для заказа (массив)
        ingredients = new String[]{ingredientsHash.get(0), ingredientsHash.get(ingredientsHash.size() - 1)};
        Order order = new Order(ingredients);
        //создание заказа
        OrderSpec.getCreateOrder(order, userAccessToken, 200)
                .assertThat()
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Неуспешное создание заказа с авторизацией без ингредиентов")
    public void unsuccessfulOrderCreationWithAuthorizationWithoutIngredients() throws JsonProcessingException {
        //авторизацию пользователя
        LogSpecs.getResponseUserAuthorization(user, 200);
        userAccessToken = LogSpecs.accessToken;
        Order order = new Order(ingredients);
        //создание заказа
        OrderSpec.getCreateOrder(order, userAccessToken, 400)
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    //тест падает, потому что заказ создается без токена (200)
    @Test
    @DisplayName("Неуспешное создание заказа без авторизации с двумя ингредиентами")
    public void unsuccessfulOrderCreationWithoutAuthorizationWithTwoIngredients() throws JsonProcessingException {
        ingredients = new String[]{ingredientsHash.get(0), ingredientsHash.get(ingredientsHash.size() - 1)};
        Order order = new Order(ingredients);
        //создание заказа
        OrderSpec.getCreateOrder(order, "ghgh", 400)
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Неуспешное создания заказа без авторизации без ингредиентов")
    public void unsuccessfulOrderCreationWithoutAuthorizationWithoutIngredients() throws JsonProcessingException {
        Order order = new Order(ingredients);
        //создание заказа
        OrderSpec.getCreateOrder(order, "", 400)
                 .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Тест неуспешного создания заказа с авторизацией и неверным хешем ингредиента")
    public void unsuccessfulOrderCreationWithAuthorizationWithInvalidHashOfIngredients() throws JsonProcessingException {
        //авторизация
        LogSpecs.getResponseUserAuthorization(user, 200);
        userAccessToken = LogSpecs.accessToken;
        //невалидный хеш
        ingredients = new String[]{"1234567890asd"};
        Order order = new Order(ingredients);
        //создание заказа
        OrderSpec.getCreateOrder(order, userAccessToken, 400)
                 .body("message", equalTo("One or more ids provided are incorrect"));
    }
}
