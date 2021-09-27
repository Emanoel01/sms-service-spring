package com.sms.twilio.dto;

import lombok.Data;

@Data
public class PasswordResetRequestDto {

    private String number;
    private String username;
    private String oneTimePassword;

}
