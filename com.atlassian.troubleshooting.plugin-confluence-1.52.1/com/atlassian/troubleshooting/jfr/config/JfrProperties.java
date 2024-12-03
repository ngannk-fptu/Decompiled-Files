/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.troubleshooting.jfr.config;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.troubleshooting.jfr.config.JfrProperty;
import com.atlassian.troubleshooting.jfr.config.JfrPropertyDefaults;
import com.atlassian.troubleshooting.jfr.config.JfrPropertyStore;
import com.atlassian.troubleshooting.jfr.event.JfrPropertiesChangedEvent;
import com.atlassian.troubleshooting.jfr.exception.JfrPropertyException;
import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class JfrProperties {
    @VisibleForTesting
    static final String PROPERTY_IMMUTABLE_I18N = "stp.jfr.property.immutable";
    @VisibleForTesting
    static final String PROPERTY_INVALID_VALUE_I18N = "stp.jfr.property.value.invalid";
    @VisibleForTesting
    static final String CUSTOM_CONFIGURATION_IS_IN_USE_I18N = "stp.jfr.property.custom.configuration";
    private final JfrPropertyDefaults defaultProperties;
    private final EventPublisher eventPublisher;
    private final JfrPropertyStore jfrPropertyStore;

    public JfrProperties(JfrPropertyDefaults defaultProperties, EventPublisher eventPublisher, JfrPropertyStore jfrPropertyStore) {
        this.defaultProperties = defaultProperties;
        this.eventPublisher = eventPublisher;
        this.jfrPropertyStore = jfrPropertyStore;
    }

    public long getMaxAge() {
        if (JfrProperty.MAX_AGE.isOverridden()) {
            return this.defaultProperties.getMaxAge();
        }
        return this.jfrPropertyStore.get(JfrProperty.MAX_AGE).map(Long::parseLong).orElse(this.defaultProperties.getMaxAge());
    }

    public long getMaxSize() {
        if (JfrProperty.MAX_SIZE.isOverridden()) {
            return this.defaultProperties.getMaxSize();
        }
        return this.jfrPropertyStore.get(JfrProperty.MAX_SIZE).map(Long::parseLong).orElse(this.defaultProperties.getMaxSize());
    }

    public int getNumberOfFilesToRemain() {
        return this.defaultProperties.getNumberOfFilesToRemain();
    }

    public String getRecordingPath() {
        return this.defaultProperties.getRecordingPath();
    }

    public String getThreadDumpPath() {
        return this.defaultProperties.getThreadDumpPath();
    }

    public String getDumpCronExpression() {
        return this.defaultProperties.getDumpCronExpression();
    }

    public String getJfrTemplatePath() {
        return this.defaultProperties.getJfrTemplatePath();
    }

    public boolean isDefaultConfiguration() {
        return this.defaultProperties.getJfrTemplatePath() == null;
    }

    @Nullable
    public Long getThreadDumpInterval() {
        if (JfrProperty.JFR_TEMPLATE_PATH.isOverridden()) {
            return null;
        }
        return this.jfrPropertyStore.get(JfrProperty.THREAD_DUMP_INTERVAL).map(Long::parseLong).orElse(null);
    }

    @Nullable
    public String getProperty(@Nonnull JfrProperty jfrProperty) {
        return this.jfrPropertyStore.get(jfrProperty).orElse(this.defaultProperties.getProperty(jfrProperty));
    }

    public void setProperty(@Nonnull JfrProperty jfrProperty, @Nullable String value) {
        Objects.requireNonNull(jfrProperty);
        this.preValidateSetConditions(jfrProperty, value);
        if (!Objects.equals(this.getProperty(jfrProperty), value)) {
            this.jfrPropertyStore.store(jfrProperty, value);
            this.eventPublisher.publish((Object)new JfrPropertiesChangedEvent());
        }
    }

    private void preValidateSetConditions(JfrProperty jfrProperty, String value) {
        if (!jfrProperty.isMutable()) {
            throw new JfrPropertyException(PROPERTY_IMMUTABLE_I18N, "Property can't be changed");
        }
        if (value != null && !jfrProperty.validate(value)) {
            throw new JfrPropertyException(PROPERTY_INVALID_VALUE_I18N, "Invalid property value");
        }
        if (JfrProperty.JFR_TEMPLATE_PATH.isOverridden() && JfrProperty.THREAD_DUMP_INTERVAL.name().equals(jfrProperty.name())) {
            throw new JfrPropertyException(CUSTOM_CONFIGURATION_IS_IN_USE_I18N, "Property can't be changed when a custom configuration is used");
        }
        if (jfrProperty.isOverridden()) {
            throw new JfrPropertyException(CUSTOM_CONFIGURATION_IS_IN_USE_I18N, "Property can't be changed when a custom configuration is used");
        }
    }
}

