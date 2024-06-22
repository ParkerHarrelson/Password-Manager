package org.passmanager;

import org.passmanager.service.EncryptionService;

public class Main {

    public static void main(String[] args) {

        EncryptionService encryptionService = new EncryptionService();

        if (args.length < 2) {
            System.out.println("Enter arguments");
            System.exit(1);
        }

        String cipherText = encryptionService.encrypt(args[0], args[1]);
        System.out.println(STR."Cipher Text: \{cipherText}");
    }
}
