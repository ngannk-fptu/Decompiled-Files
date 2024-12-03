/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.license.License
 *  com.atlassian.license.LicenseException
 *  com.atlassian.license.LicensePair
 *  com.atlassian.license.decoder.LicenseDecoder
 *  com.google.common.base.Preconditions
 *  io.atlassian.fugue.Either
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.admin.actions;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ConfluenceSidManager;
import com.atlassian.confluence.license.LicenseWebFacade;
import com.atlassian.confluence.license.util.ConfluenceLicenseUtils;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.license.License;
import com.atlassian.license.LicenseException;
import com.atlassian.license.LicensePair;
import com.atlassian.license.decoder.LicenseDecoder;
import com.google.common.base.Preconditions;
import io.atlassian.fugue.Either;
import java.util.Date;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class AbstractUpdateLicenseAction
extends ConfluenceActionSupport {
    private ClusterManager clusterManager;
    private String licenseString;
    private UserChecker userChecker;
    private boolean licenseSubmitted = false;
    private ConfluenceSidManager sidManager;
    private LicenseWebFacade webLicenseFacade;
    private ApplicationConfiguration applicationConfig;

    @Deprecated
    public @NonNull License getLicense() {
        LicensePair licensePair;
        block5: {
            Preconditions.checkState((this.applicationConfig != null ? 1 : 0) != 0, (Object)"The action has not been properly initialised, so this invocation is not supported.");
            try {
                String licenseString = (String)this.applicationConfig.getProperty((Object)"atlassian.license.message");
                if (licenseString != null) {
                    licensePair = new LicensePair(licenseString);
                    break block5;
                }
                String licenseMessage = (String)this.applicationConfig.getProperty((Object)"confluence.license.message");
                String licenseHash = (String)this.applicationConfig.getProperty((Object)"confluence.license.hash");
                if (licenseMessage != null && licenseHash != null) {
                    licensePair = new LicensePair(licenseMessage, licenseHash);
                    break block5;
                }
                throw new IllegalStateException("Unable to retrieve the license details");
            }
            catch (LicenseException e) {
                throw new IllegalStateException("Unable to parse the license details", e);
            }
        }
        License result = LicenseDecoder.getLicense((LicensePair)licensePair, (String)"CONF");
        if (result == null) {
            throw new IllegalStateException("Unable to decode the license details");
        }
        return result;
    }

    public ConfluenceLicense getConfluenceLicense() {
        Either<String, ConfluenceLicense> result = this.webLicenseFacade.retrieveLicense();
        if (result.isLeft()) {
            throw new IllegalStateException("A license must have been installed, unable to retrieve it.");
        }
        return (ConfluenceLicense)result.right().get();
    }

    public boolean isLicenseReadable() {
        return this.webLicenseFacade.retrieveLicense().isRight();
    }

    public String getLicenseString() {
        return this.licenseString;
    }

    public void setLicenseString(String licenseString) {
        this.licenseString = licenseString;
    }

    public UserChecker getUserChecker() {
        return this.userChecker;
    }

    public void setUserChecker(UserChecker userChecker) {
        this.userChecker = userChecker;
    }

    public Date getSupportPeriodEnd() {
        return new Date(ConfluenceLicenseUtils.getSupportPeriodEnd(this.getConfluenceLicense()));
    }

    public boolean isHasSupportPeriodExpired() {
        return new Date().after(this.getSupportPeriodEnd());
    }

    @Override
    public void validate() {
        Either<String, ConfluenceLicense> validationResult = this.webLicenseFacade.validateLicense(this.licenseString);
        if (validationResult.isLeft()) {
            this.addFieldError("licenseString", (String)validationResult.left().get());
        }
    }

    public String doUpdate() throws Exception {
        Either<String, ConfluenceLicense> installationResult = this.webLicenseFacade.installLicense(this.licenseString);
        if (installationResult.isLeft()) {
            this.addFieldError("licenseString", (String)installationResult.left().get());
            return "error";
        }
        this.licenseString = "";
        return "success";
    }

    public String getSid() {
        try {
            return this.sidManager.getSid();
        }
        catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isLicenseSubmitted() {
        return this.licenseSubmitted;
    }

    public void setLicenseSubmitted(boolean licenseSubmitted) {
        this.licenseSubmitted = licenseSubmitted;
    }

    public void setClusterManager(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    protected ClusterManager getClusterManager() {
        return this.clusterManager;
    }

    public void setSidManager(ConfluenceSidManager sidManager) {
        this.sidManager = sidManager;
    }

    public void setWebLicenseFacade(LicenseWebFacade webLicenseFacade) {
        this.webLicenseFacade = webLicenseFacade;
    }

    public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
        this.applicationConfig = (ApplicationConfiguration)Preconditions.checkNotNull((Object)applicationConfig);
    }
}

