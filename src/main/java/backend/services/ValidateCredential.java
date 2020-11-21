package backend.services;

import backend.model.loginModel;

import java.security.SecureRandom;
import java.util.Base64;

public class ValidateCredential {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public boolean ValidateCredential(loginModel request, loginModel credentialDB) {

        if (!request.getPassword().equals(credentialDB.getPassword())) {
            return false;
        }
        return  true;
    }


      public String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
