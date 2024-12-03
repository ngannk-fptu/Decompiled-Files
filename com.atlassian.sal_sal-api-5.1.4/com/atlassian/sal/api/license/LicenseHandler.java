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
import com.atlassian.sal.api.i18n.InvalidOperationException;
import com.atlassian.sal.api.license.MultiProductLicenseDetails;
import com.atlassian.sal.api.license.RawProductLicense;
import com.atlassian.sal.api.license.SingleProductLicenseDetailsView;
import com.atlassian.sal.api.validate.MultipleLicensesValidationResult;
import com.atlassian.sal.api.validate.ValidationResult;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PublicApi
public interface LicenseHandler {
    @Deprecated
    public void setLicense(String var1);

    public boolean hostAllowsMultipleLicenses();

    public boolean hostAllowsCustomProducts();

    public Set<String> getProductKeys();

    public void addProductLicense(@Nonnull String var1, @Nonnull String var2) throws InvalidOperationException;

    public void addProductLicenses(@Nonnull Set<RawProductLicense> var1) throws InvalidOperationException;

    public void removeProductLicense(@Nonnull String var1) throws InvalidOperationException;

    @Nonnull
    public ValidationResult validateProductLicense(@Nonnull String var1, @Nonnull String var2, @Nullable Locale var3);

    @Nonnull
    public MultipleLicensesValidationResult validateMultipleProductLicenses(@Nonnull Set<RawProductLicense> var1, @Nullable Locale var2);

    @Nullable
    public String getServerId();

    @Nullable
    @Deprecated
    public String getSupportEntitlementNumber();

    @Nonnull
    public SortedSet<String> getAllSupportEntitlementNumbers();

    @Nullable
    public String getRawProductLicense(String var1);

    @Nullable
    public SingleProductLicenseDetailsView getProductLicenseDetails(@Nonnull String var1);

    @Nonnull
    public Collection<MultiProductLicenseDetails> getAllProductLicenses();

    @Nonnull
    public MultiProductLicenseDetails decodeLicenseDetails(@Nonnull String var1);
}

