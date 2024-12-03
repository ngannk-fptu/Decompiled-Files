/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.troubleshooting.jfr.config;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.troubleshooting.jfr.config.JfrProperty;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JfrPropertyStore {
    private static final String PREFIX_KEY = "jfr.properties:";
    private final PluginSettingsFactory pluginSettingsFactory;

    @Autowired
    public JfrPropertyStore(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory);
    }

    public Optional<String> get(@Nonnull JfrProperty jfrProperty) {
        return Optional.ofNullable(this.pluginSettingsFactory.createGlobalSettings().get(PREFIX_KEY + jfrProperty.getPropertyName())).map(String.class::cast);
    }

    public void store(@Nonnull JfrProperty jfrProperty, @Nullable String value) {
        this.pluginSettingsFactory.createGlobalSettings().put(PREFIX_KEY + jfrProperty.getPropertyName(), (Object)value);
    }
}

