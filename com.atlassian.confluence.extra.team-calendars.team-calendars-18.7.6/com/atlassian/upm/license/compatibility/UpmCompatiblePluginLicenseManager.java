/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.upm.api.license.PluginLicenseManager
 *  com.atlassian.upm.api.license.entity.LicenseError
 *  com.atlassian.upm.api.license.entity.PluginLicense
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.license.compatibility;

import com.atlassian.extras.api.ProductLicense;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.atlassian.upm.api.license.entity.LicenseError;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.license.compatibility.CompatibleLicenseStatus;
import com.atlassian.upm.license.compatibility.CompatiblePluginLicenseManager;
import com.google.common.base.Preconditions;
import java.net.URI;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpmCompatiblePluginLicenseManager
implements CompatiblePluginLicenseManager {
    private static final Logger log = LoggerFactory.getLogger(UpmCompatiblePluginLicenseManager.class);
    private final CompatiblePluginLicenseManager defaultPluginLicenseManager;
    private final PluginLicenseManager licenseManager;
    private final String pluginKey;

    public UpmCompatiblePluginLicenseManager(CompatiblePluginLicenseManager defaultPluginLicenseManager, PluginLicenseManager licenseManager, String pluginKey) {
        this.defaultPluginLicenseManager = (CompatiblePluginLicenseManager)Preconditions.checkNotNull((Object)defaultPluginLicenseManager, (Object)"defaultPluginLicenseManager");
        this.licenseManager = (PluginLicenseManager)Preconditions.checkNotNull((Object)licenseManager, (Object)"licenseManager");
        this.pluginKey = (String)Preconditions.checkNotNull((Object)pluginKey, (Object)"pluginKey");
    }

    @Override
    public ProductLicense setLicense(String rawLicense) {
        throw new UnsupportedOperationException("Cannot set license with UPM's PluginLicenseManager. Use UPM's user interface instead. See CompatiblePluginLicenseManager.getPluginLicenseAdministrationUri()");
    }

    @Override
    public ProductLicense getCurrentLicense() {
        Iterator i$ = this.licenseManager.getLicense().iterator();
        if (i$.hasNext()) {
            PluginLicense pluginLicense = (PluginLicense)i$.next();
            return this.getLicense(pluginLicense.getRawLicense());
        }
        return null;
    }

    @Override
    public ProductLicense getLicense(String rawLicense) {
        return this.defaultPluginLicenseManager.getLicense(rawLicense);
    }

    @Override
    public CompatibleLicenseStatus getCurrentLicenseStatus() {
        for (PluginLicense license : this.licenseManager.getLicense()) {
            if (license.isValid()) {
                return license.isEvaluation() ? CompatibleLicenseStatus.EVALUATION : CompatibleLicenseStatus.ACTIVE;
            }
            Iterator i$ = license.getError().iterator();
            if (!i$.hasNext()) continue;
            LicenseError error = (LicenseError)i$.next();
            switch (error) {
                case EXPIRED: {
                    return license.isEvaluation() ? CompatibleLicenseStatus.EVALUATION_EXPIRED : CompatibleLicenseStatus.EXPIRED;
                }
                case TYPE_MISMATCH: {
                    return CompatibleLicenseStatus.INCOMPATIBLE_TYPE;
                }
                case USER_MISMATCH: 
                case EDITION_MISMATCH: {
                    return CompatibleLicenseStatus.USER_MISMATCH;
                }
                case VERSION_MISMATCH: {
                    return CompatibleLicenseStatus.MAINTENANCE_EXPIRED;
                }
            }
            log.info("Unknown license error: " + license.getError());
            return CompatibleLicenseStatus.INACTIVE;
        }
        return CompatibleLicenseStatus.INVALID;
    }

    @Override
    public CompatibleLicenseStatus getLicenseStatus(String rawLicense) {
        return this.defaultPluginLicenseManager.getLicenseStatus(rawLicense);
    }

    @Override
    public URI getPluginLicenseAdministrationUri() {
        return URI.create("/plugins/servlet/upm?fragment=manage/" + this.pluginKey);
    }
}

