/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.mavenpasswordobfuscator;

import aQute.lib.base64.Base64;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MavenPasswordObfuscator {
    private static final Pattern DECORATED_PASSWORD_P = Pattern.compile("\\{\\s*(?<expr>(?:[a-z0-9+/]{4})*(?:[a-z0-9+/]{2}==|[a-z0-9+/]{3}=)?)\\s*\\}", 2);
    private static final int SALT_SIZE = 8;
    private static final int CHUNK_SIZE = 16;
    private static final String DIGEST_ALG = "SHA-256";
    private static final String KEY_ALG = "AES";
    private static final String CIPHER_ALG = "AES/CBC/PKCS5Padding";
    private static final SecureRandom secureRandom = new SecureRandom();

    public static byte[] encrypt(byte[] payload, String passPhrase) throws Exception {
        byte[] salt = new byte[8];
        secureRandom.nextBytes(salt);
        Cipher cipher = MavenPasswordObfuscator.createCipher(passPhrase, salt, 1);
        byte[] encryptedBytes = cipher.doFinal(payload);
        int len = encryptedBytes.length;
        byte padLen = (byte)(16 - (8 + len + 1) % 16);
        int totalLen = 8 + len + padLen + 1;
        byte[] allEncryptedBytes = new byte[totalLen];
        secureRandom.nextBytes(allEncryptedBytes);
        System.arraycopy(salt, 0, allEncryptedBytes, 0, 8);
        allEncryptedBytes[8] = padLen;
        System.arraycopy(encryptedBytes, 0, allEncryptedBytes, 9, len);
        return allEncryptedBytes;
    }

    public static byte[] decrypt(byte[] encryptedPayload, String passPhrase) throws Exception {
        byte[] salt = new byte[8];
        System.arraycopy(encryptedPayload, 0, salt, 0, 8);
        byte padLen = encryptedPayload[8];
        byte[] encryptedBytes = new byte[encryptedPayload.length - 8 - 1 - padLen];
        System.arraycopy(encryptedPayload, 9, encryptedBytes, 0, encryptedBytes.length);
        Cipher cipher = MavenPasswordObfuscator.createCipher(passPhrase, salt, 2);
        return cipher.doFinal(encryptedBytes);
    }

    public static String encrypt(String clearText, String passPhrase) throws Exception {
        byte[] encrypted = MavenPasswordObfuscator.encrypt(clearText.getBytes(StandardCharsets.UTF_8), passPhrase);
        return "{" + Base64.encodeBase64(encrypted) + "}";
    }

    public static String decrypt(String base64Encrypted, String passPhrase) throws Exception {
        Matcher matcher = DECORATED_PASSWORD_P.matcher(base64Encrypted);
        if (!matcher.matches()) {
            return null;
        }
        String expr = matcher.group("expr");
        byte[] encryptedPayload = Base64.decodeBase64(expr);
        byte[] payload = MavenPasswordObfuscator.decrypt(encryptedPayload, passPhrase);
        return new String(payload, StandardCharsets.UTF_8);
    }

    private static Cipher createCipher(String passPhrase, byte[] salt, int mode) throws Exception {
        MessageDigest digester = MessageDigest.getInstance(DIGEST_ALG);
        byte[] key = new byte[16];
        byte[] iv = new byte[16];
        digester.update(passPhrase.getBytes(StandardCharsets.UTF_8));
        digester.update(salt, 0, 8);
        byte[] digest = digester.digest();
        System.arraycopy(digest, 0, key, 0, 16);
        System.arraycopy(digest, 16, iv, 0, 16);
        Cipher cipher = Cipher.getInstance(CIPHER_ALG);
        cipher.init(mode, (Key)new SecretKeySpec(key, KEY_ALG), new IvParameterSpec(iv));
        return cipher;
    }

    public static boolean isObfuscatedPassword(String passphrase) {
        return passphrase != null && DECORATED_PASSWORD_P.matcher(passphrase).matches();
    }
}

