/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.config.db.HibernateConfig
 *  org.apache.commons.lang3.time.StopWatch
 *  org.hibernate.SessionFactory
 *  org.hibernate.dialect.Dialect
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.interceptor.DefaultTransactionAttribute
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.core.persistence.schema.api.SchemaInformationService;
import com.atlassian.confluence.impl.hibernate.DataAccessUtils;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.domain.DenormalisedServiceStateRecord;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.DenormalisedPermissionsDdlOperations;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.H2DenormalisedPermissionsDdlOperations;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.HsqlDenormalisedPermissionsDdlOperations;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.MySqlDenormalisedPermissionsDdlOperations;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.OracleDenormalisedPermissionsDdlOperations;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.PostgresDenormalisedPermissionsDdlOperations;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.SqlServerDenormalisedPermissionsDdlOperations;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.domain.SpacePermissionType;
import com.atlassian.confluence.upgrade.ddl.CreateIndexCommand;
import com.atlassian.confluence.upgrade.ddl.DdlExecutor;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.time.StopWatch;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.TransactionTemplate;

public class DenormalisedPermissionsDdlExecutor {
    private static final Logger log = LoggerFactory.getLogger(DenormalisedPermissionsDdlExecutor.class);
    public static final String CONTENT_SID_INDEX_NAME = "DENORMALISED_CONTENT_VIEW_PERMISSIONS".toLowerCase() + "_denorm_content_sid_idx";
    public static final String DENORMALISED_SID_NAME_TYPE_INDEX_NAME = "denormalised_sid_name_type_uniq_idx";
    private final SessionFactory sessionFactory;
    private final PlatformTransactionManager txManager;
    private final SchemaInformationService schemaInformationService;

    public DenormalisedPermissionsDdlExecutor(SessionFactory sessionFactory, PlatformTransactionManager txManager, SchemaInformationService schemaInformationService) {
        this.sessionFactory = sessionFactory;
        this.txManager = txManager;
        this.schemaInformationService = schemaInformationService;
    }

