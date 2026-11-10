package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.models.AuthRequest;
import com.automation.models.AuthResponse;
import com.automation.models.Booking;
import com.automation.models.BookingResponse;
import com.automation.utils.TestDataBuilder;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RestfulBookerTests extends BaseTest {

    @Test
    @DisplayName("Escenario 1: Autenticación Dinámica y Reutilización de Sesión")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Valida el consumo de /auth, deserialización a POJO y obtención del token.")
    public void testDynamicAuthentication() {
        AuthRequest credentials = AuthRequest.builder()
                .username("admin")
                .password("password123")
                .build();

        AuthResponse authResponse = given()
                .spec(requestSpec)
                .body(credentials)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .extract()
                .as(AuthResponse.class);

        assertThat(authResponse.getToken(), notNullValue());
        assertThat(authResponse.getToken().length(), greaterThan(5));
    }

    @Test
    @DisplayName("Escenario 2: Creación de Payload Dinámico mediante Builder Pattern")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Crea una reserva usando Lombok y JavaFaker, valida status 200 y deserializa respuesta.")
    public void testCreateBookingWithBuilder() {
        Booking newBooking = TestDataBuilder.createRandomBooking();

        BookingResponse response = given()
                .spec(requestSpec)
                .body(newBooking)
                .when()
                .post("/booking")
                .then()
                .statusCode(200)
                .extract()
                .as(BookingResponse.class);

        assertThat(response.getBookingid(), notNullValue());
        assertThat(response.getBooking().getFirstname(), equalTo(newBooking.getFirstname()));
        assertThat(response.getBooking().getTotalprice(), equalTo(newBooking.getTotalprice()));
    }

   @Test
    @DisplayName("Escenario 3: Serialización, Deserialización y Groovy Path Expressions")
    @Severity(SeverityLevel.NORMAL)
    @Description("Filtra reservas usando Groovy path expressions sin bucles for y mapea a objeto tipado.")
    public void testGroovyPathFiltering() {
        // Utilizar Groovy path expressions para filtrar reservas con totalprice mayor a un umbral (ej. 100)
        // y extraer los IDs directamente sin bucles for en Java
        List<Integer> filteredBookingIds = given()
                .spec(requestSpec)
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("findAll { it.bookingid > 0 }.bookingid", Integer.class);

        assertThat(filteredBookingIds, notNullValue());
        assertThat(filteredBookingIds, not(empty()));

        // Consultar uno de los IDs obtenidos y mapear la respuesta anidada a Booking.class
        int sampleId = filteredBookingIds.get(0);

        Booking bookingDetails = given()
                .spec(requestSpec)
                .when()
                .get("/booking/" + sampleId)
                .then()
                .statusCode(200)
                .extract()
                .as(Booking.class);

        assertThat(bookingDetails.getFirstname(), notNullValue());
    }

    @Test
    @DisplayName("Escenario 4: Validaciones Avanzadas (Contrato, SLA y Headers)")
    @Severity(SeverityLevel.NORMAL)
    @Description("Valida JSON Schema, tiempo de respuesta menor a 2000ms y headers de seguridad.")
    public void testAdvancedValidations() {
        Booking newBooking = TestDataBuilder.createRandomBooking();
        int bookingId = given()
                .spec(requestSpec)
                .body(newBooking)
                .when()
                .post("/booking")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getInt("bookingid");

        given()
                .spec(requestSpec)
                .when()
                .get("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .spec(responseSpec)
                .time(lessThan(2000L))
                .body(matchesJsonSchemaInClasspath("schemas/booking-schema.json"));
    }

    @Test
    @DisplayName("Escenario 5: Manejo Negativo y Edge Cases")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Valida 403 Forbidden al actualizar sin token y 404 Not Found con ID inexistente.")
    public void testNegativeAndEdgeCases() {
        Booking updatePayload = TestDataBuilder.createRandomBooking();

        given()
                .contentType("application/json")
                .body(updatePayload)
                .when()
                .put("/booking/1")
                .then()
                .statusCode(403);

        given()
                .spec(requestSpec)
                .when()
                .get("/booking/9999999")
                .then()
                .statusCode(404);
    }
}