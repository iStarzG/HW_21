package tests;


import components.TestDataAPI;
import io.qameta.allure.Owner;
import io.restassured.response.Response;
import models.LoginBodyDataForSchema;
import models.LoginBodyDateLombokModel;
import models.LoginResponseDateLombokModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static specs.LoginSpec.*;


public class LoginAPIReqResTest extends TestBaseAPI {

    TestDataAPI testData = new TestDataAPI();

    @Owner("iStarzG")
    @Tag("Successful")
    @DisplayName("Проверка токена ответа после отправки верных значений")
    @Test
    void loginSuccessfulWithCorrectValues() {
        LoginBodyDateLombokModel authData = new LoginBodyDateLombokModel();
        authData.setEmail("eve.holt@reqres.in");
        authData.setPassword("cityslicka");
        LoginResponseDateLombokModel response = step("Make request", () ->
                given(loginRequestSpec)
                        .body(authData)
                        .when()
                        .post("/login")
                        .then()
                        .spec(loginResponseSpec)
                        .extract().as(LoginResponseDateLombokModel.class));
        step("Check response", () -> {
            assertThat(response.getToken(), is(equalTo(testData.token)));
        });
        //    assertEquals(testData.token, response.getToken()));
    }

    @Owner("iStarzG")
    @Tag("Negative")
    @DisplayName("Проверка ответа с ошибкой")
    @Test
    void noContentJSONLoginTest() {
        LoginBodyDateLombokModel authData = new LoginBodyDateLombokModel();
        authData.setEmail("eve.holt@reqres.in");
        authData.setPassword("cityslicka");

        LoginResponseDateLombokModel response = step("Make request", () ->
                given(noJSONSpec)
                        .body(authData)
                        .when()
                        .post("/login")
                        .then()
                        .spec(missingErrorSpec)
                        .extract().as(LoginResponseDateLombokModel.class));
        step("Check response", () -> {
            assertThat(response.getError(), is(equalTo(testData.missingEmail)));
        });
        //   assertEquals(testData.missingEmail, response.getError()));

    }

    @Owner("iStarzG")
    @Tag("Negative")
    @DisplayName("Проверка ответа с ошибкой после отправки данных без пароля")
    @Test
    void missingPasswordLoginTest() {
        LoginBodyDateLombokModel authData = new LoginBodyDateLombokModel();
        authData.setEmail("eve.holt@reqres.in");
        LoginResponseDateLombokModel response = step("Make request", () ->
                given(loginRequestSpec)
                        .body(authData)
                        .when()
                        .post("/login")
                        .then()
                        .spec(missingErrorSpec)
                        .extract().as(LoginResponseDateLombokModel.class));
        step("Check response", () -> {
            assertThat(response.getError(), is(equalTo(testData.missingPassword)));
        });

        //    assertEquals(testData.missingPassword, response.getError()));
    }

    @Owner("iStarzG")
    @Tag("Negative")
    @DisplayName("Проверка ответа ошибки после отправки данных без адреса почты")
    @Test
    void loginUnsuccessfulNoEmail() {
        LoginBodyDateLombokModel authData = new LoginBodyDateLombokModel();
        authData.setPassword("cityslicka");
        LoginResponseDateLombokModel response = step("Make request", () ->
                given(loginRequestSpec)
                        .body(authData)
                        .when()
                        .post("/login")
                        .then()
                        .spec(missingErrorSpec)
                        .extract().as(LoginResponseDateLombokModel.class));
        step("Check response", () -> {
            assertThat(response.getError(), is(equalTo(testData.missingEmail)));
        });
        //      assertEquals(testData.missingEmail, response.getError()));


    }

    @Owner("iStarzG")
    @Tag("Successful")
    @DisplayName("Успешная проверка данных в ответе с взаимодействием схемы")
    @Test
    void singleUserSuccessfulLoginWithSchema() {
        LoginBodyDataForSchema data = new LoginBodyDataForSchema();
        Response response = step("Make Request", () ->
                given(loginRequestSpecForSchema)
                        .when()
                        .get("/users/2")
                        .then()
                        .body(matchesJsonSchemaInClasspath("schemas/schemaLoginsReqres"))
                        .spec(loginResponseSpecForSchema)
                        .extract().response());
        step("Make Response", () -> {
            assertAll("Проверка данных в ответе",
                    () -> assertThat(response.path(data.getId()), is(2)),
                    () -> assertThat(response.path(data.getEmail()), hasToString(testData.email)),
                    () -> assertThat(response.path(data.getFirstName()), hasToString(testData.firstName)),
                    () -> assertThat(response.path(data.getLastName()), hasToString(testData.lastName)));
        });
    }

    @Owner("iStarzG")
    @Tag("Negative")
    @DisplayName("Проверка ответа с пустыми данными")
    @Test
    void emptyValuesLoginTest() {
        LoginBodyDateLombokModel authData = new LoginBodyDateLombokModel();
        LoginResponseDateLombokModel response = step("Make request", () ->
                given(loginRequestSpec)
                        .body(authData)
                        .when()
                        .post("/login")
                        .then()
                        .spec(missingErrorSpec)
                        .extract().as(LoginResponseDateLombokModel.class));
        step("Check response", () -> {
            assertThat(response.getError(), is(equalTo(testData.missingEmail)));
        });
        //      assertEquals(testData.missingEmail, response.getError()));
    }
}
