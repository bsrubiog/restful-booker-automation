package com.automation.filters;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import static io.restassured.RestAssured.given;

public class AuthenticationFilter implements Filter {
    private static String token;

    public static synchronized String getToken() {
        if (token == null) {
            token = given()
                    .contentType("application/json")
                    .body("{\"username\" : \"admin\", \"password\" : \"password123\"}")
                    .when()
                    .post("/auth")
                    .jsonPath()
                    .getString("token");
        }
        return token;
    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        if (requestSpec.getMethod().equalsIgnoreCase("PUT") || requestSpec.getMethod().equalsIgnoreCase("DELETE")) {
            requestSpec.header("Cookie", "token=" + getToken());
        }
        return ctx.next(requestSpec, responseSpec);
    }
}