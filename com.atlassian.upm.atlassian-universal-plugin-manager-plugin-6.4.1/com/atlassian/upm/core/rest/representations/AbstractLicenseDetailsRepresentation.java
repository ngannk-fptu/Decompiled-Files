/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.rest.representations;

import java.util.Date;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public abstract class AbstractLicenseDetailsRepresentation {
    @JsonProperty
    private final boolean valid;
    @JsonProperty
    private final Boolean evaluation;
    @JsonProperty
    private final Integer maximumNumberOfUsers;
    @JsonProperty
    private final String licenseType;
    @JsonProperty
    private final String creationDateString;
    @JsonProperty
    private final Date expiryDate;
    @JsonProperty
    private final String expiryDateString;
    @JsonProperty
    private final String supportEntitlementNumber;
    @JsonProperty
    private final String organizationName;
    @JsonProperty
    private final String contactEmail;
    @JsonProperty
    private final Boolean enterprise;
    @JsonProperty
    private final Boolean dataCenter;
    @JsonProperty
    private final Boolean subscription;
    @JsonProperty
    private final boolean active;
    @JsonProperty
    private final Boolean autoRenewal;

    @JsonCreator
    public AbstractLicenseDetailsRepresentation(@JsonProperty(value="valid") Boolean valid, @JsonProperty(value="evaluation") Boolean evaluation, @JsonProperty(value="maximumNumberOfUsers") Integer maximumNumberOfUsers, @JsonProperty(value="licenseType") String licenseType, @JsonProperty(value="creationDateString") String creationDateString, @JsonProperty(value="expiryDate") Date expiryDate, @JsonProperty(value="expiryDateString") String expiryDateString, @JsonProperty(value="pluginSupportEntitlementNumber") String supportEntitlementNumber, @JsonProperty(value="organizationName") String organizationName, @JsonProperty(value="contactEmail") String contactEmail, @JsonProperty(value="enterprise") Boolean enterprise, @JsonProperty(value="dataCenter") Boolean dataCenter, @JsonProperty(value="subscription") Boolean subscription, @JsonProperty(value="active") Boolean active, @JsonProperty(value="autoRenewal") Boolean autoRenewal) {
        this.valid = valid == null ? false : valid;
        this.evaluation = evaluation == null ? false : evaluation;
        this.maximumNumberOfUsers = maximumNumberOfUsers;
        this.licenseType = licenseType;
        this.creationDateString = creationDateString;
        this.expiryDate = expiryDate;
        this.expiryDateString = expiryDateString;
        this.supportEntitlementNumber = supportEntitlementNumber;
        this.organizationName = organizationName;
        this.contactEmail = contactEmail;
        this.enterprise = enterprise == null ? false : enterprise;
        this.dataCenter = dataCenter == null ? false : dataCenter;
        this.subscription = subscription == null ? false : subscription;
        this.active = active == null ? false : active;
        this.autoRenewal = autoRenewal == null ? false : autoRenewal;
    }

    public AbstractLicenseDetailsRepresentation() {
        this.valid = false;
        this.evaluation = null;
        this.maximumNumberOfUsers = null;
        this.licenseType = null;
        this.creationDateString = null;
        this.expiryDate = null;
        this.expiryDateString = null;
        this.supportEntitlementNumber = null;
        this.organizationName = null;
        this.contactEmail = null;
        this.enterprise = null;
        this.dataCenter = null;
        this.subscription = null;
        this.active = false;
        this.autoRenewal = null;
    }

    public boolean isValid() {
        return this.valid;
    }

    public boolean isActive() {
        return this.active;
    }

    public Boolean isSubscription() {
        return this.subscription;
    }

    public Boolean isAutoRenewal() {
        return this.autoRenewal;
    }

    @JsonIgnore
    public Boolean isEvaluation() {
        return this.evaluation;
    }

    public Boolean getEvaluation() {
        return this.isEvaluation();
    }

    public Integer getMaximumNumberOfUsers() {
        return this.maximumNumberOfUsers;
    }

    public Date getExpiryDate() {
        return this.expiryDate;
    }

    public String getExpiryDateString() {
        return this.expiryDateString;
    }

    public String getLicenseType() {
        return this.licenseType;
    }

    public String getCreationDateString() {
        return this.creationDateString;
    }

    public String getSupportEntitlementNumber() {
        return this.supportEntitlementNumber;
    }

    public String getOrganizationName() {
        return this.organizationName;
    }

    public String getContactEmail() {
        return this.contactEmail;
    }

    public Boolean isEnterprise() {
        return this.enterprise;
    }

    public Boolean getDataCenter() {
        return this.isDataCenter();
    }

    @JsonIgnore
    public Boolean isDataCenter() {
        return this.dataCenter;
    }
}

