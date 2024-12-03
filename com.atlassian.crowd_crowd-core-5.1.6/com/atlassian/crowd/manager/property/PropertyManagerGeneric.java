/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.dao.property.PropertyDAO
 *  com.atlassian.crowd.event.configuration.AuditLogConfigurationUpdatedEvent
 *  com.atlassian.crowd.event.configuration.ConfigurationPropertyUpdatedEvent
 *  com.atlassian.crowd.event.configuration.LookAndFeelUpdatedEvent
 *  com.atlassian.crowd.event.configuration.SmtpServerUpdatedEvent
 *  com.atlassian.crowd.exception.ObjectNotFoundException
 *  com.atlassian.crowd.integration.Constants
 *  com.atlassian.crowd.manager.audit.AuditLogConfiguration
 *  com.atlassian.crowd.manager.audit.RetentionPeriod
 *  com.atlassian.crowd.manager.authentication.ImmutableCrowdSpecificRememberMeSettings
 *  com.atlassian.crowd.manager.property.PropertyManagerException
 *  com.atlassian.crowd.manager.rememberme.CrowdSpecificRememberMeSettings
 *  com.atlassian.crowd.model.authentication.CookieConfiguration
 *  com.atlassian.crowd.model.backup.BackupConfiguration
 *  com.atlassian.crowd.model.lookandfeel.LookAndFeelConfiguration
 *  com.atlassian.crowd.model.property.Property
 *  com.atlassian.crowd.util.ImageInfo
 *  com.atlassian.crowd.util.mail.SMTPServer
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.google.common.base.Throwables
 *  com.google.common.collect.ImmutableList
 *  com.google.common.primitives.Longs
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.mail.internet.InternetAddress
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.crowd.manager.property;

