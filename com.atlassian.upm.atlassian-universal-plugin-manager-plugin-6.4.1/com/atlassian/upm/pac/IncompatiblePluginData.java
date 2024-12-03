/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.pac;

import com.atlassian.upm.core.Plugin;
import java.util.Objects;

public class IncompatiblePluginData {
    private String key;
    private String name;
    private String version;
    private IncompatibilityType incompatibilityType;

    IncompatiblePluginData(String key, String version, String name, IncompatibilityType incompatibilityType) {
        this.key = Objects.requireNonNull(key, "key");
        this.version = Objects.requireNonNull(version, "version");
        this.name = Objects.requireNonNull(name, "name");
        this.incompatibilityType = Objects.requireNonNull(incompatibilityType, "incompatibilityType");
    }

    public IncompatiblePluginData(Plugin plugin, IncompatibilityType incompatibilityType) {
        this(plugin.getKey(), plugin.getVersion(), plugin.getName(), incompatibilityType);
    }

    public String getKey() {
        return this.key;
    }

    public String getVersion() {
        return this.version;
    }

    public String getName() {
        return this.name;
    }

    public IncompatibilityType getIncompatibilityType() {
        return this.incompatibilityType;
    }

    public boolean isDataCenter() {
        switch (this.incompatibilityType) {
            case DATA_CENTER: 
            case LEGACY_DATA_CENTER: {
                return true;
            }
        }
        return false;
    }

    public boolean isIncompatibleWithHostProduct() {
        switch (this.incompatibilityType) {
            case APPLICATION: 
            case APPLICATION_VERSION: {
                return true;
            }
        }
        return false;
    }

    public static enum IncompatibilityType {
        APPLICATION,
        APPLICATION_VERSION,
        DATA_CENTER,
        LEGACY_DATA_CENTER;

    }
}

