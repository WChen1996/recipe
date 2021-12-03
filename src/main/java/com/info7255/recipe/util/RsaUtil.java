package com.info7255.recipe.util;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
@Component
public class RsaUtil {
    @Bean
    public KeyPair getKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair=keyPairGenerator.generateKeyPair();
        //System.out.println(keyPair.getPrivate().getEncoded());
        //System.out.println(keyPair.getPublic().getEncoded());
        return keyPair;
    }
}
