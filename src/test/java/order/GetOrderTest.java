package order;

import userreg.UserReg;
import userreg.LogSpecs;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

import static config.Requests.NOT_AUTHORISE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;

public class GetOrderTest {
    private String userAccessToken;
    private int numberOfOrders;

    UserReg user = new UserReg();

    @Before
    public void tearUp() throws Exception {
        //создание пользователя
        user = UserReg.getRandomUser();
        userAccessToken = LogSpecs.getResponseCreateUser(user,200).accessToken;
        //количество заказов
        numberOfOrders = 5;
        //создание списка заказов пользователя
        OrderSpec.createListOfOrders(user, numberOfOrders);
    }

    @After //удаление УЗ
    public void tearDown() throws Exception {
        LogSpecs.getResponseUserDeleted(userAccessToken, 202);
    }

    @Test
    @DisplayName("Список заказов авторизованного пользователя")
    public void getListOfOrdersOfAnAuthorizedUser() throws JsonProcessingException {
        //авторизация
        userAccessToken = LogSpecs.getResponseUserAuthorization(user, 200).accessToken;
        //получения списка заказов
        ArrayList<Integer> orderNumber =
                new ArrayList<>(OrderSpec.getOrderList(userAccessToken, 200)
                        .extract()
                        .path("orders.number"));
        assertEquals(numberOfOrders, orderNumber.size());
    }

    @Test
    @DisplayName("Список заказов неавторизованного пользователя")
    public void getUnauthorizedUserOrderList() throws JsonProcessingException {
        //получения списка заказов
        OrderSpec.getOrderList("", 401)
                .body("message",equalTo(NOT_AUTHORISE));
    }
}
