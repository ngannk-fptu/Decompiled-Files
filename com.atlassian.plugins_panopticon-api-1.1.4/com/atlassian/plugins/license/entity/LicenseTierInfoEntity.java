/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugins.license.entity;

import java.util.Objects;
import java.util.StringJoiner;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class LicenseTierInfoEntity {
    @JsonProperty(value="applicationKey")
    private String applicationKey;
    @JsonProperty(value="maximumNumberOfUsers")
    private long maximumNumberOfUsers;
    @JsonProperty(value="licenseExpiryDate")
    private Long licenseExpiryDate;
    @JsonProperty(value="applicationDisplayName")
    private String applicationDisplayName;

    @JsonCreator
    public LicenseTierInfoEntity(@JsonProperty(value="applicationKey") String applicationKey, @JsonProperty(value="maximumNumberOfUsers") long maximumNumberOfUsers, @JsonProperty(value="licenseExpiryDate") Long licenseExpiryDate, @JsonProperty(value="applicationDisplayName") String applicationDisplayName) {
        this.applicationKey = applicationKey;
        this.maximumNumberOfUsers = maximumNumberOfUsers;
        this.licenseExpiryDate = licenseExpiryDate;
        this.applicationDisplayName = applicationDisplayName;
    }

    public String getApplicationKey() {
        return this.applicationKey;
    }

    public void setApplicationKey(String applicationKey) {
        this.applicationKey = applicationKey;
    }

    public long getMaximumNumberOfUsers() {
        return this.maximumNumberOfUsers;
    }

    public void setMaximumNumberOfUsers(long maximumNumberOfUsers) {
        this.maximumNumberOfUsers = maximumNumberOfUsers;
    }

    public Long getLicenseExpiryDate() {
        return this.licenseExpiryDate;
    }

    public void setLicenseExpiryDate(Long licenseExpiryDate) {
        this.licenseExpiryDate = licenseExpiryDate;
    }

    public String getApplicationDisplayName() {
        return this.applicationDisplayName;
    }

    public void setApplicationDisplayName(String applicationDisplayName) {
        this.applicationDisplayName = applicationDisplayName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(LicenseTierInfoEntity data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LicenseTierInfoEntity that = (LicenseTierInfoEntity)o;
        return Objects.equals(this.getApplicationKey(), that.getApplicationKey()) && Objects.equals(this.getMaximumNumberOfUsers(), that.getMaximumNumberOfUsers()) && Objects.equals(this.getLicenseExpiryDate(), that.getLicenseExpiryDate()) && Objects.equals(this.getApplicationDisplayName(), that.getApplicationDisplayName());
    }

    public int hashCode() {
        return Objects.hash(this.getApplicationKey(), this.getMaximumNumberOfUsers(), this.getLicenseExpiryDate(), this.getApplicationDisplayName());
    }

    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]").add("applicationKey=" + this.getApplicationKey()).add("maximumNumberOfUsers=" + this.getMaximumNumberOfUsers()).add("licenseExpiryDate=" + this.getLicenseExpiryDate()).add("applicationDisplayName=" + this.getApplicationDisplayName()).toString();
    }

    public static final class Builder {
        private String applicationKey;
        private long maximumNumberOfUsers;
        private Long licenseExpiryDate;
        private String applicationDisplayName;

        private Builder() {
        }

        private Builder(LicenseTierInfoEntity initialData) {
            this.applicationKey = initialData.getApplicationKey();
            this.maximumNumberOfUsers = initialData.getMaximumNumberOfUsers();
            this.licenseExpiryDate = initialData.getLicenseExpiryDate();
            this.applicationDisplayName = initialData.getApplicationDisplayName();
        }

        public Builder setApplicationKey(String applicationKey) {
            this.applicationKey = applicationKey;
            return this;
        }

        public Builder setMaximumNumberOfUsers(long maximumNumberOfUsers) {
            this.maximumNumberOfUsers = maximumNumberOfUsers;
            return this;
        }

        public Builder setLicenseExpiryDate(Long licenseExpiryDate) {
            this.licenseExpiryDate = licenseExpiryDate;
            return this;
        }

        public Builder setApplicationDisplayName(String applicationDisplayName) {
            this.applicationDisplayName = applicationDisplayName;
            return this;
        }

        public LicenseTierInfoEntity build() {
            return new LicenseTierInfoEntity(this.applicationKey, this.maximumNumberOfUsers, this.licenseExpiryDate, this.applicationDisplayName);
        }
    }
}

