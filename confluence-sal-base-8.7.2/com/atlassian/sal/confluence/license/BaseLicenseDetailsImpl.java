/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.sal.api.license.BaseLicenseDetails
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.sal.confluence.license;

import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.sal.api.license.BaseLicenseDetails;
import java.util.Date;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BaseLicenseDetailsImpl
implements BaseLicenseDetails {
    private final ConfluenceLicense confluenceLicense;

    public BaseLicenseDetailsImpl(@NonNull ConfluenceLicense confluenceLicense) {
        this.confluenceLicense = confluenceLicense;
    }

    protected ConfluenceLicense getConfluenceLicense() {
        return this.confluenceLicense;
    }

    public boolean isEvaluationLicense() {
        return this.confluenceLicense.isEvaluation();
    }

    public @NonNull String getLicenseTypeName() {
        return this.confluenceLicense.getLicenseType().name();
    }

    public String getOrganisationName() {
        return this.confluenceLicense.getOrganisation().getName();
    }

    public @Nullable String getSupportEntitlementNumber() {
        return this.confluenceLicense.getSupportEntitlementNumber();
    }

    public String getDescription() {
        return this.confluenceLicense.getDescription();
    }

    public String getServerId() {
        return this.confluenceLicense.getServerId();
    }

    public boolean isPerpetualLicense() {
        return false;
    }

    public @Nullable Date getLicenseExpiryDate() {
        return this.confluenceLicense.getExpiryDate();
    }

    public @Nullable Date getMaintenanceExpiryDate() {
        return this.confluenceLicense.getMaintenanceExpiryDate();
    }

    public boolean isDataCenter() {
        return this.confluenceLicense.isClusteringEnabled();
    }

    public boolean isEnterpriseLicensingAgreement() {
        throw new UnsupportedOperationException("Cannot detect whether confluence license is ELA");
    }

    public @Nullable String getProperty(@NonNull String key) {
        return this.confluenceLicense.getProperty(key);
    }
}

