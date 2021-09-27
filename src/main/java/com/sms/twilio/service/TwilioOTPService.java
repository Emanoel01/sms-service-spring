package com.sms.twilio.service;

import com.sms.twilio.config.TwilioConfig;
import com.sms.twilio.dto.OtpStatus;
import com.sms.twilio.dto.PasswordResetRequestDto;
import com.sms.twilio.dto.PasswordResetResponseDto;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class TwilioOTPService {


    @Autowired
    private TwilioConfig twilioConfig;

    private  PasswordResetResponseDto passwordResetResponseDto = null;

    private Map<String,String> otpMap = new HashMap<>();

    public Mono<PasswordResetResponseDto>
    sendOTPForPasswordReset(PasswordResetRequestDto passwordResetRequestDto){

        try {
            PhoneNumber to = new PhoneNumber((passwordResetRequestDto.getNumber()));
            PhoneNumber from = new PhoneNumber(this.twilioConfig.getTrialNumber());
            String otp = generateOTP();
            String otpMessage = "Dear Customer, your OTP is ##"+otp;

            Message message = Message.creator(to, from,otpMessage).create();
            this.otpMap.put(passwordResetRequestDto.getUsername(),otp);
            passwordResetResponseDto = new PasswordResetResponseDto(OtpStatus.DELIVERED, otpMessage);

        }catch (Exception e){
            passwordResetResponseDto = new PasswordResetResponseDto(OtpStatus.FAILED, e.getMessage());
        }

        return Mono.just(passwordResetResponseDto);
    }

    public Mono<String> validateOTP(String userInputOtp, String username){
        if(userInputOtp.equals(this.otpMap.get(username))){
            return Mono.just(("Valid OTP please  proceed with your transaction!"));
        }else{
            return Mono.error(new IllegalArgumentException("Invalid OTP please retry!"));
        }
    }

    private String generateOTP(){
        return new DecimalFormat("000000").format(new Random().nextInt(999999));
    }



}
