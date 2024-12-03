/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.extras.api.AtlassianLicense
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.fugue.Maybe
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.license;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.license.exception.LicenseException;
import com.atlassian.confluence.util.FugueConversionUtil;
import com.atlassian.extras.api.AtlassianLicense;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.fugue.Maybe;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
public interface LicenseService {
    public @NonNull ConfluenceLicense retrieve() throws LicenseException;

    public @NonNull AtlassianLicense retrieveAtlassianLicense() throws LicenseException;

    @Deprecated
    public @NonNull Maybe<ProductLicense> retrieve(Product var1) throws LicenseException;

    default public @NonNull Optional<ProductLicense> retrieveForProduct(Product product) throws LicenseException {
        return FugueConversionUtil.toOptional(this.retrieve(product));
    }

    public @NonNull ConfluenceLicense validate(String var1) throws LicenseException;

    public @NonNull ProductLicense validate(String var1, Product var2) throws LicenseException;

    public @NonNull ConfluenceLicense install(String var1) throws LicenseException;

    public boolean isLicensedForDataCenter() throws LicenseException;

    public boolean isLicensedForDataCenterOrExempt() throws LicenseException;
}

