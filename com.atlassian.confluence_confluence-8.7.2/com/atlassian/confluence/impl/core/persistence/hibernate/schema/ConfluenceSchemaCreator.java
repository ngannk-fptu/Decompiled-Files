/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  javax.persistence.PersistenceException
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.SessionFactory
 *  org.hibernate.boot.Metadata
 *  org.hibernate.tool.hbm2ddl.SchemaExport
 *  org.hibernate.tool.schema.TargetType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.interceptor.DefaultTransactionAttribute
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.impl.core.persistence.hibernate.schema;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.core.persistence.hibernate.HibernateDatabaseCapabilities;
import com.atlassian.confluence.impl.core.persistence.hibernate.HibernateMetadataSource;
import com.atlassian.confluence.impl.core.persistence.hibernate.schema.ContentPermissionSchemaHelper;
import com.atlassian.confluence.impl.core.persistence.hibernate.schema.RelationConstraintsSchemaHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.DenormalisedPermissionsDdlExecutor;
import com.atlassian.confluence.upgrade.ddl.AlterColumnNullabilityCommand;
import com.atlassian.confluence.upgrade.ddl.AlterTableExecutor;
import com.atlassian.confluence.upgrade.ddl.CreateIndexCommand;
import com.atlassian.confluence.upgrade.ddl.DdlExecutor;
import com.atlassian.confluence.upgrade.ddl.DropTableCommand;
import com.atlassian.confluence.upgrade.ddl.HibernateAlterTableExecutor;
import com.atlassian.confluence.upgrade.ddl.NullChoice;
import com.atlassian.confluence.upgrade.upgradetask.BandanaKeyUniqueConstraintUpgradeTask;
import com.atlassian.confluence.upgrade.upgradetask.DataAccessUtils;
import com.atlassian.confluence.upgrade.upgradetask.EmbeddedCrowdSchemaUpgradeTask;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.TransactionTemplate;

