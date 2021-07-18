package com.uber.uberapi.services.otp;

import com.uber.uberapi.models.OTP;

public interface OTPService {

    public void sendRideStartOTP(OTP rideStartOTP) ;


    public void sendPhoneNumberConfirmationOTP(OTP otp) ;
}
