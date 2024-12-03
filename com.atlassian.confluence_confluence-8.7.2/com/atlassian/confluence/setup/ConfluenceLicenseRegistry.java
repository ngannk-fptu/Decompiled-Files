/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.fugue.Option
 *  com.atlassian.license.LicenseException
 *  com.atlassian.license.LicensePair
 *  com.atlassian.license.LicenseRegistry
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.fugue.Option;
import com.atlassian.license.LicenseException;
import com.atlassian.license.LicensePair;
import com.atlassian.license.LicenseRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class ConfluenceLicenseRegistry
implements LicenseRegistry {
    private final Logger log = LoggerFactory.getLogger(ConfluenceLicenseRegistry.class);
    private BootstrapManager bootstrapManager;

    public String getLicenseHash() {
        this.log.debug("Deprecated API getLicenseHash() called");
        Option<LicensePair> license = this.retrieveLicensePair();
        if (license.isEmpty()) {
            return null;
        }
        return ((LicensePair)license.get()).getHashString();
    }

    public void setLicenseHash(String string) {
        this.log.debug("Deprecated API setLicenseHash() called");
        throw new UnsupportedOperationException("Deprecated API setLicenseHash() called");
    }

    public String getLicenseMessage() {
        this.log.debug("Deprecated API getLicenseMessage() called");
        Option<LicensePair> license = this.retrieveLicensePair();
        if (license.isEmpty()) {
            return null;
        }
        return ((LicensePair)license.get()).getLicenseString();
    }

    public void setLicenseMessage(String string) {
        this.log.debug("Deprecated API setLicenseMessage() called");
        throw new UnsupportedOperationException("Deprecated API setLicenseMessage() called");
    }

    private BootstrapManager getBootstrapManager() {
        if (this.bootstrapManager == null) {
            this.bootstrapManager = (BootstrapManager)BootstrapUtils.getBootstrapManager();
        }
        return this.bootstrapManager;
    }

    public void setBootstrapManager(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    private Option<LicensePair> retrieveLicensePair() {
        try {
            String atlassianLicense = this.getBootstrapManager().getString("atlassian.license.message");
            if (atlassianLicense != null) {
                return Option.some((Object)new LicensePair(atlassianLicense));
            }
            String licenseMessage = this.getBootstrapManager().getString("confluence.license.message");
            String licenseHash = this.getBootstrapManager().getString("confluence.license.hash");
            if (licenseMessage != null && licenseHash != null) {
                return Option.some((Object)new LicensePair(licenseMessage, licenseHash));
            }
            this.log.warn("Unable to locate license details.");
        }
        catch (LicenseException ex) {
            this.log.warn("Unable to retrieve the license pair for the installed license", (Throwable)ex);
        }
        return Option.none();
    }
}

