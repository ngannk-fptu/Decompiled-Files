/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.AtlassianLicense
 *  com.atlassian.extras.api.LicenseException
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.extras.api.plugin.PluginLicense
 *  com.atlassian.extras.decoder.api.LicenseVerificationException
 *  com.atlassian.extras.decoder.v2.Version2LicenseDecoder
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.upm.license.internal.impl;

import com.atlassian.extras.api.AtlassianLicense;
import com.atlassian.extras.api.LicenseException;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.extras.decoder.api.LicenseVerificationException;
import com.atlassian.extras.decoder.v2.Version2LicenseDecoder;
import com.atlassian.plugin.Plugin;
import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Either;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.HostLicenseProvider;
import com.atlassian.upm.license.internal.LicenseEntityFactory;
import com.atlassian.upm.license.internal.LicenseManagerProvider;
import com.atlassian.upm.license.internal.PluginLicenseDowngradeError;
import com.atlassian.upm.license.internal.PluginLicenseError;
import com.atlassian.upm.license.internal.PluginLicenseValidator;
import com.atlassian.upm.license.internal.ProductLicenses;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PluginLicenseValidatorImpl
implements PluginLicenseValidator {
    private final LicenseEntityFactory factory;
    private final LicenseManagerProvider licManagerProvider;
    private final UpmPluginAccessor pluginAccessor;
    private final HostLicenseProvider hostLicenseProvider;

    public PluginLicenseValidatorImpl(LicenseEntityFactory factory, LicenseManagerProvider licManagerProvider, UpmPluginAccessor pluginAccessor, HostLicenseProvider hostLicenseProvider) {
        this.licManagerProvider = Objects.requireNonNull(licManagerProvider, "licManagerProvider");
        this.factory = Objects.requireNonNull(factory, "factory");
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor, "pluginAccessor");
        this.hostLicenseProvider = Objects.requireNonNull(hostLicenseProvider, "hostLicenseProvider");
    }

    @Override
    public Either<PluginLicenseError, PluginLicense> validate(String pluginKey, String rawLicense) {
        try {
            Product product = new Product(pluginKey, pluginKey, true);
            ProductLicense license = this.getProductLicense(product, rawLicense);
            if (license == null || !ProductLicenses.isActive(license, product).isDefined()) {
                return Either.left(new PluginLicenseError(PluginLicenseError.Type.INVALID_LICENSE_ERROR));
            }
            if (!(license instanceof com.atlassian.extras.api.plugin.PluginLicense)) {
                return Either.left(new PluginLicenseError(PluginLicenseError.Type.UNKNOWN_VALIDATION_ERROR));
            }
            Option<Plugin> plugin = this.pluginAccessor.getPlugin(pluginKey);
            return Either.right(this.factory.getPluginLicense(license, pluginKey, plugin, rawLicense, this.hostLicenseProvider.getHostApplicationLicenseAttributes(), this.isForgedLicense(rawLicense)));
        }
        catch (LicenseException licenseException) {
            return Either.left(new PluginLicenseError(PluginLicenseError.Type.UNKNOWN_VALIDATION_ERROR, licenseException));
        }
    }

    protected boolean isForgedLicense(String rawLicense) {
        Version2LicenseDecoder licenseDecoderWithHashVerification = new Version2LicenseDecoder(true, true);
        try {
            licenseDecoderWithHashVerification.decode(rawLicense);
        }
        catch (LicenseVerificationException licenseVerificationException) {
            return true;
        }
        return false;
    }

    public Set<PluginLicenseDowngradeError> validateDowngrade(PluginLicense currentLicense, PluginLicense newLicense) {
        HashSet<PluginLicenseDowngradeError> errorSet = new HashSet<PluginLicenseDowngradeError>();
        if (!currentLicense.isEvaluation()) {
            if (newLicense.isEvaluation()) {
                errorSet.add(PluginLicenseDowngradeError.EVALUATION_DOWNGRADE);
            }
            if (currentLicense.isDataCenter() || !newLicense.isDataCenter()) {
                if (PluginLicenseValidatorImpl.diff(currentLicense.getExpiryDate(), newLicense.getExpiryDate()) > 0) {
                    errorSet.add(PluginLicenseDowngradeError.EXPIRY_DATE_DOWNGRADE);
                }
                if (PluginLicenseValidatorImpl.diff(currentLicense.getMaintenanceExpiryDate(), newLicense.getMaintenanceExpiryDate()) > 0) {
                    errorSet.add(PluginLicenseDowngradeError.MAINTENANCE_EXPIRY_DATE_DOWNGRADE);
                }
            }
            if (currentLicense.getEditionType() == newLicense.getEditionType() && PluginLicenseValidatorImpl.diff(currentLicense.getEdition(), newLicense.getEdition()) > 0) {
                switch (currentLicense.getEditionType()) {
                    case USER_COUNT: {
                        errorSet.add(PluginLicenseDowngradeError.USER_DOWNGRADE);
                        break;
                    }
                    case ROLE_COUNT: {
                        errorSet.add(PluginLicenseDowngradeError.ROLE_DOWNGRADE);
                        break;
                    }
                }
            }
        }
        return Collections.unmodifiableSet(errorSet);
    }

    private static <T extends Comparable> int diff(Option<T> left, Option<T> right) {
        return (Integer)right.map(r -> (Integer)left.map(l -> l.compareTo(r)).getOrElse(1)).orElse(left.map(a -> -1)).getOrElse(0);
    }

    private ProductLicense getProductLicense(Product plugin, String rawLicense) {
        AtlassianLicense atlassianLicense = this.licManagerProvider.registerPlugin(plugin).getLicense(rawLicense);
        return atlassianLicense.getProductLicense(plugin);
    }
}

