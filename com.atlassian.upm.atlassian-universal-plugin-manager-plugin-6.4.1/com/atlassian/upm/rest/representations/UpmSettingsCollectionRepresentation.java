/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import com.atlassian.upm.UpmSettings;
import java.util.Collection;
import java.util.Collections;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class UpmSettingsCollectionRepresentation {
    @JsonProperty
    private final Collection<UpmSettingRepresentation> settings;

    @JsonCreator
    public UpmSettingsCollectionRepresentation(@JsonProperty(value="settings") Collection<UpmSettingRepresentation> settings) {
        this.settings = Collections.unmodifiableCollection(settings);
    }

    public Collection<UpmSettingRepresentation> getSettings() {
        return this.settings;
    }

    @JsonIgnore
    public boolean isPacDisabled() {
        return this.getSettingValue(UpmSettings.PAC_DISABLED);
    }

    @JsonIgnore
    public boolean isRequestsDisabled() {
        return this.getSettingValue(UpmSettings.REQUESTS_DISABLED);
    }

    @JsonIgnore
    public boolean isEmailDisabled() {
        return this.getSettingValue(UpmSettings.EMAIL_DISABLED);
    }

    @JsonIgnore
    public boolean isAutoUpdateEnabled() {
        return this.getSettingValue(UpmSettings.AUTO_UPDATE_ENABLED);
    }

    private boolean getSettingValue(UpmSettings s) {
        for (UpmSettingRepresentation setting : this.settings) {
            if (!s.getKey().equals(setting.getKey())) continue;
            return setting.getValue();
        }
        return false;
    }

    public static class UpmSettingRepresentation {
        @JsonProperty
        private String key;
        @JsonProperty
        private boolean value;
        @JsonProperty
        private boolean requiresRefresh;
        @JsonProperty
        private boolean defaultCheckedValue;
        @JsonProperty
        private boolean readOnly;

        @JsonCreator
        public UpmSettingRepresentation(@JsonProperty(value="key") String key, @JsonProperty(value="value") boolean value, @JsonProperty(value="requiresRefresh") boolean requiresRefresh, @JsonProperty(value="defaultCheckedValue") boolean defaultCheckedValue, @JsonProperty(value="readOnly") boolean readOnly) {
            this.key = key;
            this.value = value;
            this.requiresRefresh = requiresRefresh;
            this.defaultCheckedValue = defaultCheckedValue;
            this.readOnly = readOnly;
        }

        public String getKey() {
            return this.key;
        }

        public boolean getValue() {
            return this.value;
        }

        public boolean isRequiresRefresh() {
            return this.requiresRefresh;
        }

        public boolean getDefaultCheckedValue() {
            return this.defaultCheckedValue;
        }

        public boolean isReadOnly() {
            return this.readOnly;
        }
    }
}

