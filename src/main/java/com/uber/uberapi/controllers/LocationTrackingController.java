package com.uber.uberapi.controllers;

import com.uber.uberapi.exceptions.InvalidDriverException;
import com.uber.uberapi.exceptions.InvalidPassengerException;
import com.uber.uberapi.models.Driver;
import com.uber.uberapi.models.ExactLocation;
import com.uber.uberapi.models.Passenger;
import com.uber.uberapi.repositories.DriverRepository;
import com.uber.uberapi.repositories.PassengerRepository;
import com.uber.uberapi.services.Constants;
import com.uber.uberapi.services.locationtracking.LocationTrackingService;
import com.uber.uberapi.services.messagequeue.MessageQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/location")
public class LocationTrackingController {

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    LocationTrackingService locationTrackingService;

    @Autowired
    Constants constants;

    @Autowired
    MessageQueue messageQueue;

    public Driver getDriverFromId(Long driverId) {
        Optional<Driver> driver = driverRepository.findById(driverId);
        if (driver.isEmpty()) {
            throw new InvalidDriverException("No driver with id " + driverId);
        }
        return driver.get();
    }

    @PutMapping("/driver/{driverId}")
    public void updateDriverLocation(@PathVariable Long driverId,
                                     @RequestBody ExactLocation data) {
        Driver driver = getDriverFromId(driverId);
        ExactLocation location = ExactLocation.builder()
                .longitude(data.getLongitude())
                .latitude(data.getLatitude())
                .build();
        messageQueue.sendMessage(
                constants.getLocationTrackingTopicName(),
                new LocationTrackingService.Message(driver, location)
        );
    }

    public Passenger getPassengerFromId(Long passengerId) {
        Optional<Passenger> passenger = passengerRepository.findById(passengerId);
        if (passenger.isEmpty()) {
            throw new InvalidPassengerException("No passenger with id " + passengerId);
        }
        return passenger.get();
    }

    @PutMapping("/passenger/{passengerId}")
    public void updatePassengerLocation(@PathVariable Long passengerId,
                                        @RequestBody ExactLocation location) {
        Passenger passenger = getPassengerFromId(passengerId);
        passenger.setLastKnownLocation(ExactLocation.builder()
                .longitude(location.getLongitude())
                .latitude(location.getLatitude())
                .build());
        passengerRepository.save(passenger);
    }

}
