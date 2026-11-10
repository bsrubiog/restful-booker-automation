package com.automation.base;

import com.automation.filters.AuthenticationFilter;
import com.automation.utils.ConfigurationManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {
    protected static RequestSpecification requestSpec;
    protected static ResponseSpecification responseSpec;

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = ConfigurationManager.getProperty("base.url");

        requestSpec = new RequestSpecBuilder()
                .setContentType("application/json")
                .setAccept("application/json")
                .addFilter(new AllureRestAssured())
                .addFilter(new AuthenticationFilter())
                .setRelaxedHTTPSValidation()
                .build();

        responseSpec = new ResponseSpecBuilder()
                .expectHeader("Content-Type", "application/json; charset=utf-8")
                .build();
    }
}