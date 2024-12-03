/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.base.Preconditions
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.ServletContextListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup;

import com.atlassian.confluence.internal.health.JohnsonEventLevel;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.license.exception.LicenseException;
import com.atlassian.confluence.setup.johnson.JohnsonUtils;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.base.Preconditions;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidLicenseContextListener
implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(ValidLicenseContextListener.class);
    private LicenseService licenseService;
    private Boolean forceRun;

    public void contextInitialized(ServletContextEvent event) {
        log.debug("ValidLicenseContextListener contextInitialized called");
        if (this.shouldRunListener()) {
            try {
                this.getLicenseService().retrieve();
            }
            catch (LicenseException e) {
                log.error("No valid license found.");
                JohnsonUtils.raiseJohnsonEventRequiringTranslation(JohnsonEventType.LICENSE_INCONSISTENCY, "license.invalid.error", null, JohnsonEventLevel.ERROR);
            }
        }
        log.debug("ValidLicenseContextListener contextInitialized completed successfully");
    }

    private boolean shouldRunListener() {
        if (this.forceRun != null) {
            return this.forceRun;
        }
        return GeneralUtil.isSetupComplete() && ContainerManager.isContainerSetup();
    }

    void setForceRun(Boolean forceRun) {
        this.forceRun = forceRun;
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }

    private LicenseService getLicenseService() {
        if (this.licenseService == null) {
            return (LicenseService)ContainerManager.getComponent((String)"licenseService");
        }
        return this.licenseService;
    }

    public void setLicenseService(LicenseService licenseService) {
        this.licenseService = (LicenseService)Preconditions.checkNotNull((Object)licenseService);
    }
}

