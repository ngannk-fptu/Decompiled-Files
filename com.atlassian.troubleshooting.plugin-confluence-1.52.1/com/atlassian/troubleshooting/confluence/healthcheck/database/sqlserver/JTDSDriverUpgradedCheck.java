/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.status.service.systeminfo.DatabaseInfo
 *  com.atlassian.confluence.upgrade.BuildNumber
 *  org.apache.commons.lang.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.healthcheck.database.sqlserver;

import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.DatabaseInfo;
import com.atlassian.confluence.upgrade.BuildNumber;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import java.io.Serializable;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class JTDSDriverUpgradedCheck
implements SupportHealthCheck {
    private static final BuildNumber AUTOMATIC_JTDS_UPGRADE_VERSION = new BuildNumber("8602");
    private static final String JTDS_DRIVER_CLASS = "net.sourceforge.jtds.jdbc.Driver";
    private final BootstrapManager bootstrapManager;
    private final SystemInformationService systemInformationService;
    private final SupportHealthStatusBuilder supportHealthStatusBuilder;

    @Autowired
    public JTDSDriverUpgradedCheck(BootstrapManager bootstrapManager, SystemInformationService systemInformationService, SupportHealthStatusBuilder supportHealthStatusBuilder) {
        this.bootstrapManager = bootstrapManager;
        this.systemInformationService = systemInformationService;
        this.supportHealthStatusBuilder = supportHealthStatusBuilder;
    }

    @Override
    public boolean isNodeSpecific() {
        return false;
    }

    @Override
    public SupportHealthStatus check() {
        String datasourceDriverClass;
        DatabaseInfo databaseInfo = this.systemInformationService.getDatabaseInfo();
        String jdbcDriverClass = (String)this.bootstrapManager.getProperty("hibernate.connection.driver_class");
        String datasource = (String)this.bootstrapManager.getProperty("hibernate.connection.datasource");
        String string = datasourceDriverClass = StringUtils.isBlank((String)datasource) || StringUtils.isBlank((String)databaseInfo.getDriverName()) ? "" : databaseInfo.getDriverName();
        if (JTDS_DRIVER_CLASS.equals(datasourceDriverClass) || JTDS_DRIVER_CLASS.equals(jdbcDriverClass)) {
            return this.warnIfRequired();
        }
        return this.supportHealthStatusBuilder.ok(this, "confluence.healthcheck.jtds.driver.upgrade.ok", new Serializable[0]);
    }

    private SupportHealthStatus warnIfRequired() {
        if (this.currentBuildNumber().isLowerThan(AUTOMATIC_JTDS_UPGRADE_VERSION)) {
            return this.supportHealthStatusBuilder.ok(this, "confluence.healthcheck.jtds.driver.upgrade.recommended", new Serializable[0]);
        }
        return this.supportHealthStatusBuilder.warning(this, "confluence.healthcheck.jtds.driver.upgrade.required", new Serializable[0]);
    }

    private BuildNumber currentBuildNumber() {
        return new BuildNumber(this.systemInformationService.getConfluenceInfo().getBuildNumber());
    }
}

