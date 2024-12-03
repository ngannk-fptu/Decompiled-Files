/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.encryption;

import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.encryption.EncryptionSecretManager;
import com.atlassian.migration.agent.service.encryption.exception.EncryptionException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptionService {
    private static final Logger log = LoggerFactory.getLogger(EncryptionService.class);
    private static final int IV_LENGTH = 12;
    private static final String PREFIX = "encrypted_";
    private static final String VALIDATE_PLAINTEXT = "VALIDATE_PLAINTEXT";
    private final EncryptionSecretManager encryptionSecretManager;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final AnalyticsEventService analyticsEventService;

    public EncryptionService(EncryptionSecretManager encryptionSecretManager, MigrationDarkFeaturesManager migrationDarkFeaturesManager, AnalyticsEventBuilder analyticsEventBuilder, AnalyticsEventService analyticsEventService, CloudSiteService cloudSiteService) {
        this.encryptionSecretManager = encryptionSecretManager;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.analyticsEventService = analyticsEventService;
        this.encryptAllTokensAtBoot(cloudSiteService);
    }

    private static byte[] generateIV() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstanceStrong();
        byte[] iv = new byte[12];
        random.nextBytes(iv);
        return iv;
    }

    private static byte[] merge(byte[] iv, byte[] encryptedtext) {
        String encodedIV = Base64.getEncoder().encodeToString(iv);
        String encodedEncryptedText = Base64.getEncoder().encodeToString(encryptedtext);
        String concatenatedText = encodedIV + "-" + encodedEncryptedText;
        return concatenatedText.getBytes(StandardCharsets.UTF_8);
    }

    public String encrypt(String token) throws EncryptionException {
        if (this.migrationDarkFeaturesManager.isTokenEncryptionEnabled()) {
            try {
                if (this.verifyEncrypted(token)) {
                    return token;
                }
                byte[] plainText = token.getBytes(StandardCharsets.UTF_8);
                byte[] iv = EncryptionService.generateIV();
                SecretKey secretKey = this.encryptionSecretManager.getEncryptionSecret();
                Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
                cipher.init(1, (Key)secretKey, parameterSpec);
                byte[] encryptedtext = cipher.doFinal(plainText);
                byte[] mergedEncryptedtext = EncryptionService.merge(iv, encryptedtext);
                byte[] finalEncryptedText = Base64.getEncoder().encode(mergedEncryptedtext);
                byte[] finaloutput = this.addPrefix(finalEncryptedText, PREFIX);
                return new String(finaloutput, StandardCharsets.UTF_8);
            }
            catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
                this.sendEncryptionFailedAnalyticsEvent(e.getMessage());
                throw new EncryptionException(e.getMessage(), e);
            }
            catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
                this.sendEncryptionFailedAnalyticsEvent(e.getMessage());
                throw new EncryptionException(e.getMessage(), e, EncryptionException.EncryptionErrorCode.INVALID_SECRET_KEY);
            }
            catch (EncryptionException encryptionException) {
                this.sendEncryptionFailedAnalyticsEvent(encryptionException.getMessage());
                throw encryptionException;
            }
        }
        return token;
    }

    public String decrypt(String crypticText) throws EncryptionException {
        if (this.migrationDarkFeaturesManager.isTokenEncryptionEnabled()) {
            try {
                if (!this.verifyEncrypted(crypticText)) {
                    return crypticText;
                }
                byte[] encryptedText = crypticText.getBytes(StandardCharsets.UTF_8);
                encryptedText = this.removePrefix(encryptedText, PREFIX.length());
                encryptedText = Base64.getDecoder().decode(encryptedText);
                SecretKey secretKey = this.encryptionSecretManager.getEncryptionSecret();
                String[] splitText = new String(encryptedText, StandardCharsets.UTF_8).split("-");
                byte[] retrievedIV = Base64.getDecoder().decode(splitText[0]);
                byte[] retrievedEncryptedText = Base64.getDecoder().decode(splitText[1]);
                Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                GCMParameterSpec parameterSpec = new GCMParameterSpec(128, retrievedIV);
                cipher.init(2, (Key)secretKey, parameterSpec);
                byte[] decryptedBytes = cipher.doFinal(retrievedEncryptedText);
                return new String(decryptedBytes, StandardCharsets.UTF_8);
            }
            catch (AEADBadTagException e) {
                this.sendEncryptionFailedAnalyticsEvent(e.getMessage());
                log.warn("Token has been changed or corrupted. Returning the token as it is.", (Throwable)e);
                return crypticText;
            }
            catch (IllegalArgumentException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                this.sendEncryptionFailedAnalyticsEvent(e.getMessage());
                throw new EncryptionException(e.getMessage(), e, EncryptionException.EncryptionErrorCode.INVALID_SECRET_KEY);
            }
            catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException e) {
                this.sendEncryptionFailedAnalyticsEvent(e.getMessage());
                throw new EncryptionException(e.getMessage(), e);
            }
            catch (EncryptionException encryptionException) {
                this.sendEncryptionFailedAnalyticsEvent(encryptionException.getMessage());
                throw encryptionException;
            }
        }
        return crypticText;
    }

    private boolean verifyEncrypted(String encryptedText) {
        return encryptedText.startsWith(PREFIX);
    }

    private byte[] addPrefix(byte[] array, String prefix) {
        byte[] prefixBytes = prefix.getBytes(StandardCharsets.UTF_8);
        byte[] prefixedArray = new byte[prefixBytes.length + array.length];
        System.arraycopy(prefixBytes, 0, prefixedArray, 0, prefixBytes.length);
        System.arraycopy(array, 0, prefixedArray, prefixBytes.length, array.length);
        return prefixedArray;
    }

    private byte[] removePrefix(byte[] array, int prefixLength) {
        byte[] removedPrefixArray = new byte[array.length - prefixLength];
        System.arraycopy(array, prefixLength, removedPrefixArray, 0, array.length - prefixLength);
        return removedPrefixArray;
    }

    public void validateEncryption() {
        if (!VALIDATE_PLAINTEXT.equals(this.decrypt(this.encrypt(VALIDATE_PLAINTEXT)))) {
            this.sendEncryptionFailedAnalyticsEvent("Encryption is corrupted");
            throw new IllegalArgumentException("Encryption is corrupted");
        }
    }

    private void sendEncryptionFailedAnalyticsEvent(String reason) {
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildTokenEncryptionFailureAnalyticEvent(reason));
    }

    private void encryptAllTokensAtBoot(CloudSiteService cloudSiteService) {
        if (this.migrationDarkFeaturesManager.isTokenEncryptionEnabled()) {
            try {
                List<CloudSite> cloudSites = cloudSiteService.getAllSites();
                cloudSites.forEach(cloudSite -> {
                    String newContainerToken = this.encrypt(cloudSite.getContainerToken());
                    cloudSiteService.createOrUpdate(cloudSite.getCloudId(), cloudSite.getCloudUrl(), newContainerToken, Optional.of(cloudSite.getEdition().getKey()), cloudSite.getCloudType());
                });
            }
            catch (Exception e) {
                log.error("Failed to encrypt all tokens at boot with error : " + e.getMessage(), (Throwable)e);
            }
        }
    }
}

