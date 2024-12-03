/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin;

import com.atlassian.annotations.PublicApi;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.StringUtils;

@PublicApi
public final class ModuleCompleteKey {
    @VisibleForTesting
    protected static final String SEPARATOR = ":";
    private final String pluginKey;
    private final String moduleKey;

    public ModuleCompleteKey(String completeKey) {
        this(ModuleCompleteKey.pluginKeyFromCompleteKey(completeKey), ModuleCompleteKey.moduleKeyFromCompleteKey(completeKey));
    }

    public ModuleCompleteKey(String pluginKey, String moduleKey) {
        this.pluginKey = StringUtils.trimToEmpty((String)pluginKey);
        if (!this.isValidKey(this.pluginKey)) {
            throw new IllegalArgumentException("Invalid plugin key specified: " + this.pluginKey);
        }
        this.moduleKey = StringUtils.trimToEmpty((String)moduleKey);
        if (this.moduleKey.isEmpty()) {
            throw new IllegalArgumentException("Invalid module key specified: " + this.moduleKey);
        }
    }

    private boolean isValidKey(String key) {
        return !key.isEmpty() && !key.contains(SEPARATOR);
    }

    public String getModuleKey() {
        return this.moduleKey;
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public String getCompleteKey() {
        return this.pluginKey + SEPARATOR + this.moduleKey;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ModuleCompleteKey that = (ModuleCompleteKey)o;
        if (!this.moduleKey.equals(that.moduleKey)) {
            return false;
        }
        return this.pluginKey.equals(that.pluginKey);
    }

    public int hashCode() {
        int result = this.pluginKey.hashCode();
        result = 31 * result + this.moduleKey.hashCode();
        return result;
    }

    public String toString() {
        return this.getCompleteKey();
    }

    @VisibleForTesting
    static String pluginKeyFromCompleteKey(String completeKey) {
        if (completeKey != null) {
            return completeKey.split(SEPARATOR)[0];
        }
        return "";
    }

    @VisibleForTesting
    static String moduleKeyFromCompleteKey(String completeKey) {
        String[] split;
        if (completeKey != null && (split = completeKey.split(SEPARATOR, 2)).length == 2) {
            return split[1];
        }
        return "";
    }
}

