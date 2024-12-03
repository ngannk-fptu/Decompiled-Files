/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.springframework.dao.IncorrectResultSizeDataAccessException
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup.postgres;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.TriggerEvent;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class BasePostgresDdlHelper {
    protected final JdbcTemplate jdbcTemplate;
    protected final String schemaName;

    public BasePostgresDdlHelper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.schemaName = this.getSchemaName(jdbcTemplate);
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void createTriggersAndFunctions() {
        this.jdbcTemplate.execute(this.getCreateEmptyTriggerFunctionDdl());
        this.getCreateTriggerScripts().forEach(arg_0 -> ((JdbcTemplate)this.jdbcTemplate).execute(arg_0));
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void enableService() {
        this.jdbcTemplate.execute(this.getCreateTriggerFunctionDdl());
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void disableService() {
        this.jdbcTemplate.execute(this.getCreateEmptyTriggerFunctionDdl());
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    @VisibleForTesting
    public void dropTriggersAndFunctions() {
        this.getDropTriggerScripts().forEach(arg_0 -> ((JdbcTemplate)this.jdbcTemplate).execute(arg_0));
        this.jdbcTemplate.execute(this.getDropFunctionDdl());
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void dropIndex(String indexName) {
        this.jdbcTemplate.execute(this.getDropIndexDdl(indexName));
    }

    protected String getTriggerName(TriggerEvent event) {
        return this.getTriggerName() + event.name().toLowerCase();
    }

    protected abstract String getTriggerName();

    protected abstract String getTriggerTableName();

    protected abstract String getTriggerFunctionName();

    protected abstract String getCreateTriggerFunctionDdl();

    protected String getCreateEmptyTriggerFunctionDdl() {
        return "CREATE OR REPLACE FUNCTION " + this.getTriggerFunctionName() + "()\n   RETURNS TRIGGER AS $BODY$\nBEGIN\n  RETURN NEW;\nEND\n\n$BODY$\nLANGUAGE plpgsql;";
    }

    private String getDropFunctionDdl() {
        return "DROP FUNCTION IF EXISTS " + this.getTriggerFunctionName() + "();";
    }

    protected String getCreateTriggerDdl(TriggerEvent event) {
        return "CREATE TRIGGER " + this.getTriggerName(event) + "\nAFTER " + event.name() + "\nON " + this.getTriggerTableName() + "\nFOR EACH ROW\nEXECUTE PROCEDURE " + this.getTriggerFunctionName() + "();";
    }

    protected String getDropTriggerDdl(String triggerName) {
        return "DROP TRIGGER IF EXISTS " + triggerName + " ON " + this.getTriggerTableName() + ";";
    }

    protected List<String> getCreateTriggerScripts() {
        return Arrays.stream(TriggerEvent.values()).map(this::getCreateTriggerDdl).collect(Collectors.toList());
    }

    private List<String> getDropTriggerScripts() {
        return Arrays.stream(TriggerEvent.values()).map(this::getTriggerName).map(this::getDropTriggerDdl).collect(Collectors.toList());
    }

    private String getDropIndexDdl(String indexName) {
        return "DROP INDEX IF EXISTS " + indexName + ";";
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public boolean indexExist(String indexName, String tableName) {
        Integer indexCount = (Integer)this.jdbcTemplate.queryForObject(this.getIndexExistDDL(indexName, tableName), Integer.class);
        return indexCount != null && indexCount == 1;
    }

    private String getIndexExistDDL(String indexName, String tableName) {
        return "SELECT COUNT(*) FROM pg_indexes\nWHERE indexname = '" + indexName.toLowerCase() + "' \nAND tablename = '" + tableName.toLowerCase() + "' \nAND schemaname = '" + this.schemaName + "';";
    }

    private String getSchemaName(JdbcTemplate jdbcTemplate) {
        try {
            String schemaName = (String)jdbcTemplate.queryForObject("SELECT current_schema();", String.class);
            if (schemaName == null) {
                throw new IllegalStateException("Unable to determine schema name. Expected default schema name, but [SELECT current_schema();] query returned NULL");
            }
            return schemaName;
        }
        catch (IncorrectResultSizeDataAccessException e) {
            throw new IllegalStateException("Unable to determine schema name. Expected one value, but [SELECT current_schema();] query returned more than one record", e);
        }
    }
}

