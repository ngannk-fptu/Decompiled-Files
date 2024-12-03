/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  org.hibernate.SessionFactory
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.atlassian.confluence.upgrade.ddl.DdlExecutor;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.SessionFactory;

public class DbTrueFalseTypeFixUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final String BUILD_NUMBER = "9011";
    private final DdlExecutor ddlExecutor;
    private final SessionFactory sessionFactory;

    public DbTrueFalseTypeFixUpgradeTask(DdlExecutor ddlExecutor, SessionFactory sessionFactory) {
        this.ddlExecutor = ddlExecutor;
        this.sessionFactory = sessionFactory;
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public String getBuildNumber() {
        return BUILD_NUMBER;
    }

    public void doUpgrade() throws Exception {
        if (Boolean.getBoolean("confluence.upgrade.db-true-false-type-upgrade-task.disable")) {
            log.info("Skipping DbTrueFalseTypeFixUpgradeTask");
            return;
        }
        log.info("Started TrueFalseType upgrade task.");
        List<String> statements = this.prepareStatements();
        if (!statements.isEmpty()) {
            this.ddlExecutor.executeDdlStatements(statements);
        }
        log.info("Completed TrueFalseType upgrade task.");
    }

    private List<String> prepareStatements() {
        ArrayList<String> statements = new ArrayList<String>();
        this.addUpdateStatement(statements, "cwd_group", "active");
        this.addUpdateStatement(statements, "cwd_group", "local");
        this.addUpdateStatement(statements, "cwd_user", "active");
        this.addUpdateStatement(statements, "cwd_application", "active");
        this.addUpdateStatement(statements, "cwd_app_dir_mapping", "allow_all");
        this.addUpdateStatement(statements, "cwd_directory", "active");
        return statements;
    }

    private void addUpdateStatement(List<String> statements, String tableName, String columnName) {
        List recordsWithLowerCaseValue = this.sessionFactory.getCurrentSession().createNativeQuery(this.hasLowerCaseValueQuery(tableName, columnName)).setMaxResults(1).list();
        if (!recordsWithLowerCaseValue.isEmpty()) {
            log.info("Records in lower case found in table {}, table will be updated", (Object)tableName);
            statements.add(this.getUpdateQuery(tableName, columnName));
        }
    }

    private String hasLowerCaseValueQuery(String tableName, String columnName) {
        return String.format("SELECT 1 FROM %1$s WHERE %2$s=LOWER(%2$s)", tableName, columnName);
    }

    private String getUpdateQuery(String tableName, String columnName) {
        return String.format("UPDATE %1$s SET %2$s=UPPER(%2$s) WHERE %2$s=LOWER(%2$s)", tableName, columnName);
    }
}

