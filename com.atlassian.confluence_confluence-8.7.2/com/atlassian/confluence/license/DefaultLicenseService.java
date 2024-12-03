/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.crowd.embedded.spi.DcLicenseChecker
 *  com.atlassian.extras.api.AtlassianLicense
 *  com.atlassian.extras.api.LicenseException
 *  com.atlassian.extras.api.LicenseManager
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.extras.common.LicenseException
 *  com.atlassian.extras.core.AtlassianLicenseFactory
 *  com.atlassian.extras.core.DefaultAtlassianLicenseFactory
 *  com.atlassian.extras.core.confluence.ConfluenceProductLicenseFactory
 *  com.atlassian.extras.decoder.api.LicenseVerificationException
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.license;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.internal.license.LicenseServiceInternal;
import com.atlassian.confluence.internal.license.store.LicenseStoreInternal;
import com.atlassian.confluence.license.exception.EmptyLicenseValidationException;
import com.atlassian.confluence.license.exception.ForgedLicenseException;
import com.atlassian.confluence.license.exception.InvalidLicenseException;
import com.atlassian.confluence.license.exception.MissingConfluenceLicenseValidationException;
import com.atlassian.confluence.license.validator.LicenseValidator;
import com.atlassian.crowd.embedded.spi.DcLicenseChecker;
import com.atlassian.extras.api.AtlassianLicense;
import com.atlassian.extras.api.LicenseManager;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.extras.common.LicenseException;
import com.atlassian.extras.core.AtlassianLicenseFactory;
import com.atlassian.extras.core.DefaultAtlassianLicenseFactory;
import com.atlassian.extras.core.confluence.ConfluenceProductLicenseFactory;
import com.atlassian.extras.decoder.api.LicenseVerificationException;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.Properties;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class DefaultLicenseService
implements LicenseServiceInternal,
DcLicenseChecker {
    private static final Logger log = LoggerFactory.getLogger(DefaultLicenseService.class);
    private LicenseStoreInternal store;
    private LicenseManager decoder;
    private AtlassianLicenseFactory licenseFactory;
    private static final String BYPASS_DATA_CENTER_CHECK_PROPERTY_KEY = "confluence.bypass.data.center.check";
    private LicenseValidator validator;

    public DefaultLicenseService(LicenseStoreInternal store, LicenseManager decoder, LicenseValidator validator, AtlassianLicenseFactory licenseFactory) {
        this.store = (LicenseStoreInternal)Preconditions.checkNotNull((Object)store);
        this.decoder = (LicenseManager)Preconditions.checkNotNull((Object)decoder);
        this.validator = (LicenseValidator)Preconditions.checkNotNull((Object)validator);
        this.licenseFactory = (AtlassianLicenseFactory)Preconditions.checkNotNull((Object)licenseFactory);
    }

    @Override
    public @NonNull ConfluenceLicense retrieve() {
        return this.assertConfluenceLicense(this.store.retrieve());
    }

    @Override
    public @NonNull AtlassianLicense retrieveAtlassianLicense() throws com.atlassian.extras.api.LicenseException {
        return this.store.retrieve();
    }

    @Override
    @Deprecated
    public @NonNull Maybe<ProductLicense> retrieve(Product product) throws com.atlassian.extras.api.LicenseException {
        ProductLicense license = this.store.retrieve().getProductLicense(product);
        return license == null ? Option.none() : Option.some((Object)license);
    }

    @Override
    public @NonNull ConfluenceLicense install(String licenseString) {
        String sanitisedLicenseString = this.sanitise(licenseString);
        ConfluenceLicense license = this.validate(sanitisedLicenseString);
        this.store.install(sanitisedLicenseString);
        return license;
    }

    @Override
    public boolean isLicensed() throws com.atlassian.extras.api.LicenseException {
        return this.store.retrieveOptional().isPresent();
    }

    @Override
    public boolean isLicensedForDataCenter() {
        return this.retrieve().isClusteringEnabled();
    }

    @Override
    public boolean isLicensedForDataCenterOrExempt() {
        ConfluenceLicense license = this.retrieve();
        return this.isLicensedForDataCenter() || "true".equals(license.getProperty(BYPASS_DATA_CENTER_CHECK_PROPERTY_KEY));
    }

    @Override
    public @NonNull ConfluenceLicense validate(String licenseString) {
        return this.decodeAndValidate(this.sanitise(licenseString));
    }

    @Override
    public @NonNull ProductLicense validate(String licenseString, Product product) throws com.atlassian.extras.api.LicenseException {
        return this.assertProductLicense(this.decode(licenseString), product);
    }

    private ConfluenceLicense decodeAndValidate(String licenseString) {
        ConfluenceLicense license = this.assertConfluenceLicense(this.decode(licenseString));
        this.validator.validate(license);
        return license;
    }

    @VisibleForTesting
    public AtlassianLicense decode(String licenseString) {
        try {
            AtlassianLicense license = this.decodeAndValidateLicense(licenseString);
            if (license == null) {
                throw new InvalidLicenseException(licenseString);
            }
            return license;
        }
        catch (LicenseException e) {
            throw new InvalidLicenseException(licenseString, e);
        }
    }

    @VisibleForTesting
    public String sanitise(String licenseString) {
        if (StringUtils.isEmpty((CharSequence)licenseString)) {
            throw new EmptyLicenseValidationException();
        }
        return licenseString.replaceAll("\\\\n\\\\", "");
    }

    @VisibleForTesting
    public ConfluenceLicense assertConfluenceLicense(AtlassianLicense license) {
        ProductLicense confluenceLicense = license.getProductLicense(Product.CONFLUENCE);
        if (confluenceLicense == null) {
            throw new MissingConfluenceLicenseValidationException(license.getProductLicenses());
        }
        if (!(confluenceLicense instanceof ConfluenceLicense)) {
            throw new IllegalStateException(String.format("Received an instance of [%s] which is not a child of [%s], either [%s] does not create this type anymore or it was not registered under %s [%s] in [%s].", license.getClass().getName(), ConfluenceLicense.class.getName(), ConfluenceProductLicenseFactory.class.getName(), Product.CONFLUENCE.getName(), Product.class.getName(), DefaultAtlassianLicenseFactory.class.getName()));
        }
        return (ConfluenceLicense)confluenceLicense;
    }

    @VisibleForTesting
    public ProductLicense assertProductLicense(AtlassianLicense license, Product product) {
        ProductLicense productLicense = license.getProductLicense(product);
        if (productLicense == null) {
            throw new InvalidLicenseException("Invalid license for " + product.getName());
        }
        return productLicense;
    }

    public AtlassianLicense decodeAndValidateLicense(@Nonnull String licenseString) {
        try {
            return this.decoder.getLicense(licenseString);
        }
        catch (LicenseVerificationException e) {
            Properties licenseProperties = e.getLicenseProperties();
            if (licenseProperties == null || licenseProperties.isEmpty()) {
                log.warn("The provided license did not have any valid properties");
            }
            AtlassianLicense decoded = this.licenseFactory.getLicense(licenseProperties);
            return this.getForgedLicense(decoded);
        }
        catch (Exception e) {
            log.warn("The provided license could not be decoded", (Throwable)e);
        }
        throw new IllegalArgumentException();
    }

    public AtlassianLicense getForgedLicense(AtlassianLicense decode) {
        ProductLicense productLicense = decode.getProductLicense(Product.CONFLUENCE);
        if (productLicense instanceof ConfluenceLicense) {
            throw new ForgedLicenseException();
        }
        return null;
    }

    public boolean isDcLicense() {
        return this.isLicensedForDataCenter();
    }
}

