package com.uber.uberapi.controllers;

import com.uber.uberapi.exceptions.InvalidBookingException;
import com.uber.uberapi.exceptions.InvalidDriverException;
import com.uber.uberapi.models.Booking;
import com.uber.uberapi.models.Driver;
import com.uber.uberapi.models.OTP;
import com.uber.uberapi.models.Review;
import com.uber.uberapi.repositories.BookingRepository;
import com.uber.uberapi.repositories.DriverRepository;
import com.uber.uberapi.repositories.ReviewRepository;
import com.uber.uberapi.services.BookingService;
import com.uber.uberapi.services.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/driver")
@RestController
public class DriverController {
// ALL ENDPOINTS THAT DRIVER CAN USE

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    BookingService bookingService;

    @Autowired
    Constants constants;


    public Driver getDriverFromId(Long driverId) {
        Optional<Driver> driver = driverRepository.findById(driverId);
        if (driver.isEmpty()) {
            throw new InvalidDriverException("No Driver with id " + driverId);
        }
        return driver.get();
    }

    private Booking getBookingFromId(Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new InvalidBookingException("No Booking with id " + booking);
        }
        return booking.get();
    }

    public Booking getDriverBookingFromId(Long bookingId, Driver driver) {
        Booking booking = getBookingFromId(bookingId);
        if (!booking.getDriver().equals(driver)) {
            throw new InvalidBookingException(" Driver " + driver.getBookings() + " has no such booking " + bookingId);
        }
        return booking;
    }

    @GetMapping("/{driverId}")
    public Driver getDriver(@PathVariable(name = "driverId") Long driverId) {
        // make sure auth is done and has same driver id
        Driver driver = getDriverFromId(driverId);
        return driver;
    }

    @PatchMapping("/{driverId}")
    public void changeAvailability(@PathVariable(name = "driverId") Long driverId, @PathVariable Boolean available) {
        System.out.println("hitting driverid api");
        Driver driver = getDriverFromId(driverId);
        driver.setIsAvailable(available);
        driverRepository.save(driver);
    }

    @GetMapping("{driverId}/bookings")
    public List<Booking> getBookings(@PathVariable(name = "driverId") Long driverId) {
        Driver driver = getDriverFromId(driverId);
        return driver.getBookings();
    }

    @GetMapping("{driverId}/bookings/{bookingId}")
    public Booking getBooking(@PathVariable(name = "driverId") Long driverId, @PathVariable(name = "bookingId") Long bookingId) {
        Driver driver = getDriverFromId(driverId);
        return getDriverBookingFromId(bookingId, driver);
    }

    @PostMapping("{driverId}/bookings/{bookingId}")
    public void acceptBooking(@RequestParam(name = "driverId") Long driverId, @RequestParam(name = "bookingId") Long bookingId) {
        Driver driver = getDriverFromId(driverId);
        Booking booking = getBookingFromId(bookingId);
        bookingService.acceptBooking(driver, booking);
    }

    @DeleteMapping("{driverId}/bookings/{bookingId}")
    public void cancelBooking(@PathVariable(name = "driverId") Long driverId, @PathVariable(name = "bookingId") Long bookingId) {
        Driver driver = getDriverFromId(driverId);
        Booking booking = getDriverBookingFromId(bookingId, driver);
        bookingService.cancelByDriver(driver, booking);
        bookingService.cancelByDriver(driver, booking);
    }

    @PatchMapping("{driverId}/bookings/{bookingId}/start")
    public void startRide(@PathVariable(name = "driverId") Long driverId,
                          @PathVariable(name = "bookingId") Long bookingId,
                          @PathVariable OTP otp) {
        Driver driver = getDriverFromId(driverId);
        Booking booking = getDriverBookingFromId(bookingId, driver);
        booking.startRide(otp, constants.getRideStartOTPExpiryMinutes());
        bookingRepository.save(booking);
    }

    @PatchMapping("{driverId}/bookings/{bookingId}/end")
    public void endRide(@PathVariable(name = "driverId") Long driverId,
                        @PathVariable(name = "bookingId") Long bookingId) {
        Driver driver = getDriverFromId(driverId);
        Booking booking = getDriverBookingFromId(bookingId, driver);
        booking.endRide();
        driverRepository.save(driver);
        bookingRepository.save(booking);
    }

    @PatchMapping("{driverId}/bookings/{bookingId}/rate")
    public void rateRide(@PathVariable(name = "driverId") Long driverId,
                         @PathVariable(name = "bookingId") Long bookingId,
                         @RequestBody Review data) {
        Driver driver = getDriverFromId(driverId);
        Booking booking = getDriverBookingFromId(bookingId, driver);
        Review review = Review.builder()
                .note(data.getNote())
                .ratingOutOfFive(data.getRatingOutOfFive())
                .build();
        booking.setReviewByDriver(review);
        reviewRepository.save(review);
        bookingRepository.save(booking);
    }


}
