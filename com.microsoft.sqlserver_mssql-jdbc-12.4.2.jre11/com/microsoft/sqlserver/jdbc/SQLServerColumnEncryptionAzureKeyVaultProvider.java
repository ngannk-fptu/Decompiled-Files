/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.azure.core.credential.TokenCredential
 *  com.azure.core.http.HttpPipeline
 *  com.azure.identity.ManagedIdentityCredentialBuilder
 *  com.azure.security.keyvault.keys.KeyClient
 *  com.azure.security.keyvault.keys.KeyClientBuilder
 *  com.azure.security.keyvault.keys.cryptography.CryptographyClient
 *  com.azure.security.keyvault.keys.cryptography.CryptographyClientBuilder
 *  com.azure.security.keyvault.keys.cryptography.models.KeyWrapAlgorithm
 *  com.azure.security.keyvault.keys.cryptography.models.SignResult
 *  com.azure.security.keyvault.keys.cryptography.models.SignatureAlgorithm
 *  com.azure.security.keyvault.keys.cryptography.models.UnwrapResult
 *  com.azure.security.keyvault.keys.cryptography.models.VerifyResult
 *  com.azure.security.keyvault.keys.cryptography.models.WrapResult
 *  com.azure.security.keyvault.keys.models.KeyType
 *  com.azure.security.keyvault.keys.models.KeyVaultKey
 */
