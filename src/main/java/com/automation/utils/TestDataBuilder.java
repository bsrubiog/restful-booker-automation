package com.automation.utils;

import com.github.javafaker.Faker;
import com.automation.models.Booking;
import com.automation.models.BookingDates;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class TestDataBuilder {
    private static final Faker faker = new Faker();

    public static Booking createRandomBooking() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String checkin = formatter.format(faker.date().future(5, TimeUnit.DAYS));
        String checkout = formatter.format(faker.date().future(10, TimeUnit.DAYS));

        BookingDates dates = BookingDates.builder()
                .checkin(checkin)
                .checkout(checkout)
                .build();

        return Booking.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .totalprice(faker.number().numberBetween(100, 1500))
                .depositpaid(true)
                .bookingdates(dates)
                .additionalneeds(faker.food().vegetable())
                .build();
    }
}