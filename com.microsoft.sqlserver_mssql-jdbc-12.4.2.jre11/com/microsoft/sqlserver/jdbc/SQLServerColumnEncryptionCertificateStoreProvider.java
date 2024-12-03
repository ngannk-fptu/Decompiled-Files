/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.AuthenticationJNI;
import com.microsoft.sqlserver.jdbc.DLLException;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionKeyStoreProvider;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.util.Locale;
import java.util.logging.Logger;

public final class SQLServerColumnEncryptionCertificateStoreProvider
extends SQLServerColumnEncryptionKeyStoreProvider {
    private static final Logger windowsCertificateStoreLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionCertificateStoreProvider");
    static boolean isWindows = System.getProperty("os.name").toLowerCase(Locale.ENGLISH).startsWith("windows");
    String name = "MSSQL_CERTIFICATE_STORE";
    static final String LOCAL_MACHINE_DIRECTORY = "LocalMachine";
    static final String CURRENT_USER_DIRECTORY = "CurrentUser";
    static final String MY_CERTIFICATE_STORE = "My";

    public SQLServerColumnEncryptionCertificateStoreProvider() {
        windowsCertificateStoreLogger.entering(SQLServerColumnEncryptionCertificateStoreProvider.class.getName(), "SQLServerColumnEncryptionCertificateStoreProvider");
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public byte[] encryptColumnEncryptionKey(String masterKeyPath, String encryptionAlgorithm, byte[] plainTextColumnEncryptionKey) throws SQLServerException {
        throw new SQLServerException(null, SQLServerException.getErrString("R_InvalidWindowsCertificateStoreEncryption"), null, 0, false);
    }

    @Override
    public byte[] decryptColumnEncryptionKey(String masterKeyPath, String encryptionAlgorithm, byte[] encryptedColumnEncryptionKey) throws SQLServerException {
        windowsCertificateStoreLogger.entering(SQLServerColumnEncryptionCertificateStoreProvider.class.getName(), "decryptColumnEncryptionKey", "Decrypting Column Encryption Key.");
        if (!isWindows) {
            throw new SQLServerException(SQLServerException.getErrString("R_notSupported"), null);
        }
        byte[] plainCek = this.decryptColumnEncryptionKeyWindows(masterKeyPath, encryptionAlgorithm, encryptedColumnEncryptionKey);
        windowsCertificateStoreLogger.exiting(SQLServerColumnEncryptionCertificateStoreProvider.class.getName(), "decryptColumnEncryptionKey", "Finished decrypting Column Encryption Key.");
        return plainCek;
    }

    @Override
    public boolean verifyColumnMasterKeyMetadata(String masterKeyPath, boolean allowEnclaveComputations, byte[] signature) throws SQLServerException {
        try {
            return AuthenticationJNI.VerifyColumnMasterKeyMetadata(masterKeyPath, allowEnclaveComputations, signature);
        }
        catch (DLLException e) {
            DLLException.buildException(e.getErrCode(), e.getParam1(), e.getParam2(), e.getParam3());
            return false;
        }
    }

    private byte[] decryptColumnEncryptionKeyWindows(String masterKeyPath, String encryptionAlgorithm, byte[] encryptedColumnEncryptionKey) throws SQLServerException {
        try {
            return AuthenticationJNI.DecryptColumnEncryptionKey(masterKeyPath, encryptionAlgorithm, encryptedColumnEncryptionKey);
        }
        catch (DLLException e) {
            DLLException.buildException(e.getErrCode(), e.getParam1(), e.getParam2(), e.getParam3());
            return null;
        }
    }
}

