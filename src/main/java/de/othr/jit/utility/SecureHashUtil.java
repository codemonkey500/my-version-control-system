package de.othr.jit.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * This class provides a method for SHA-1 hashing.
 * @author codemonkey500
 *
 */
public class SecureHashUtil {
    
    private static final Logger LOGGER = Logger
            .getLogger(SecureHashUtil.class.getName());
    
    private SecureHashUtil() {
    }
    
    
    /**
     * Use this method to generate a SHA-1 hash code
     * 
     * @param target - is the byte[] to be hashed
     * @return - a String representation of the computed hash code
     */
    public static String computeHash(byte[] target) {

        String result = "";

        try {

            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(target);
            result = Base64.getUrlEncoder().encodeToString(hash);

        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, ExceptionUtils.getStackTrace(e));
        }

        return result;
    }
}
