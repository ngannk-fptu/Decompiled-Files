/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.CertificateDetails;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

class KeyStoreProviderCommon {
    static final String RSA_ENCRYPTION_ALGORITHM = "RSA_OAEP";
    static byte[] version = new byte[]{1};

    private KeyStoreProviderCommon() {
        throw new UnsupportedOperationException(SQLServerException.getErrString("R_notSupported"));
    }

    static void validateEncryptionAlgorithm(String encryptionAlgorithm, boolean isEncrypt) throws SQLServerException {
        String errString;
        String string = errString = isEncrypt ? "R_NullKeyEncryptionAlgorithm" : "R_NullKeyEncryptionAlgorithmInternal";
        if (null == encryptionAlgorithm) {
            throw new SQLServerException(null, SQLServerException.getErrString(errString), null, 0, false);
        }
        String string2 = errString = isEncrypt ? "R_InvalidKeyEncryptionAlgorithm" : "R_InvalidKeyEncryptionAlgorithmInternal";
        if (!RSA_ENCRYPTION_ALGORITHM.equalsIgnoreCase(encryptionAlgorithm.trim())) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString(errString));
            Object[] msgArgs = new Object[]{encryptionAlgorithm, RSA_ENCRYPTION_ALGORITHM};
            throw new SQLServerException(form.format(msgArgs), null);
        }
    }

    static void validateNonEmptyMasterKeyPath(String masterKeyPath) throws SQLServerException {
        if (null == masterKeyPath || masterKeyPath.trim().length() == 0) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_InvalidMasterKeyDetails"), null, 0, false);
        }
    }

    static byte[] decryptColumnEncryptionKey(String masterKeyPath, String encryptionAlgorithm, byte[] encryptedColumnEncryptionKey, CertificateDetails certificateDetails) throws SQLServerException {
        if (null == encryptedColumnEncryptionKey) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_NullEncryptedColumnEncryptionKey"), null, 0, false);
        }
        if (0 == encryptedColumnEncryptionKey.length) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_EmptyEncryptedColumnEncryptionKey"), null, 0, false);
        }
        KeyStoreProviderCommon.validateEncryptionAlgorithm(encryptionAlgorithm, false);
        int currentIndex = version.length;
        short keyPathLength = KeyStoreProviderCommon.convertTwoBytesToShort(encryptedColumnEncryptionKey, currentIndex);
        short cipherTextLength = KeyStoreProviderCommon.convertTwoBytesToShort(encryptedColumnEncryptionKey, currentIndex += 2);
        currentIndex += 2;
        int signatureLength = encryptedColumnEncryptionKey.length - (currentIndex += keyPathLength) - cipherTextLength;
        byte[] cipherText = new byte[cipherTextLength];
        System.arraycopy(encryptedColumnEncryptionKey, currentIndex, cipherText, 0, cipherTextLength);
        byte[] signature = new byte[signatureLength];
        System.arraycopy(encryptedColumnEncryptionKey, currentIndex += cipherTextLength, signature, 0, signatureLength);
        byte[] hash = new byte[encryptedColumnEncryptionKey.length - signature.length];
        System.arraycopy(encryptedColumnEncryptionKey, 0, hash, 0, encryptedColumnEncryptionKey.length - signature.length);
        if (!KeyStoreProviderCommon.verifyRSASignature(hash, signature, certificateDetails.certificate, masterKeyPath)) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidCertificateSignature"));
            Object[] msgArgs = new Object[]{masterKeyPath};
            throw new SQLServerException(form.format(msgArgs), null);
        }
        return KeyStoreProviderCommon.decryptRSAOAEP(cipherText, certificateDetails);
    }

    private static byte[] decryptRSAOAEP(byte[] cipherText, CertificateDetails certificateDetails) throws SQLServerException {
        byte[] plainCEK = null;
        try {
            Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
            rsa.init(2, certificateDetails.privateKey);
            rsa.update(cipherText);
            plainCEK = rsa.doFinal();
        }
        catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_CEKDecryptionFailed"));
            Object[] msgArgs = new Object[]{e.getMessage()};
            throw new SQLServerException(form.format(msgArgs), e);
        }
        return plainCEK;
    }

    static boolean verifyRSASignature(byte[] hash, byte[] signature, X509Certificate certificate, String masterKeyPath) throws SQLServerException {
        boolean verificationSuccess = false;
        try {
            Signature signVerify = Signature.getInstance("SHA256withRSA");
            signVerify.initVerify(certificate.getPublicKey());
            signVerify.update(hash);
            verificationSuccess = signVerify.verify(signature);
        }
        catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidCertificateSignature"));
            Object[] msgArgs = new Object[]{masterKeyPath};
            throw new SQLServerException(form.format(msgArgs), e);
        }
        return verificationSuccess;
    }

    private static short convertTwoBytesToShort(byte[] input, int index) throws SQLServerException {
        if (index + 1 >= input.length) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_ByteToShortConversion"), null, 0, false);
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(input[index]);
        byteBuffer.put(input[index + 1]);
        short shortVal = byteBuffer.getShort(0);
        return shortVal;
    }
}

