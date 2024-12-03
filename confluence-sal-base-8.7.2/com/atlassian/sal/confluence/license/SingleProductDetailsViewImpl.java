/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.sal.api.license.SingleProductLicenseDetailsView
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.sal.confluence.license;

import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.sal.api.license.SingleProductLicenseDetailsView;
import com.atlassian.sal.confluence.license.BaseLicenseDetailsImpl;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SingleProductDetailsViewImpl
extends BaseLicenseDetailsImpl
implements SingleProductLicenseDetailsView {
    public SingleProductDetailsViewImpl(@NonNull ConfluenceLicense confluenceLicense) {
        super(confluenceLicense);
    }

    public @NonNull String getProductKey() {
        return "conf";
    }

    public boolean isUnlimitedNumberOfUsers() {
        return this.getConfluenceLicense().isUnlimitedNumberOfUsers();
    }

    public int getNumberOfUsers() {
        return this.getConfluenceLicense().getMaximumNumberOfUsers();
    }

    public @NonNull String getProductDisplayName() {
        return this.getConfluenceLicense().getProduct().getName();
    }
}

