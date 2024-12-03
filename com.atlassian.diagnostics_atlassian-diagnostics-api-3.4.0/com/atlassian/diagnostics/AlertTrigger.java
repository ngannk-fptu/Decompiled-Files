/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.diagnostics;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class AlertTrigger {
    private final String module;
    private final String pluginKey;
    private final String pluginVersion;

    private AlertTrigger(Builder builder) {
        this.module = builder.module;
        this.pluginKey = builder.pluginKey == null ? "not-detected" : builder.pluginKey;
        this.pluginVersion = builder.pluginVersion;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AlertTrigger trigger = (AlertTrigger)o;
        return Objects.equals(this.module, trigger.module) && Objects.equals(this.pluginKey, trigger.pluginKey) && Objects.equals(this.pluginVersion, trigger.pluginVersion);
    }

    @Nonnull
    public Optional<String> getModule() {
        return Optional.ofNullable(this.module);
    }

    @Nonnull
    public String getPluginKey() {
        return this.pluginKey;
    }

    @Nonnull
    public Optional<String> getPluginVersion() {
        return Optional.ofNullable(this.pluginVersion);
    }

    public int hashCode() {
        return Objects.hash(this.module, this.pluginKey, this.pluginVersion);
    }

    public static class Builder {
        private String module;
        private String pluginKey;
        private String pluginVersion;

        @Nonnull
        public AlertTrigger build() {
            return new AlertTrigger(this);
        }

        @Nonnull
        public Builder module(@Nullable String value) {
            this.module = StringUtils.trimToNull((String)value);
            return this;
        }

        @Nonnull
        public Builder plugin(@Nullable String key, @Nullable String version) {
            this.pluginKey = StringUtils.trimToNull((String)key);
            this.pluginVersion = StringUtils.trimToNull((String)version);
            return this;
        }
    }
}

