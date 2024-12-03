/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigResolver;
import com.typesafe.config.ConfigValue;

public final class ConfigResolveOptions {
    private final boolean useSystemEnvironment;
    private final boolean allowUnresolved;
    private final ConfigResolver resolver;
    private static final ConfigResolver NULL_RESOLVER = new ConfigResolver(){

        @Override
        public ConfigValue lookup(String path) {
            return null;
        }

        @Override
        public ConfigResolver withFallback(ConfigResolver fallback) {
            return fallback;
        }
    };

    private ConfigResolveOptions(boolean useSystemEnvironment, boolean allowUnresolved, ConfigResolver resolver) {
        this.useSystemEnvironment = useSystemEnvironment;
        this.allowUnresolved = allowUnresolved;
        this.resolver = resolver;
    }

    public static ConfigResolveOptions defaults() {
        return new ConfigResolveOptions(true, false, NULL_RESOLVER);
    }

    public static ConfigResolveOptions noSystem() {
        return ConfigResolveOptions.defaults().setUseSystemEnvironment(false);
    }

    public ConfigResolveOptions setUseSystemEnvironment(boolean value) {
        return new ConfigResolveOptions(value, this.allowUnresolved, this.resolver);
    }

    public boolean getUseSystemEnvironment() {
        return this.useSystemEnvironment;
    }

    public ConfigResolveOptions setAllowUnresolved(boolean value) {
        return new ConfigResolveOptions(this.useSystemEnvironment, value, this.resolver);
    }

    public ConfigResolveOptions appendResolver(ConfigResolver value) {
        if (value == null) {
            throw new ConfigException.BugOrBroken("null resolver passed to appendResolver");
        }
        if (value == this.resolver) {
            return this;
        }
        return new ConfigResolveOptions(this.useSystemEnvironment, this.allowUnresolved, this.resolver.withFallback(value));
    }

    public ConfigResolver getResolver() {
        return this.resolver;
    }

    public boolean getAllowUnresolved() {
        return this.allowUnresolved;
    }
}

