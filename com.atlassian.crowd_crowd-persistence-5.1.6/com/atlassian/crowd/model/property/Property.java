/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.Validate
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.model.property;

import com.atlassian.crowd.model.property.PropertyId;
import java.io.Serializable;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Property
implements Serializable {
    public static final String CROWD_PROPERTY_KEY = "crowd";
    public static final String CACHE_TIME = "cache.time";
    public static final String TOKEN_SEED = "token.seed";
    public static final String DEPLOYMENT_TITLE = "deployment.title";
    public static final String DOMAIN = "domain";
    public static final String CACHE_ENABLED = "cache.enabled";
    public static final String SESSION_TIME = "session.time";
    public static final String MAILSERVER_HOST = "mailserver.host";
    public static final String MAILSERVER_PREFIX = "mailserver.prefix";
    public static final String MAILSERVER_SENDER = "mailserver.sender";
    public static final String MAILSERVER_USERNAME = "mailserver.username";
    public static final String MAILSERVER_PASSWORD = "mailserver.password";
    public static final String FORGOTTEN_PASSWORD_EMAIL_TEMPLATE = "mailserver.message.template";
    public static final String FORGOTTEN_USERNAME_EMAIL_TEMPLATE = "email.template.forgotten.username";
    public static final String PASSWORD_EXPIRATION_REMINDER_EMAIL_TEMPLATE = "email.template.password.expiration.reminder";
    public static final String EMAIL_CHANGE_VALIDATION_EMAIL_TEMPLATE = "email.template.email.change.validation";
    public static final String EMAIL_CHANGE_INFO_EMAIL_TEMPLATE = "email.template.email.change.info";
    public static final String DES_ENCRYPTION_KEY = "des.encryption.key";
    public static final String CURRENT_LICENSE_RESOURCE_TOTAL = "current.license.resource.total";
    public static final String NOTIFICATION_EMAIL = "notification.email";
    public static final String BUILD_NUMBER = "build.number";
    @Deprecated
    public static final String GZIP_ENABLED = "gzip.enabled";
    public static final String TRUSTED_PROXY_SERVERS = "trusted.proxy.servers";
    public static final String DATABASE_TOKEN_STORAGE_ENABLED = "database.token.storage.enabled";
    public static final String USE_WEB_AVATARS = "webavatars.enabled";
    public static final String MAILSERVER_JNDI_LOCATION = "mailserver.jndi";
    public static final String MAILSERVER_START_TLS = "mailserver.startTLS";
    public static final String MAILSERVER_PORT = "mailserver.port";
    public static final String MAILSERVER_TIMEOUT = "mailserver.timeout";
    public static final String MAILSERVER_USE_SSL = "mailserver.usessl";
    public static final String INCLUDE_IP_ADDRESS_IN_VALIDATION_FACTORS = "validation.factors.include_ip_address";
    public static final String SECURE_COOKIE = "secure.cookie";
    public static final String SSO_COOKE_NAME_PROPERTY = "cookie.tokenkey";
    public static final String CROWD_BASE_URL = "base.url";
    public static final String AUDIT_LOG_RETENTION_PERIOD = "audit.log.retention.period";
    public static final String SAML_KEY_CERTIFICATE_PAIR_TO_SIGN = "saml.key.certificate.pair.id";
    public static final String REMEMBER_ME_ENABLED_PROPERTY_NAME = "rememberme.enabled";
    public static final String REMEMBER_ME_EXPIRY_IN_SECONDS_PROPERTY_NAME = "rememberme.duration";
    public static final String SCHEDULED_BACKUP_ENABLED = "backup.scheduled.enabled";
    public static final String EXPORT_USERS_FROM_CONNECTORS_DURING_BACKUP_ENABLED = "backup.export.users.from.connector.enabled";
    public static final String RESET_DOMAIN_FOR_BACKUP_ENABLED = "backup.reset.domain.enabled";
    public static final String BACKUP_SCHEDULED_TIME_HOUR = "backup.scheduled.time.hour";
    public static final String BACKUP_SCHEDULED_TIME_MINUTE = "backup.scheduled.time.minute";
    public static final String LOOK_AND_FEEL_CONFIGURATION_PROPERTY_NAME = "lookandfeel.configuration";
    public static final String CROWD_DEFAULT_ENCRYPTOR_PROPERTY = "crowd.encryption.encryptor.default";
    public static final String EMAIL_RENDERED_AS_HTML = "crowd.email.html.render";
    private PropertyId propertyId;
    private String value;

    public Property(String key, String name, String value) {
        Validate.notNull((Object)key, (String)"key cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)key, (String)"name cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)key, (String)"value cannot be null", (Object[])new Object[0]);
        this.propertyId = new PropertyId();
        this.propertyId.setKey(key);
        this.propertyId.setName(name);
        this.value = value;
    }

    protected Property() {
    }

    private PropertyId getPropertyId() {
        return this.propertyId;
    }

    private void setPropertyId(PropertyId propertyId) {
        this.propertyId = propertyId;
    }

    public String getKey() {
        return this.propertyId.getKey();
    }

    public String getName() {
        return this.propertyId.getName();
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Property)) {
            return false;
        }
        Property property = (Property)o;
        if (this.propertyId != null ? !this.propertyId.equals(property.propertyId) : property.propertyId != null) {
            return false;
        }
        return !(this.value != null ? !this.value.equals(property.value) : property.value != null);
    }

    public int hashCode() {
        int result = this.propertyId != null ? this.propertyId.hashCode() : 0;
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        return result;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("key", (Object)this.getKey()).append("name", (Object)this.getName()).append("value", (Object)this.getValue()).toString();
    }
}

