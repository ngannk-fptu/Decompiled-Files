/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize$Inclusion
 */
package com.atlassian.confluence.license.rest.model;

import java.util.Date;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown=true)
public final class LicenseDetailsModel {
    @JsonProperty
    private final String licenseType;
    @JsonProperty
    private final boolean dataCenter;
    @JsonProperty
    private final boolean subscription;
    @JsonProperty
    private final boolean evaluation;
    @JsonProperty
    private final boolean expired;
    @JsonProperty
    private final Date creationDate;
    @JsonProperty
    private final Date purchaseDate;
    @JsonProperty
    private final Date expiryDate;
    @JsonProperty
    private final Date maintenanceExpiryDate;

    @JsonCreator
    private LicenseDetailsModel() {
        this(LicenseDetailsModel.builder());
    }

    private LicenseDetailsModel(Builder builder) {
        this.licenseType = builder.licenseType;
        this.dataCenter = builder.dataCenter;
        this.subscription = builder.subscription;
        this.evaluation = builder.evaluation;
        this.expired = builder.expired;
        this.creationDate = builder.creationDate;
        this.purchaseDate = builder.purchaseDate;
        this.expiryDate = builder.expiryDate;
        this.maintenanceExpiryDate = builder.maintenanceExpiryDate;
    }

    public String getLicenseType() {
        return this.licenseType;
    }

    public boolean isDataCenter() {
        return this.dataCenter;
    }

    public boolean isSubscription() {
        return this.subscription;
    }

    public boolean isEvaluation() {
        return this.evaluation;
    }

    public boolean isExpired() {
        return this.expired;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public Date getPurchaseDate() {
        return this.purchaseDate;
    }

    public Date getExpiryDate() {
        return this.expiryDate;
    }

    public Date getMaintenanceExpiryDate() {
        return this.maintenanceExpiryDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String licenseType;
        private boolean dataCenter;
        private boolean subscription;
        private boolean evaluation;
        private boolean expired;
        private Date creationDate;
        private Date purchaseDate;
        private Date expiryDate;
        private Date maintenanceExpiryDate;

        public Builder licenseType(@NonNull String licenseType) {
            Objects.requireNonNull(licenseType);
            this.licenseType = licenseType;
            return this;
        }

        public Builder dataCenter(boolean dataCenter) {
            this.dataCenter = dataCenter;
            return this;
        }

        public Builder subscription(boolean subscription) {
            this.subscription = subscription;
            return this;
        }

        public Builder evaluation(boolean evaluation) {
            this.evaluation = evaluation;
            return this;
        }

        public Builder expired(boolean expired) {
            this.expired = expired;
            return this;
        }

        public Builder creationDate(@NonNull Date creationDate) {
            Objects.requireNonNull(creationDate);
            this.creationDate = creationDate;
            return this;
        }

        public Builder purchaseDate(@NonNull Date purchaseDate) {
            Objects.requireNonNull(purchaseDate);
            this.purchaseDate = purchaseDate;
            return this;
        }

        public Builder expiryDate(@Nullable Date expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder maintenanceExpiryDate(@Nullable Date maintenanceExpiryDate) {
            this.maintenanceExpiryDate = maintenanceExpiryDate;
            return this;
        }

        public LicenseDetailsModel build() {
            return new LicenseDetailsModel(this);
        }
    }
}

