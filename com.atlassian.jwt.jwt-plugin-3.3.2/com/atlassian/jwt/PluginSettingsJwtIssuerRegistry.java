/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt;

import com.atlassian.jwt.JwtIssuer;
import com.atlassian.jwt.MutableJwtRegistry;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public class PluginSettingsJwtIssuerRegistry
implements MutableJwtRegistry {
    static final String DEFAULT_PREFIX = "jwt";
    static final String PROP_NAME = "name";
    static final String PROP_SECRET = "secret";
    private final PluginSettings pluginSettings;
    private final String settingPrefix;

    public PluginSettingsJwtIssuerRegistry(PluginSettingsFactory factory) {
        this(factory, DEFAULT_PREFIX);
    }

    public PluginSettingsJwtIssuerRegistry(PluginSettingsFactory factory, String prefix) {
        this.pluginSettings = factory.createGlobalSettings();
        this.settingPrefix = prefix;
    }

    @Override
    @Nonnull
    public JwtIssuer addIssuer(@Nonnull String issuerName, @Nonnull String sharedSecret) {
        SimpleJwtIssuer existing = this.getPluginSettingsIssuer(issuerName);
        if (existing != null && sharedSecret.equals(existing.getSharedSecret())) {
            return existing;
        }
        if (existing != null) {
            throw new IllegalStateException("The JWT issuer " + issuerName + " already exists");
        }
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put(PROP_NAME, issuerName);
        properties.put(PROP_SECRET, sharedSecret);
        String settingsKey = this.settingsKey(issuerName);
        this.pluginSettings.put(settingsKey, properties);
        return new SimpleJwtIssuer(issuerName, sharedSecret);
    }

    @Override
    public JwtIssuer getIssuer(@Nonnull String issuer) {
        return this.getPluginSettingsIssuer(issuer);
    }

    @Override
    public boolean removeIssuer(@Nonnull String issuerName) {
        return this.pluginSettings.remove(this.settingsKey(issuerName)) != null;
    }

    private SimpleJwtIssuer getPluginSettingsIssuer(String issuerName) {
        String settingsKey = this.settingsKey(issuerName);
        Object value = this.pluginSettings.get(settingsKey);
        if (value instanceof Map) {
            Map properties = (Map)value;
            return new SimpleJwtIssuer((String)properties.get(PROP_NAME), (String)properties.get(PROP_SECRET));
        }
        return null;
    }

    private String settingsKey(String issuerName) {
        return this.settingPrefix + ".issuers." + issuerName;
    }

    private static class SimpleJwtIssuer
    implements JwtIssuer {
        private final String name;
        private final String secret;

        private SimpleJwtIssuer(String name, String secret) {
            this.name = name;
            this.secret = secret;
        }

        @Override
        @Nonnull
        public String getName() {
            return this.name;
        }

        @Override
        public String getSharedSecret() {
            return this.secret;
        }
    }
}

