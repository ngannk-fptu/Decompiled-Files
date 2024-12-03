/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.cluster.monitoring.rest;

import com.atlassian.plugin.ModuleCompleteKey;
import java.io.Serializable;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DataSupplier
implements Serializable {
    private final ModuleCompleteKey moduleCompleteKey;
    private final String i18nNameKey;
    private final String i18nNameLabel;

    public DataSupplier(ModuleCompleteKey moduleCompleteKey, String i18nNameKey, String i18nNameLabel) {
        this.moduleCompleteKey = Objects.requireNonNull(moduleCompleteKey);
        this.i18nNameKey = Objects.requireNonNull(i18nNameKey);
        this.i18nNameLabel = Objects.requireNonNull(i18nNameLabel);
    }

    @JsonProperty
    public String getPluginKey() {
        return this.moduleCompleteKey.getPluginKey();
    }

    @JsonProperty
    public String getModuleKey() {
        return this.moduleCompleteKey.getModuleKey();
    }

    @JsonProperty
    public String getI18nNameKey() {
        return this.i18nNameKey;
    }

    @JsonProperty
    public String getI18nNameLabel() {
        return this.i18nNameLabel;
    }
}

