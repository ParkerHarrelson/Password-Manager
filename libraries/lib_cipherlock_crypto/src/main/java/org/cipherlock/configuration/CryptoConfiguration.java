package org.cipherlock.configuration;

import org.cipherlock.service.AESEncryptionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptoConfiguration {

    @Bean
    public AESEncryptionService encryptionService() {
        return new AESEncryptionService();
    }

}
