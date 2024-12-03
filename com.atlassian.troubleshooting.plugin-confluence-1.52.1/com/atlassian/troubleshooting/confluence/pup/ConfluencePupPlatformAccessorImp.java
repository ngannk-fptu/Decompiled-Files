/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.synchrony.api.SynchronyProcessManager
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.status.service.systeminfo.DatabaseInfo
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang.StringUtils
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.pup;

import com.atlassian.confluence.plugins.synchrony.api.SynchronyProcessManager;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.DatabaseInfo;
import com.atlassian.troubleshooting.healthcheck.accessors.DbPlatform;
import com.atlassian.troubleshooting.healthcheck.accessors.DbPlatformFactory;
import com.atlassian.troubleshooting.healthcheck.impl.DbVersionExtractor;
import com.atlassian.troubleshooting.healthcheck.model.DbType;
import com.atlassian.troubleshooting.preupgrade.accessors.ConfluencePupPlatformAccessor;
import com.atlassian.troubleshooting.preupgrade.model.MicroservicePreUpgradeDataDTO;
import com.atlassian.troubleshooting.preupgrade.modz.Modifications;
import com.atlassian.troubleshooting.preupgrade.modz.ModzDetectorService;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.apache.commons.lang.StringUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfluencePupPlatformAccessorImp
implements ConfluencePupPlatformAccessor {
    private final SystemInformationService sysInfoService;
    private final DbVersionExtractor dbVersionExtractor;
    private final ModzDetectorService modzDetectorService;
    private final BundleContext bundleContext;
    private final DbPlatformFactory dbPlatformFactory;

    @Autowired
    public ConfluencePupPlatformAccessorImp(SystemInformationService sysInfoService, DbVersionExtractor dbVersionExtractor, ModzDetectorService modzDetectorService, BundleContext bundleContext, DbPlatformFactory dbPlatformFactory) {
        this.sysInfoService = Objects.requireNonNull(sysInfoService);
        this.dbVersionExtractor = Objects.requireNonNull(dbVersionExtractor);
        this.modzDetectorService = Objects.requireNonNull(modzDetectorService);
        this.bundleContext = Objects.requireNonNull(bundleContext);
        this.dbPlatformFactory = Objects.requireNonNull(dbPlatformFactory);
    }

    @Override
    @Nonnull
    public Optional<DbPlatform> getCurrentDbPlatform() {
        DatabaseInfo databaseInfo = this.sysInfoService.getDatabaseInfo();
        return this.dialectNameToDbType(databaseInfo.getDialect()).map(dbType -> this.dbPlatformFactory.create((DbType)((Object)dbType), this.dbVersionExtractor.getSupportedPlatformVersionComparisonString((DbType)((Object)dbType), databaseInfo.getVersion())));
    }

    @Override
    public boolean isSynchronyStandalone() {
        Object processManagerObject;
        ServiceReference processManagerRef = this.bundleContext.getServiceReference("com.atlassian.confluence.plugins.synchrony.api.SynchronyProcessManager");
        if (processManagerRef != null && (processManagerObject = this.bundleContext.getService(processManagerRef)) instanceof SynchronyProcessManager) {
            SynchronyProcessManager synchronyProcessManager = (SynchronyProcessManager)processManagerObject;
            return synchronyProcessManager.isSynchronyClusterManuallyManaged();
        }
        return true;
    }

    @Override
    public String getVersion() {
        return this.sysInfoService.getConfluenceInfo().getVersion();
    }

    @Override
    @Nonnull
    public Optional<Modifications> getModifiedFiles() {
        return this.modzDetectorService.getModifications();
    }

    @Override
    @Nonnull
    public MicroservicePreUpgradeDataDTO.Version.SubProduct calculateSubProduct() {
        return MicroservicePreUpgradeDataDTO.Version.SubProduct.CONFLUENCE;
    }

    private Optional<DbType> dialectNameToDbType(String dialect) {
        return Arrays.stream(DbType.values()).filter(dbType -> StringUtils.containsIgnoreCase((String)dialect, (String)this.getHibernateDialectSubString((DbType)((Object)dbType)))).findFirst();
    }

    private String getHibernateDialectSubString(DbType dbType) {
        switch (dbType) {
            case oracle: {
                return "Oracle";
            }
            case sqlServer: {
                return "SQLServer";
            }
            case mysql: {
                return "MySQL";
            }
            case h2: {
                return "H2";
            }
            case postgres: {
                return "PostgreSQL";
            }
            case mariaDB: {
                return "MariaDB";
            }
        }
        throw new RuntimeException("Unexpected DbType: " + (Object)((Object)dbType));
    }
}

