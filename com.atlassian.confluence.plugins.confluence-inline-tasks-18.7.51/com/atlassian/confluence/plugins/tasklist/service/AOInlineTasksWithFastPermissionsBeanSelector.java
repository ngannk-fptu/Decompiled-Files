/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.confluence.security.denormalisedpermissions.BulkPermissionService
 *  com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionStateManager
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.status.service.systeminfo.DatabaseInfo
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.util.concurrent.LazyReference
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.plugins.tasklist.ao.dao.AOInlineTasksWithFastPermissionsDao;
import com.atlassian.confluence.plugins.tasklist.ao.dao.AOMySQL8InlineTasksWithFastPermissionsDao;
import com.atlassian.confluence.plugins.tasklist.ao.dao.AOOracleInlineTasksWithFastPermissionsDao;
import com.atlassian.confluence.plugins.tasklist.ao.dao.AOPostgresInlineTasksWithFastPermissionsDao;
import com.atlassian.confluence.plugins.tasklist.ao.dao.AOSqlServerInlineTasksWithFastPermissionsDao;
import com.atlassian.confluence.security.denormalisedpermissions.BulkPermissionService;
import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionStateManager;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.DatabaseInfo;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.util.concurrent.LazyReference;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AOInlineTasksWithFastPermissionsBeanSelector {
    private static final Logger log = LoggerFactory.getLogger(AOInlineTasksWithFastPermissionsBeanSelector.class);
    private final ActiveObjects ao;
    private final BulkPermissionService bulkPermissionService;
    private final DarkFeaturesManager darkFeaturesManager;
    private final DenormalisedPermissionStateManager denormalisedPermissionStateManager;
    private final LazyReference<Optional<AOInlineTasksWithFastPermissionsDao>> aoInlineTasksWithFastPermissionsDaoRef;
    private final SystemInformationService systemInformationService;

    public AOInlineTasksWithFastPermissionsBeanSelector(@ComponentImport ActiveObjects ao, @ComponentImport BulkPermissionService bulkPermissionService, @ComponentImport DarkFeaturesManager darkFeaturesManager, @ComponentImport DenormalisedPermissionStateManager denormalisedPermissionStateManager, @ComponentImport SystemInformationService systemInformationService) {
        this.ao = ao;
        this.bulkPermissionService = bulkPermissionService;
        this.darkFeaturesManager = darkFeaturesManager;
        this.denormalisedPermissionStateManager = denormalisedPermissionStateManager;
        this.systemInformationService = systemInformationService;
        this.aoInlineTasksWithFastPermissionsDaoRef = new LazyReference<Optional<AOInlineTasksWithFastPermissionsDao>>(){

            protected Optional<AOInlineTasksWithFastPermissionsDao> create() {
                return AOInlineTasksWithFastPermissionsBeanSelector.this.buildDatabaseSpecificBean();
            }
        };
    }

    public Optional<AOInlineTasksWithFastPermissionsDao> getDatabaseSpecificDao() {
        return (Optional)this.aoInlineTasksWithFastPermissionsDaoRef.get();
    }

    public boolean isDatabaseSupported() {
        return ((Optional)this.aoInlineTasksWithFastPermissionsDaoRef.get()).isPresent();
    }

    public DatabaseInfo getDatabaseInfo() {
        return this.systemInformationService.getDatabaseInfo();
    }

    private Optional<AOInlineTasksWithFastPermissionsDao> buildDatabaseSpecificBean() {
        DatabaseInfo databaseInfo = this.systemInformationService.getDatabaseInfo();
        String dialect = databaseInfo.getDialect();
        if (StringUtils.isEmpty((CharSequence)dialect)) {
            log.warn("Dialect is empty, fast permissions will be disabled in Task Report Macro.");
            return Optional.empty();
        }
        log.debug("Database dialect: {}, version: {}", (Object)databaseInfo.getDialect(), (Object)databaseInfo.getVersion());
        if (dialect.contains("Postgre")) {
            log.debug("Postgres dialect detected: {}", (Object)dialect);
            return Optional.of(new AOPostgresInlineTasksWithFastPermissionsDao(this.ao, this.bulkPermissionService, this.darkFeaturesManager, this.denormalisedPermissionStateManager));
        }
        if (dialect.contains("Oracle")) {
            log.debug("Oracle dialect detected: {}", (Object)dialect);
            return Optional.of(new AOOracleInlineTasksWithFastPermissionsDao(this.ao, this.bulkPermissionService, this.darkFeaturesManager, this.denormalisedPermissionStateManager));
        }
        if (dialect.contains("SQLServer")) {
            log.debug("MS SQL dialect detected: {}", (Object)dialect);
            return Optional.of(new AOSqlServerInlineTasksWithFastPermissionsDao(this.ao, this.bulkPermissionService, this.darkFeaturesManager, this.denormalisedPermissionStateManager));
        }
        if (dialect.contains("MySQL")) {
            log.debug("MySQL dialect detected: {}", (Object)dialect);
            try {
                Integer majorVersionNumber = this.extractMajorVersionNumber(databaseInfo.getVersion());
                if (majorVersionNumber < 8) {
                    log.info("Task Report Macro can work faster with MySQL 8+. Current MySQL DB is {}. Consider upgrading to MySQL 8+ to improve Task Report Macro performance.", (Object)databaseInfo.getVersion());
                    return Optional.empty();
                }
            }
            catch (IllegalArgumentException e) {
                log.warn("Unable to detect MySQL version. Fast permissions functionality will be disabled", (Throwable)e);
                return Optional.empty();
            }
            return Optional.of(new AOMySQL8InlineTasksWithFastPermissionsDao(this.ao, this.bulkPermissionService, this.darkFeaturesManager, this.denormalisedPermissionStateManager));
        }
        log.debug("Undefined dialect detected: {}", (Object)dialect);
        return Optional.empty();
    }

    private Integer extractMajorVersionNumber(String version) {
        if (StringUtils.isEmpty((CharSequence)version)) {
            throw new IllegalArgumentException("DB version is empty");
        }
        int pos = version.indexOf(".");
        if (pos < 0) {
            throw new IllegalArgumentException("Unable to extract the major version from the DB version: '" + version + "'");
        }
        String majorVersion = version.substring(0, pos);
        try {
            return Integer.parseInt(majorVersion);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(e);
        }
    }
}

