/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.CertificateDetails;
import com.microsoft.sqlserver.jdbc.KeyStoreProviderCommon;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionKeyStoreProvider;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.Util;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class SQLServerColumnEncryptionJavaKeyStoreProvider
extends SQLServerColumnEncryptionKeyStoreProvider {
    String name = "MSSQL_JAVA_KEYSTORE";
    String keyStorePath = null;
    char[] keyStorePwd = null;
    private static final Logger javaKeyStoreLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionJavaKeyStoreProvider");

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public SQLServerColumnEncryptionJavaKeyStoreProvider(String keyStoreLocation, char[] keyStoreSecret) throws SQLServerException {
        javaKeyStoreLogger.entering(SQLServerColumnEncryptionJavaKeyStoreProvider.class.getName(), "SQLServerColumnEncryptionJavaKeyStoreProvider");
        if (null == keyStoreLocation || 0 == keyStoreLocation.length()) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidConnectionSetting"));
            Object[] msgArgs = new Object[]{"keyStoreLocation", keyStoreLocation};
            throw new SQLServerException(form.format(msgArgs), null);
        }
        this.keyStorePath = keyStoreLocation;
        if (javaKeyStoreLogger.isLoggable(Level.FINE)) {
            javaKeyStoreLogger.fine("Path of key store provider is set.");
        }
        if (null == keyStoreSecret) {
            keyStoreSecret = "".toCharArray();
        }
        this.keyStorePwd = new char[keyStoreSecret.length];
        System.arraycopy(keyStoreSecret, 0, this.keyStorePwd, 0, keyStoreSecret.length);
        if (javaKeyStoreLogger.isLoggable(Level.FINE)) {
            javaKeyStoreLogger.fine("Password for key store provider is set.");
        }
        javaKeyStoreLogger.exiting(SQLServerColumnEncryptionJavaKeyStoreProvider.class.getName(), "SQLServerColumnEncryptionJavaKeyStoreProvider");
    }

    @Override
    public byte[] decryptColumnEncryptionKey(String masterKeyPath, String encryptionAlgorithm, byte[] encryptedColumnEncryptionKey) throws SQLServerException {
        javaKeyStoreLogger.entering(SQLServerColumnEncryptionJavaKeyStoreProvider.class.getName(), "decryptColumnEncryptionKey", "Decrypting Column Encryption Key.");
        KeyStoreProviderCommon.validateNonEmptyMasterKeyPath(masterKeyPath);
        CertificateDetails certificateDetails = this.getCertificateDetails(masterKeyPath);
        byte[] plainCEK = KeyStoreProviderCommon.decryptColumnEncryptionKey(masterKeyPath, encryptionAlgorithm, encryptedColumnEncryptionKey, certificateDetails);
        javaKeyStoreLogger.exiting(SQLServerColumnEncryptionJavaKeyStoreProvider.class.getName(), "decryptColumnEncryptionKey", "Finished decrypting Column Encryption Key.");
        return plainCEK;
    }

    @Override
    public boolean verifyColumnMasterKeyMetadata(String masterKeyPath, boolean allowEnclaveComputations, byte[] signature) throws SQLServerException {
        if (!allowEnclaveComputations) {
            return false;
        }
        KeyStoreProviderCommon.validateNonEmptyMasterKeyPath(masterKeyPath);
        CertificateDetails certificateDetails = this.getCertificateDetails(masterKeyPath);
        byte[] signedHash = null;
        boolean isValid = false;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(this.name.toLowerCase().getBytes(StandardCharsets.UTF_16LE));
            md.update(masterKeyPath.toLowerCase().getBytes(StandardCharsets.UTF_16LE));
            md.update("true".getBytes(StandardCharsets.UTF_16LE));
            byte[] dataToVerify = md.digest();
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign((PrivateKey)certificateDetails.privateKey);
            sig.update(dataToVerify);
            signedHash = sig.sign();
            sig.initVerify(certificateDetails.certificate.getPublicKey());
            sig.update(dataToVerify);
            isValid = sig.verify(signature);
        }
        catch (NoSuchAlgorithmException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_NoSHA256Algorithm"), e);
        }
        catch (InvalidKeyException | SignatureException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_SignatureNotMatch"));
            Object[] msgArgs = new Object[]{Util.byteToHexDisplayString(signature), signedHash != null ? Util.byteToHexDisplayString(signedHash) : " ", masterKeyPath, ": " + e.getMessage()};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        if (!isValid) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_SignatureNotMatch"));
            Object[] msgArgs = new Object[]{Util.byteToHexDisplayString(signature), Util.byteToHexDisplayString(signedHash), masterKeyPath, ""};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        return isValid;
    }

    public byte[] signColumnMasterKeyMetadata(String masterKeyPath, boolean allowEnclaveComputations) throws SQLServerException {
        if (!allowEnclaveComputations) {
            return null;
        }
        KeyStoreProviderCommon.validateNonEmptyMasterKeyPath(masterKeyPath);
        CertificateDetails certificateDetails = this.getCertificateDetails(masterKeyPath);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(this.name.toLowerCase().getBytes(StandardCharsets.UTF_16LE));
            md.update(masterKeyPath.toLowerCase().getBytes(StandardCharsets.UTF_16LE));
            md.update("true".getBytes(StandardCharsets.UTF_16LE));
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign((PrivateKey)certificateDetails.privateKey);
            sig.update(md.digest());
            return sig.sign();
        }
        catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_NoSHA256Algorithm"), e);
        }
    }

    private CertificateDetails getCertificateDetails(String masterKeyPath) throws SQLServerException {
        FileInputStream fis = null;
        KeyStore keyStore = null;
        CertificateDetails certificateDetails = null;
        try {
            if (null == masterKeyPath || 0 == masterKeyPath.length()) {
                throw new SQLServerException(null, SQLServerException.getErrString("R_InvalidMasterKeyDetails"), null, 0, false);
            }
            try {
                keyStore = KeyStore.getInstance("JKS");
                fis = new FileInputStream(this.keyStorePath);
                keyStore.load(fis, this.keyStorePwd);
            }
            catch (IOException e) {
                if (null != fis) {
                    fis.close();
                }
                keyStore = KeyStore.getInstance("PKCS12");
                fis = new FileInputStream(this.keyStorePath);
                keyStore.load(fis, this.keyStorePwd);
            }
            certificateDetails = this.getCertificateDetailsByAlias(keyStore, masterKeyPath);
        }
        catch (FileNotFoundException fileNotFound) {
            throw new SQLServerException((Object)this, SQLServerException.getErrString("R_KeyStoreNotFound"), null, 0, false);
        }
        catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidKeyStoreFile"));
            Object[] msgArgs = new Object[]{this.keyStorePath};
            throw new SQLServerException(form.format(msgArgs), e);
        }
        finally {
            try {
                if (null != fis) {
                    fis.close();
                }
            }
            catch (IOException iOException) {}
        }
        if (certificateDetails == null) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_CertificateError"));
            Object[] msgArgs = new Object[]{masterKeyPath, this.name};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        return certificateDetails;
    }

    private CertificateDetails getCertificateDetailsByAlias(KeyStore keyStore, String alias) throws SQLServerException {
        try {
            X509Certificate publicCertificate = (X509Certificate)keyStore.getCertificate(alias);
            Key keyPrivate = keyStore.getKey(alias, this.keyStorePwd);
            if (null == publicCertificate) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_CertificateNotFoundForAlias"));
                Object[] msgArgs = new Object[]{alias, "MSSQL_JAVA_KEYSTORE"};
                throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
            }
            if (null == keyPrivate) {
                throw new UnrecoverableKeyException();
            }
            return new CertificateDetails(publicCertificate, keyPrivate);
        }
        catch (UnrecoverableKeyException unrecoverableKeyException) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_UnrecoverableKeyAE"));
            Object[] msgArgs = new Object[]{alias};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        catch (KeyStoreException | NoSuchAlgorithmException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_CertificateError"));
            Object[] msgArgs = new Object[]{alias, this.name};
            throw new SQLServerException(form.format(msgArgs), e);
        }
    }

    @Override
    public byte[] encryptColumnEncryptionKey(String masterKeyPath, String encryptionAlgorithm, byte[] plainTextColumnEncryptionKey) throws SQLServerException {
        javaKeyStoreLogger.entering(SQLServerColumnEncryptionJavaKeyStoreProvider.class.getName(), "encryptColumnEncryptionKey", "Encrypting Column Encryption Key.");
        byte[] version = KeyStoreProviderCommon.version;
        KeyStoreProviderCommon.validateNonEmptyMasterKeyPath(masterKeyPath);
        if (null == plainTextColumnEncryptionKey) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_NullColumnEncryptionKey"), null, 0, false);
        }
        if (0 == plainTextColumnEncryptionKey.length) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_EmptyColumnEncryptionKey"), null, 0, false);
        }
        KeyStoreProviderCommon.validateEncryptionAlgorithm(encryptionAlgorithm, true);
        CertificateDetails certificateDetails = this.getCertificateDetails(masterKeyPath);
        byte[] cipherText = this.encryptRSAOAEP(plainTextColumnEncryptionKey, certificateDetails);
        byte[] cipherTextLength = this.getLittleEndianBytesFromShort((short)cipherText.length);
        byte[] masterKeyPathBytes = masterKeyPath.toLowerCase().getBytes(StandardCharsets.UTF_16LE);
        byte[] keyPathLength = this.getLittleEndianBytesFromShort((short)masterKeyPathBytes.length);
        byte[] dataToSign = new byte[version.length + keyPathLength.length + cipherTextLength.length + masterKeyPathBytes.length + cipherText.length];
        int destinationPosition = version.length;
        System.arraycopy(version, 0, dataToSign, 0, version.length);
        System.arraycopy(keyPathLength, 0, dataToSign, destinationPosition, keyPathLength.length);
        System.arraycopy(cipherTextLength, 0, dataToSign, destinationPosition += keyPathLength.length, cipherTextLength.length);
        System.arraycopy(masterKeyPathBytes, 0, dataToSign, destinationPosition += cipherTextLength.length, masterKeyPathBytes.length);
        System.arraycopy(cipherText, 0, dataToSign, destinationPosition += masterKeyPathBytes.length, cipherText.length);
        byte[] signedHash = this.rsaSignHashedData(dataToSign, certificateDetails);
        int encryptedColumnEncryptionKeyLength = version.length + cipherTextLength.length + keyPathLength.length + cipherText.length + masterKeyPathBytes.length + signedHash.length;
        byte[] encryptedColumnEncryptionKey = new byte[encryptedColumnEncryptionKeyLength];
        int currentIndex = 0;
        System.arraycopy(version, 0, encryptedColumnEncryptionKey, currentIndex, version.length);
        System.arraycopy(keyPathLength, 0, encryptedColumnEncryptionKey, currentIndex += version.length, keyPathLength.length);
        System.arraycopy(cipherTextLength, 0, encryptedColumnEncryptionKey, currentIndex += keyPathLength.length, cipherTextLength.length);
        System.arraycopy(masterKeyPathBytes, 0, encryptedColumnEncryptionKey, currentIndex += cipherTextLength.length, masterKeyPathBytes.length);
        System.arraycopy(cipherText, 0, encryptedColumnEncryptionKey, currentIndex += masterKeyPathBytes.length, cipherText.length);
        System.arraycopy(signedHash, 0, encryptedColumnEncryptionKey, currentIndex += cipherText.length, signedHash.length);
        javaKeyStoreLogger.exiting(SQLServerColumnEncryptionJavaKeyStoreProvider.class.getName(), "encryptColumnEncryptionKey", "Finished encrypting Column Encryption Key.");
        return encryptedColumnEncryptionKey;
    }

    private byte[] encryptRSAOAEP(byte[] plainText, CertificateDetails certificateDetails) throws SQLServerException {
        byte[] cipherText = null;
        try {
            Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
            rsa.init(1, certificateDetails.certificate.getPublicKey());
            rsa.update(plainText);
            cipherText = rsa.doFinal();
        }
        catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_EncryptionFailed"));
            Object[] msgArgs = new Object[]{e.getMessage()};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        return cipherText;
    }

    private byte[] rsaSignHashedData(byte[] dataToSign, CertificateDetails certificateDetails) throws SQLServerException {
        byte[] signedHash = null;
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign((PrivateKey)certificateDetails.privateKey);
            signature.update(dataToSign);
            signedHash = signature.sign();
        }
        catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_EncryptionFailed"));
            Object[] msgArgs = new Object[]{e.getMessage()};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        return signedHash;
    }

    private byte[] getLittleEndianBytesFromShort(short value) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return byteBuffer.putShort(value).array();
    }

    private boolean rsaVerifySignature(byte[] dataToVerify, byte[] signature, CertificateDetails certificateDetails) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign((PrivateKey)certificateDetails.privateKey);
        sig.update(dataToVerify);
        byte[] signedHash = sig.sign();
        sig.initVerify(certificateDetails.certificate.getPublicKey());
        sig.update(dataToVerify);
        return sig.verify(signature);
    }
}

