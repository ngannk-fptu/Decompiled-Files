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
import com.atlassian.sal.api.license.BaseLicenseDetails;
import com.atlassian.sal.api.license.ProductLicense;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PublicApi
public interface MultiProductLicenseDetails
extends BaseLicenseDetails {
    @Nonnull
    public Set<ProductLicense> getProductLicenses();

    @Nonnull
    public Set<ProductLicense> getEmbeddedLicenses();

    @Nullable
    public ProductLicense getProductLicense(@Nonnull String var1);
}