    public void createSpaceDatabaseObjects(boolean enableService) {
        StopWatch globalStopWatch = StopWatch.createStarted();
        DenormalisedPermissionsDdlOperations ddlOperations = this.createDdlOperationsForCurrentDatabase();
        DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(0);
        new TransactionTemplate(this.txManager, (TransactionDefinition)transactionDefinition).execute(status -> {
            JdbcTemplate jdbcTemplate = DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession());
            this.createSpaceTriggersAndFunctions(ddlOperations, jdbcTemplate, enableService);
            this.createSpacePermissionTriggersAndFunctions(ddlOperations, jdbcTemplate, enableService);
            return null;
        });
        log.info("All space triggers and functions were create successfully in {} ms", (Object)globalStopWatch.getTime());
    }

    public void createContentDatabaseObjects(boolean enableService) {
        StopWatch globalStopWatch = StopWatch.createStarted();
        DenormalisedPermissionsDdlOperations ddlOperations = this.createDdlOperationsForCurrentDatabase();
        DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(0);
        new TransactionTemplate(this.txManager, (TransactionDefinition)transactionDefinition).execute(status -> {
            JdbcTemplate jdbcTemplate = DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession());
            this.createContentTriggersAndFunctions(ddlOperations, jdbcTemplate, enableService);
            this.createContentPermissionTriggersAndFunctions(ddlOperations, jdbcTemplate, enableService);
            this.createContentPermissionSetTriggersAndFunctions(ddlOperations, jdbcTemplate, enableService);
            return null;
        });
        log.info("All content triggers and functions were create successfully in {} ms", (Object)globalStopWatch.getTime());
    }

    public void deactivateSpaceTriggers() {
        DenormalisedPermissionsDdlOperations ddlOperations = this.createDdlOperationsForCurrentDatabase();
        DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(0);
        new TransactionTemplate(this.txManager, (TransactionDefinition)transactionDefinition).execute(status -> {
            JdbcTemplate jdbcTemplate = DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession());
            this.disableSpaceTriggers(ddlOperations, jdbcTemplate);
            this.disableSpacePermissionTriggers(ddlOperations, jdbcTemplate);
            return null;
        });
    }

    public void deactivateContentTriggers() {
        DenormalisedPermissionsDdlOperations ddlOperations = this.createDdlOperationsForCurrentDatabase();
        DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(0);
        new TransactionTemplate(this.txManager, (TransactionDefinition)transactionDefinition).execute(status -> {
            JdbcTemplate jdbcTemplate = DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession());
            this.disableContentTriggers(ddlOperations, jdbcTemplate);
            this.disableContentPermissionTriggers(ddlOperations, jdbcTemplate);
            this.disableContentPermissionSetTriggers(ddlOperations, jdbcTemplate);
            return null;
        });
    }

    public void activateSpaceTriggers() {
        DenormalisedPermissionsDdlOperations ddlOperations = this.createDdlOperationsForCurrentDatabase();
        DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(0);
        new TransactionTemplate(this.txManager, (TransactionDefinition)transactionDefinition).execute(status -> {
            JdbcTemplate jdbcTemplate = DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession());
            this.enableSpaceTriggers(ddlOperations, jdbcTemplate);
            this.enableSpacePermissionTriggers(ddlOperations, jdbcTemplate);
            return null;
        });
    }

    public void activateContentTriggers() {
        DenormalisedPermissionsDdlOperations ddlOperations = this.createDdlOperationsForCurrentDatabase();
        DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(0);
        new TransactionTemplate(this.txManager, (TransactionDefinition)transactionDefinition).execute(status -> {
            JdbcTemplate jdbcTemplate = DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession());
            this.enableContentTriggers(ddlOperations, jdbcTemplate);
            this.enableContentPermissionTriggers(ddlOperations, jdbcTemplate);
            this.enableContentPermissionSetTriggers(ddlOperations, jdbcTemplate);
            return null;
        });
    }

    public void dropSpaceDatabaseObjects() {
        DenormalisedPermissionsDdlOperations ddlOperations = this.createDdlOperationsForCurrentDatabase();
        DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(0);
        new TransactionTemplate(this.txManager, (TransactionDefinition)transactionDefinition).execute(status -> {
            JdbcTemplate jdbcTemplate = DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession());
            this.dropSpacePermissionTriggersAndFunctions(ddlOperations, jdbcTemplate);
            this.dropSpaceTriggersAndFunctions(ddlOperations, jdbcTemplate);
            return null;
        });
    }

    public void dropContentDatabaseObjects() {
        DenormalisedPermissionsDdlOperations ddlOperations = this.createDdlOperationsForCurrentDatabase();
        DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(0);
        new TransactionTemplate(this.txManager, (TransactionDefinition)transactionDefinition).execute(status -> {
            JdbcTemplate jdbcTemplate = DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession());
            this.dropContentPermissionSetTriggersAndFunctions(ddlOperations, jdbcTemplate);
            this.dropContentPermissionTriggersAndFunctions(ddlOperations, jdbcTemplate);
            this.dropContentTriggersAndFunctions(ddlOperations, jdbcTemplate);
            return null;
        });
    }

    public List<CreateIndexCommand> getAdditionalIndexes(DdlExecutor ddlExecutor) {
        ArrayList<CreateIndexCommand> allIndexes = new ArrayList<CreateIndexCommand>();
        allIndexes.addAll(this.getAdditionalSpaceIndexes(ddlExecutor));
        allIndexes.addAll(this.getAdditionalContentIndexes(ddlExecutor));
        return allIndexes;
    }

    public List<CreateIndexCommand> getAdditionalSpaceIndexes(DdlExecutor ddlExecutor) {
        ArrayList<CreateIndexCommand> createIndexCommandList = new ArrayList<CreateIndexCommand>();
        createIndexCommandList.add(ddlExecutor.createCreateIndexCommand(DENORMALISED_SID_NAME_TYPE_INDEX_NAME, "DENORMALISED_SID", true, "NAME", "TYPE"));
        for (SpacePermissionType type : SpacePermissionType.values()) {
            createIndexCommandList.add(ddlExecutor.createCreateIndexCommand(this.getDenormalisedSpacePermissionsIndexName(type), type.getTableName(), false, "SPACE_ID", "SID_ID"));
        }
        return createIndexCommandList;
    }

    @VisibleForTesting
    public String getDenormalisedSpacePermissionsIndexName(SpacePermissionType spacePermissionType) {
        return spacePermissionType.getTableName().toLowerCase() + "_denorm_space_sid_idx";
    }

    public List<CreateIndexCommand> getAdditionalContentIndexes(DdlExecutor ddlExecutor) {
        ArrayList<CreateIndexCommand> createIndexCommandList = new ArrayList<CreateIndexCommand>();
        createIndexCommandList.add(ddlExecutor.createCreateIndexCommand(CONTENT_SID_INDEX_NAME, "DENORMALISED_CONTENT_VIEW_PERMISSIONS", false, "CONTENT_ID", "SID_ID"));
        return createIndexCommandList;
    }

    public boolean indexExist(DenormalisedServiceStateRecord.ServiceType serviceType, String indexName, String tableName) {
        DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(0);
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.txManager, (TransactionDefinition)transactionDefinition);
        DenormalisedPermissionsDdlOperations ddlOperations = this.createDdlOperationsForCurrentDatabase();
        Boolean indexExist = (Boolean)transactionTemplate.execute(status -> {
            JdbcTemplate jdbcTemplate = DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession());
            return ddlOperations.indexExist(jdbcTemplate, serviceType, indexName, tableName);
        });
        return indexExist != null && indexExist != false;
    }

    public void dropAdditionalContentIndexes() {
        DenormalisedPermissionsDdlOperations ddlOperations = this.createDdlOperationsForCurrentDatabase();
        DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(0);
        new TransactionTemplate(this.txManager, (TransactionDefinition)transactionDefinition).execute(status -> {
            JdbcTemplate jdbcTemplate = DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession());
            this.dropAdditionalContentIndexes(ddlOperations, jdbcTemplate);
            return null;
        });
    }

    public void dropAdditionalSpaceIndexes() {
        DenormalisedPermissionsDdlOperations ddlOperations = this.createDdlOperationsForCurrentDatabase();
        DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(0);
        new TransactionTemplate(this.txManager, (TransactionDefinition)transactionDefinition).execute(status -> {
            JdbcTemplate jdbcTemplate = DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession());
            this.dropAdditionalSpaceIndexes(ddlOperations, jdbcTemplate);
            return null;
        });
    }

    private void createSpaceTriggersAndFunctions(DenormalisedPermissionsDdlOperations ddlOperations, JdbcTemplate jdbcTemplate, boolean enableService) {
        StopWatch stopWatch = StopWatch.createStarted();
        ddlOperations.createSpaceTriggersAndFunctions(jdbcTemplate);
        if (enableService) {
            ddlOperations.enableSpaceServiceTriggers(jdbcTemplate);
        }
        log.debug("Space triggers and functions were created successfully in {} ms", (Object)stopWatch.getTime());
    }

    private void createSpacePermissionTriggersAndFunctions(DenormalisedPermissionsDdlOperations ddlOperations, JdbcTemplate jdbcTemplate, boolean enableService) {
        StopWatch stopWatch = StopWatch.createStarted();
        ddlOperations.createSpacePermissionTriggersAndFunctions(jdbcTemplate);
        if (enableService) {
            ddlOperations.enableSpacePermissionServiceTriggers(jdbcTemplate);
        }
        log.debug("Space permission triggers and functions were created successfully in {} ms", (Object)stopWatch.getTime());
    }

    private void createContentTriggersAndFunctions(DenormalisedPermissionsDdlOperations ddlOperations, JdbcTemplate jdbcTemplate, boolean enableService) {
        StopWatch stopWatch = StopWatch.createStarted();
        ddlOperations.createContentTriggersAndFunctions(jdbcTemplate);
        if (enableService) {
            ddlOperations.enableContentServiceTriggers(jdbcTemplate);
        }
        log.debug("Content triggers and functions were created successfully in {} ms", (Object)stopWatch.getTime());
    }

    private void createContentPermissionTriggersAndFunctions(DenormalisedPermissionsDdlOperations ddlOperations, JdbcTemplate jdbcTemplate, boolean enableService) {
        StopWatch stopWatch = StopWatch.createStarted();
        ddlOperations.createContentPermissionTriggersAndFunctions(jdbcTemplate);
        if (enableService) {
            ddlOperations.enableContentPermissionServiceTriggers(jdbcTemplate);
        }
        log.debug("Content permission triggers and functions were created successfully in {} ms", (Object)stopWatch.getTime());
    }

    private void createContentPermissionSetTriggersAndFunctions(DenormalisedPermissionsDdlOperations ddlOperations, JdbcTemplate jdbcTemplate, boolean enableService) {
        StopWatch stopWatch = StopWatch.createStarted();
        ddlOperations.createContentPermissionSetTriggersAndFunctions(jdbcTemplate);
        if (enableService) {
            ddlOperations.enableContentPermissionSetServiceTriggers(jdbcTemplate);
        }
        log.debug("Content permission set triggers and functions were created successfully in {} ms", (Object)stopWatch.getTime());
    }

    private void dropSpaceTriggersAndFunctions(DenormalisedPermissionsDdlOperations ddlOperations, JdbcTemplate jdbcTemplate) {
        StopWatch stopWatch = StopWatch.createStarted();
        ddlOperations.dropAllSpaceTriggersAndFunctions(jdbcTemplate);
        log.debug("Space triggers and functions were dropped successfully in {} ms", (Object)stopWatch.getTime());
    }

    private void dropSpacePermissionTriggersAndFunctions(DenormalisedPermissionsDdlOperations ddlOperations, JdbcTemplate jdbcTemplate) {
        StopWatch stopWatch = StopWatch.createStarted();
        ddlOperations.dropAllSpacePermissionTriggersAndFunctions(jdbcTemplate);
        log.debug("Space permission triggers and functions were dropped successfully in {} ms", (Object)stopWatch.getTime());
    }

    private void dropContentTriggersAndFunctions(DenormalisedPermissionsDdlOperations ddlOperations, JdbcTemplate jdbcTemplate) {
        StopWatch stopWatch = StopWatch.createStarted();
        ddlOperations.dropAllContentTriggersAndFunctions(jdbcTemplate);
        log.debug("Content triggers and functions were dropped successfully in {} ms", (Object)stopWatch.getTime());
    }

    private void dropContentPermissionTriggersAndFunctions(DenormalisedPermissionsDdlOperations ddlOperations, JdbcTemplate jdbcTemplate) {
        StopWatch stopWatch = StopWatch.createStarted();
        ddlOperations.dropAllContentPermissionTriggersAndFunctions(jdbcTemplate);
        log.debug("Content permission triggers and functions were dropped successfully in {} ms", (Object)stopWatch.getTime());
    }

    private void dropContentPermissionSetTriggersAndFunctions(DenormalisedPermissionsDdlOperations ddlOperations, JdbcTemplate jdbcTemplate) {
        StopWatch stopWatch = StopWatch.createStarted();
        ddlOperations.dropAllContentPermissionSetTriggersAndFunctions(jdbcTemplate);
        log.debug("Content permission set triggers and functions were dropped successfully in {} ms", (Object)stopWatch.getTime());
    }

    private void enableSpaceTriggers(DenormalisedPermissionsDdlOperations ddlOperations, JdbcTemplate jdbcTemplate) {
        StopWatch stopWatch = StopWatch.createStarted();
        ddlOperations.enableSpaceServiceTriggers(jdbcTemplate);
        log.debug("Space triggers were enabled successfully in {} ms", (Object)stopWatch.getTime());
    }

    private void enableSpacePermissionTriggers(DenormalisedPermissionsDdlOperations ddlOperations, JdbcTemplate jdbcTemplate) {
        StopWatch stopWatch = StopWatch.createStarted();
        ddlOperations.enableSpacePermissionServiceTriggers(jdbcTemplate);
        log.debug("Space permission triggers were enabled successfully in {} ms", (Object)stopWatch.getTime());
    }

    private void enableContentTriggers(DenormalisedPermissionsDdlOperations ddlOperations, JdbcTemplate jdbcTemplate) {
        StopWatch stopWatch = StopWatch.createStarted();
        ddlOperations.enableContentServiceTriggers(jdbcTemplate);
        log.debug("Content triggers were enabled successfully in {} ms", (Object)stopWatch.getTime());
    }

    private void enableContentPermissionTriggers(DenormalisedPermissionsDdlOperations ddlOperations, JdbcTemplate jdbcTemplate) {
        StopWatch stopWatch = StopWatch.createStarted();
        ddlOperations.enableContentPermissionServiceTriggers(jdbcTemplate);
        log.debug("Content permission triggers were enabled successfully in {} ms", (Object)stopWatch.getTime());
    }

    private void enableContentPermissionSetTriggers(DenormalisedPermissionsDdlOperations ddlOperations, JdbcTemplate jdbcTemplate) {
        StopWatch stopWatch = StopWatch.createStarted();
        ddlOperations.enableContentPermissionSetServiceTriggers(jdbcTemplate);
        log.debug("Content permission set triggers were enabled successfully in {} ms", (Object)stopWatch.getTime());
    }

    private void disableSpaceTriggers(DenormalisedPermissionsDdlOperations ddlOperations, JdbcTemplate jdbcTemplate) {
        StopWatch stopWatch = StopWatch.createStarted();
        ddlOperations.disableSpaceServiceTriggers(jdbcTemplate);
        log.debug("Space triggers were disabled successfully in {} ms", (Object)stopWatch.getTime());
    }

    private void disableSpacePermissionTriggers(DenormalisedPermissionsDdlOperations ddlOperations, JdbcTemplate jdbcTemplate) {
        StopWatch stopWatch = StopWatch.createStarted();
        ddlOperations.disableSpacePermissionServiceTriggers(jdbcTemplate);
        log.debug("Space permission triggers were disabled successfully in {} ms", (Object)stopWatch.getTime());
    }

    private void disableContentTriggers(DenormalisedPermissionsDdlOperations ddlOperations, JdbcTemplate jdbcTemplate) {
        StopWatch stopWatch = StopWatch.createStarted();
        ddlOperations.disableContentServiceTriggers(jdbcTemplate);
        log.debug("Content triggers were disabled successfully in {} ms", (Object)stopWatch.getTime());
    }

    private void disableContentPermissionTriggers(DenormalisedPermissionsDdlOperations ddlOperations, JdbcTemplate jdbcTemplate) {
        StopWatch stopWatch = StopWatch.createStarted();
        ddlOperations.disableContentPermissionServiceTriggers(jdbcTemplate);
        log.debug("Content permission triggers were disabled successfully in {} ms", (Object)stopWatch.getTime());
    }

    private void disableContentPermissionSetTriggers(DenormalisedPermissionsDdlOperations ddlOperations, JdbcTemplate jdbcTemplate) {
        StopWatch stopWatch = StopWatch.createStarted();
        ddlOperations.disableContentPermissionSetServiceTriggers(jdbcTemplate);
        log.debug("Content permission set triggers were disabled successfully in {} ms", (Object)stopWatch.getTime());
    }

    private void dropAdditionalContentIndexes(DenormalisedPermissionsDdlOperations ddlOperations, JdbcTemplate jdbcTemplate) {
        StopWatch stopWatch = StopWatch.createStarted();
        ddlOperations.dropIndex(jdbcTemplate, DenormalisedServiceStateRecord.ServiceType.CONTENT, CONTENT_SID_INDEX_NAME, "DENORMALISED_CONTENT_VIEW_PERMISSIONS");
        log.debug("Content indexes were dropped successfully in {} ms", (Object)stopWatch.getTime());
    }

    private void dropAdditionalSpaceIndexes(DenormalisedPermissionsDdlOperations ddlOperations, JdbcTemplate jdbcTemplate) {
        StopWatch stopWatch = StopWatch.createStarted();
        ddlOperations.dropIndex(jdbcTemplate, DenormalisedServiceStateRecord.ServiceType.SPACE, DENORMALISED_SID_NAME_TYPE_INDEX_NAME, "DENORMALISED_SID");
        for (SpacePermissionType type : SpacePermissionType.values()) {
            ddlOperations.dropIndex(jdbcTemplate, DenormalisedServiceStateRecord.ServiceType.SPACE, this.getDenormalisedSpacePermissionsIndexName(type), type.getTableName());
        }
        log.debug("Space indexes were dropped successfully in {} ms", (Object)stopWatch.getTime());
    }

    private DenormalisedPermissionsDdlOperations createDdlOperationsForCurrentDatabase() {
        Dialect dialect = this.schemaInformationService.getDialect();
        String dialectName = dialect.toString();
        if (HibernateConfig.isHsqlDialect((String)dialectName)) {
            return new HsqlDenormalisedPermissionsDdlOperations();
        }
        if (HibernateConfig.isH2Dialect((String)dialectName)) {
            return new H2DenormalisedPermissionsDdlOperations();
        }
        if (HibernateConfig.isPostgreSqlDialect((String)dialectName)) {
            return new PostgresDenormalisedPermissionsDdlOperations();
        }
        if (HibernateConfig.isSqlServerDialect((String)dialectName)) {
            return new SqlServerDenormalisedPermissionsDdlOperations();
        }
        if (HibernateConfig.isMySqlDialect((String)dialectName)) {
            return new MySqlDenormalisedPermissionsDdlOperations();
        }
        if (HibernateConfig.isOracleDialect((String)dialectName)) {
            return new OracleDenormalisedPermissionsDdlOperations();
        }
        throw new IllegalStateException("Current database is not supported by denormalised permissions: " + dialectName);
    }
}

