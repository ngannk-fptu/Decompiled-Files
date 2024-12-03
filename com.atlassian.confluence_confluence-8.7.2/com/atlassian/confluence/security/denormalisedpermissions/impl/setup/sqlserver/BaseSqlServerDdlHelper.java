/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup.sqlserver;

import com.atlassian.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class BaseSqlServerDdlHelper {
    private final JdbcTemplate jdbcTemplate;
    private final String schemaName;

    public BaseSqlServerDdlHelper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.schemaName = this.getSchemaName(jdbcTemplate);
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void createTriggersAndFunctions() {
        this.jdbcTemplate.execute(this.getCreateFunction(false));
        this.jdbcTemplate.execute(this.getCreateTriggerDdl());
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void enableService() {
        this.jdbcTemplate.execute(this.getCreateFunction(true));
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void disableService() {
        this.jdbcTemplate.execute(this.getCreateFunction(false));
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    @VisibleForTesting
    public void dropTriggersAndFunctions() {
        this.jdbcTemplate.execute(this.getDropTriggerDdl());
        this.jdbcTemplate.execute(this.getDropFunctionName());
    }

    protected String getFullTriggerName() {
        return "[" + this.schemaName + "]." + this.getTriggerName();
    }

    protected String getFullFunctionName() {
        return "[" + this.schemaName + "]." + this.getFunctionName();
    }

    protected String getFullTableName() {
        return "[" + this.schemaName + "]." + this.getTableName();
    }

    protected String getDropTriggerDdl() {
        return "DROP TRIGGER IF EXISTS " + this.getFullTriggerName();
    }

    protected abstract String getTriggerName();

    protected abstract String getFunctionName();

    protected abstract String getTableName();

    protected abstract String getCreateTriggerDdl();

    protected String getDropFunctionName() {
        return "DROP FUNCTION IF EXISTS " + this.getFullFunctionName();
    }

    protected String getCreateFunction(boolean isServiceEnabled) {
        return "CREATE OR ALTER FUNCTION " + this.getFullFunctionName() + "()\nRETURNS BIT\nAS\nBEGIN\n    RETURN " + (isServiceEnabled ? "1" : "0") + "\nEND";
    }

    private String getSchemaName(JdbcTemplate jdbcTemplate) {
        List schemaNameList = jdbcTemplate.queryForList("SELECT SCHEMA_NAME();", String.class);
        if (schemaNameList.size() != 1) {
            throw new IllegalStateException("Unable to determine schema name. Expected one value, but received " + schemaNameList.size() + " values: " + String.join((CharSequence)", ", schemaNameList));
        }
        return (String)schemaNameList.get(0);
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void dropIndex(String indexName, String tableName) {
        this.jdbcTemplate.execute(this.getDropIndexDDL(indexName, tableName));
    }

    private String getDropIndexDDL(String indexName, String tableName) {
        return "DROP INDEX IF EXISTS " + indexName + " ON [" + this.schemaName + "]." + tableName + ";";
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public boolean indexExist(String indexName, String tableName) {
        Integer indexCount = (Integer)this.jdbcTemplate.queryForObject(this.getIndexExistDDL(indexName, tableName), Integer.class);
        return indexCount != null && indexCount == 1;
    }

    private String getIndexExistDDL(String indexName, String tableName) {
        return "SELECT COUNT(*) \nFROM sys.indexes i \nJOIN sys.tables t ON i.object_id = t.object_id \nJOIN sys.schemas s ON s.schema_id = t.schema_id \nWHERE i.name = '" + indexName + "' AND t.name = '" + tableName + "' AND s.name = '" + this.schemaName + "';";
    }
}

