/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.settings;

import com.atlassian.annotations.ExperimentalApi;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
public class GlobalSettings {
    @JsonProperty
    private final long attachmentMaxSizeBytes;
    @JsonProperty
    private final String defaultTimezoneId;
    @JsonProperty
    private final String globalDefaultLocale;
    @JsonProperty
    private final String baseUrl;

    public static GlobalSettingsBuilder builder() {
        return new GlobalSettingsBuilder();
    }

    @JsonCreator
    private GlobalSettings() {
        this(GlobalSettings.builder());
    }

    private GlobalSettings(GlobalSettingsBuilder builder) {
        this.attachmentMaxSizeBytes = builder.attachmentMaxSizeBytes;
        this.defaultTimezoneId = builder.defaultTimezoneId;
        this.globalDefaultLocale = builder.globalDefaultLocale;
        this.baseUrl = builder.baseUrl;
    }

    public long getAttachmentMaxSizeBytes() {
        return this.attachmentMaxSizeBytes;
    }

    public String getDefaultTimezoneId() {
        return this.defaultTimezoneId;
    }

    public String getGlobalDefaultLocale() {
        return this.globalDefaultLocale;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public static class GlobalSettingsBuilder {
        private long attachmentMaxSizeBytes;
        private String defaultTimezoneId;
        private String globalDefaultLocale;
        private String baseUrl;

        private GlobalSettingsBuilder() {
        }

        public GlobalSettings build() {
            return new GlobalSettings(this);
        }

        public GlobalSettingsBuilder attachmentMaxSizeBytes(long attachmentMaxSizeBytes) {
            this.attachmentMaxSizeBytes = attachmentMaxSizeBytes;
            return this;
        }

        public GlobalSettingsBuilder defaultTimezoneId(String defaultTimezoneId) {
            this.defaultTimezoneId = defaultTimezoneId;
            return this;
        }

        public GlobalSettingsBuilder globalDefaultLocale(String globalDefaultLocale) {
            this.globalDefaultLocale = globalDefaultLocale;
            return this;
        }

        public GlobalSettingsBuilder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }
    }
}

