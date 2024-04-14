package userreg;

import org.apache.commons.lang3.RandomStringUtils;

public class UserReg {

    private String email;
    private String password;
    private String name;

    public UserReg() {
    }

    public UserReg(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserReg(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    //создание "случайного" пользователя
    public static UserReg getRandomUser() {
        return new UserReg(
                RandomStringUtils.randomAlphanumeric(10) + "@example.com",
                "123456",
                "TestUser_" + RandomStringUtils.randomAlphanumeric(6)
        );
    }

    //создание "случайного" пользователя без пароля
    public static UserReg getRandomUserWithoutPassword() {
        return new UserReg(
                RandomStringUtils.randomAlphanumeric(10) + "@example.com",
                "",
                "TestUser_" + RandomStringUtils.randomAlphanumeric(6)
        );
    }

    //создание "случайного" пользователя без имени
    public static UserReg getRandomUserWithoutName() {
        return new UserReg(
                "",
                "123456",
                "TestUser_" + RandomStringUtils.randomAlphanumeric(6)
        );
    }

    //создание "случайного" пользователя без email
    public static UserReg getRandomUserWithoutEmail() {
        return new UserReg(
                "",
                "123456",
                "TestUser_" + RandomStringUtils.randomAlphanumeric(6)
        );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

