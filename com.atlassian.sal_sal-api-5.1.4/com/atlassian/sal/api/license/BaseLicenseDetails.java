/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.sal.api.license;

import com.atlassian.annotations.PublicApi;
import java.util.Date;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PublicApi
public interface BaseLicenseDetails {
    public boolean isEvaluationLicense();

    @Nonnull
    public String getLicenseTypeName();

    public String getOrganisationName();

    @Nullable
    public String getSupportEntitlementNumber();

    public String getDescription();

    public String getServerId();

    public boolean isPerpetualLicense();

    @Nullable
    public Date getLicenseExpiryDate();

    @Nullable
    public Date getMaintenanceExpiryDate();

    public boolean isDataCenter();

    public boolean isEnterpriseLicensingAgreement();

    @Nullable
    public String getProperty(@Nonnull String var1);
}

