/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.azure.core.credential.AccessToken
 *  com.azure.core.credential.TokenRequestContext
 *  com.azure.identity.DefaultAzureCredential
 *  com.azure.identity.DefaultAzureCredentialBuilder
 *  com.azure.identity.ManagedIdentityCredential
 *  com.azure.identity.ManagedIdentityCredentialBuilder
 */
package com.microsoft.sqlserver.jdbc;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.identity.ManagedIdentityCredential;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.microsoft.sqlserver.jdbc.CryptoMetadata;
import com.microsoft.sqlserver.jdbc.EncryptionKeyInfo;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionKeyStoreProvider;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerEncryptionAlgorithm;
import com.microsoft.sqlserver.jdbc.SQLServerEncryptionAlgorithmFactoryList;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import com.microsoft.sqlserver.jdbc.SQLServerSymmetricKey;
import com.microsoft.sqlserver.jdbc.SQLServerSymmetricKeyCache;
import com.microsoft.sqlserver.jdbc.SqlAuthenticationToken;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

class SQLServerSecurityUtility {
    private static final Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.SQLServerSecurityUtility");
    static final int GONE = 410;
    static final int TOO_MANY_RESQUESTS = 429;
    static final int NOT_FOUND = 404;
    static final int INTERNAL_SERVER_ERROR = 500;
    static final int NETWORK_CONNECT_TIMEOUT_ERROR = 599;
    static final String WINDOWS_KEY_STORE_NAME = "MSSQL_CERTIFICATE_STORE";
    private static final String INTELLIJ_KEEPASS_PASS = "INTELLIJ_KEEPASS_PATH";
    private static final String ADDITIONALLY_ALLOWED_TENANTS = "ADDITIONALLY_ALLOWED_TENANTS";

    private SQLServerSecurityUtility() {
        throw new UnsupportedOperationException(SQLServerException.getErrString("R_notSupported"));
    }

