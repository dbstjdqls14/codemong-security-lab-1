package com.codemong.securitylab.me;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MeService {

    public MeResponse me(Authentication authentication) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Step 03에서 구현하세요.");
    }
}
