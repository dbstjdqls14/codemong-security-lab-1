package com.codemong.securitylab.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponse(
        String accessToken,
        String refreshToken
) {
    public static LoginResponse accessOnly(String accessToken) {
        return new LoginResponse(accessToken, null);
    }

    public static LoginResponse withRefresh(String accessToken, String refreshToken) {
        return new LoginResponse(accessToken, refreshToken);
    }
}
