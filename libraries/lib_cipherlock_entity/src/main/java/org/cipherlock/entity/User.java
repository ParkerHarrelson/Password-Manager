package org.cipherlock.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


import java.io.Serializable;

@Data
@Entity
@Table(name = "user", schema = "cipherlock")
public class User extends ExpirableEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid", unique = true, nullable = false)
    private Integer userID;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "email", unique = true)
    @Email(message = "Email should be valid")
    @Pattern(
            regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$",
            message = "Email should match the pattern"
    )
    private String email;

}
