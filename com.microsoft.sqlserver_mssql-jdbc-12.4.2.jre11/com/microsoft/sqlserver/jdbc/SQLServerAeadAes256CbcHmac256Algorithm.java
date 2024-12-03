/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerAeadAes256CbcHmac256EncryptionKey;
import com.microsoft.sqlserver.jdbc.SQLServerEncryptionAlgorithm;
import com.microsoft.sqlserver.jdbc.SQLServerEncryptionType;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerSecurityUtility;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

class SQLServerAeadAes256CbcHmac256Algorithm
extends SQLServerEncryptionAlgorithm {
    private static final Logger aeLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.SQLServerAeadAes256CbcHmac256Algorithm");
    static final String AEAD_AES_256_CBC_HMAC_SHA256 = "AEAD_AES_256_CBC_HMAC_SHA256";
    private static final String HMAC_SHA_256 = "HmacSHA256";
    private SQLServerAeadAes256CbcHmac256EncryptionKey columnEncryptionkey;
    private byte algorithmVersion;
    private boolean isDeterministic = false;
    private int blockSizeInBytes = 16;
    private byte[] version = new byte[]{1};
    private byte[] versionSize = new byte[]{1};
    private int minimumCipherTextLengthInBytesNoAuthenticationTag = 1 + this.blockSizeInBytes + this.blockSizeInBytes;
    private int minimumCipherTextLengthInBytesWithAuthenticationTag = this.minimumCipherTextLengthInBytesNoAuthenticationTag + 32;

    SQLServerAeadAes256CbcHmac256Algorithm(SQLServerAeadAes256CbcHmac256EncryptionKey columnEncryptionkey, SQLServerEncryptionType encryptionType, byte algorithmVersion) {
        this.columnEncryptionkey = columnEncryptionkey;
        if (encryptionType == SQLServerEncryptionType.DETERMINISTIC) {
            this.isDeterministic = true;
        }
        this.algorithmVersion = algorithmVersion;
        this.version[0] = algorithmVersion;
    }

    @Override
    byte[] encryptData(byte[] plainText) throws SQLServerException {
        return this.encryptData(plainText, true);
    }

    protected byte[] encryptData(byte[] plainText, boolean hasAuthenticationTag) throws SQLServerException {
        aeLogger.entering(SQLServerAeadAes256CbcHmac256Algorithm.class.getName(), "encryptData", "Encrypting data.");
        assert (plainText != null);
        byte[] iv = new byte[this.blockSizeInBytes];
        SecretKeySpec skeySpec = new SecretKeySpec(this.columnEncryptionkey.getEncryptionKey(), "AES");
        if (this.isDeterministic) {
            try {
                iv = SQLServerSecurityUtility.getHMACWithSHA256(plainText, this.columnEncryptionkey.getIVKey(), this.blockSizeInBytes);
            }
            catch (InvalidKeyException | NoSuchAlgorithmException e) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_EncryptionFailed"));
                Object[] msgArgs = new Object[]{e.getMessage()};
                throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
            }
        } else {
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
        }
        int numBlocks = plainText.length / this.blockSizeInBytes + 1;
        int hmacStartIndex = 1;
        int authenticationTagLen = hasAuthenticationTag ? 32 : 0;
        int ivStartIndex = hmacStartIndex + authenticationTagLen;
        int cipherStartIndex = ivStartIndex + this.blockSizeInBytes;
        int outputBufSize = 1 + authenticationTagLen + iv.length + numBlocks * this.blockSizeInBytes;
        byte[] outBuffer = new byte[outputBufSize];
        outBuffer[0] = this.algorithmVersion;
        System.arraycopy(iv, 0, outBuffer, ivStartIndex, iv.length);
        try {
            IvParameterSpec ivector = new IvParameterSpec(iv);
            Cipher encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            encryptCipher.init(1, (Key)skeySpec, ivector);
            int count = 0;
            int cipherIndex = cipherStartIndex;
            if (numBlocks > 1) {
                count = (numBlocks - 1) * this.blockSizeInBytes;
                cipherIndex += encryptCipher.update(plainText, 0, count, outBuffer, cipherIndex);
            }
            byte[] buffTmp = encryptCipher.doFinal(plainText, count, plainText.length - count);
            System.arraycopy(buffTmp, 0, outBuffer, cipherIndex, buffTmp.length);
            if (hasAuthenticationTag) {
                Mac hmac = Mac.getInstance(HMAC_SHA_256);
                SecretKeySpec initkey = new SecretKeySpec(this.columnEncryptionkey.getMacKey(), HMAC_SHA_256);
                hmac.init(initkey);
                hmac.update(this.version, 0, this.version.length);
                hmac.update(iv, 0, iv.length);
                hmac.update(outBuffer, cipherStartIndex, numBlocks * this.blockSizeInBytes);
                hmac.update(this.versionSize, 0, this.version.length);
                byte[] hash = hmac.doFinal();
                System.arraycopy(hash, 0, outBuffer, hmacStartIndex, authenticationTagLen);
            }
        }
        catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | ShortBufferException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_EncryptionFailed"));
            Object[] msgArgs = new Object[]{e.getMessage()};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        aeLogger.exiting(SQLServerAeadAes256CbcHmac256Algorithm.class.getName(), "encryptData", "Data encrypted.");
        return outBuffer;
    }

    @Override
    byte[] decryptData(byte[] cipherText) throws SQLServerException {
        return this.decryptData(cipherText, true);
    }

    private byte[] decryptData(byte[] cipherText, boolean hasAuthenticationTag) throws SQLServerException {
        int minimumCipherTextLength;
        assert (cipherText != null);
        byte[] iv = new byte[this.blockSizeInBytes];
        int n = minimumCipherTextLength = hasAuthenticationTag ? this.minimumCipherTextLengthInBytesWithAuthenticationTag : this.minimumCipherTextLengthInBytesNoAuthenticationTag;
        if (cipherText.length < minimumCipherTextLength) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidCipherTextSize"));
            Object[] msgArgs = new Object[]{cipherText.length, minimumCipherTextLength};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        int startIndex = 0;
        if (cipherText[startIndex] != this.algorithmVersion) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidAlgorithmVersion"));
            Object[] msgArgs = new Object[]{String.format("%02X ", cipherText[startIndex]), String.format("%02X ", this.algorithmVersion)};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        ++startIndex;
        int authenticationTagOffset = 0;
        if (hasAuthenticationTag) {
            authenticationTagOffset = startIndex;
            startIndex += 32;
        }
        System.arraycopy(cipherText, startIndex, iv, 0, iv.length);
        int cipherTextOffset = startIndex += iv.length;
        int cipherTextCount = cipherText.length - startIndex;
        if (hasAuthenticationTag) {
            byte[] authenticationTag;
            try {
                authenticationTag = this.prepareAuthenticationTag(iv, cipherText, cipherTextOffset, cipherTextCount);
            }
            catch (InvalidKeyException | NoSuchAlgorithmException e) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_DecryptionFailed"));
                Object[] msgArgs = new Object[]{e.getMessage()};
                throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
            }
            if (!SQLServerSecurityUtility.compareBytes(authenticationTag, cipherText, authenticationTagOffset, cipherTextCount)) {
                throw new SQLServerException((Object)this, SQLServerException.getErrString("R_InvalidAuthenticationTag"), null, 0, false);
            }
        }
        return this.decryptData(iv, cipherText, cipherTextOffset, cipherTextCount);
    }

    private byte[] decryptData(byte[] iv, byte[] cipherText, int offset, int count) throws SQLServerException {
        aeLogger.entering(SQLServerAeadAes256CbcHmac256Algorithm.class.getName(), "decryptData", "Decrypting data.");
        assert (cipherText != null);
        assert (iv != null);
        byte[] plainText = null;
        SecretKeySpec skeySpec = new SecretKeySpec(this.columnEncryptionkey.getEncryptionKey(), "AES");
        IvParameterSpec ivector = new IvParameterSpec(iv);
        try {
            Cipher decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            decryptCipher.init(2, (Key)skeySpec, ivector);
            plainText = decryptCipher.doFinal(cipherText, offset, count);
        }
        catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_DecryptionFailed"));
            Object[] msgArgs = new Object[]{e.getMessage()};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        aeLogger.exiting(SQLServerAeadAes256CbcHmac256Algorithm.class.getName(), "decryptData", "Data decrypted.");
        return plainText;
    }

    private byte[] prepareAuthenticationTag(byte[] iv, byte[] cipherText, int offset, int length) throws NoSuchAlgorithmException, InvalidKeyException {
        assert (cipherText != null);
        byte[] authenticationTag = new byte[32];
        Mac hmac = Mac.getInstance(HMAC_SHA_256);
        SecretKeySpec key = new SecretKeySpec(this.columnEncryptionkey.getMacKey(), HMAC_SHA_256);
        hmac.init(key);
        hmac.update(this.version, 0, this.version.length);
        hmac.update(iv, 0, iv.length);
        hmac.update(cipherText, offset, length);
        hmac.update(this.versionSize, 0, this.version.length);
        byte[] computedHash = hmac.doFinal();
        System.arraycopy(computedHash, 0, authenticationTag, 0, authenticationTag.length);
        return authenticationTag;
    }
}

