package org.example.repository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHashing {
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");


            byte[] hashBytes = digest.digest(password.getBytes());


            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
            return null;
        }
    }

    public static boolean verifyPassword(String inputPassword, String hashedPassword) {

        String inputPasswordHash = hashPassword(inputPassword);
        return inputPasswordHash.equals(hashedPassword);
    }
}
