package com.uber.uberapi.services.locationtracking;

import com.uber.uberapi.models.Driver;
import com.uber.uberapi.models.ExactLocation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationTrackingService {

    public List<Driver> getDriversNearLocation(ExactLocation pickupLocation){
        return null; // todo:
    }
}