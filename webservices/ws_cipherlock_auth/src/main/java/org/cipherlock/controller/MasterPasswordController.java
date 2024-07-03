package org.cipherlock.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cipherlock.dto.ResponseDTO.PasswordValidationResponseDTO;
import org.cipherlock.service.MasterPasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.cipherlock.constants.ApplicationConstants.*;

@Slf4j
@RestController
@RequestMapping(value = URL_PASSWORD_BASE)
@AllArgsConstructor
public class MasterPasswordController {

    private final MasterPasswordService masterPasswordService;

    @PostMapping(value = URL_PASSWORD_STRENGTH)
    public ResponseEntity<Integer> checkPasswordStrength(@RequestBody String password) {
        return ResponseEntity.ok(masterPasswordService.checkPasswordStrength(password));
    }

    @PostMapping(value = URL_PASSWORD_VALIDATE)
    public ResponseEntity<PasswordValidationResponseDTO> validatePassword(@RequestBody String password) {
        return ResponseEntity.ok(masterPasswordService.validatePassword(password));
    }

    @GetMapping(value = URL_PASSWORD_GENERATE)
    public ResponseEntity<String> generatePassword() {
        return ResponseEntity.ok(masterPasswordService.generatePassword());
    }
}
