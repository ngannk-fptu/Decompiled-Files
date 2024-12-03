/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  com.google.common.collect.ImmutableList
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.NativeQuery
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.DataAccessException
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.core.persistence.hibernate.HibernateDatabaseCapabilities;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.atlassian.confluence.upgrade.ddl.AlterTableCommand;
import com.atlassian.confluence.upgrade.ddl.AlterTableExecutor;
import com.atlassian.confluence.upgrade.ddl.DropForeignKeyConstraintCommand;
import com.atlassian.confluence.upgrade.ddl.DropUniqueConstraintByColumnsCommand;
import com.atlassian.confluence.upgrade.ddl.DropUniqueConstraintCommand;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

public class Hibernate5ConstraintNamesPreSchemaUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(Hibernate5ConstraintNamesPreSchemaUpgradeTask.class);
    private static final String[] DROP_FK_CONSTRAINTS = new String[]{"ATTACHMENTDATA", "FK9DC3E34D34A4917E", "BODYCONTENT", "FKA898D4778DD41734", "CONFANCESTORS", "FK9494E23C37E35A2E", "CONFANCESTORS", "FK9494E23CC45E94DC", "CONTENT", "FK6382C05917D4A070", "CONTENT", "FK6382C05974B18345", "CONTENT", "FK6382C0598C38FBEA", "CONTENT", "FK6382C059B2DC6081", "CONTENT", "FK6382C059B97E9230", "CONTENT", "FK6382C059E5B1125", "CONTENT_LABEL", "FKF0E7436E27072AEF", "CONTENT_LABEL", "FKF0E7436E8DD41734", "CONTENT_LABEL", "FKF0E7436ED32042E4", "CONTENT_PERM", "FKBD74B31676E33274", "CONTENT_PERM_SET", "FKBF45A7992CAF22C1", "CONTENT_RELATION", "FK841CAF22351D64C3", "CONTENT_RELATION", "FK841CAF22DB772979", "CONTENTPROPERTIES", "FK984C5E4C8DD41734", "cwd_app_dir_mapping", "FK52050E2FB347AA6A", "cwd_user_credential_record", "FK76F874F73AEE0F", "external_members", "FKD8C8D8A5117D5FDA", "external_members", "FKD8C8D8A5F25E5D5F", "EXTRNLNKS", "FK97C10FE78DD41734", "IMAGEDETAILS", "FKA768048734A4917E", "LIKES", "FK4514B9C8DD41734", "LINKS", "FK45157998DD41734", "local_members", "FK6B8FB445117D5FDA", "local_members", "FK6B8FB445CE2B3226", "NOTIFICATIONS", "FK594ACC827072AEF", "NOTIFICATIONS", "FK594ACC8B2DC6081", "os_user_group", "FK932472461E2E76DB", "os_user_group", "FK93247246F73AEE0F", "PAGETEMPLATES", "FKBC7CE96A17D4A070", "PAGETEMPLATES", "FKBC7CE96AB2DC6081", "SPACEPERMISSIONS", "FKD33F23BEB2DC6081", "SPACES", "FK9228242D11B7BFEE", "SPACES", "FK9228242D2C72D3D2", "TRACKBACKLINKS", "FKF6977A478DD41734", "TRUSTEDAPP", "FKDDB119CA9C85ADB1", "TRUSTEDAPPRESTRICTION", "FKE8496BA235D1D865", "USERCONTENT_RELATION", "FKECD19CED351D64C3", "logininfo", "FK_logininfo_USERNAME"};
    private static final String[] DROP_UNIQUE_CONSTRAINTS = new String[]{"attachmentdata", "attachmentdata_attachmentid_key", "confversion", "confversion_buildnumber_key", "cwd_application", "cwd_application_lower_application_name_key", "cwd_directory", "cwd_directory_lower_directory_name_key", "groups", "groups_groupname_key", "logininfo", "logininfo_username_key", "os_group", "os_group_groupname_key", "os_user", "os_user_username_key", "plugindata", "plugindata_filename_key", "plugindata", "plugindata_pluginkey_key", "scheduler_clustered_jobs", "scheduler_clustered_jobs_job_id_key", "spaces", "spaces_spacekey_key", "trustedapp", "trustedapp_name_key", "trustedapp", "trustedapp_public_key_id_key", "users", "users_name_key"};
    private static final String[] DROP_UNIQUE_CONSTRAINTS_MYSQL_ORACLE = new String[]{"ATTACHMENTDATA", "ATTACHMENTID", "CONFVERSION", "BUILDNUMBER", "cwd_application", "lower_application_name", "cwd_directory", "lower_directory_name", "groups", "groupname", "logininfo", "USERNAME", "users", "users_name_key", "os_group", "groupname", "os_user", "username", "PLUGINDATA", "FILENAME", "PLUGINDATA", "PLUGINKEY", "scheduler_clustered_jobs", "job_id", "SPACES", "SPACEKEY", "SPACES", "spaces_spacekey_key", "TRUSTEDAPP", "NAME", "TRUSTEDAPP", "PUBLIC_KEY_ID", "users", "name"};
    private static final String[] DROP_UNIQUE_CONSTRAINTS_MSSQL = new String[]{"ATTACHMENTDATA", "UQ__ATTACHME_%", "CONFVERSION", "UQ__CONFVERS_%", "PLUGINDATA", "UQ__PLUGINDA_%", "SPACES", "UQ__SPACES_%", "TRUSTEDAPP", "UQ__TRUSTEDA_%", "cwd_application", "UQ__cwd_appl_%", "cwd_directory", "UQ__cwd_dire_%", "groups", "UQ__groups_%", "logininfo", "UQ__logininf_%", "os_group", "UQ__os_group_%", "os_user", "UQ__os_user_%", "scheduler_clustered_jobs", "UQ__schedule_%", "users", "UQ__users_%"};
    private final HibernateDatabaseCapabilities databaseCapabilities;
    private final AlterTableExecutor alterTableExecutor;
    private final SessionFactory sessionFactory;

    public Hibernate5ConstraintNamesPreSchemaUpgradeTask(HibernateDatabaseCapabilities databaseCapabilities, AlterTableExecutor alterTableExecutor, SessionFactory sessionFactory) {
        this.databaseCapabilities = databaseCapabilities;
        this.alterTableExecutor = alterTableExecutor;
        this.sessionFactory = sessionFactory;
    }

    public String getBuildNumber() {
        return "7106";
    }

    public String getShortDescription() {
        return "Drop the hibernate 2 constraints in preparation for them to be re-created in the hibernate 5 naming style";
    }

    public void doUpgrade() throws Exception {
        log.info("Starting {}", (Object)((Object)((Object)this)).getClass().getSimpleName());
        this.dropFKConstraints();
        if (!this.databaseCapabilities.isH2() && !this.databaseCapabilities.isHSQL()) {
            this.dropUniqueConstraints();
        }
        log.info("Finished {}", (Object)((Object)((Object)this)).getClass().getSimpleName());
    }

    private void dropFKConstraints() {
        for (int i = 0; i < DROP_FK_CONSTRAINTS.length; i += 2) {
            String tableName = DROP_FK_CONSTRAINTS[i];
            String constraintName = DROP_FK_CONSTRAINTS[i + 1];
            DropForeignKeyConstraintCommand command = new DropForeignKeyConstraintCommand(this.databaseCapabilities.isMySql(), constraintName);
            ImmutableList commands = ImmutableList.of((Object)command);
            try {
                this.alterTableExecutor.alterTable(tableName, (List<? extends AlterTableCommand>)commands);
                continue;
            }
            catch (DataAccessException e) {
                log.info("Ignoring non-existence of fk constraint: {}", (Object)constraintName);
            }
        }
    }

    private void dropUniqueConstraints() {
        if (this.databaseCapabilities.isOracle()) {
            this.dropUniqueConstraintsOracle();
            return;
        }
        if (this.databaseCapabilities.isSqlServer()) {
            this.dropUniqueConstraintsMssql();
            return;
        }
        if (this.databaseCapabilities.isMySql()) {
            this.dropUniqueConstraints(DROP_UNIQUE_CONSTRAINTS_MYSQL_ORACLE);
            return;
        }
        this.dropUniqueConstraints(DROP_UNIQUE_CONSTRAINTS);
    }

    private void dropUniqueConstraints(String[] dropUniqueConstraints) {
        for (int i = 0; i < dropUniqueConstraints.length; i += 2) {
            String tableName = dropUniqueConstraints[i];
            String constraintName = dropUniqueConstraints[i + 1];
            DropUniqueConstraintCommand command = this.alterTableExecutor.createDropUniqueConstraintCommand(constraintName);
            ImmutableList commands = ImmutableList.of((Object)command);
            try {
                this.alterTableExecutor.alterTable(tableName, (List<? extends AlterTableCommand>)commands);
                continue;
            }
            catch (DataAccessException e) {
                log.info("Ignoring non-existence of unique constraint: {}", (Object)constraintName);
            }
        }
    }

    private void dropUniqueConstraintsOracle() {
        for (int i = 0; i < DROP_UNIQUE_CONSTRAINTS_MYSQL_ORACLE.length; i += 2) {
            String tableName = DROP_UNIQUE_CONSTRAINTS_MYSQL_ORACLE[i];
            String columnNames = DROP_UNIQUE_CONSTRAINTS_MYSQL_ORACLE[i + 1];
            DropUniqueConstraintByColumnsCommand command = this.alterTableExecutor.createDropUniqueConstraintByColumnsCommand(columnNames.split(","));
            ImmutableList commands = ImmutableList.of((Object)command);
            try {
                this.alterTableExecutor.alterTable(tableName, (List<? extends AlterTableCommand>)commands);
                continue;
            }
            catch (DataAccessException e) {
                log.info("Ignoring non-existence of unique constraint: {}.{}", (Object)tableName, (Object)columnNames);
            }
        }
    }

    private void dropUniqueConstraintsMssql() {
        String[] constraintNames = this.readMssqlDropConstraintNames();
        this.dropUniqueConstraints(constraintNames);
    }

    private String[] readMssqlDropConstraintNames() {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < DROP_UNIQUE_CONSTRAINTS_MSSQL.length; i += 2) {
            String tableName = DROP_UNIQUE_CONSTRAINTS_MSSQL[i];
            String constraintLike = DROP_UNIQUE_CONSTRAINTS_MSSQL[i + 1];
            for (String constraintName : this.readMssqlConstraintNames(constraintLike)) {
                list.add(tableName);
                list.add(constraintName);
            }
        }
        return list.toArray(new String[list.size()]);
    }

    private List<String> readMssqlConstraintNames(String constraintLike) {
        NativeQuery query = this.sessionFactory.getCurrentSession().createNativeQuery("SELECT name FROM sys.objects\nWHERE type_desc LIKE '%CONSTRAINT'\n  AND name like ?");
        query.setParameter(1, (Object)constraintLike);
        return query.list();
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }
}

