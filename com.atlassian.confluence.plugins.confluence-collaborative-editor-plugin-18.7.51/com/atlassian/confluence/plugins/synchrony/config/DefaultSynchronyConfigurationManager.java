/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.confluence.setup.settings.UnknownFeatureException
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.tenancy.api.event.TenantArrivedEvent
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.http.HttpEntity
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.entity.ContentType
 *  org.apache.http.entity.StringEntity
 *  org.apache.http.util.EntityUtils
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.type.TypeReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.config;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.plugins.synchrony.config.ConfigFailureEvent;
import com.atlassian.confluence.plugins.synchrony.config.ConfigSuccessEvent;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.confluence.plugins.synchrony.service.http.SynchronyHttpClientFactory;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.setup.settings.UnknownFeatureException;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.tenancy.api.event.TenantArrivedEvent;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.crypto.Cipher;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="synchronyConfigurationManager")
@ExportAsService(value={SynchronyConfigurationManager.class})
public class DefaultSynchronyConfigurationManager
implements SynchronyConfigurationManager,
InitializingBean,
DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(DefaultSynchronyConfigurationManager.class);
    public static final String BANDANA_KEY_COLLAB_APP_ID = "synchrony_collaborative_editor_app_id";
    public static final String BANDANA_KEY_COLLAB_APP_SECRET = "synchrony_collaborative_editor_app_secret";
    public static final String BANDANA_KEY_COLLAB_APP_REGISTERED = "synchrony_collaborative_editor_app_registered";
    public static final String BANDANA_KEY_COLLAB_APP_BASE_URL = "synchrony_collaborative_editor_app_base_url";
    public static final String BANDANA_KEY_COLLAB_APP_PASSPHRASE = "synchrony_collaborative_editor_app_passphrase";
    private static final String BANDANA_KEY_COLLAB_APP_PUBLIC_KEY = "synchrony_collaborative_editor_app_public_key";
    private static final String SYNCHRONY_PUBLIC_KEY_ENDPOINT = "/jwt-key";
    public static final String SYNCHRONY_SERVICE_DEBUG_SYS_PROP = "synchrony.service.debug";
    private static final String SYNCHRONY_SECRET_ENDPOINT = "/apps/secret";
    private static final String SYNCHRONY_DEFAULT_BASE_URL = "http://localhost:8091/synchrony";
    private static final String SYNCHRONY_DEFAULT_SERVICE_URL = "http://localhost:8091/synchrony/v1";
    private static final String SYNCHRONY_SERVICE_AUTH_TOKEN_SYS_PROP = "synchrony.service.authtoken";
    private static final String SYNCHRONY_ENABLE_DATA_ENCRYPTION_SYS_PROP = "synchrony.enable.data.encryption";
    private static final String SYNCHRONY_RESPONSE_MESSAGE_KEY = "message";
    private static final String SYNCHRONY_PASSPHRASE_BANDANA_CACHE_LOCK_NAME = "com.atlassian.confluence.plugins.synchrony.config.passphrase.lock";
    private static final String SYNCHRONY_REGISTRATION_BANDANA_CACHE_LOCK_NAME = "com.atlassian.confluence.plugins.synchrony.config.synchrony.rego.lock";
    public static final String SYNCHRONY_ENCRYPTION_DISABLED = "synchrony.encryption.disabled";
    private static final long LOCK_TIMEOUT = 5000L;
    private static final String LOG_TAG = "[Collab editing plugin]";
    public static final String SYNCHRONY_DEBUG_SUFFIX = "-debug";
    private static final TypeReference<Map<String, ?>> JSON_MAP_RESPONSE_TYPE = new TypeReference<Map<String, ?>>(){};
    private final ObjectMapper jackson = new ObjectMapper();
    private final BandanaManager bandanaManager;
    private final DarkFeaturesManager darkFeaturesManager;
    private final EventPublisher eventPublisher;
    private final SynchronyHttpClientFactory synchronyHttpClientFactory;
    private final ClusterLockService clusterLockService;
    private final ApplicationConfiguration applicationConfiguration;
    private final ClusterManager clusterManager;
    private String publicKey;
    private Optional<String> externalBaseUrl;
    private String internalBaseUrl;
    private int internalPort;

    @Autowired
    public DefaultSynchronyConfigurationManager(@ComponentImport BandanaManager bandanaManager, @ComponentImport DarkFeaturesManager darkFeaturesManager, @ComponentImport EventPublisher eventPublisher, SynchronyHttpClientFactory synchronyHttpClientFactory, @ComponentImport ClusterLockService clusterLockService, @ComponentImport ApplicationConfiguration applicationConfiguration, @ComponentImport ClusterManager clusterManager) throws NoSuchAlgorithmException {
        this.bandanaManager = bandanaManager;
        this.darkFeaturesManager = darkFeaturesManager;
        this.eventPublisher = eventPublisher;
        this.synchronyHttpClientFactory = synchronyHttpClientFactory;
        this.clusterLockService = clusterLockService;
        this.applicationConfiguration = applicationConfiguration;
        this.clusterManager = clusterManager;
        this.externalBaseUrl = Optional.empty();
        this.internalBaseUrl = SYNCHRONY_DEFAULT_BASE_URL;
        if (this.isSynchronyEncryptionEnabled()) assert (Cipher.getMaxAllowedKeyLength("AES") > 128) : "This JDK is missing the JCE policy files required for strong encryption.";
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @Override
    @Deprecated
    public String getResourcesUrl() {
        return this.getExternalBaseUrl() + "/resources";
    }

    private String getBaseUrl() {
        if (this.isUsingLocalSynchrony()) {
            return this.getInternalBaseUrl();
        }
        return this.getExternalBaseUrl();
    }

    @Override
    public String getExternalServiceUrl() {
        if (this.isDebug() || this.isUsingLocalSynchrony()) {
            return this.getDefaultServerExternalServiceUrl();
        }
        return this.getExternalServiceUrlFromEnvironment(SYNCHRONY_DEFAULT_SERVICE_URL);
    }

    @Override
    public String getExternalBaseUrl() {
        if (!this.externalBaseUrl.isPresent() && (this.isDebug() || this.isUsingLocalSynchrony())) {
            this.externalBaseUrl = Optional.ofNullable(this.getConfigValue(BANDANA_KEY_COLLAB_APP_BASE_URL, null));
        }
        return this.externalBaseUrl.orElse(StringUtils.removeEnd((String)this.getExternalServiceUrlFromEnvironment(SYNCHRONY_DEFAULT_SERVICE_URL), (String)"/v1"));
    }

    private String getExternalServiceUrlFromEnvironment(String defaultValue) {
        String serviceUrl = System.getProperty("synchrony.service.url");
        return serviceUrl != null ? serviceUrl : defaultValue;
    }

    @Nonnull
    private String getDefaultServerExternalServiceUrl() {
        String[] externalBaseUrls = this.getExternalBaseUrl().split(",");
        return externalBaseUrls.length > 0 ? externalBaseUrls[0] + "/v1" : SYNCHRONY_DEFAULT_SERVICE_URL;
    }

    @Override
    public void setExternalBaseUrl(String url) {
        this.setConfigValue(BANDANA_KEY_COLLAB_APP_BASE_URL, url);
        this.externalBaseUrl = Optional.of(url);
    }

    private Optional<String> getInternalServiceUrlFromEnvironment() {
        return Optional.ofNullable(System.getProperty("synchrony.local.service.url"));
    }

    @Override
    public String getInternalServiceUrl() {
        if (this.isUsingLocalSynchrony() || this.isDebug()) {
            return this.getInternalBaseUrl() + "/v1";
        }
        return this.getInternalServiceUrlFromEnvironment().orElse(this.getExternalServiceUrlFromEnvironment(SYNCHRONY_DEFAULT_SERVICE_URL));
    }

    @Override
    public void setInternalBaseUrl(String url) {
        this.internalBaseUrl = url;
    }

    private String getInternalBaseUrl() {
        return this.internalBaseUrl;
    }

    String getDebugAppId() {
        String appID = this.getConfigValue(BANDANA_KEY_COLLAB_APP_ID, null);
        return appID != null ? appID + SYNCHRONY_DEBUG_SUFFIX : appID;
    }

    @Override
    public String getAppID() {
        return this.getConfigValue(BANDANA_KEY_COLLAB_APP_ID, null);
    }

    @Override
    public String getConfiguredAppID() {
        return this.isDebug() ? this.getDebugAppId() : this.getAppID();
    }

    @Override
    public void setAppId(String appId) {
        this.setConfigValue(BANDANA_KEY_COLLAB_APP_ID, appId);
    }

    @Override
    public void setRegistrationComplete() {
        this.setConfigValue(BANDANA_KEY_COLLAB_APP_REGISTERED, "true");
    }

    public void setRegistrationIncomplete() {
        this.setConfigValue(BANDANA_KEY_COLLAB_APP_REGISTERED, "false");
    }

    @Override
    public boolean isRegistrationComplete() {
        return this.getConfigValue(BANDANA_KEY_COLLAB_APP_REGISTERED, "false").equals("true");
    }

    @Override
    public String getAppSecret() {
        return this.getConfigValue(BANDANA_KEY_COLLAB_APP_SECRET, null);
    }

    @Override
    public void setAppSecret(String appSecret) {
        this.setConfigValue(BANDANA_KEY_COLLAB_APP_SECRET, appSecret);
    }

    @Override
    public void setPassphrase(String passphrase) {
        this.setConfigValue(BANDANA_KEY_COLLAB_APP_PASSPHRASE, passphrase);
    }

    @Override
    public String getPassphrase() {
        return this.getConfigValue(BANDANA_KEY_COLLAB_APP_PASSPHRASE, null);
    }

    @Override
    public String generatePassphrase() {
        return this.generateSecureRandomString32();
    }

    @Override
    public void setSynchronyPublicKey(String publicKey) {
        if (publicKey == null) {
            return;
        }
        if (this.isDebug()) {
            this.setConfigValue(BANDANA_KEY_COLLAB_APP_PUBLIC_KEY, publicKey);
        } else {
            this.publicKey = publicKey;
        }
    }

    @Override
    public String getSynchronyPublicKey() {
        if (this.isDebug()) {
            return this.getConfigValue(BANDANA_KEY_COLLAB_APP_PUBLIC_KEY, null);
        }
        return this.publicKey;
    }

    @Override
    public boolean isDebug() {
        return Boolean.getBoolean(SYNCHRONY_SERVICE_DEBUG_SYS_PROP) && !this.isSynchronyProdOverrideEnabled();
    }

    @Override
    public boolean isUsingLocalSynchrony() {
        return !this.clusterManager.isClustered() || this.clusterManager.isClustered() && StringUtils.isBlank((CharSequence)System.getProperty("synchrony.service.url"));
    }

    @Override
    @Deprecated
    public boolean isSynchronyEnabled() {
        return this.isSharedDraftsEnabled();
    }

    private String getConfigValue(String key, String defaultVal) {
        String value = (String)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, key);
        if (value == null) {
            value = defaultVal;
        }
        return value;
    }

    private void setConfigValue(String key, String value) {
        if (key == null) {
            return;
        }
        if (value == null || value.isEmpty()) {
            this.bandanaManager.removeValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, key);
        } else {
            this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, key, (Object)value);
        }
    }

    @Override
    public String generateAppID() {
        return "Synchrony-" + UUID.nameUUIDFromBytes(DefaultSynchronyConfigurationManager.generateSecureRandomBytes(32)).toString();
    }

    @Override
    public String generateAppSecret() {
        return this.generateSecureRandomString32();
    }

    private String generateSecureRandomString32() {
        return Base64.getEncoder().encodeToString(DefaultSynchronyConfigurationManager.generateSecureRandomBytes(32));
    }

    @EventListener
    public void onTenantArrived(TenantArrivedEvent event) {
        if (this.isUsingLocalSynchrony()) {
            return;
        }
        this.generateStorePassphraseIfMissing();
        this.registerWithSynchrony();
        if (this.isDebug() || this.publicKey == null) {
            this.retrievePublicKey();
        }
    }

    @Override
    public void generateStorePassphraseIfMissing() {
        block8: {
            String passphrase = this.getPassphrase();
            if (passphrase == null) {
                ClusterLock lock = this.getPassphraseLock();
                try {
                    if (lock.tryLock(5000L, TimeUnit.MILLISECONDS)) {
                        try {
                            passphrase = this.getPassphrase();
                            if (passphrase == null) {
                                logger.info("{} Generating a new passphrase.", (Object)LOG_TAG);
                                passphrase = this.generatePassphrase();
                                this.setPassphrase(passphrase);
                            }
                            break block8;
                        }
                        finally {
                            lock.unlock();
                        }
                    }
                    logger.warn("{} Could not obtain lock to generate and store passphrase.", (Object)LOG_TAG);
                }
                catch (InterruptedException e) {
                    logger.warn("{} Thread interrupted: Could not obtain lock to generate and store passphrase.", (Object)LOG_TAG);
                    logger.debug("", (Throwable)e);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean removeSynchronyCredentials() {
        ClusterLock registrationLock = this.getRegistrationLock();
        ClusterLock passphraseLock = this.getPassphraseLock();
        try {
            boolean deregistrationComplete = false;
            if (registrationLock.tryLock(5000L, TimeUnit.MILLISECONDS)) {
                this.setRegistrationIncomplete();
                this.setAppId(null);
                this.setAppSecret(null);
                deregistrationComplete = true;
            }
            if (deregistrationComplete && passphraseLock.tryLock(5000L, TimeUnit.MILLISECONDS)) {
                this.setPassphrase(null);
                boolean bl = true;
                return bl;
            }
        }
        catch (InterruptedException e) {
            logger.warn("{} Thread interrupted: Could not obtain lock when generating Synchrony credentials.", (Object)LOG_TAG);
            logger.debug("", (Throwable)e);
        }
        finally {
            registrationLock.unlock();
            passphraseLock.unlock();
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean registerWithSynchrony() {
        String appId = this.getAppID();
        if (appId != null && appId.contains(" ")) {
            this.setRegistrationIncomplete();
            ClusterLock lock = this.getRegistrationLock();
            try {
                if (lock.tryLock(5000L, TimeUnit.MILLISECONDS)) {
                    appId = this.getAppID();
                    if (appId != null && appId.contains(" ")) {
                        this.setAppId(null);
                        this.setAppSecret(null);
                    }
                } else {
                    logger.warn("{} Could not obtain lock when generating Synchrony credentials.", (Object)LOG_TAG);
                }
            }
            catch (InterruptedException e) {
                logger.warn("{} Thread interrupted: Could not obtain lock when generating Synchrony credentials.", (Object)LOG_TAG);
                logger.debug("", (Throwable)e);
            }
            finally {
                lock.unlock();
            }
        }
        if (!this.isRegistrationComplete()) {
            this.getAndSetAppIdAndSecret();
            appId = this.getAppID();
            String appSecret = this.getAppSecret();
            try {
                int statusCode = this.postConfigToSynchrony(appId, appSecret);
                int debugStatusCode = this.postConfigToSynchrony(appId + SYNCHRONY_DEBUG_SUFFIX, appSecret);
                if (statusCode != 200 || debugStatusCode != 200) {
                    this.eventPublisher.publish((Object)new ConfigFailureEvent());
                    return false;
                }
                this.setRegistrationComplete();
                this.eventPublisher.publish((Object)new ConfigSuccessEvent());
            }
            catch (Exception e) {
                this.eventPublisher.publish((Object)new ConfigFailureEvent());
                return false;
            }
        }
        return true;
    }

    void getAndSetAppIdAndSecret() {
        if (this.getAppID() == null || this.getAppSecret() == null) {
            ClusterLock lock = this.getRegistrationLock();
            try {
                if (lock.tryLock(5000L, TimeUnit.MILLISECONDS)) {
                    if (this.getAppID() == null) {
                        this.setAppId(this.generateAppID());
                    }
                    if (this.getAppSecret() == null) {
                        this.setAppSecret(this.generateAppSecret());
                    }
                } else {
                    logger.warn("{} Could not obtain lock when generating Synchrony credentials.", (Object)LOG_TAG);
                }
            }
            catch (InterruptedException e) {
                logger.warn("{} Thread interrupted: Could not obtain lock when generating Synchrony credentials.", (Object)LOG_TAG);
                logger.debug("", (Throwable)e);
            }
            finally {
                lock.unlock();
            }
        }
    }

    @Override
    public boolean retrievePublicKey() {
        boolean success = false;
        String pubKeyEndpoint = this.getBaseUrl() + SYNCHRONY_PUBLIC_KEY_ENDPOINT;
        HttpGet getKey = new HttpGet(pubKeyEndpoint);
        try (CloseableHttpResponse response = this.synchronyHttpClientFactory.get().execute((HttpUriRequest)getKey);){
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String pk = EntityUtils.toString((HttpEntity)response.getEntity(), (Charset)ContentType.TEXT_PLAIN.getCharset());
                if (pk != null && !pk.isEmpty()) {
                    this.setSynchronyPublicKey(pk);
                    success = true;
                }
            } else {
                logger.info("{} Could not retrieve public key for real-time collaboration service at {}. Status code: {}", new Object[]{LOG_TAG, pubKeyEndpoint, statusCode});
            }
            EntityUtils.consume((HttpEntity)response.getEntity());
        }
        catch (IOException e) {
            logger.info("{} Could not retrieve public key for real-time collaboration service at {} with exception: {}", new Object[]{LOG_TAG, pubKeyEndpoint, e.getMessage()});
            logger.trace("", (Throwable)e);
        }
        return success;
    }

    @Override
    public int postConfigToSynchrony(String appid, String secret) throws Exception {
        int statusCode;
        String verifiedServiceURL = this.getInternalServiceUrl();
        String authToken = System.getProperty(SYNCHRONY_SERVICE_AUTH_TOKEN_SYS_PROP);
        HttpPost getSecret = new HttpPost(verifiedServiceURL + SYNCHRONY_SECRET_ENDPOINT);
        HashMap<String, String> payload = new HashMap<String, String>();
        payload.put("appid", appid);
        payload.put("secret", secret);
        payload.put("auth-token", authToken);
        ObjectMapper objectMapper = new ObjectMapper();
        getSecret.setEntity((HttpEntity)new StringEntity(objectMapper.writeValueAsString(payload)));
        getSecret.setHeader("content-type", "application/json");
        try (CloseableHttpResponse secretResponse = this.synchronyHttpClientFactory.get().execute((HttpUriRequest)getSecret);){
            statusCode = secretResponse.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                logger.info("{} Synchrony response ({}): Could not verify {} AppID/Secret for real-time collaboration service at endpoint: {}{}", new Object[]{LOG_TAG, statusCode, this.isDebug() ? " debug" : "", verifiedServiceURL, SYNCHRONY_SECRET_ENDPOINT});
            }
            this.logJSONResponseField(appid, secretResponse, SYNCHRONY_RESPONSE_MESSAGE_KEY);
            EntityUtils.consume((HttpEntity)secretResponse.getEntity());
        }
        catch (ConnectException e) {
            logger.info("{} Synchrony connection failure: Could not generate/verify AppID/Secret for real-time collaboration service at {}", (Object)LOG_TAG, (Object)verifiedServiceURL);
            statusCode = 503;
        }
        return statusCode;
    }

    public void logJSONResponseField(String appid, CloseableHttpResponse response, String jsonField) throws Exception {
        String responseBody = EntityUtils.toString((HttpEntity)response.getEntity(), (Charset)ContentType.APPLICATION_JSON.getCharset());
        Map responseMap = (Map)this.jackson.readValue(responseBody, JSON_MAP_RESPONSE_TYPE);
        if (responseMap.containsKey(jsonField)) {
            logger.warn("{} Response message for {}: {}", new Object[]{LOG_TAG, appid, responseMap.get(jsonField)});
        }
    }

    public static byte[] generateSecureRandomBytes(int length) {
        SecureRandom secRand;
        try {
            secRand = SecureRandom.getInstance("NativePRNG");
        }
        catch (NoSuchAlgorithmException e) {
            secRand = new SecureRandom();
        }
        byte[] bytes = new byte[length];
        secRand.nextBytes(bytes);
        return bytes;
    }

    @Override
    public boolean isSharedDraftsEnabled() {
        return this.darkFeaturesManager.getSiteDarkFeatures().isFeatureEnabled("site-wide.shared-drafts");
    }

    @Override
    @Deprecated
    public boolean isSynchronyExplicitlyDisabled() {
        return this.isSharedDraftsExplicitlyDisabled();
    }

    @Override
    public boolean isSharedDraftsExplicitlyDisabled() {
        return this.darkFeaturesManager.getSiteDarkFeatures().isFeatureEnabled("site-wide.shared-drafts.disable");
    }

    @Override
    public boolean isSynchronyProdOverrideEnabled() {
        return this.darkFeaturesManager.getSiteDarkFeatures().isFeatureEnabled("site-wide.synchrony-prod-override");
    }

    @Override
    public boolean isSynchronyEncryptionEnabled() {
        return !Boolean.parseBoolean(System.getProperty(SYNCHRONY_ENCRYPTION_DISABLED, "false")) && (!this.applicationConfiguration.getBooleanProperty((Object)SYNCHRONY_ENCRYPTION_DISABLED) || Boolean.parseBoolean(System.getProperty(SYNCHRONY_ENABLE_DATA_ENCRYPTION_SYS_PROP, "false")));
    }

    @Override
    @Deprecated
    public void enableSynchrony() {
    }

    @Override
    @Deprecated
    public void disableSynchrony() {
    }

    private void enableSiteFeature(String featureKey) {
        try {
            this.darkFeaturesManager.enableSiteFeature(featureKey);
        }
        catch (UnknownFeatureException e) {
            logger.error("{} {}", (Object)LOG_TAG, (Object)e.getMessage());
        }
    }

    private void disableSiteFeature(String featureKey) {
        try {
            this.darkFeaturesManager.disableSiteFeature(featureKey);
        }
        catch (UnknownFeatureException e) {
            logger.error("{} {}", (Object)LOG_TAG, (Object)e.getMessage());
        }
    }

    private ClusterLock getRegistrationLock() {
        return this.clusterLockService.getLockForName(SYNCHRONY_REGISTRATION_BANDANA_CACHE_LOCK_NAME);
    }

    private ClusterLock getPassphraseLock() {
        return this.clusterLockService.getLockForName(SYNCHRONY_PASSPHRASE_BANDANA_CACHE_LOCK_NAME);
    }

    @Override
    public int getInternalPort() {
        return this.internalPort;
    }

    @Override
    public void setInternalPort(int localPort) {
        this.internalPort = localPort;
    }

    @Override
    public void disableSharedDrafts() {
        logger.warn("{} Disabling Shared Drafts", (Object)LOG_TAG);
        this.disableSiteFeature("site-wide.shared-drafts");
        this.enableSiteFeature("site-wide.shared-drafts.disable");
    }

    @Override
    public void enableSharedDrafts() {
        logger.warn("{} Enabling Shared Drafts", (Object)LOG_TAG);
        this.disableSiteFeature("site-wide.shared-drafts.disable");
        this.enableSiteFeature("site-wide.shared-drafts");
    }
}