package com.microsoft.sqlserver.jdbc;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpPipeline;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.azure.security.keyvault.keys.KeyClient;
import com.azure.security.keyvault.keys.KeyClientBuilder;
import com.azure.security.keyvault.keys.cryptography.CryptographyClient;
import com.azure.security.keyvault.keys.cryptography.CryptographyClientBuilder;
import com.azure.security.keyvault.keys.cryptography.models.KeyWrapAlgorithm;
import com.azure.security.keyvault.keys.cryptography.models.SignResult;
import com.azure.security.keyvault.keys.cryptography.models.SignatureAlgorithm;
import com.azure.security.keyvault.keys.cryptography.models.UnwrapResult;
import com.azure.security.keyvault.keys.cryptography.models.VerifyResult;
import com.azure.security.keyvault.keys.cryptography.models.WrapResult;
import com.azure.security.keyvault.keys.models.KeyType;
import com.azure.security.keyvault.keys.models.KeyVaultKey;
import com.microsoft.sqlserver.jdbc.CMKMetadataSignatureInfo;
import com.microsoft.sqlserver.jdbc.KeyStoreProviderCommon;
import com.microsoft.sqlserver.jdbc.KeyVaultHttpPipelineBuilder;
import com.microsoft.sqlserver.jdbc.KeyVaultTokenCredential;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionKeyStoreProvider;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerKeyVaultAuthenticationCallback;
import com.microsoft.sqlserver.jdbc.SimpleTtlCache;
import com.microsoft.sqlserver.jdbc.Util;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLServerColumnEncryptionAzureKeyVaultProvider
extends SQLServerColumnEncryptionKeyStoreProvider {
    private static final Logger akvLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionAzureKeyVaultProvider");
    private static final int KEY_NAME_INDEX = 4;
    private static final int KEY_URL_SPLIT_LENGTH_WITH_VERSION = 6;
    private static final String KEY_URL_DELIMITER = "/";
    private static final String NULL_VALUE = "R_NullValue";
    private HttpPipeline keyVaultPipeline;
    private KeyVaultTokenCredential keyVaultTokenCredential;
    String name = "AZURE_KEY_VAULT";
    private static final String MSSQL_JDBC_PROPERTIES = "mssql-jdbc.properties";
    private static final String AKV_TRUSTED_ENDPOINTS_KEYWORD = "AKVTrustedEndpoints";
    private static final String RSA_ENCRYPTION_ALGORITHM_WITH_OAEP_FOR_AKV = "RSA-OAEP";
    private static final String SHA_256 = "SHA-256";
    private static final List<String> akvTrustedEndpoints = SQLServerColumnEncryptionAzureKeyVaultProvider.getTrustedEndpoints();
    private static final byte[] firstVersion = new byte[]{1};
    private Map<String, KeyClient> cachedKeyClients = new ConcurrentHashMap<String, KeyClient>();
    private Map<String, CryptographyClient> cachedCryptographyClients = new ConcurrentHashMap<String, CryptographyClient>();
    private TokenCredential credential;
    private final SimpleTtlCache<String, byte[]> columnEncryptionKeyCache = new SimpleTtlCache();
    private final SimpleTtlCache<CMKMetadataSignatureInfo, Boolean> cmkMetadataSignatureVerificationCache = new SimpleTtlCache(Duration.ofDays(10L));

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Duration getColumnEncryptionKeyCacheTtl() {
        return this.columnEncryptionKeyCache.getCacheTtl();
    }

    @Override
    public void setColumnEncryptionCacheTtl(Duration duration) {
        this.columnEncryptionKeyCache.setCacheTtl(duration);
    }

    public SQLServerColumnEncryptionAzureKeyVaultProvider(String clientId, String clientKey) throws SQLServerException {
        if (null == clientId || clientId.isEmpty()) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString(NULL_VALUE));
            Object[] msgArgs1 = new Object[]{"Client ID"};
            throw new SQLServerException(form.format(msgArgs1), null);
        }
        if (null == clientKey || clientKey.isEmpty()) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString(NULL_VALUE));
            Object[] msgArgs1 = new Object[]{"Client Key"};
            throw new SQLServerException(form.format(msgArgs1), null);
        }
        this.keyVaultTokenCredential = new KeyVaultTokenCredential(clientId, clientKey);
        this.keyVaultPipeline = new KeyVaultHttpPipelineBuilder().credential(this.keyVaultTokenCredential).buildPipeline();
    }

    SQLServerColumnEncryptionAzureKeyVaultProvider() throws SQLServerException {
        this.setCredential((TokenCredential)new ManagedIdentityCredentialBuilder().build());
    }

    SQLServerColumnEncryptionAzureKeyVaultProvider(String clientId) throws SQLServerException {
        if (null == clientId || clientId.isEmpty()) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString(NULL_VALUE));
            Object[] msgArgs1 = new Object[]{"Client ID"};
            throw new SQLServerException(form.format(msgArgs1), null);
        }
        this.setCredential((TokenCredential)new ManagedIdentityCredentialBuilder().clientId(clientId).build());
    }

    public SQLServerColumnEncryptionAzureKeyVaultProvider(TokenCredential tokenCredential) throws SQLServerException {
        if (null == tokenCredential) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString(NULL_VALUE));
            Object[] msgArgs1 = new Object[]{"Token Credential"};
            throw new SQLServerException(form.format(msgArgs1), null);
        }
        this.setCredential(tokenCredential);
    }

    @Deprecated(since="12.1.0", forRemoval=true)
    public SQLServerColumnEncryptionAzureKeyVaultProvider(SQLServerKeyVaultAuthenticationCallback authenticationCallback) throws SQLServerException {
        if (null == authenticationCallback) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString(NULL_VALUE));
            Object[] msgArgs1 = new Object[]{"SQLServerKeyVaultAuthenticationCallback"};
            throw new SQLServerException(form.format(msgArgs1), null);
        }
        this.keyVaultTokenCredential = new KeyVaultTokenCredential(authenticationCallback);
        this.keyVaultPipeline = new KeyVaultHttpPipelineBuilder().credential(this.keyVaultTokenCredential).buildPipeline();
    }

    private void setCredential(TokenCredential credential) throws SQLServerException {
        if (null == credential) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString(NULL_VALUE));
            Object[] msgArgs1 = new Object[]{"Credential"};
            throw new SQLServerException(form.format(msgArgs1), null);
        }
        this.credential = credential;
    }

    @Override
    public byte[] decryptColumnEncryptionKey(String masterKeyPath, String encryptionAlgorithm, byte[] encryptedColumnEncryptionKey) throws SQLServerException {
        this.validateNonEmptyAKVPath(masterKeyPath);
        if (null == encryptedColumnEncryptionKey) {
            throw new SQLServerException(SQLServerException.getErrString("R_NullEncryptedColumnEncryptionKey"), null);
        }
        if (0 == encryptedColumnEncryptionKey.length) {
            throw new SQLServerException(SQLServerException.getErrString("R_EmptyEncryptedColumnEncryptionKey"), null);
        }
        KeyWrapAlgorithm keyWrapAlgorithm = this.validateEncryptionAlgorithm(encryptionAlgorithm);
        int keySizeInBytes = this.getAKVKeySize(masterKeyPath);
        if (encryptedColumnEncryptionKey[0] != firstVersion[0]) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidEcryptionAlgorithmVersion"));
            Object[] msgArgs = new Object[]{String.format("%02X ", encryptedColumnEncryptionKey[0]), String.format("%02X ", firstVersion[0])};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        boolean allowCache = false;
        String encryptedColumnEncryptionKeyHexString = Util.byteToHexDisplayString(encryptedColumnEncryptionKey);
        if (this.columnEncryptionKeyCache.getCacheTtl().getSeconds() > 0L) {
            allowCache = true;
            if (this.columnEncryptionKeyCache.contains(encryptedColumnEncryptionKeyHexString)) {
                return this.columnEncryptionKeyCache.get(encryptedColumnEncryptionKeyHexString);
            }
        }
        int currentIndex = firstVersion.length;
        short keyPathLength = this.convertTwoBytesToShort(encryptedColumnEncryptionKey, currentIndex);
        short cipherTextLength = this.convertTwoBytesToShort(encryptedColumnEncryptionKey, currentIndex += 2);
        currentIndex += 2;
        currentIndex += keyPathLength;
        if (cipherTextLength != keySizeInBytes) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_AKVKeyLengthError"));
            Object[] msgArgs = new Object[]{cipherTextLength, keySizeInBytes, masterKeyPath};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        int signatureLength = encryptedColumnEncryptionKey.length - currentIndex - cipherTextLength;
        if (signatureLength != keySizeInBytes) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_AKVSignatureLengthError"));
            Object[] msgArgs = new Object[]{signatureLength, keySizeInBytes, masterKeyPath};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        byte[] cipherText = new byte[cipherTextLength];
        System.arraycopy(encryptedColumnEncryptionKey, currentIndex, cipherText, 0, cipherTextLength);
        byte[] signature = new byte[signatureLength];
        System.arraycopy(encryptedColumnEncryptionKey, currentIndex += cipherTextLength, signature, 0, signatureLength);
        byte[] hash = new byte[encryptedColumnEncryptionKey.length - signature.length];
        System.arraycopy(encryptedColumnEncryptionKey, 0, hash, 0, encryptedColumnEncryptionKey.length - signature.length);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(SHA_256);
        }
        catch (NoSuchAlgorithmException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_NoSHA256Algorithm"), e);
        }
        md.update(hash);
        byte[] dataToVerify = md.digest();
        if (null == dataToVerify) {
            throw new SQLServerException(SQLServerException.getErrString("R_HashNull"), null);
        }
        if (!this.azureKeyVaultVerifySignature(dataToVerify, signature, masterKeyPath)) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_CEKSignatureNotMatchCMK"));
            Object[] msgArgs = new Object[]{Util.byteToHexDisplayString(signature), masterKeyPath};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        byte[] decryptedCEK = this.azureKeyVaultUnWrap(masterKeyPath, keyWrapAlgorithm, cipherText);
        if (allowCache) {
            this.columnEncryptionKeyCache.put(encryptedColumnEncryptionKeyHexString, decryptedCEK);
        }
        return decryptedCEK;
    }

    private short convertTwoBytesToShort(byte[] input, int index) throws SQLServerException {
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

    @Override
    public byte[] encryptColumnEncryptionKey(String masterKeyPath, String encryptionAlgorithm, byte[] columnEncryptionKey) throws SQLServerException {
        this.validateNonEmptyAKVPath(masterKeyPath);
        if (null == columnEncryptionKey) {
            throw new SQLServerException(SQLServerException.getErrString("R_NullColumnEncryptionKey"), null);
        }
        if (0 == columnEncryptionKey.length) {
            throw new SQLServerException(SQLServerException.getErrString("R_EmptyCEK"), null);
        }
        KeyWrapAlgorithm keyWrapAlgorithm = this.validateEncryptionAlgorithm(encryptionAlgorithm);
        int keySizeInBytes = this.getAKVKeySize(masterKeyPath);
        byte[] version = new byte[]{firstVersion[0]};
        byte[] masterKeyPathBytes = masterKeyPath.toLowerCase(Locale.ENGLISH).getBytes(StandardCharsets.UTF_16LE);
        byte[] keyPathLength = new byte[]{(byte)((short)masterKeyPathBytes.length & 0xFF), (byte)((short)masterKeyPathBytes.length >> 8 & 0xFF)};
        byte[] cipherText = this.azureKeyVaultWrap(masterKeyPath, keyWrapAlgorithm, columnEncryptionKey);
        byte[] cipherTextLength = new byte[]{(byte)((short)cipherText.length & 0xFF), (byte)((short)cipherText.length >> 8 & 0xFF)};
        if (cipherText.length != keySizeInBytes) {
            throw new SQLServerException(SQLServerException.getErrString("R_CipherTextLengthNotMatchRSASize"), null);
        }
        byte[] dataToHash = new byte[version.length + keyPathLength.length + cipherTextLength.length + masterKeyPathBytes.length + cipherText.length];
        int destinationPosition = version.length;
        System.arraycopy(version, 0, dataToHash, 0, version.length);
        System.arraycopy(keyPathLength, 0, dataToHash, destinationPosition, keyPathLength.length);
        System.arraycopy(cipherTextLength, 0, dataToHash, destinationPosition += keyPathLength.length, cipherTextLength.length);
        System.arraycopy(masterKeyPathBytes, 0, dataToHash, destinationPosition += cipherTextLength.length, masterKeyPathBytes.length);
        System.arraycopy(cipherText, 0, dataToHash, destinationPosition += masterKeyPathBytes.length, cipherText.length);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(SHA_256);
        }
        catch (NoSuchAlgorithmException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_NoSHA256Algorithm"), e);
        }
        md.update(dataToHash);
        byte[] dataToSign = md.digest();
        byte[] signedHash = this.azureKeyVaultSignHashedData(dataToSign, masterKeyPath);
        if (signedHash.length != keySizeInBytes) {
            throw new SQLServerException(SQLServerException.getErrString("R_SignedHashLengthError"), null);
        }
        if (!this.azureKeyVaultVerifySignature(dataToSign, signedHash, masterKeyPath)) {
            throw new SQLServerException(SQLServerException.getErrString("R_InvalidSignatureComputed"), null);
        }
        int encryptedColumnEncryptionKeyLength = version.length + cipherTextLength.length + keyPathLength.length + cipherText.length + masterKeyPathBytes.length + signedHash.length;
        byte[] encryptedColumnEncryptionKey = new byte[encryptedColumnEncryptionKeyLength];
        int currentIndex = 0;
        System.arraycopy(version, 0, encryptedColumnEncryptionKey, currentIndex, version.length);
        System.arraycopy(keyPathLength, 0, encryptedColumnEncryptionKey, currentIndex += version.length, keyPathLength.length);
        System.arraycopy(cipherTextLength, 0, encryptedColumnEncryptionKey, currentIndex += keyPathLength.length, cipherTextLength.length);
        System.arraycopy(masterKeyPathBytes, 0, encryptedColumnEncryptionKey, currentIndex += cipherTextLength.length, masterKeyPathBytes.length);
        System.arraycopy(cipherText, 0, encryptedColumnEncryptionKey, currentIndex += masterKeyPathBytes.length, cipherText.length);
        System.arraycopy(signedHash, 0, encryptedColumnEncryptionKey, currentIndex += cipherText.length, signedHash.length);
        return encryptedColumnEncryptionKey;
    }

    private KeyWrapAlgorithm validateEncryptionAlgorithm(String encryptionAlgorithm) throws SQLServerException {
        if (null == encryptionAlgorithm) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_NullKeyEncryptionAlgorithm"), null, 0, false);
        }
        if ("RSA_OAEP".equalsIgnoreCase(encryptionAlgorithm)) {
            encryptionAlgorithm = RSA_ENCRYPTION_ALGORITHM_WITH_OAEP_FOR_AKV;
        }
        if (!RSA_ENCRYPTION_ALGORITHM_WITH_OAEP_FOR_AKV.equalsIgnoreCase(encryptionAlgorithm.trim())) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidKeyEncryptionAlgorithm"));
            Object[] msgArgs = new Object[]{encryptionAlgorithm, RSA_ENCRYPTION_ALGORITHM_WITH_OAEP_FOR_AKV};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        return KeyWrapAlgorithm.fromString((String)encryptionAlgorithm);
    }

    private void validateNonEmptyAKVPath(String masterKeyPath) throws SQLServerException {
        if (null == masterKeyPath || masterKeyPath.trim().isEmpty()) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_AKVPathNull"));
            Object[] msgArgs = new Object[]{masterKeyPath};
            throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
        }
        URI parsedUri = null;
        try {
            parsedUri = new URI(masterKeyPath);
            String host = parsedUri.getHost();
            if (null != host) {
                host = host.toLowerCase(Locale.ENGLISH);
            }
            for (String endpoint : akvTrustedEndpoints) {
                if (null == host || !host.endsWith(endpoint)) continue;
                return;
            }
        }
        catch (URISyntaxException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_AKVURLInvalid"));
            Object[] msgArgs = new Object[]{masterKeyPath};
            throw new SQLServerException(form.format(msgArgs), null, 0, (Throwable)e);
        }
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_AKVMasterKeyPathInvalid"));
        Object[] msgArgs = new Object[]{masterKeyPath};
        throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
    }

    private byte[] azureKeyVaultWrap(String masterKeyPath, KeyWrapAlgorithm encryptionAlgorithm, byte[] columnEncryptionKey) throws SQLServerException {
        if (null == columnEncryptionKey) {
            throw new SQLServerException(SQLServerException.getErrString("R_CEKNull"), null);
        }
        CryptographyClient cryptoClient = this.getCryptographyClient(masterKeyPath);
        WrapResult wrappedKey = cryptoClient.wrapKey(KeyWrapAlgorithm.RSA_OAEP, columnEncryptionKey);
        return wrappedKey.getEncryptedKey();
    }

    private byte[] azureKeyVaultUnWrap(String masterKeyPath, KeyWrapAlgorithm encryptionAlgorithm, byte[] encryptedColumnEncryptionKey) throws SQLServerException {
        if (null == encryptedColumnEncryptionKey) {
            throw new SQLServerException(SQLServerException.getErrString("R_EncryptedCEKNull"), null);
        }
        if (0 == encryptedColumnEncryptionKey.length) {
            throw new SQLServerException(SQLServerException.getErrString("R_EmptyEncryptedCEK"), null);
        }
        CryptographyClient cryptoClient = this.getCryptographyClient(masterKeyPath);
        UnwrapResult unwrappedKey = cryptoClient.unwrapKey(encryptionAlgorithm, encryptedColumnEncryptionKey);
        return unwrappedKey.getKey();
    }

    private CryptographyClient getCryptographyClient(String masterKeyPath) throws SQLServerException {
        if (this.cachedCryptographyClients.containsKey(masterKeyPath)) {
            return this.cachedCryptographyClients.get(masterKeyPath);
        }
        KeyVaultKey retrievedKey = this.getKeyVaultKey(masterKeyPath);
        CryptographyClient cryptoClient = null != this.credential ? new CryptographyClientBuilder().credential(this.credential).keyIdentifier(retrievedKey.getId()).buildClient() : new CryptographyClientBuilder().pipeline(this.keyVaultPipeline).keyIdentifier(retrievedKey.getId()).buildClient();
        this.cachedCryptographyClients.putIfAbsent(masterKeyPath, cryptoClient);
        return this.cachedCryptographyClients.get(masterKeyPath);
    }

    private byte[] azureKeyVaultSignHashedData(byte[] dataToSign, String masterKeyPath) throws SQLServerException {
        assert (null != dataToSign && 0 != dataToSign.length);
        CryptographyClient cryptoClient = this.getCryptographyClient(masterKeyPath);
        SignResult signedData = cryptoClient.sign(SignatureAlgorithm.RS256, dataToSign);
        return signedData.getSignature();
    }

    private boolean azureKeyVaultVerifySignature(byte[] dataToVerify, byte[] signature, String masterKeyPath) throws SQLServerException {
        assert (null != dataToVerify && 0 != dataToVerify.length);
        assert (null != signature && 0 != signature.length);
        CryptographyClient cryptoClient = this.getCryptographyClient(masterKeyPath);
        VerifyResult valid = cryptoClient.verify(SignatureAlgorithm.RS256, dataToVerify, signature);
        return valid.isValid();
    }

    private int getAKVKeySize(String masterKeyPath) throws SQLServerException {
        KeyVaultKey retrievedKey = this.getKeyVaultKey(masterKeyPath);
        return retrievedKey.getKey().getN().length;
    }

    private KeyVaultKey getKeyVaultKey(String masterKeyPath) throws SQLServerException {
        String[] keyTokens = masterKeyPath.split(KEY_URL_DELIMITER);
        String keyName = keyTokens[4];
        String keyVersion = null;
        if (keyTokens.length == 6) {
            keyVersion = keyTokens[keyTokens.length - 1];
        }
        try {
            KeyClient keyClient = this.getKeyClient(masterKeyPath);
            KeyVaultKey retrievedKey = null != keyVersion ? keyClient.getKey(keyName, keyVersion) : keyClient.getKey(keyName);
            if (null == retrievedKey) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_AKVKeyNotFound"));
                Object[] msgArgs = new Object[]{keyTokens[keyTokens.length - 1]};
                throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
            }
            if (retrievedKey.getKeyType() != KeyType.RSA && retrievedKey.getKeyType() != KeyType.RSA_HSM) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_NonRSAKey"));
                Object[] msgArgs = new Object[]{retrievedKey.getKeyType().toString()};
                throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
            }
            return retrievedKey;
        }
        catch (RuntimeException e) {
            throw new SQLServerException(e.getMessage(), e);
        }
    }

    private KeyClient getKeyClient(String masterKeyPath) {
        if (this.cachedKeyClients.containsKey(masterKeyPath)) {
            return this.cachedKeyClients.get(masterKeyPath);
        }
        String vaultUrl = SQLServerColumnEncryptionAzureKeyVaultProvider.getVaultUrl(masterKeyPath);
        KeyClient keyClient = null != this.credential ? new KeyClientBuilder().credential(this.credential).vaultUrl(vaultUrl).buildClient() : new KeyClientBuilder().pipeline(this.keyVaultPipeline).vaultUrl(vaultUrl).buildClient();
        this.cachedKeyClients.putIfAbsent(masterKeyPath, keyClient);
        return this.cachedKeyClients.get(masterKeyPath);
    }

    private static String getVaultUrl(String masterKeyPath) {
        String[] keyTokens = masterKeyPath.split(KEY_URL_DELIMITER);
        String hostName = keyTokens[2];
        return "https://" + hostName;
    }

    @Override
    public boolean verifyColumnMasterKeyMetadata(String masterKeyPath, boolean allowEnclaveComputations, byte[] signature) throws SQLServerException {
        if (!allowEnclaveComputations) {
            return false;
        }
        KeyStoreProviderCommon.validateNonEmptyMasterKeyPath(masterKeyPath);
        CMKMetadataSignatureInfo key = new CMKMetadataSignatureInfo(masterKeyPath, allowEnclaveComputations, signature);
        if (this.cmkMetadataSignatureVerificationCache.contains(key)) {
            return this.cmkMetadataSignatureVerificationCache.get(key);
        }
        byte[] signedHash = null;
        boolean isValid = false;
        try {
            MessageDigest md = MessageDigest.getInstance(SHA_256);
            md.update(this.name.toLowerCase().getBytes(StandardCharsets.UTF_16LE));
            md.update(masterKeyPath.toLowerCase().getBytes(StandardCharsets.UTF_16LE));
            md.update("true".getBytes(StandardCharsets.UTF_16LE));
            byte[] dataToVerify = md.digest();
            if (null == dataToVerify) {
                throw new SQLServerException(SQLServerException.getErrString("R_HashNull"), null);
            }
            signedHash = this.azureKeyVaultSignHashedData(dataToVerify, masterKeyPath);
            if (null == signedHash) {
                throw new SQLServerException(SQLServerException.getErrString("R_SignedHashLengthError"), null);
            }
            isValid = this.azureKeyVaultVerifySignature(dataToVerify, signature, masterKeyPath);
            this.cmkMetadataSignatureVerificationCache.put(key, isValid);
        }
        catch (NoSuchAlgorithmException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_NoSHA256Algorithm"), e);
        }
        catch (SQLServerException e) {
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
        byte[] signedHash = null;
        try {
            MessageDigest md = MessageDigest.getInstance(SHA_256);
            md.update(this.name.toLowerCase().getBytes(StandardCharsets.UTF_16LE));
            md.update(masterKeyPath.toLowerCase().getBytes(StandardCharsets.UTF_16LE));
            md.update("true".getBytes(StandardCharsets.UTF_16LE));
            byte[] dataToVerify = md.digest();
            if (null == dataToVerify) {
                throw new SQLServerException(SQLServerException.getErrString("R_HashNull"), null);
            }
            signedHash = this.azureKeyVaultSignHashedData(dataToVerify, masterKeyPath);
        }
        catch (NoSuchAlgorithmException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_NoSHA256Algorithm"), e);
        }
        if (null == signedHash) {
            throw new SQLServerException(SQLServerException.getErrString("R_SignedHashLengthError"), null);
        }
        return signedHash;
    }

    private static List<String> getTrustedEndpoints() {
        String endpoints;
        Properties mssqlJdbcProperties = SQLServerColumnEncryptionAzureKeyVaultProvider.getMssqlJdbcProperties();
        ArrayList<String> trustedEndpoints = new ArrayList<String>();
        boolean append = true;
        if (null != mssqlJdbcProperties && null != (endpoints = mssqlJdbcProperties.getProperty(AKV_TRUSTED_ENDPOINTS_KEYWORD)) && !endpoints.trim().isEmpty()) {
            String[] entries;
            if (';' != (endpoints = endpoints.trim()).charAt(0)) {
                append = false;
            } else {
                endpoints = endpoints.substring(1);
            }
            for (String entry : entries = endpoints.split(";")) {
                if (null == entry || entry.trim().isEmpty()) continue;
                trustedEndpoints.add(entry.trim());
            }
        }
        if (append) {
            trustedEndpoints.add("vault.azure.net");
            trustedEndpoints.add("vault.azure.cn");
            trustedEndpoints.add("vault.usgovcloudapi.net");
            trustedEndpoints.add("vault.microsoftazure.de");
            trustedEndpoints.add("managedhsm.azure.net");
            trustedEndpoints.add("managedhsm.azure.cn");
            trustedEndpoints.add("managedhsm.usgovcloudapi.net");
            trustedEndpoints.add("managedhsm.microsoftazure.de");
        }
        return trustedEndpoints;
    }

    private static Properties getMssqlJdbcProperties() {
        Properties props;
        block7: {
            props = null;
            try (FileInputStream in = new FileInputStream(MSSQL_JDBC_PROPERTIES);){
                props = new Properties();
                props.load(in);
            }
            catch (IOException e) {
                if (!akvLogger.isLoggable(Level.FINER)) break block7;
                akvLogger.finer("Unable to load the mssql-jdbc.properties file: " + e);
            }
        }
        return null != props && !props.isEmpty() ? props : null;
    }

    int getColumnEncryptionKeyCacheSize() {
        return this.columnEncryptionKeyCache.getCacheSize();
    }

    int getCmkMetadataSignatureVerificationCacheSize() {
        return this.cmkMetadataSignatureVerificationCache.getCacheSize();
    }
}