import com.atlassian.crowd.dao.property.PropertyDAO;
import com.atlassian.crowd.event.configuration.AuditLogConfigurationUpdatedEvent;
import com.atlassian.crowd.event.configuration.ConfigurationPropertyUpdatedEvent;
import com.atlassian.crowd.event.configuration.LookAndFeelUpdatedEvent;
import com.atlassian.crowd.event.configuration.SmtpServerUpdatedEvent;
import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.integration.Constants;
import com.atlassian.crowd.manager.audit.AuditLogConfiguration;
import com.atlassian.crowd.manager.audit.RetentionPeriod;
import com.atlassian.crowd.manager.authentication.ImmutableCrowdSpecificRememberMeSettings;
import com.atlassian.crowd.manager.property.InternalPropertyManager;
import com.atlassian.crowd.manager.property.PropertyManagerException;
import com.atlassian.crowd.manager.rememberme.CrowdSpecificRememberMeSettings;
import com.atlassian.crowd.model.authentication.CookieConfiguration;
import com.atlassian.crowd.model.backup.BackupConfiguration;
import com.atlassian.crowd.model.lookandfeel.LookAndFeelConfiguration;
import com.atlassian.crowd.model.property.Property;
import com.atlassian.crowd.util.ImageInfo;
import com.atlassian.crowd.util.mail.SMTPServer;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Longs;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.net.URI;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.mail.internet.InternetAddress;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PropertyManagerGeneric
implements InternalPropertyManager {
    private static final Logger logger = LoggerFactory.getLogger(PropertyManagerGeneric.class);
    private static final int DEFAULT_SESSION_TIME_IN_MINUTES = 5;
    public static final int DEFAULT_SCHEDULED_BACKUP_HOUR = 2;
    public static final int DEFAULT_SCHEDULED_BACKUP_MINUTE = 0;
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    public static final int MAX_LOGO_SIZE_IN_BYTES = 0xA00000;
    public static final String LOGO_IMAGE_KEY = "logo_image_info_property_key";
    static final String BASE64_FILE_PROPERTY_NAME_FORMAT = "file.%s";
    public static final String IMAGE_TOO_LARGE_ERROR_MESSAGE = "The image you\u2019re trying to upload is larger than %s MB.";
    private final PropertyDAO propertyDAO;
    private final EventPublisher eventPublisher;

    public PropertyManagerGeneric(PropertyDAO propertyDAO, EventPublisher eventPublisher) {
        this.propertyDAO = propertyDAO;
        this.eventPublisher = eventPublisher;
    }

    public String getDeploymentTitle() throws PropertyManagerException {
        return this.getPropertyInternal("deployment.title");
    }

    public void setDeploymentTitle(String title) {
        this.setProperty("deployment.title", title);
    }

    public String getDomain() {
        return this.getString("domain", null);
    }

    public void setDomain(String domain) {
        this.setProperty("domain", domain);
    }

    public boolean isSecureCookie() {
        return this.getBoolean("secure.cookie", false);
    }

    public void setSecureCookie(boolean secure) {
        this.setBooleanProperty("secure.cookie", this.isSecureCookie(), secure);
    }

    public void setCacheEnabled(boolean enabled) {
        this.setBooleanProperty("cache.enabled", this.isCacheEnabled(), enabled);
    }

    public boolean isCacheEnabled() {
        return this.getBoolean("cache.enabled", false);
    }

    public long getSessionTime() {
        return this.getOptionalProperty("session.time").map(Longs::tryParse).map(TimeUnit.MILLISECONDS::toMinutes).orElse(5L);
    }

    public void setSessionTime(long time) {
        this.setProperty("session.time", Long.toString(TimeUnit.MINUTES.toMillis(time)));
    }

    public SMTPServer getSMTPServer() throws PropertyManagerException {
        InternetAddress fromAddress = this.getPropertyInternal("mailserver.sender", InternetAddress::new);
        String prefix = this.getString("mailserver.prefix", null);
        String jndiLocation = this.getString("mailserver.jndi", null);
        return Strings.isNullOrEmpty((String)jndiLocation) ? this.buildSMTPServer(fromAddress, prefix) : new SMTPServer(jndiLocation, fromAddress, prefix);
    }

    private SMTPServer buildSMTPServer(InternetAddress fromAddress, String prefix) throws PropertyManagerException {
        String host = this.getPropertyInternal("mailserver.host");
        String password = this.getString("mailserver.password", null);
        String username = this.getString("mailserver.username", null);
        int port = this.getIntOrThrowIllegalArgumentException("mailserver.port", 25);
        boolean useSSL = this.getBoolean("mailserver.usessl", false);
        int timeout = this.getIntOrThrowIllegalArgumentException("mailserver.timeout", 60);
        boolean startTLS = this.getBoolean("mailserver.startTLS", false);
        return SMTPServer.builder().setPort(port).setPrefix(prefix).setFrom(fromAddress).setPassword(password).setUsername(username).setHost(host).setUseSSL(useSSL).setTimeout(timeout).setStartTLS(startTLS).build();
    }

    public void setSMTPServer(SMTPServer server) {
        SMTPServer oldValue = this.safeGetSMTPServer();
        this.setProperty("mailserver.prefix", server.getPrefix(), false);
        this.setProperty("mailserver.sender", server.getFrom().toString(), false);
        if (StringUtils.isNotBlank((CharSequence)server.getJndiLocation())) {
            this.setProperty("mailserver.jndi", server.getJndiLocation(), false);
            this.setProperty("mailserver.host", "", false);
            this.setProperty("mailserver.password", "", false);
            this.setProperty("mailserver.username", "", false);
            this.setProperty("mailserver.port", "", false);
            this.setProperty("mailserver.usessl", "", false);
            this.setProperty("mailserver.timeout", "", false);
            this.setProperty("mailserver.startTLS", "", false);
        } else {
            this.setProperty("mailserver.host", server.getHost(), false);
            this.setProperty("mailserver.password", server.getPassword(), false);
            this.setProperty("mailserver.username", server.getUsername(), false);
            this.setProperty("mailserver.port", String.valueOf(server.getPort()), false);
            this.setProperty("mailserver.usessl", String.valueOf(server.getUseSSL()), false);
            this.setProperty("mailserver.timeout", String.valueOf(server.getTimeout()), false);
            this.setProperty("mailserver.startTLS", String.valueOf(server.isStartTLS()), false);
            this.setProperty("mailserver.jndi", "", false);
        }
        SMTPServer newValue = this.safeGetSMTPServer();
        if (!Objects.equals(oldValue, newValue)) {
            this.eventPublisher.publish((Object)new SmtpServerUpdatedEvent(oldValue, newValue));
        }
    }

    private SMTPServer safeGetSMTPServer() {
        try {
            return this.getSMTPServer();
        }
        catch (PropertyManagerException e) {
            return null;
        }
    }

    public Key getDesEncryptionKey() throws PropertyManagerException {
        return this.getPropertyInternal("des.encryption.key", keyStr -> {
            DESKeySpec ks = new DESKeySpec(Base64.decodeBase64((String)keyStr));
            return SecretKeyFactory.getInstance("DES").generateSecret(ks);
        });
    }

    @SuppressFBWarnings(value={"DES_USAGE"}, justification="Only used for DESPasswordEncoder, which is not used by default")
    public void generateDesEncryptionKey() throws PropertyManagerException {
        if (this.getOptionalProperty("des.encryption.key").isPresent()) {
            return;
        }
        try {
            SecretKey key = KeyGenerator.getInstance("DES").generateKey();
            this.setProperty("des.encryption.key", Base64.encodeBase64String((byte[])key.getEncoded()));
        }
        catch (NoSuchAlgorithmException e) {
            throw new PropertyManagerException(e.getMessage(), (Throwable)e);
        }
    }

    @Deprecated
    public void setSMTPTemplate(String template) {
        this.setProperty("mailserver.message.template", template);
    }

    @Deprecated
    public String getSMTPTemplate() throws PropertyManagerException {
        return this.getPropertyInternal("mailserver.message.template");
    }

    public void setCurrentLicenseResourceTotal(int total) {
        this.setProperty("current.license.resource.total", Integer.toString(total), false);
    }

    public int getCurrentLicenseResourceTotal() {
        try {
            return this.getInt("current.license.resource.total", 0);
        }
        catch (Exception e) {
            logger.debug("Failed to find current resource total.", (Throwable)e);
            return 0;
        }
    }

    public void setNotificationEmail(String notificationEmail) {
        this.setNotificationEmails((List<String>)ImmutableList.of((Object)notificationEmail));
    }

    @Override
    public void setNotificationEmails(List<String> serverAlertAddresses) {
        try {
            this.setProperty("notification.email", JSON_MAPPER.writeValueAsString(serverAlertAddresses));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getNotificationEmail() throws PropertyManagerException {
        try {
            Collection notificationEmails = this.getNotificationEmails();
            return (String)notificationEmails.get(0);
        }
        catch (Exception e) {
            throw new PropertyManagerException(e.getMessage(), (Throwable)e);
        }
    }

    public List<String> getNotificationEmails() throws PropertyManagerException {
        return (List)this.getPropertyInternal("notification.email", value -> ImmutableList.copyOf((Object[])((Object[])JSON_MAPPER.readValue(value, String[].class))));
    }

    public boolean isGzipEnabled() throws PropertyManagerException {
        return true;
    }

    public void setGzipEnabled(boolean gzip) {
    }

    public Integer getBuildNumber() throws PropertyManagerException {
        return this.getPropertyInternal("build.number", Integer::valueOf);
    }

    public void setBuildNumber(Integer buildNumber) {
        this.setProperty("build.number", buildNumber.toString());
    }

    public String getTrustedProxyServers() throws PropertyManagerException {
        return this.getPropertyInternal("trusted.proxy.servers");
    }

    public void setTrustedProxyServers(String proxyServers) {
        this.setProperty("trusted.proxy.servers", proxyServers);
    }

    public void setAuditLogConfiguration(AuditLogConfiguration newConfiguration) {
        AuditLogConfiguration oldConfiguration = this.getAuditLogConfiguration();
        if (!Objects.equals(oldConfiguration, newConfiguration)) {
            this.setProperty("audit.log.retention.period", newConfiguration.getRetentionPeriod().name(), false);
            this.eventPublisher.publish((Object)new AuditLogConfigurationUpdatedEvent(oldConfiguration, newConfiguration));
        }
    }

    public AuditLogConfiguration getAuditLogConfiguration() {
        try {
            return new AuditLogConfiguration(RetentionPeriod.valueOf((String)this.getProperty("audit.log.retention.period")));
        }
        catch (ObjectNotFoundException | IllegalArgumentException e) {
            if (e instanceof IllegalArgumentException) {
                logger.warn("Found invalid audit log retention period persisted in database, using the default instead", e);
            }
            return AuditLogConfiguration.defaultConfiguration();
        }
    }

    public boolean isUsingDatabaseTokenStorage() {
        return this.getBoolean("database.token.storage.enabled", true);
    }

    public void setUsingDatabaseTokenStorage(boolean isUsingDatabaseTokenStorage) {
        this.setBooleanProperty("database.token.storage.enabled", this.isUsingDatabaseTokenStorage(), isUsingDatabaseTokenStorage);
    }

    public boolean isIncludeIpAddressInValidationFactors() {
        return this.getBoolean("validation.factors.include_ip_address", true);
    }

    public boolean isUseWebAvatars() {
        return this.getBoolean("webavatars.enabled", false);
    }

    public void setUseWebAvatars(boolean useWebAvatars) {
        this.setBooleanProperty("webavatars.enabled", this.isUseWebAvatars(), useWebAvatars);
    }

    public CookieConfiguration getCookieConfiguration() {
        String cookieName = this.getString("cookie.tokenkey", Constants.COOKIE_TOKEN_KEY);
        return new CookieConfiguration(this.getDomain(), this.isSecureCookie(), cookieName);
    }

    public void setCookieConfiguration(CookieConfiguration cookieConfiguration) {
        this.setDomain(cookieConfiguration.getDomain());
        this.setSecureCookie(cookieConfiguration.isSecure());
        this.setProperty("cookie.tokenkey", cookieConfiguration.getName());
    }

    public void setIncludeIpAddressInValidationFactors(boolean includeIpAddressInValidationFactors) {
        this.setBooleanProperty("validation.factors.include_ip_address", this.isIncludeIpAddressInValidationFactors(), includeIpAddressInValidationFactors);
    }

    @Override
    public void setRememberMeConfiguration(CrowdSpecificRememberMeSettings configuration) {
        this.setProperty("rememberme.enabled", Boolean.toString(configuration.isEnabled()));
        this.setProperty("rememberme.duration", Long.toString(configuration.getExpirationDuration().getSeconds()));
    }

    @Override
    public CrowdSpecificRememberMeSettings getRememberMeConfiguration() {
        boolean enabled = this.getBoolean("rememberme.enabled", true);
        Duration expirationDuration = this.getOptionalProperty("rememberme.duration").map(Long::parseLong).map(Duration::ofSeconds).orElse(CrowdSpecificRememberMeSettings.DEFAULT_EXPIRATION_DURATION);
        return new ImmutableCrowdSpecificRememberMeSettings(enabled, expirationDuration);
    }

    public void removeProperty(String name) {
        this.propertyDAO.remove("crowd", name);
    }

    protected Property getPropertyObject(String name) throws ObjectNotFoundException {
        return this.propertyDAO.find("crowd", name);
    }

    void setBooleanProperty(String name, boolean from, boolean to) {
        this.setProperty(name, Boolean.toString(to), false);
        if (from != to) {
            this.eventPublisher.publish((Object)new ConfigurationPropertyUpdatedEvent(name, Boolean.toString(from), Boolean.toString(to)));
        }
    }

    public String getProperty(String name) throws ObjectNotFoundException {
        Property property = this.getPropertyObject(name);
        return property.getValue();
    }

    public Optional<String> getOptionalProperty(String name) {
        try {
            return Optional.ofNullable(this.getProperty(name));
        }
        catch (ObjectNotFoundException e) {
            return Optional.empty();
        }
    }

    private String getPropertyInternal(String name) throws PropertyManagerException {
        try {
            return this.getProperty(name);
        }
        catch (ObjectNotFoundException e) {
            throw new PropertyManagerException(e.getMessage(), (Throwable)e);
        }
    }

    private <T> T getPropertyInternal(String name, PropertyTransformer<T> transformer) throws PropertyManagerException {
        try {
            return transformer.apply(this.getProperty(name));
        }
        catch (Exception e) {
            Throwables.propagateIfPossible((Throwable)e, PropertyManagerException.class);
            throw new PropertyManagerException(e.getMessage(), (Throwable)e);
        }
    }

    public void setProperty(String name, String value) {
        this.setProperty(name, value, true);
    }

    @VisibleForTesting
    void setProperty(String name, String value, boolean publishEvent) {
        String oldValue;
        Property property = null;
        try {
            property = this.getPropertyObject(name);
        }
        catch (ObjectNotFoundException objectNotFoundException) {
            // empty catch block
        }
        if (property == null) {
            property = new Property("crowd", name, value);
            oldValue = null;
        } else {
            oldValue = property.getValue();
            property.setValue(value);
        }
        this.propertyDAO.update(property);
        String oldValueOrNull = Strings.emptyToNull((String)oldValue);
        String newValueOrNull = Strings.emptyToNull((String)value);
        if (publishEvent && !Objects.equals(oldValueOrNull, newValueOrNull)) {
            this.eventPublisher.publish((Object)new ConfigurationPropertyUpdatedEvent(name, oldValueOrNull, newValueOrNull));
        }
    }

    public String getString(String property, String defaultValue) {
        return this.getOptionalProperty(property).orElse(defaultValue);
    }

    public boolean getBoolean(String property, boolean defaultValue) {
        return this.getOptionalProperty(property).map(Boolean::valueOf).orElse(defaultValue);
    }

    public int getInt(String property, int defaultValue) {
        return this.getInt(property, defaultValue, true);
    }

    public int getIntOrThrowIllegalArgumentException(String property, int defaultValue) {
        return this.getInt(property, defaultValue, false);
    }

    private int getInt(String property, int defaultValue, boolean logOnly) throws IllegalArgumentException {
        Optional<String> value = this.getOptionalProperty(property);
        try {
            return value.map(Integer::parseInt).orElse(defaultValue);
        }
        catch (NumberFormatException e) {
            if (logOnly) {
                logger.warn("Corrupted value found for property {}. Found {} instead of an integer", (Object)property, value.orElse(null));
                return defaultValue;
            }
            throw new IllegalArgumentException(property + " is not a valid number", e);
        }
    }

    public void setBaseUrl(URI url) {
        Preconditions.checkArgument((boolean)url.isAbsolute(), (Object)"Base url needs to be absolute");
        this.setProperty("base.url", url.toString());
    }

    public URI getBaseUrl() throws PropertyManagerException {
        return this.getPropertyInternal("base.url", URI::new);
    }

    public Optional<Long> getPrivateKeyCertificatePairToSign() {
        return this.getOptionalProperty("saml.key.certificate.pair.id").map(Long::parseLong);
    }

    public void setPrivateKeyCertificateToSign(long privateKeyCertificatePairId) {
        this.setProperty("saml.key.certificate.pair.id", String.valueOf(privateKeyCertificatePairId), false);
    }

    public BackupConfiguration getBackupConfiguration() {
        boolean restoreUsersFromConnectors = this.getBoolean("backup.export.users.from.connector.enabled", false);
        boolean scheduledBackupEnabled = this.getBoolean("backup.scheduled.enabled", true);
        boolean resetDomainEnabled = this.getBoolean("backup.reset.domain.enabled", true);
        int scheduledTimeHour = this.getInt("backup.scheduled.time.hour", 2);
        int scheduledTimeMinute = this.getInt("backup.scheduled.time.minute", 0);
        return BackupConfiguration.builder().setBackupConnectorEnabled(Boolean.valueOf(restoreUsersFromConnectors)).setScheduledBackupEnabled(Boolean.valueOf(scheduledBackupEnabled)).setResetDomainEnabled(Boolean.valueOf(resetDomainEnabled)).setBackupTimeHour(scheduledTimeHour).setBackupTimeMinute(scheduledTimeMinute).build();
    }

    public void saveBackupConfiguration(BackupConfiguration config) {
        this.setProperty("backup.export.users.from.connector.enabled", Boolean.toString(config.isBackupConnectorEnabled()));
        this.setProperty("backup.scheduled.enabled", Boolean.toString(config.isScheduledBackupEnabled()));
        this.setProperty("backup.reset.domain.enabled", Boolean.toString(config.isResetDomainEnabled()));
        this.setProperty("backup.scheduled.time.hour", String.valueOf(config.getBackupTimeHour()));
        this.setProperty("backup.scheduled.time.minute", String.valueOf(config.getBackupTimeMinute()));
    }

    @Nonnull
    public Optional<LookAndFeelConfiguration> getLookAndFeelConfiguration() throws PropertyManagerException {
        return this.getOptionalJsonProperty("lookandfeel.configuration", LookAndFeelConfiguration.class);
    }

    public synchronized void setLookAndFeelConfiguration(LookAndFeelConfiguration lookAndFeelConfiguration, ImageInfo updatedLogoInfo) throws PropertyManagerException {
        this.checkMaxLength(lookAndFeelConfiguration.getAnnouncementText(), 560, "announcementText");
        this.checkMaxLength(lookAndFeelConfiguration.getHeader(), 20, "header");
        this.checkMaxLength(lookAndFeelConfiguration.getWelcomeText(), 40, "welcomeText");
        Optional<LookAndFeelConfiguration> oldConfiguration = this.getLookAndFeelConfiguration();
        this.setJsonProperty("lookandfeel.configuration", lookAndFeelConfiguration);
        if (updatedLogoInfo != null) {
            this.checkImageMaxByteSize(updatedLogoInfo.getImageBase64(), 0xA00000, 10);
            this.saveBase64Image(LOGO_IMAGE_KEY, updatedLogoInfo);
        }
        this.eventPublisher.publish((Object)new LookAndFeelUpdatedEvent((LookAndFeelConfiguration)oldConfiguration.orElse(null), lookAndFeelConfiguration));
    }

    public void removeLookAndFeelConfiguration() throws PropertyManagerException {
        Optional<LookAndFeelConfiguration> lookAndFeelConfiguration = this.getLookAndFeelConfiguration();
        this.removeBase64File(LOGO_IMAGE_KEY);
        this.removeProperty("lookandfeel.configuration");
        lookAndFeelConfiguration.ifPresent(config -> this.eventPublisher.publish((Object)new LookAndFeelUpdatedEvent(config, null)));
    }

    public Optional<ImageInfo> getLogoImage() throws PropertyManagerException {
        return this.getOptionalJsonProperty(String.format(BASE64_FILE_PROPERTY_NAME_FORMAT, LOGO_IMAGE_KEY), ImageInfo.class);
    }

    private void saveBase64Image(String imageKey, ImageInfo imageInfo) throws PropertyManagerException {
        this.setJsonProperty(String.format(BASE64_FILE_PROPERTY_NAME_FORMAT, imageKey), imageInfo);
    }

    private void removeBase64File(@Nullable String imageKey) {
        if (imageKey != null) {
            this.removeProperty(String.format(BASE64_FILE_PROPERTY_NAME_FORMAT, imageKey));
        }
    }

    private void setJsonProperty(String property, Object json) throws PropertyManagerException {
        try {
            String asJson = JSON_MAPPER.writeValueAsString(json);
            this.setProperty(property, asJson, false);
        }
        catch (IOException e) {
            throw new PropertyManagerException((Throwable)e);
        }
    }

    private <T> Optional<T> getOptionalJsonProperty(String property, Class<T> clz) throws PropertyManagerException {
        try {
            String asJson = this.getProperty(property);
            return Optional.of(JSON_MAPPER.readValue(asJson, clz));
        }
        catch (ObjectNotFoundException e) {
            return Optional.empty();
        }
        catch (IOException e) {
            throw new PropertyManagerException((Throwable)e);
        }
    }

    private void checkImageMaxByteSize(String object, int maxByteSize, int maxMegaByteSize) {
        Preconditions.checkArgument((object.getBytes().length <= maxByteSize ? 1 : 0) != 0, (String)IMAGE_TOO_LARGE_ERROR_MESSAGE, (Object)String.valueOf(maxMegaByteSize));
    }

    private void checkMaxLength(@Nullable String text, int maxLength, String fieldName) {
        Preconditions.checkArgument((Strings.nullToEmpty((String)text).length() <= maxLength ? 1 : 0) != 0, (Object)String.format("'%s' length must be less than or equal to %d", fieldName, maxLength));
    }

    private static interface PropertyTransformer<T> {
        public T apply(String var1) throws Exception;
    }
}

