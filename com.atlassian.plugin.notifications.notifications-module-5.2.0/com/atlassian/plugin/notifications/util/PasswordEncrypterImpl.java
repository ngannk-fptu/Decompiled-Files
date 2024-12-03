/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.license.LicenseHandler
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.log4j.Logger
 */
package com.atlassian.plugin.notifications.util;

import com.atlassian.plugin.notifications.util.PasswordEncrypter;
import com.atlassian.sal.api.license.LicenseHandler;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class PasswordEncrypterImpl
implements PasswordEncrypter {
    private static final Logger log = Logger.getLogger(PasswordEncrypterImpl.class);
    private final LicenseHandler licenseHandler;

    public PasswordEncrypterImpl(LicenseHandler licenseHandler) {
        this.licenseHandler = licenseHandler;
    }

    @Override
    public String encrypt(String password) {
        byte[] encrypted;
        try {
            String serverId = this.licenseHandler.getServerId();
            byte[] key = serverId.getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(1, secretKeySpec);
            encrypted = cipher.doFinal(password.getBytes());
        }
        catch (Exception e) {
            log.debug((Object)"Error encrypting", (Throwable)e);
            encrypted = new byte[]{};
        }
        BigInteger bi = new BigInteger(1, encrypted);
        return String.format("%0" + (encrypted.length << 1) + "X", bi);
    }

    @Override
    public String decrypt(String encryptedPassword) {
        if (StringUtils.isBlank((CharSequence)encryptedPassword)) {
            return null;
        }
        try {
            byte[] ciphertext = PasswordEncrypterImpl.hexStringToByteArray(encryptedPassword);
            String serverId = this.licenseHandler.getServerId();
            byte[] key = serverId.getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(2, secretKeySpec);
            byte[] original = cipher.doFinal(ciphertext);
            return new String(original);
        }
        catch (Exception e) {
            log.debug((Object)"error encrypting", (Throwable)e);
            return "";
        }
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte)((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}

