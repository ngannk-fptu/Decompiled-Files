/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugins.authentication.impl.rest.model;

import com.atlassian.plugins.authentication.api.config.JustInTimeConfig;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonProperty;

public class JitConfigEntity {
    @JsonProperty(value="user-provisioning-enabled")
    private Boolean enableUserProvisioning;
    @JsonProperty(value="mapping-display-name")
    private String mappingDisplayName;
    @JsonProperty(value="mapping-email")
    private String mappingEmail;
    @JsonProperty(value="mapping-groups")
    private String mappingGroups;
    @JsonProperty(value="additional-openid-scopes")
    private List<String> additionalJitScopes;

    public JitConfigEntity() {
    }

    public JitConfigEntity(JustInTimeConfig justInTimeConfig) {
        this.enableUserProvisioning = justInTimeConfig.isEnabled().orElse(null);
        this.mappingDisplayName = justInTimeConfig.getDisplayNameMappingExpression().orElse(null);
        this.mappingEmail = justInTimeConfig.getEmailMappingExpression().orElse(null);
        this.mappingGroups = justInTimeConfig.getGroupsMappingSource().orElse(null);
        this.additionalJitScopes = justInTimeConfig.getAdditionalJitScopes();
    }

    public Boolean getEnableUserProvisioning() {
        return this.enableUserProvisioning;
    }

    public String getMappingDisplayName() {
        return this.mappingDisplayName;
    }

    public String getMappingEmail() {
        return this.mappingEmail;
    }

    public String getMappingGroups() {
        return this.mappingGroups;
    }

    @Nullable
    public List<String> getAdditionalJitScopes() {
        return this.additionalJitScopes;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        JitConfigEntity that = (JitConfigEntity)o;
        return Objects.equals(this.enableUserProvisioning, that.enableUserProvisioning) && Objects.equals(this.mappingDisplayName, that.mappingDisplayName) && Objects.equals(this.mappingEmail, that.mappingEmail) && Objects.equals(this.mappingGroups, that.mappingGroups) && Objects.equals(this.additionalJitScopes, that.additionalJitScopes);
    }

    public int hashCode() {
        return Objects.hash(this.enableUserProvisioning, this.mappingDisplayName, this.mappingEmail, this.mappingGroups, this.additionalJitScopes);
    }

    public String toString() {
        return "JitConfigEntity{enableUserProvisioning=" + this.enableUserProvisioning + ", mappingDisplayName='" + this.mappingDisplayName + '\'' + ", mappingEmail='" + this.mappingEmail + '\'' + ", mappingGroups='" + this.mappingGroups + '\'' + ", additionalJitScopes=" + this.additionalJitScopes + '}';
    }

    public static interface Config {
        public static final String USER_PROVISIONING_ENABLED = "user-provisioning-enabled";
        public static final String MAPPING_DISPLAY_NAME = "mapping-display-name";
        public static final String MAPPING_EMAIL = "mapping-email";
        public static final String MAPPING_GROUPS = "mapping-groups";
        public static final String MAPPING_ADDITIONAL_JIT_SCOPES = "additional-openid-scopes";
    }
}

