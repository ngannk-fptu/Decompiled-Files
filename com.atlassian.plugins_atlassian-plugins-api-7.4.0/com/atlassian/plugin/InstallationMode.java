/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin;

import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum InstallationMode {
    REMOTE("remote"),
    LOCAL("local");

    private static final Logger LOGGER;
    private final String key;

    private InstallationMode(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public static Optional<InstallationMode> of(String name) {
        for (InstallationMode mode : InstallationMode.values()) {
            if (!mode.getKey().equalsIgnoreCase(name)) continue;
            return Optional.of(mode);
        }
        if (StringUtils.isNotEmpty((CharSequence)name)) {
            LOGGER.warn("Could not match installation mode '{}' to any of existing {}. Ignoring.", (Object)name, (Object)InstallationMode.values());
        }
        return Optional.empty();
    }

    static {
        LOGGER = LoggerFactory.getLogger(InstallationMode.class);
    }
}

