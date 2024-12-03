/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.sal.api.license.MultiProductLicenseDetails
 *  com.atlassian.sal.api.license.ProductLicense
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.sal.confluence.license;

import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.sal.api.license.MultiProductLicenseDetails;
import com.atlassian.sal.api.license.ProductLicense;
import com.atlassian.sal.confluence.license.BaseLicenseDetailsImpl;
import java.util.Collections;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MultiProductLicenseDetailsImpl
extends BaseLicenseDetailsImpl
implements MultiProductLicenseDetails {
    public MultiProductLicenseDetailsImpl(@NonNull ConfluenceLicense confluenceLicense) {
        super(confluenceLicense);
    }

    public @NonNull Set<ProductLicense> getProductLicenses() {
        return Collections.singleton((ProductLicense)this.getConfluenceLicense());
    }

    public @NonNull Set<ProductLicense> getEmbeddedLicenses() {
        return Collections.emptySet();
    }

    public @Nullable ProductLicense getProductLicense(@NonNull String productKey) {
        if (!"conf".equalsIgnoreCase(productKey)) {
            throw new IllegalArgumentException("Unsupported product key " + productKey);
        }
        return (ProductLicense)this.getConfluenceLicense();
    }
}

