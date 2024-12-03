/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.google.common.base.Strings
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.encryption;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.migration.agent.service.encryption.SecretStoreLocationType;
import com.atlassian.migration.agent.service.encryption.exception.EncryptionException;
import com.google.common.base.Strings;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptionConfigHandler {
    static final String CLUSTER_LOCK = "EncryptionConfigHandlerLock";
    private static final String SECRET_CONFIG_FILENAME = "secret.properties";
    private static final String SECRET_CONFIG_FOLDERNAME = "migration-settings";
    private static final String SECRET_KEY_LOCATION = "secret.key.location";
    private static final String SECRET_KEY = "secret.key";
    private static final Logger log = LoggerFactory.getLogger(EncryptionConfigHandler.class);
    private final BootstrapManager bootstrapManager;
    private final ClusterLockService lockService;

    public EncryptionConfigHandler(BootstrapManager bootstrapManager, ClusterLockService lockService) {
        this.bootstrapManager = bootstrapManager;
        this.lockService = lockService;
    }

    public boolean doesStorageTypeExist() throws EncryptionException {
        return this.doesPropertyExistInConfig(SECRET_KEY_LOCATION);
    }

    public byte[] getKeyFromConfig() throws IOException {
        if (!this.doesPropertyExistInConfig(SECRET_KEY)) {
            this.generateKeyInConfig();
        }
        Properties propertiesFromFile = this.readConfig();
        String secretKey = propertiesFromFile.getProperty(SECRET_KEY);
        return Base64.getDecoder().decode(secretKey);
    }

    public SecretStoreLocationType getKeyLocationFromConfig() throws EncryptionException {
        try {
            Properties propertiesFromFile = this.readConfig();
            String secretStoreLocationTypeFromProperties = propertiesFromFile.getProperty(SECRET_KEY_LOCATION);
            return SecretStoreLocationType.valueOf(secretStoreLocationTypeFromProperties.toUpperCase());
        }
        catch (Exception e) {
            throw new EncryptionException("Encryption Config has gone corrupt", e, EncryptionException.EncryptionErrorCode.INVALID_SECRET_KEY);
        }
    }

    public void setKeyLocationInConfig(SecretStoreLocationType secretStoreLocationType) throws EncryptionException {
        Properties properties = new Properties();
        properties.setProperty(SECRET_KEY_LOCATION, secretStoreLocationType.toString().toLowerCase());
        this.clusterLockAndWriteConfig(SECRET_KEY_LOCATION, properties);
    }

    private void clusterLockAndWriteConfig(String secretKeyLocation, Properties properties) {
        ClusterLock lock = this.lockService.getLockForName(CLUSTER_LOCK);
        try {
            if (lock.tryLock(1L, TimeUnit.MINUTES) && !this.doesPropertyExistInConfig(secretKeyLocation)) {
                this.writeConfig(properties);
            }
        }
        catch (InterruptedException e) {
            throw new EncryptionException("Cluster lock interrupted", e);
        }
        finally {
            lock.unlock();
        }
    }

    private void generateKeyInConfig() throws EncryptionException {
        int numBytes = 32;
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[numBytes];
        random.nextBytes(bytes);
        String encodedBytes = Base64.getEncoder().encodeToString(bytes);
        Properties properties = new Properties();
        properties.setProperty(SECRET_KEY, encodedBytes);
        this.clusterLockAndWriteConfig(SECRET_KEY, properties);
    }

    private Properties readConfig() throws IOException {
        Properties properties;
        block14: {
            properties = new Properties();
            try {
                if (!this.getSecretKeyConfigFilePath().exists()) break block14;
                try (InputStream inputStream = Files.newInputStream(this.getSecretKeyConfigFilePath().toPath(), new OpenOption[0]);){
                    properties.load(inputStream);
                }
            }
            catch (IOException e) {
                throw new IOException("Error reading encryption config", e);
            }
        }
        return properties;
    }

    private void writeConfig(Properties properties) throws EncryptionException {
        try {
            Path secretKeyConfigFolderPath = this.getSecretKeyConfigFolderPath();
            Files.createDirectories(secretKeyConfigFolderPath, new FileAttribute[0]);
            Properties existingProperties = this.readConfig();
            try (FileOutputStream outputStream = new FileOutputStream(this.getSecretKeyConfigFilePath());){
                existingProperties.putAll((Map<?, ?>)properties);
                existingProperties.store(outputStream, "Secret Properties");
            }
        }
        catch (IOException e) {
            throw new EncryptionException(e.getMessage(), e);
        }
    }

    private boolean doesPropertyExistInConfig(String tagName) {
        if (!this.getSecretKeyConfigFilePath().exists()) {
            return false;
        }
        try {
            Properties propertiesFromFile = this.readConfig();
            return !Strings.isNullOrEmpty((String)propertiesFromFile.getProperty(tagName));
        }
        catch (Exception e) {
            log.error("Error reading encryption config", (Throwable)e);
            return false;
        }
    }

    @VisibleForTesting
    protected File getSecretKeyConfigFilePath() {
        Path secretKeyConfigFolderPath = this.getSecretKeyConfigFolderPath();
        return secretKeyConfigFolderPath.resolve(SECRET_CONFIG_FILENAME).toFile();
    }

    private Path getSecretKeyConfigFolderPath() {
        return Paths.get(this.bootstrapManager.getSharedHome().getAbsolutePath(), SECRET_CONFIG_FOLDERNAME);
    }
}

