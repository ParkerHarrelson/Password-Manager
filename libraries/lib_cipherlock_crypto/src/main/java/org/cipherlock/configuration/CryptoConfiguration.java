package org.cipherlock.configuration;

import org.cipherlock.service.DecryptionService;
import org.cipherlock.service.EncryptionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptoConfiguration {

    @Bean
    public EncryptionService encryptionService() {
        return new EncryptionService();
    }

    @Bean
    public DecryptionService decryptionService() {
        return new DecryptionService();
    }
}
