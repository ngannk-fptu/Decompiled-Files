/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.extras.api.LicenseType
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.healthcheck.support;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.extras.api.LicenseType;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;

public final class HsqlHealthCheck
implements SupportHealthCheck {
    private static final String H2DIALECT = "h2dialect";
    private final LicenseService licenseService;
    private final SupportHealthStatusBuilder healthStatusBuilder;
    private final SystemInformationService systemInformationService;

    @Autowired
    public HsqlHealthCheck(SystemInformationService service, LicenseService licenseService, SupportHealthStatusBuilder supportHealthStatusBuilder) {
        this.systemInformationService = service;
        this.licenseService = licenseService;
        this.healthStatusBuilder = supportHealthStatusBuilder;
    }

    @Override
    public boolean isNodeSpecific() {
        return false;
    }

    @Override
    public SupportHealthStatus check() {
        boolean evaluationOrDevLicense;
        String databaseDialect = this.systemInformationService.getDatabaseInfo().getDialect();
        ConfluenceLicense license = this.licenseService.retrieve();
        boolean bl = evaluationOrDevLicense = license.isEvaluation() || license.getLicenseType().equals((Object)LicenseType.DEVELOPER);
        if (HibernateConfig.isHsqlDialect((String)databaseDialect)) {
            if (!evaluationOrDevLicense) {
                return this.healthStatusBuilder.major(this, "confluence.healthcheck.hsql.database.on.production", new Serializable[0]);
            }
            return this.healthStatusBuilder.ok(this, "confluence.healthcheck.hsql.database.on.eval", new Serializable[0]);
        }
        if (databaseDialect.toLowerCase().contains(H2DIALECT)) {
            if (!evaluationOrDevLicense) {
                return this.healthStatusBuilder.major(this, "confluence.healthcheck.h2.database.on.production", new Serializable[0]);
            }
            return this.healthStatusBuilder.ok(this, "confluence.healthcheck.h2.database.on.eval", new Serializable[0]);
        }
        return this.healthStatusBuilder.ok(this, "confluence.healthcheck.hsql.valid", new Serializable[0]);
    }
}