    static byte[] getHMACWithSHA256(byte[] plainText, byte[] key, int length) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] hash = new byte[length];
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec ivkeySpec = new SecretKeySpec(key, "HmacSHA256");
        mac.init(ivkeySpec);
        byte[] computedHash = mac.doFinal(plainText);
        System.arraycopy(computedHash, 0, hash, 0, hash.length);
        return hash;
    }

    static boolean compareBytes(byte[] buffer1, byte[] buffer2, int buffer2Index, int lengthToCompare) {
        if (null == buffer1 || null == buffer2) {
            return false;
        }
        if (buffer2.length - buffer2Index < lengthToCompare) {
            return false;
        }
        for (int index = 0; index < buffer1.length && index < lengthToCompare; ++index) {
            if (buffer1[index] == buffer2[buffer2Index + index]) continue;
            return false;
        }
        return true;
    }

    static SQLServerColumnEncryptionKeyStoreProvider getColumnEncryptionKeyStoreProvider(String providerName, SQLServerConnection connection, SQLServerStatement statement) throws SQLServerException {
        assert (providerName != null && providerName.length() != 0) : "Provider name should not be null or empty";
        if (statement != null && statement.hasColumnEncryptionKeyStoreProvidersRegistered()) {
            return statement.getColumnEncryptionKeyStoreProvider(providerName);
        }
        return connection.getColumnEncryptionKeyStoreProviderOnConnection(providerName);
    }

    static boolean shouldUseInstanceLevelProviderFlow(String keyStoreName, SQLServerConnection connection, SQLServerStatement statement) {
        return !keyStoreName.equalsIgnoreCase(WINDOWS_KEY_STORE_NAME) && (connection.hasConnectionColumnEncryptionKeyStoreProvidersRegistered() || null != statement && statement.hasColumnEncryptionKeyStoreProvidersRegistered());
    }

    static SQLServerSymmetricKey getKeyFromLocalProviders(EncryptionKeyInfo keyInfo, SQLServerConnection connection, SQLServerStatement statement) throws SQLServerException {
        String serverName = connection.getTrustedServerNameAE();
        assert (null != serverName) : "serverName should not be null in getKey.";
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Checking trusted master key path...");
        }
        Boolean[] hasEntry = new Boolean[1];
        List<String> trustedKeyPaths = SQLServerConnection.getColumnEncryptionTrustedMasterKeyPaths(serverName, hasEntry);
        if (hasEntry[0].booleanValue() && (null == trustedKeyPaths || trustedKeyPaths.isEmpty() || !trustedKeyPaths.contains(keyInfo.keyPath))) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_UntrustedKeyPath"));
            Object[] msgArgs = new Object[]{keyInfo.keyPath, serverName};
            throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
        }
        SQLServerException lastException = null;
        SQLServerColumnEncryptionKeyStoreProvider provider = null;
        byte[] plaintextKey = null;
        try {
            provider = SQLServerSecurityUtility.getColumnEncryptionKeyStoreProvider(keyInfo.keyStoreName, connection, statement);
            plaintextKey = provider.decryptColumnEncryptionKey(keyInfo.keyPath, keyInfo.algorithmName, keyInfo.encryptedKey);
        }
        catch (SQLServerException e) {
            lastException = e;
        }
        if (null == plaintextKey) {
            if (null != lastException) {
                throw lastException;
            }
            throw new SQLServerException(null, SQLServerException.getErrString("R_CEKDecryptionFailed"), null, 0, false);
        }
        return new SQLServerSymmetricKey(plaintextKey);
    }

    static byte[] encryptWithKey(byte[] plainText, CryptoMetadata md, SQLServerConnection connection, SQLServerStatement statement) throws SQLServerException {
        String serverName = connection.getTrustedServerNameAE();
        assert (serverName != null) : "Server name should not be null in EncryptWithKey";
        if (!md.isAlgorithmInitialized()) {
            SQLServerSecurityUtility.decryptSymmetricKey(md, connection, statement);
        }
        assert (md.isAlgorithmInitialized());
        byte[] cipherText = md.cipherAlgorithm.encryptData(plainText);
        if (null == cipherText || 0 == cipherText.length) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_NullCipherTextAE"), null, 0, false);
        }
        return cipherText;
    }

    private static String validateAndGetEncryptionAlgorithmName(byte cipherAlgorithmId, String cipherAlgorithmName) throws SQLServerException {
        if (2 != cipherAlgorithmId) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_CustomCipherAlgorithmNotSupportedAE"), null, 0, false);
        }
        return "AEAD_AES_256_CBC_HMAC_SHA256";
    }

    static void decryptSymmetricKey(CryptoMetadata md, SQLServerConnection connection, SQLServerStatement statement) throws SQLServerException {
        assert (null != md) : "md should not be null in DecryptSymmetricKey.";
        assert (null != md.cekTableEntry) : "md.EncryptionInfo should not be null in DecryptSymmetricKey.";
        assert (null != md.cekTableEntry.columnEncryptionKeyValues) : "md.EncryptionInfo.ColumnEncryptionKeyValues should not be null in DecryptSymmetricKey.";
        SQLServerSymmetricKey symKey = null;
        EncryptionKeyInfo encryptionkeyInfoChosen = null;
        SQLServerSymmetricKeyCache globalCEKCache = SQLServerSymmetricKeyCache.getInstance();
        Iterator<EncryptionKeyInfo> it = md.cekTableEntry.columnEncryptionKeyValues.iterator();
        SQLServerException lastException = null;
        while (it.hasNext()) {
            EncryptionKeyInfo keyInfo = it.next();
            try {
                symKey = SQLServerSecurityUtility.shouldUseInstanceLevelProviderFlow(keyInfo.keyStoreName, connection, statement) ? SQLServerSecurityUtility.getKeyFromLocalProviders(keyInfo, connection, statement) : globalCEKCache.getKey(keyInfo, connection);
                if (null == symKey) continue;
                encryptionkeyInfoChosen = keyInfo;
                break;
            }
            catch (SQLServerException e) {
                lastException = e;
            }
        }
        if (null == symKey) {
            if (null != lastException) {
                throw lastException;
            }
            throw new SQLServerException(null, SQLServerException.getErrString("R_CEKDecryptionFailed"), null, 0, false);
        }
        md.cipherAlgorithm = null;
        SQLServerEncryptionAlgorithm cipherAlgorithm = null;
        String algorithmName = SQLServerSecurityUtility.validateAndGetEncryptionAlgorithmName(md.cipherAlgorithmId, md.cipherAlgorithmName);
        cipherAlgorithm = SQLServerEncryptionAlgorithmFactoryList.getInstance().getAlgorithm(symKey, md.encryptionType, algorithmName);
        assert (null != cipherAlgorithm) : "Cipher algorithm cannot be null in DecryptSymmetricKey";
        md.cipherAlgorithm = cipherAlgorithm;
        md.encryptionKeyInfo = encryptionkeyInfoChosen;
    }

    static byte[] decryptWithKey(byte[] cipherText, CryptoMetadata md, SQLServerConnection connection, SQLServerStatement statement) throws SQLServerException {
        String serverName = connection.getTrustedServerNameAE();
        assert (null != serverName) : "serverName should not be null in DecryptWithKey.";
        if (!md.isAlgorithmInitialized()) {
            SQLServerSecurityUtility.decryptSymmetricKey(md, connection, statement);
        }
        assert (md.isAlgorithmInitialized()) : "Decryption Algorithm is not initialized";
        byte[] plainText = md.cipherAlgorithm.decryptData(cipherText);
        if (null == plainText) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_PlainTextNullAE"), null, 0, false);
        }
        return plainText;
    }

    static void verifyColumnMasterKeyMetadata(SQLServerConnection connection, SQLServerStatement statement, String keyStoreName, String keyPath, String serverName, boolean isEnclaveEnabled, byte[] cmkSignature) throws SQLServerException {
        Boolean[] hasEntry = new Boolean[1];
        List<String> trustedKeyPaths = SQLServerConnection.getColumnEncryptionTrustedMasterKeyPaths(serverName, hasEntry);
        if (hasEntry[0].booleanValue() && (null == trustedKeyPaths || trustedKeyPaths.isEmpty() || !trustedKeyPaths.contains(keyPath))) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_UntrustedKeyPath"));
            Object[] msgArgs = new Object[]{keyPath, serverName};
            throw new SQLServerException(form.format(msgArgs), null);
        }
        SQLServerColumnEncryptionKeyStoreProvider provider = null;
        provider = SQLServerSecurityUtility.shouldUseInstanceLevelProviderFlow(keyStoreName, connection, statement) ? SQLServerSecurityUtility.getColumnEncryptionKeyStoreProvider(keyStoreName, connection, statement) : connection.getSystemOrGlobalColumnEncryptionKeyStoreProvider(keyStoreName);
        if (!provider.verifyColumnMasterKeyMetadata(keyPath, isEnclaveEnabled, cmkSignature)) {
            throw new SQLServerException(SQLServerException.getErrString("R_VerifySignatureFailed"), null);
        }
    }

    static SqlAuthenticationToken getManagedIdentityCredAuthToken(String resource, String managedIdentityClientId) throws SQLServerException {
        ManagedIdentityCredential mic = null;
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Getting Managed Identity authentication token for: " + managedIdentityClientId);
        }
        mic = null != managedIdentityClientId && !managedIdentityClientId.isEmpty() ? new ManagedIdentityCredentialBuilder().clientId(managedIdentityClientId).build() : new ManagedIdentityCredentialBuilder().build();
        TokenRequestContext tokenRequestContext = new TokenRequestContext();
        Object scope = resource.endsWith("/.default") ? resource : resource + "/.default";
        tokenRequestContext.setScopes(Arrays.asList(scope));
        SqlAuthenticationToken sqlFedAuthToken = null;
        Optional accessTokenOptional = mic.getToken(tokenRequestContext).blockOptional();
        if (!accessTokenOptional.isPresent()) {
            throw new SQLServerException(SQLServerException.getErrString("R_ManagedIdentityTokenAcquisitionFail"), null);
        }
        AccessToken accessToken = (AccessToken)accessTokenOptional.get();
        sqlFedAuthToken = new SqlAuthenticationToken(accessToken.getToken(), accessToken.getExpiresAt().toEpochSecond());
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Got fedAuth token, expiry: " + sqlFedAuthToken.getExpiresOn().toString());
        }
        return sqlFedAuthToken;
    }

    static SqlAuthenticationToken getDefaultAzureCredAuthToken(String resource, String managedIdentityClientId) throws SQLServerException {
        String intellijKeepassPath = System.getenv(INTELLIJ_KEEPASS_PASS);
        String[] additionallyAllowedTenants = SQLServerSecurityUtility.getAdditonallyAllowedTenants();
        DefaultAzureCredentialBuilder dacBuilder = new DefaultAzureCredentialBuilder();
        DefaultAzureCredential dac = null;
        if (null != managedIdentityClientId && !managedIdentityClientId.isEmpty()) {
            dacBuilder.managedIdentityClientId(managedIdentityClientId);
        }
        if (null != intellijKeepassPath && !intellijKeepassPath.isEmpty()) {
            dacBuilder.intelliJKeePassDatabasePath(intellijKeepassPath);
        }
        if (null != additionallyAllowedTenants && additionallyAllowedTenants.length != 0) {
            dacBuilder.additionallyAllowedTenants(additionallyAllowedTenants);
        }
        dac = dacBuilder.build();
        TokenRequestContext tokenRequestContext = new TokenRequestContext();
        Object scope = resource.endsWith("/.default") ? resource : resource + "/.default";
        tokenRequestContext.setScopes(Arrays.asList(scope));
        SqlAuthenticationToken sqlFedAuthToken = null;
        Optional accessTokenOptional = dac.getToken(tokenRequestContext).blockOptional();
        if (!accessTokenOptional.isPresent()) {
            throw new SQLServerException(SQLServerException.getErrString("R_ManagedIdentityTokenAcquisitionFail"), null);
        }
        AccessToken accessToken = (AccessToken)accessTokenOptional.get();
        sqlFedAuthToken = new SqlAuthenticationToken(accessToken.getToken(), accessToken.getExpiresAt().toEpochSecond());
        return sqlFedAuthToken;
    }

    private static String[] getAdditonallyAllowedTenants() {
        String additonallyAllowedTenants = System.getenv(ADDITIONALLY_ALLOWED_TENANTS);
        if (null != additonallyAllowedTenants && !additonallyAllowedTenants.isEmpty()) {
            return System.getenv(ADDITIONALLY_ALLOWED_TENANTS).split(",");
        }
        return null;
    }
}