@Internal
public final class ConfluenceSchemaCreator {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceSchemaCreator.class);
    private final SessionFactory sessionFactory;
    private final PlatformTransactionManager txManager;
    private final HibernateMetadataSource metadataSource;
    private final DdlExecutor ddlExecutor;
    private final HibernateDatabaseCapabilities databaseCapabilities;
    private final DenormalisedPermissionsDdlExecutor denormalisedPermissionsDdlExecutor;
    private static final CreateIndexCommand CREATE_INDEX_COMMAND = new CreateIndexCommand("c_si_ct_pv_cs_cd_idx", "CONTENT", "SPACEID", "CONTENTTYPE", "PREVVER", "CONTENT_STATUS", "CREATIONDATE");
    private static final String USER_MAPPING_TABLE = "user_mapping";
    private static final String LOWER_USERNAME_COLUMN = "lower_username";

    public ConfluenceSchemaCreator(SessionFactory sessionFactory, PlatformTransactionManager txManager, HibernateMetadataSource metadataSource, DdlExecutor ddlExecutor, HibernateDatabaseCapabilities databaseCapabilities, DenormalisedPermissionsDdlExecutor denormalisedPermissionsDdlExecutor) {
        this.sessionFactory = sessionFactory;
        this.txManager = txManager;
        this.metadataSource = metadataSource;
        this.ddlExecutor = ddlExecutor;
        this.databaseCapabilities = databaseCapabilities;
        this.denormalisedPermissionsDdlExecutor = denormalisedPermissionsDdlExecutor;
    }

    public void createSchema(boolean fromXmlImport) {
        try {
            SchemaExport schemaExport = new SchemaExport();
            log.info("Dropping existing database schema");
            this.dropOldTables();
            Metadata metadata = this.metadataSource.getMetadata();
            schemaExport.drop(ConfluenceSchemaCreator.getTargetTypes(), metadata);
            log.info("Creating new database schema");
            schemaExport.createOnly(ConfluenceSchemaCreator.getTargetTypes(), metadata);
            log.info("Basic schema creation complete, creating additional constraints and indexes.");
            this.createAdditionalInitialDatabaseConstraints();
            if (!fromXmlImport) {
                this.createAdditionalDatabaseConstraints();
            }
            this.createAdditionalDatabaseIndexes();
            this.createAdditionalDenormalisedDatabaseObjects();
            log.info("Database schema successfully recreated");
        }
        catch (PersistenceException e) {
            log.error("Error while creating database schema", (Throwable)e);
        }
    }

    private void createAdditionalDenormalisedDatabaseObjects() {
        this.denormalisedPermissionsDdlExecutor.createSpaceDatabaseObjects(false);
        this.denormalisedPermissionsDdlExecutor.createContentDatabaseObjects(false);
        this.executeAdditionalSql(this.denormalisedPermissionsDdlExecutor.getAdditionalIndexes(this.ddlExecutor).stream().map(CreateIndexCommand::getStatement).collect(Collectors.toList()));
    }

    private static EnumSet<TargetType> getTargetTypes() {
        EnumSet<TargetType> targetTypes = EnumSet.of(TargetType.DATABASE);
        if (log.isDebugEnabled()) {
            targetTypes.add(TargetType.STDOUT);
        }
        return targetTypes;
    }

    private void dropOldTables() {
        String contentLockTable = "CONTENTLOCK";
        if (DataAccessUtils.isTablePresent("CONTENTLOCK", this.txManager, this.sessionFactory)) {
            this.executeAdditionalSql(Collections.singletonList(new DropTableCommand("CONTENTLOCK").getStatement()));
        }
    }

    private void createAdditionalInitialDatabaseConstraints() {
        ArrayList<String> createConstraintsSql = new ArrayList<String>();
        createConstraintsSql.add(this.getUniqueUsernameDdlStatement());
        createConstraintsSql.addAll(this.getNotNullConstraintStatements());
        this.executeAdditionalSql(createConstraintsSql);
    }

    private void createAdditionalDatabaseIndexes() {
        ArrayList<String> createIndexSql = new ArrayList<String>();
        createIndexSql.add("create index ospe_entityid_idx on OS_PROPERTYENTRY (entity_id)");
        createIndexSql.add(CREATE_INDEX_COMMAND.getStatement());
        List eventsIndexes = this.additionalSynchronyEventsIndexes().stream().map(CreateIndexCommand::getStatement).collect(Collectors.toList());
        createIndexSql.addAll(eventsIndexes);
        List snapshotsIndexes = this.additionalSynchronySnapshotsIndexes().stream().map(CreateIndexCommand::getStatement).collect(Collectors.toList());
        createIndexSql.addAll(snapshotsIndexes);
        this.executeAdditionalSql(createIndexSql);
    }

    private List<CreateIndexCommand> additionalSynchronyEventsIndexes() {
        ArrayList<CreateIndexCommand> commands = new ArrayList<CreateIndexCommand>();
        if (!this.isOracle()) {
            commands.add(this.ddlExecutor.createCreateIndexCommand("e_h_r_idx", this.quote("EVENTS"), true, this.quote("history"), this.quote("rev")));
        }
        commands.add(this.ddlExecutor.createCreateIndexCommand("e_h_p_s_idx", this.quote("EVENTS"), true, this.quote("history"), this.quote("partition"), this.quote("sequence")));
        commands.add(this.ddlExecutor.createCreateIndexCommand("e_c_i_idx", this.quote("EVENTS"), false, this.quote("contentid"), this.quote("inserted")));
        commands.add(this.ddlExecutor.createCreateIndexCommand("e_i_c_idx", this.quote("EVENTS"), false, this.quote("inserted"), this.quote("contentid")));
        return commands;
    }

    private List<CreateIndexCommand> additionalSynchronySnapshotsIndexes() {
        return Arrays.asList(this.ddlExecutor.createCreateIndexCommand("s_c_i_idx", this.quote("SNAPSHOTS"), false, this.quote("contentid"), this.quote("inserted")), this.ddlExecutor.createCreateIndexCommand("s_i_c_idx", this.quote("SNAPSHOTS"), false, this.quote("inserted"), this.quote("contentid")));
    }

    private void executeAdditionalSql(List<String> createIndexSql) {
        for (String sql : createIndexSql) {
            try {
                DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(3);
                new TransactionTemplate(this.txManager, (TransactionDefinition)transactionDefinition).execute(status -> {
                    log.debug(sql);
                    JdbcTemplate template = com.atlassian.confluence.impl.hibernate.DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession());
                    template.execute(sql);
                    return null;
                });
            }
            catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.warn("Error executing SQL during schema changes: " + sql, (Throwable)e);
                    continue;
                }
                log.warn("Error executing SQL during schema changes: {}, {}", (Object)sql, (Object)e.getMessage());
            }
        }
    }

    public void createAdditionalDatabaseConstraints() {
        ArrayList<String> createConstraintsSql = new ArrayList<String>();
        createConstraintsSql.addAll(EmbeddedCrowdSchemaUpgradeTask.getUniqueConstraintSqlStatements());
        createConstraintsSql.addAll(ContentPermissionSchemaHelper.getContentPermissionUniqueConstraintSqlStatements(this.alterTableExecutor()));
        createConstraintsSql.addAll(BandanaKeyUniqueConstraintUpgradeTask.getUniqueConstraintSqlStatements(this.alterTableExecutor()));
        createConstraintsSql.addAll(RelationConstraintsSchemaHelper.getRelationUniqueConstraintSqlStatements(this.alterTableExecutor()));
        this.executeAdditionalSql(createConstraintsSql);
    }

    private String quote(String name) {
        if (this.isMySql()) {
            return StringUtils.wrap((String)name, (String)"`");
        }
        return StringUtils.wrap((String)name, (String)"\"");
    }

    private boolean isOracle() {
        return this.databaseCapabilities.isOracle();
    }

    private boolean isMySql() {
        return this.databaseCapabilities.isMySql();
    }

    private String getUniqueUsernameDdlStatement() {
        return this.ddlExecutor.createUniqueConstraintWithMultipleNullsCommand("unq_lwr_username", USER_MAPPING_TABLE, LOWER_USERNAME_COLUMN).getStatement();
    }

    private List<String> getNotNullConstraintStatements() {
        AlterTableExecutor ddlExecutor = this.alterTableExecutor();
        HibernateDatabaseCapabilities hibernateConfig = this.databaseCapabilities;
        String varcharType = hibernateConfig.isOracle() ? "nvarchar2(255)" : "varchar(255)";
        ArrayList<String> statements = new ArrayList<String>();
        AlterColumnNullabilityCommand spacesSpaceKeyNotNull = ddlExecutor.createAlterColumnNullChoiceCommand("LOWERSPACEKEY", varcharType, NullChoice.NOT_NULL);
        statements.addAll(ddlExecutor.getAlterTableStatements("SPACES", Collections.singletonList(spacesSpaceKeyNotNull)));
        return Collections.unmodifiableList(statements);
    }

    private AlterTableExecutor alterTableExecutor() {
        return new HibernateAlterTableExecutor(this.databaseCapabilities, this.ddlExecutor);
    }
}

