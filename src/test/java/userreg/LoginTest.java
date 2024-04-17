package userreg;

import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoginTest {

    private boolean userAuthorisationSuccess;
    private String userAccessToken;

    UserReg user = new UserReg();

    //создание УЗ пользователя
    @Before
    public void createBeforeUser() throws Exception {
        user = UserReg.getRandomUser();
        LogSpecs.getResponseCreateUser(user, 200);
        userAccessToken = LogSpecs.accessToken;
    }

    //удаление УЗ пользователя
    @After
    public void deleteAfterUser() throws Exception {
        LogSpecs.getResponseUserDeleted(userAccessToken, 202);
    }

    @Test
    @DisplayName("Успешная авторизация")
    public void successfulUserAuthorization() throws JsonProcessingException {
        //данные для авторизации существующего пользователя
        //авторизация пользователя
        userAccessToken = LogSpecs.accessToken;
        userAuthorisationSuccess = LogSpecs.success;
        assertTrue(userAuthorisationSuccess);
    }

    @Test
    @DisplayName("Неуспешная авторизация: существующий пользователь + неверный логин (email)")
    public void unsuccessfulAuthorizationWithInvalidEmail() throws JsonProcessingException {
        String invalidEmail = "asafds@gmail.com";
        //данные для авторизации существующего пользователя
        UserReg createdUser = new UserReg(invalidEmail, user.getPassword());
        //авторизация пользователя
        LogSpecs.getResponseUserAuthorization(createdUser, 401);
        userAuthorisationSuccess = LogSpecs.success;
        assertFalse(userAuthorisationSuccess);
    }

    @Test
    @DisplayName("Неуспешная авторизация: существующий пользователь + неверный пароль")
    public void unsuccessfulAuthorizationWithInvalidPassword() throws JsonProcessingException {
        String invalidPassword = "000000";
        //данные для авторизации существующего пользователя
        UserReg createdUser = new UserReg(user.getEmail(), invalidPassword);
        //авторизация пользователя
        LogSpecs.getResponseUserAuthorization(createdUser, 401);
        userAuthorisationSuccess = LogSpecs.success;
        assertFalse(userAuthorisationSuccess);
    }
}
