/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.Util;
import java.security.Key;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

final class SecureStringUtil {
    static final String CIPHER_TRANSFORMATION = "AES/GCM/NoPadding";
    static final String KEYGEN_ALGORITHEM = "AES";
    static final int IV_LENGTH = 12;
    static final int KEY_SIZE = 256;
    static final int TAG_LENGTH = 16;
    SecretKeySpec secretKey;
    private Cipher encryptCipher;
    private Cipher decryptCipher;
    private static volatile SecureStringUtil instance;
    private static final Lock INSTANCE_LOCK;
    private static final Lock ENCRYPT_LOCK;
    private static final Lock DECRYPT_LOCK;

    static SecureStringUtil getInstance() throws SQLServerException {
        if (instance == null) {
            INSTANCE_LOCK.lock();
            try {
                if (instance == null) {
                    instance = new SecureStringUtil();
                }
            }
            finally {
                INSTANCE_LOCK.unlock();
            }
        }
        return instance;
    }

    private SecureStringUtil() throws SQLServerException {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance(KEYGEN_ALGORITHEM);
            keygen.init(256);
            this.secretKey = new SecretKeySpec(keygen.generateKey().getEncoded(), KEYGEN_ALGORITHEM);
            this.encryptCipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            this.decryptCipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        }
        catch (Exception e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_SecureStringInitFailed"));
            Object[] msgArgs = new Object[]{e.getMessage()};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
    }

    byte[] getEncryptedBytes(char[] chars) throws SQLServerException {
        ENCRYPT_LOCK.lock();
        try {
            if (chars == null) {
                byte[] byArray = null;
                return byArray;
            }
            byte[] iv = new byte[12];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            GCMParameterSpec ivParamSpec = new GCMParameterSpec(128, iv);
            this.encryptCipher.init(1, (Key)this.secretKey, ivParamSpec);
            byte[] cipherText = this.encryptCipher.doFinal(Util.charsToBytes(chars));
            byte[] bytes = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, bytes, 0, iv.length);
            System.arraycopy(cipherText, 0, bytes, iv.length, cipherText.length);
            byte[] byArray = bytes;
            return byArray;
        }
        catch (Exception e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_EncryptionFailed"));
            Object[] msgArgs = new Object[]{e.getMessage()};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        finally {
            ENCRYPT_LOCK.unlock();
        }
    }

    char[] getDecryptedChars(byte[] bytes) throws SQLServerException {
        char[] cArray;
        block8: {
            DECRYPT_LOCK.lock();
            byte[] plainText = null;
            try {
                if (bytes == null) {
                    char[] cArray2 = null;
                    return cArray2;
                }
                byte[] iv = new byte[12];
                System.arraycopy(bytes, 0, iv, 0, 12);
                GCMParameterSpec ivParamSpec = new GCMParameterSpec(128, iv);
                this.decryptCipher.init(2, (Key)this.secretKey, ivParamSpec);
                plainText = this.decryptCipher.doFinal(bytes, 12, bytes.length - 12);
                cArray = Util.bytesToChars(plainText);
                if (plainText == null) break block8;
            }
            catch (Exception e) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_DecryptionFailed"));
                Object[] msgArgs = new Object[]{e.getMessage()};
                throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
            }
            finally {
                if (plainText != null) {
                    Arrays.fill(plainText, (byte)0);
                }
                DECRYPT_LOCK.unlock();
            }
            Arrays.fill(plainText, (byte)0);
        }
        DECRYPT_LOCK.unlock();
        return cArray;
    }

    static {
        INSTANCE_LOCK = new ReentrantLock();
        ENCRYPT_LOCK = new ReentrantLock();
        DECRYPT_LOCK = new ReentrantLock();
    }
}

