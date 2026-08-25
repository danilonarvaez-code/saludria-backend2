package com.citas.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarPassword {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        String password = "Admin123*";

        String hash = encoder.encode(password);

        System.out.println("CONTRASEÑA: " + password);
        System.out.println("BCrypt: " + hash);
    }
}