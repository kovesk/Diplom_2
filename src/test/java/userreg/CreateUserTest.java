package userreg;

import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Test;

import static config.Requests.*;
import static org.junit.Assert.*;

public class CreateUserTest {

    private String userAccessToken;
    private boolean userCreateSuccess;
    UserReg user = new UserReg();

    //удаление УЗ пользователя
    @After
    public void deleteAfterUser() throws Exception {
        if (userCreateSuccess) {
            LogSpecs.getResponseUserDeleted(userAccessToken, 202);
        }
    }

    @Test
    @DisplayName("Успешное создание УЗ пользователя")
    public void successfulCreateUser() throws JsonProcessingException {
        //создание пользователя
        user = UserReg.getRandomUser();
        //создание УЗ пользователя
        LogSpecs.getResponseCreateUser(user, 200);

    }

    @Test
    @DisplayName("Неуспешное создание УЗ пользователя без пароля")
    public void unsuccessfulUserCreationWithOutPassword() throws JsonProcessingException {
        //создание пользователя без пароля
        user = UserReg.getRandomUserWithoutPassword();
        //создание УЗ пользователя
        LogSpecs response = LogSpecs.getResponseCreateUser(user, 403);
        assertFalse(response.success);
        assertEquals(WRONG_CREDS, response.message);
    }

    @Test
    @DisplayName("Неуспешное создание УЗ пользователя без имени")
    public void unsuccessfulUserCreationWithOutName() throws JsonProcessingException {
        //создание пользователя без имени
        user = UserReg.getRandomUserWithoutName();
        //создание УЗ пользователя
        LogSpecs response = LogSpecs.getResponseCreateUser(user, 403);
        assertFalse(response.success);
        assertEquals(WRONG_CREDS, response.message);
    }

    @Test
    @DisplayName("Неуспешное создание УЗ пользователя без почты")
    public void unsuccessfulUserCreationWithOutEmail() throws JsonProcessingException {
        //создание пользователя без email
        user = UserReg.getRandomUserWithoutEmail();
        //создание УЗ пользователя
        LogSpecs response = LogSpecs.getResponseCreateUser(user, 403);
        assertFalse(response.success);
        assertEquals(WRONG_CREDS, response.message);
    }

    @Test
    @DisplayName("Неуспешное создание УЗ пользователя, который уже есть в базе (с повторяющимся email)")
    public void unsuccessfulUserCreationWithValidCredentials() throws JsonProcessingException {
        //создание пользователя
        user = UserReg.getRandomUser();
        //создание УЗ пользователя
        LogSpecs initResponse = LogSpecs.getResponseCreateUser(user, 200);
        userAccessToken = initResponse.accessToken;
        userCreateSuccess = initResponse.success;
        //создание УЗ пользователя, который уже зарегистрирован
        LogSpecs response = LogSpecs.getResponseCreateUser(user, 403);
        assertFalse(response.success);
        assertEquals(USER_EXIST, response.message);
    }
}
