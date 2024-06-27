package org.cipherlock.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.cipherlock.constants.ApplicationConstants.URL_USER_BASE;

@Slf4j
@RestController
@RequestMapping(value = URL_USER_BASE)
@AllArgsConstructor
public class UserController {


}
