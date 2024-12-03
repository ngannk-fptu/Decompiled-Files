/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.annotations.VisibleForTesting
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.plugin;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.annotations.VisibleForTesting;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
public final class ModuleCompleteKey {
    public static final String SEPARATOR = ":";
    @JsonProperty
    private final String pluginKey;
    @JsonProperty
    private final String moduleKey;

    public ModuleCompleteKey(String completeKey) {
        if (completeKey == null || completeKey.isEmpty()) {
            throw new IllegalArgumentException("Blank module complete key specified: " + completeKey);
        }
        String[] parts = completeKey.split(SEPARATOR);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid module complete key specified: " + completeKey);
        }
        this.pluginKey = this.throwIfInvalid(parts[0], "Invalid plugin key specified");
        this.moduleKey = this.throwIfInvalid(parts[1], "Invalid module key specified");
    }

    @JsonCreator
    public ModuleCompleteKey(@JsonProperty(value="pluginKey") String pluginKey, @JsonProperty(value="moduleKey") String moduleKey) {
        this.pluginKey = this.throwIfInvalid(pluginKey, "Invalid plugin key specified");
        this.moduleKey = this.throwIfInvalid(moduleKey, "Invalid module key specified");
    }

    private String throwIfInvalid(String key, String errorMessage) {
        if (key == null || key.isEmpty() || key.trim().length() == 0 || key.contains(SEPARATOR)) {
            throw new IllegalArgumentException(errorMessage + ": " + key);
        }
        return key.trim();
    }

    public String getModuleKey() {
        return this.moduleKey;
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    @JsonIgnore
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
        return Objects.equals(this.moduleKey, that.moduleKey) && Objects.equals(this.pluginKey, that.pluginKey);
    }

    public int hashCode() {
        return Objects.hash(this.pluginKey, this.moduleKey);
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

