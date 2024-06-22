package org.passmanager.configuration;

import org.passmanager.service.DecryptionService;
import org.passmanager.service.EncryptionService;
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
