package userreg;

import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static config.Requests.NOT_AUTHORISE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class UpdateTest {

    private String userAccessToken;

    UserReg user = new UserReg();

    @Before
    public void tearUp() throws Exception {
        //создание пользователя
        user = UserReg.getRandomUser();
        //создание УЗ пользователя
        userAccessToken = LogSpecs.getResponseCreateUser(user,200).accessToken;
    }

    //удаление УЗ пользователя
    @After
    public void tearDown() throws Exception {
        LogSpecs.getResponseUserDeleted(userAccessToken, 202);
    }

    @Test
    @DisplayName("Успешное изменение пароля авторизованного пользователя")
    public void passwordChangePositive() throws JsonProcessingException {
        UserReg createdUser = new UserReg(user.getEmail(), user.getPassword());
        //авторизация пользователя
        LogSpecs.getResponseUserAuthorization(createdUser, 200);
        //изменение пароля пользователя
        String updatedPassword = "123" + user.getPassword();
        UserReg updatedUser = new UserReg(user.getEmail(), updatedPassword, user.getName());
        //изменение данных пользователя
        LogSpecs.getResponseUpdateUserData(updatedUser, userAccessToken, 200);
        //авторизация с измененным паролем
        userAccessToken = LogSpecs.getResponseUserAuthorization(updatedUser, 200).accessToken;
        assertThat(userAccessToken, notNullValue());
    }

    @Test
    @DisplayName("Успешное изменение имени авторизованного пользователя")
    public void nameChangePositive() throws JsonProcessingException {
        UserReg createdUser = new UserReg(user.getEmail(), user.getPassword());
        //авторизация пользователя
        userAccessToken = LogSpecs.getResponseUserAuthorization(createdUser, 200).accessToken;
        //изменение имени пользователя
        String updatedName = "123" + user.getName();
        UserReg updatedUser = new UserReg(user.getEmail(), user.getPassword(), updatedName);
        //изменение данных пользователя
        LogSpecs.getResponseUpdateUserData(updatedUser, userAccessToken, 200)
                .body("user.name",equalTo(updatedName));
    }

    @Test
    @DisplayName("Успешное изменение почты авторизованного пользователя")
    public void emailChangePositive() throws JsonProcessingException {
        UserReg createdUser = new UserReg(user.getEmail(), user.getPassword());
        //авторизация пользователя
        userAccessToken = LogSpecs.getResponseUserAuthorization(createdUser, 200).accessToken;
        //изменение email пользователя
        String updatedEmail = "123" + user.getEmail();
        UserReg updatedUser = new UserReg(updatedEmail, user.getPassword(), user.getName());
        //изменение данных пользователя
        LogSpecs.getResponseUpdateUserData(updatedUser, userAccessToken, 200)
                .body("user.email",equalTo(updatedEmail.toLowerCase()));
    }

    @Test
    @DisplayName("Неуспешного изменение пароля неавторизованного пользователя")
    public void passwordChangeFail() throws JsonProcessingException {
        //изменение пароля пользователя
        String updatedPassword = "123" + user.getPassword();
        UserReg updatedUser = new UserReg(user.getEmail(), updatedPassword, user.getName());
        //изменение данных пользователя
        LogSpecs.getResponseUpdateUserData(updatedUser, "", 401)
                .body("message",equalTo(NOT_AUTHORISE));
    }

    @Test
    @DisplayName("Неуспешное изменение имени неавторизованного пользователя")
    public void nameChangeFail() throws JsonProcessingException {
        //изменение имени пользователя
        String updatedName = "New" + user.getName();
        UserReg updatedUser = new UserReg(user.getEmail(), user.getPassword(), updatedName);
        //изменение данных пользователя
        LogSpecs.getResponseUpdateUserData(updatedUser, "", 401)
                .body("message",equalTo(NOT_AUTHORISE));
    }

    @Test
    @DisplayName("Неуспешное изменение почты неавторизованного пользователя")
    public void emailChangeFail() throws JsonProcessingException {
        //изменение email пользователя
        String updatedEmail = "New" + user.getEmail();
        UserReg updatedUser = new UserReg(updatedEmail, user.getPassword(), user.getName());
        //изменение данных пользователя
        LogSpecs.getResponseUpdateUserData(updatedUser, "", 401)
                .body("message",equalTo(NOT_AUTHORISE));
    }
}
