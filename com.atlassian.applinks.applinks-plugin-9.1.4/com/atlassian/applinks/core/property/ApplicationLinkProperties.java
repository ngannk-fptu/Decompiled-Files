/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.OAuth2ConnectionType
 *  com.atlassian.applinks.api.PropertySet
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.google.common.base.Function
 *  javax.annotation.Nullable
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.core.property;

import com.atlassian.applinks.api.OAuth2ConnectionType;
import com.atlassian.applinks.api.PropertySet;
import com.atlassian.applinks.spi.application.TypeId;
import com.google.common.base.Function;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationLinkProperties {
    private static final String AUTH_PROVIDER_PREFIX = "auth";
    private static final EnumSet<Property> STANDARD_PROPERTIES = EnumSet.allOf(Property.class);
    private final Lock customPropertyWriteLock = new ReentrantLock();
    private final Lock authenticationProviderWriteLock = new ReentrantLock();
    private static final Logger LOG = LoggerFactory.getLogger((String)ApplicationLinkProperties.class.getName());
    private static final Logger log = LoggerFactory.getLogger(ApplicationLinkProperties.class);
    private final PropertySet applinksAdminPropertySet;
    private final PropertySet applinksPropertySet;

    public ApplicationLinkProperties(PropertySet applinksAdminPropertySet, PropertySet applinksPropertySet) {
        this.applinksAdminPropertySet = applinksAdminPropertySet;
        this.applinksPropertySet = applinksPropertySet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setProperties(ApplicationLinkProperties props) {
        Objects.requireNonNull(props, "props must not be null");
        for (Property propertyKey : EnumSet.allOf(Property.class)) {
            Object value = props.applinksAdminPropertySet.getProperty(propertyKey.key());
            if (value == null) continue;
            this.logCopyingProperties(propertyKey, value, props);
            this.applinksAdminPropertySet.putProperty(propertyKey.key(), value);
            if (propertyKey == Property.PROPERTY_KEYS) {
                this.customPropertyWriteLock.lock();
                try {
                    for (String customPropertyKey : (List)value) {
                        Object customPropertyValue = props.applinksPropertySet.getProperty(customPropertyKey);
                        this.logCopyingChildProperty(propertyKey.key, customPropertyKey, customPropertyValue, props);
                        this.applinksPropertySet.putProperty(customPropertyKey, customPropertyValue);
                    }
                    continue;
                }
                finally {
                    this.customPropertyWriteLock.unlock();
                    continue;
                }
            }
            if (propertyKey != Property.AUTH_PROVIDER_KEYS) continue;
            this.authenticationProviderWriteLock.lock();
            try {
                for (String authProviderKey : (List)value) {
                    Object authProviderValue = props.applinksAdminPropertySet.getProperty(this.hashedAuthProviderKey(authProviderKey));
                    this.logCopyingChildProperty(propertyKey.key(), authProviderKey, authProviderValue, props);
                    this.applinksAdminPropertySet.putProperty(this.hashedAuthProviderKey(authProviderKey), authProviderValue);
                }
            }
            finally {
                this.authenticationProviderWriteLock.unlock();
            }
        }
    }

    private String hashedAuthProviderKey(String authProviderKey) {
        return "auth." + this.createHashedProviderKey(authProviderKey);
    }

    private String createHashedProviderKey(String providerKey) {
        String hashedValue = DigestUtils.md5Hex((String)providerKey);
        return hashedValue;
    }

    public TypeId getType() {
        String id = (String)this.applinksAdminPropertySet.getProperty(Property.TYPE.key());
        return id != null ? new TypeId(id) : null;
    }

    public void setType(TypeId type) {
        this.logSettingProperty(Property.TYPE.key(), type.get());
        this.applinksAdminPropertySet.putProperty(Property.TYPE.key(), (Object)type.get());
    }

    public String getName() {
        return (String)this.applinksAdminPropertySet.getProperty(Property.NAME.key());
    }

    public void setName(String name) {
        this.logSettingProperty(Property.NAME.key(), name.trim());
        this.applinksAdminPropertySet.putProperty(Property.NAME.key(), (Object)name.trim());
    }

    public URI getDisplayUrl() {
        return this.getUri(Property.DISPLAY_URL.key());
    }

    public void setDisplayUrl(URI url) {
        this.checkURLValid(url);
        this.logSettingProperty(Property.DISPLAY_URL.key(), url);
        this.setUri(Property.DISPLAY_URL.key(), url);
    }

    public void setDisplayUrl(String url) {
        this.logSettingProperty(Property.DISPLAY_URL.key(), url);
        this.setUri(Property.DISPLAY_URL.key(), url);
    }

    public URI getRpcUrl() {
        return this.getUri(Property.RPC_URL.key());
    }

    public void setRpcUrl(URI url) {
        this.checkURLValid(url);
        this.logSettingProperty(Property.RPC_URL.key(), url);
        this.setUri(Property.RPC_URL.key(), url);
    }

    public void setRpcUrl(String url) {
        this.logSettingProperty(Property.RPC_URL.key(), url);
        this.setUri(Property.RPC_URL.key(), url);
    }

    private void checkURLValid(URI uri) {
        if (uri != null) {
            try {
                boolean isValidURL;
                URL urlToValidate = uri.toURL();
                String urlProtocol = urlToValidate.getProtocol();
                boolean bl = isValidURL = "http".equals(urlProtocol) || "https".equals(urlProtocol);
                if (!isValidURL) {
                    throw new IllegalArgumentException("URL " + uri.toString() + " is not valid");
                }
            }
            catch (MalformedURLException e) {
                throw new IllegalArgumentException("URL " + uri.toString() + " is not valid");
            }
        }
    }

    public boolean isPrimary() {
        return Boolean.parseBoolean((String)this.applinksAdminPropertySet.getProperty(Property.PRIMARY.key()));
    }

    public void setIsPrimary(boolean isPrimary) {
        this.logSettingProperty(Property.PRIMARY.key(), isPrimary);
        this.applinksAdminPropertySet.putProperty(Property.PRIMARY.key(), (Object)String.valueOf(isPrimary));
    }

    public OAuth2ConnectionType getOAuth2ConnectionType() {
        String connectionType = (String)this.applinksPropertySet.getProperty(Property.OAUTH2_CONNECTION_TYPE.key());
        return connectionType != null ? OAuth2ConnectionType.valueOf((String)connectionType) : null;
    }

    public void setOAuth2ConnectionType(OAuth2ConnectionType oAuth2ConnectionType) {
        this.logSettingProperty(Property.OAUTH2_CONNECTION_TYPE.key(), oAuth2ConnectionType);
        this.applinksPropertySet.putProperty(Property.OAUTH2_CONNECTION_TYPE.key(), (Object)oAuth2ConnectionType.toString());
    }

    public boolean isSystem() {
        return Boolean.parseBoolean((String)this.applinksAdminPropertySet.getProperty(Property.SYSTEM.key())) || Boolean.parseBoolean((String)this.applinksPropertySet.getProperty(Property.SYSTEM.key()));
    }

    public void setSystem(boolean isSystem) {
        this.logSettingProperty(Property.SYSTEM.key(), isSystem);
        this.applinksAdminPropertySet.putProperty(Property.SYSTEM.key(), (Object)String.valueOf(isSystem));
        this.applinksPropertySet.putProperty(Property.SYSTEM.key(), (Object)String.valueOf(isSystem));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void remove() {
        try {
            this.authenticationProviderWriteLock.lock();
            List<String> providerKeys = this.getProviderKeys();
            for (String providerKey : providerKeys) {
                String hashedProviderKey = this.hashedAuthProviderKey(providerKey);
                this.logRemovingHashedProperty("ProviderConfig", providerKey, hashedProviderKey, new Function<String, Object>(){

                    public Object apply(@Nullable String hashedProviderKey) {
                        return ApplicationLinkProperties.this.applinksAdminPropertySet.getProperty(hashedProviderKey);
                    }
                });
                this.applinksAdminPropertySet.removeProperty(hashedProviderKey);
            }
            this.setProviderKeys(Collections.emptyList());
        }
        finally {
            this.authenticationProviderWriteLock.unlock();
        }
        try {
            this.customPropertyWriteLock.lock();
            for (String key : this.getCustomPropertyKeys()) {
                this.logRemovingProperty(key, new Function<String, Object>(){

                    public Object apply(@Nullable String key) {
                        return ApplicationLinkProperties.this.applinksPropertySet.getProperty(key);
                    }
                });
                this.applinksPropertySet.removeProperty(key);
            }
            this.setPropertyKeys(Collections.emptyList());
        }
        finally {
            this.customPropertyWriteLock.unlock();
        }
        for (Property standardProperty : STANDARD_PROPERTIES) {
            this.logRemovingProperty(standardProperty.key(), new Function<String, Object>(){

                public Object apply(@Nullable String key) {
                    return ApplicationLinkProperties.this.applinksAdminPropertySet.getProperty(key);
                }
            });
            this.applinksAdminPropertySet.removeProperty(standardProperty.key());
        }
        this.applinksPropertySet.removeProperty(Property.SYSTEM.key());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setProviderConfig(String providerKey, Map<String, String> config) {
        String hashedProviderKey = this.hashedAuthProviderKey(providerKey);
        this.logSettingHashedProperty("ProviderConfig", providerKey, hashedProviderKey, this.toProperties(config));
        this.applinksAdminPropertySet.putProperty(hashedProviderKey, (Object)this.toProperties(config));
        try {
            this.authenticationProviderWriteLock.lock();
            List<String> providerKeys = this.getProviderKeys();
            if (!providerKeys.contains(providerKey)) {
                providerKeys.add(providerKey);
                this.setProviderKeys(providerKeys);
            }
        }
        finally {
            this.authenticationProviderWriteLock.unlock();
        }
    }

    public void removeProviderConfig(String providerKey) {
        this.logRemovingHashedProperty("ProviderConfig", providerKey, this.hashedAuthProviderKey(providerKey), new Function<String, Object>(){

            public Object apply(@Nullable String key) {
                return ApplicationLinkProperties.this.applinksAdminPropertySet.getProperty(key);
            }
        });
        this.applinksAdminPropertySet.removeProperty(this.hashedAuthProviderKey(providerKey));
        try {
            this.authenticationProviderWriteLock.lock();
            List<String> providerKeys = this.getProviderKeys();
            providerKeys.remove(providerKey);
            this.setProviderKeys(providerKeys);
        }
        finally {
            this.authenticationProviderWriteLock.unlock();
        }
    }

    public Map<String, String> getProviderConfig(String providerKey) {
        log.debug("Getting provider config; key: {}", (Object)providerKey);
        Object obj = this.applinksAdminPropertySet.getProperty(this.hashedAuthProviderKey(providerKey));
        if (obj == null || !(obj instanceof Properties)) {
            return null;
        }
        return this.toMap((Properties)obj);
    }

    public List<String> getProviderKeys() {
        ArrayList list = (ArrayList)this.applinksAdminPropertySet.getProperty(Property.AUTH_PROVIDER_KEYS.key());
        if (list == null) {
            list = new ArrayList();
        }
        return list;
    }

    public Object getProperty(String key) {
        return this.applinksPropertySet.getProperty(key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object putProperty(String key, Object value) {
        this.logPuttingProperty(key, value);
        Object oldValue = this.applinksPropertySet.putProperty(key, value);
        try {
            this.customPropertyWriteLock.lock();
            List<String> propertyKeys = this.getCustomPropertyKeys();
            if (!propertyKeys.contains(key)) {
                propertyKeys.add(key);
                this.setPropertyKeys(propertyKeys);
            }
        }
        finally {
            this.customPropertyWriteLock.unlock();
        }
        return oldValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object removeProperty(String key) {
        this.logRemovingProperty(key, new Function<String, Object>(){

            public Object apply(@Nullable String key) {
                return ApplicationLinkProperties.this.applinksPropertySet.getProperty(key);
            }
        });
        Object removedValue = this.applinksPropertySet.removeProperty(key);
        if (removedValue != null) {
            try {
                this.customPropertyWriteLock.lock();
                List<String> properties = this.getCustomPropertyKeys();
                properties.remove(key);
                this.setPropertyKeys(properties);
            }
            finally {
                this.customPropertyWriteLock.unlock();
            }
        }
        return removedValue;
    }

    private List<String> getCustomPropertyKeys() {
        ArrayList customPropertyKeys = (ArrayList)this.applinksAdminPropertySet.getProperty(Property.PROPERTY_KEYS.key());
        if (customPropertyKeys == null) {
            customPropertyKeys = new ArrayList();
        }
        return customPropertyKeys;
    }

    private void setPropertyKeys(List<String> customPropertyKeys) {
        this.logSettingProperty(Property.PROPERTY_KEYS.key(), customPropertyKeys);
        this.applinksAdminPropertySet.putProperty(Property.PROPERTY_KEYS.key(), customPropertyKeys);
    }

    private void setUri(String key, URI uri) {
        this.applinksAdminPropertySet.putProperty(key, (Object)(uri != null ? uri.toString() : null));
    }

    private void setUri(String key, String uri) {
        this.applinksAdminPropertySet.putProperty(key, (Object)uri);
    }

    private URI getUri(String key) {
        String uri = (String)this.applinksAdminPropertySet.getProperty(key);
        if (uri == null) {
            return null;
        }
        try {
            return new URI(uri);
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Failed to deserialise stored %s URI (%s) reason: %s", key, uri, e.getReason()));
        }
    }

    private void setProviderKeys(List<String> providerKeys) {
        this.logSettingProperty(Property.AUTH_PROVIDER_KEYS.key(), providerKeys);
        this.applinksAdminPropertySet.putProperty(Property.AUTH_PROVIDER_KEYS.key(), providerKeys);
    }

    public boolean authProviderIsConfigured(String providerKey) {
        return this.getProviderKeys().contains(providerKey);
    }

    private Properties toProperties(Map<String, String> map) {
        Properties props = new Properties();
        props.putAll(map);
        return props;
    }

    private Map<String, String> toMap(Properties props) {
        return Collections.unmodifiableMap(new HashMap<Object, Object>(props));
    }

    private void logSettingProperty(String property, Object value) {
        this.logStoringProperty("Setting", property, value);
    }

    private void logPuttingProperty(String property, Object value) {
        this.logStoringProperty("Putting", property, value);
    }

    private void logStoringProperty(String context, String property, Object value) {
        if (LOG.isDebugEnabled()) {
            String message = String.format("%s %s property with value [%s]", context, property, value.toString());
            LOG.debug(message);
        }
    }

    private void logCopyingProperties(Property Key2, Object value, ApplicationLinkProperties applicationLinkProperties) {
        if (LOG.isDebugEnabled()) {
            String message = String.format("Copying key [%s] with value [%s] from link to [%s]", Key2.key(), value, applicationLinkProperties.getRpcUrl().toASCIIString());
            LOG.debug(message);
        }
    }

    private void logCopyingChildProperty(String childType, String property, Object value, ApplicationLinkProperties applicationLinkProperties) {
        if (LOG.isDebugEnabled()) {
            String message = String.format("Copying %s property [%s] with value [%s] from link to [%s]", childType, property, value, applicationLinkProperties.getRpcUrl().toASCIIString());
            LOG.debug(message);
        }
    }

    private void logRemovingHashedProperty(String context, String property, String hashedProperty, Function<String, Object> valueFunction) {
        if (LOG.isDebugEnabled()) {
            String message = String.format("Removing %s property [%s] hashedProperty [%s] with value [%s]", context, property, hashedProperty, valueFunction.apply((Object)hashedProperty));
            LOG.debug(message);
        }
    }

    private void logRemovingProperty(String property, Function<String, Object> valueFunction) {
        if (LOG.isDebugEnabled()) {
            String message = String.format("Removing property [%s] with value [%s]", property, valueFunction.apply((Object)property));
            LOG.debug(message);
        }
    }

    private void logSettingHashedProperty(String property, String hashedProperty, Function<String, Object> valueFunction) {
        if (LOG.isDebugEnabled()) {
            String message = String.format("Setting property [%s] hashedProperty [%s] with value [%s]", property, hashedProperty, valueFunction.apply((Object)hashedProperty));
            LOG.debug(message);
        }
    }

    private void logSettingHashedProperty(String context, String property, String hashedProperty, Object value) {
        if (LOG.isDebugEnabled()) {
            String message = String.format("Setting %s property [%s] hashedProperty [%s] with value [%s]", context, property, hashedProperty, value);
            LOG.debug(message);
        }
    }

    public static enum Property {
        TYPE("type"),
        NAME("name"),
        DISPLAY_URL("display.url"),
        RPC_URL("rpc.url"),
        PRIMARY("primary"),
        AUTH_PROVIDER_KEYS("providerKeys"),
        SYSTEM("system"),
        PROPERTY_KEYS("propertyKeys"),
        OAUTH2_CONNECTION_TYPE("oauth2ConnectionType");

        private final String key;

        private Property(String key) {
            this.key = key;
        }

        public String key() {
            return this.key;
        }
    }
}

