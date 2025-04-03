package ds.helloworld;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Example {
    public static String calculate(String input) {
        try {
            // 1. Get an instance of the SHA-256 MessageDigest algorithm
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // 2. Calculate the digest (hash) of the input string
            byte[] digest = md.digest(input.getBytes());

            // 3. Convert the byte array to a hexadecimal string representation
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            // 4. Display the SHA-256 hash
            // System.out.println("SHA-256 Hash of \"" + input + "\": " + hexString.toString());
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-256 algorithm not found: " + e.getMessage());
        }
        return null;
    }
}
