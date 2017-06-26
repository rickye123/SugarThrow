package com.example.richa.sugarthrow;

/*
Password hashing class by Karan Balkar at https://karanbalkar.com/2013/05/tutorial-28-implement-sha1-and-md5-hashing-in-android/
 */

import android.util.Base64;
import android.util.Log;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class PasswordHash {

    /**
     * Method taken from Balkar (2013).
     * @param password - the password to be hashed
     */
    String computeSHAHash(String password) {

        MessageDigest mdSha1 = null;
        try {
            mdSha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e1) {
            Log.e("myapp", "Error initializing SHA1 message digest");
        }

        try {
            assert mdSha1 != null;
            mdSha1.update(password.getBytes("ASCII"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] data = mdSha1.digest();
        try {
            return convertToHex(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * Method taken from Balkar (2013). Convert the string to hex to encode
     * @param data - the data in bytes
     * @return converted string
     * @throws java.io.IOException - throw exception
     */
    private static String convertToHex(byte[] data) throws java.io.IOException {

        StringBuilder sb = new StringBuilder();
        String hex;

        hex = Base64.encodeToString(data, 0, data.length, 0);

        sb.append(hex);

        return sb.toString();

    }

}
