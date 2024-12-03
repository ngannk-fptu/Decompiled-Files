/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ApplicationImpl
 *  com.atlassian.crowd.model.sso.NameIdFormat
 */
package com.atlassian.crowd.model.sso;

import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ApplicationImpl;
import com.atlassian.crowd.model.sso.NameIdFormat;
import java.util.Objects;

public class ApplicationSamlConfigurationEntity {
    private Long applicationId;
    private ApplicationImpl application;
    private Boolean enabled;
    private String assertionConsumerServiceUrl;
    private String audienceUrl;
    private NameIdFormat nameIdFormat;
    private boolean addUserAttributesEnabled;

    ApplicationSamlConfigurationEntity() {
    }

    public ApplicationSamlConfigurationEntity(Application application) {
        this.application = ApplicationImpl.convertIfNeeded((Application)application);
    }

    public ApplicationSamlConfigurationEntity(Application application, Boolean enabled, String assertionConsumerServiceUrl, String audienceUrl, NameIdFormat nameIdFormat, boolean addUserAttributesEnabled) {
        this.application = ApplicationImpl.convertIfNeeded((Application)application);
        this.enabled = enabled;
        this.assertionConsumerServiceUrl = assertionConsumerServiceUrl;
        this.audienceUrl = audienceUrl;
        this.nameIdFormat = nameIdFormat;
        this.addUserAttributesEnabled = addUserAttributesEnabled;
    }

    public Long getApplicationId() {
        return this.applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public ApplicationImpl getApplication() {
        return this.application;
    }

    public void setApplication(ApplicationImpl application) {
        this.application = application;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getAssertionConsumerServiceUrl() {
        return this.assertionConsumerServiceUrl;
    }

    public void setAssertionConsumerServiceUrl(String assertionConsumerServiceUrl) {
        this.assertionConsumerServiceUrl = assertionConsumerServiceUrl;
    }

    public String getAudienceUrl() {
        return this.audienceUrl;
    }

    public void setAudienceUrl(String audienceUrl) {
        this.audienceUrl = audienceUrl;
    }

    public NameIdFormat getNameIdFormat() {
        return this.nameIdFormat;
    }

    public void setNameIdFormat(NameIdFormat nameIdFormat) {
        this.nameIdFormat = nameIdFormat;
    }

    public boolean isAddUserAttributesEnabled() {
        return this.addUserAttributesEnabled;
    }

    public void setAddUserAttributesEnabled(boolean addUserAttributesEnabled) {
        this.addUserAttributesEnabled = addUserAttributesEnabled;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ApplicationSamlConfigurationEntity that = (ApplicationSamlConfigurationEntity)o;
        return Objects.equals(this.applicationId, that.applicationId) && Objects.equals(this.application, that.application) && Objects.equals(this.enabled, that.enabled) && Objects.equals(this.assertionConsumerServiceUrl, that.assertionConsumerServiceUrl) && Objects.equals(this.audienceUrl, that.audienceUrl) && Objects.equals(this.nameIdFormat, that.nameIdFormat) && Objects.equals(this.addUserAttributesEnabled, that.addUserAttributesEnabled);
    }

    public int hashCode() {
        return Objects.hash(this.applicationId, this.application, this.enabled, this.assertionConsumerServiceUrl, this.audienceUrl, this.nameIdFormat, this.addUserAttributesEnabled);
    }
}

