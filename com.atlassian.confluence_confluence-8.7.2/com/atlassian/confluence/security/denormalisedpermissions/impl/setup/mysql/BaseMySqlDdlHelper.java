/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.springframework.dao.IncorrectResultSizeDataAccessException
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup.mysql;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.TriggerEvent;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class BaseMySqlDdlHelper {
    protected final JdbcTemplate jdbcTemplate;
    protected final String schemaName;

    public BaseMySqlDdlHelper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.schemaName = this.getSchemaName(jdbcTemplate);
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void createTriggersAndFunctions() {
        this.jdbcTemplate.execute(this.getDropProcedureDDl());
        this.jdbcTemplate.execute(this.getCreateProcedure(true));
        this.getCreateTriggerScripts().forEach(arg_0 -> ((JdbcTemplate)this.jdbcTemplate).execute(arg_0));
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void enableService() {
        this.jdbcTemplate.execute(this.getDropProcedureDDl());
        this.jdbcTemplate.execute(this.getCreateProcedure(false));
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void disableService() {
        this.jdbcTemplate.execute(this.getDropProcedureDDl());
        this.jdbcTemplate.execute(this.getCreateProcedure(true));
    }

    @VisibleForTesting
    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void dropTriggersAndFunctions() {
        this.getDropTriggerScripts().forEach(arg_0 -> ((JdbcTemplate)this.jdbcTemplate).execute(arg_0));
        this.jdbcTemplate.execute(this.getDropProcedureDDl());
    }

    protected String getTriggerName(TriggerEvent event) {
        return this.getTriggerName() + event.name().toLowerCase();
    }

    protected abstract String getTriggerName();

    protected abstract String getProcedureName();

    protected abstract String getCreateUpdateTriggerDdl();

    protected abstract String getCreateInsertTriggerDdl();

    protected abstract String getCreateDeleteTriggerDdl();

    protected TriggerEvent[] getSupportedEvents() {
        return TriggerEvent.values();
    }

    protected List<String> getCreateTriggerScripts() {
        return Arrays.asList(this.getCreateUpdateTriggerDdl(), this.getCreateInsertTriggerDdl(), this.getCreateDeleteTriggerDdl());
    }

    private String getCreateProcedure(boolean isServiceDisabled) {
        return "CREATE PROCEDURE " + this.getProcedureName() + "(OUT isServiceDisabled BOOL)\nBEGIN\n    SET isServiceDisabled = " + (isServiceDisabled ? "TRUE" : "FALSE") + ";\nEND";
    }

    private String getDropProcedureDDl() {
        return "DROP PROCEDURE IF EXISTS " + this.getProcedureName() + ";";
    }

    private String getDropTriggerDdl(TriggerEvent event) {
        return "DROP TRIGGER IF EXISTS " + this.getTriggerName(event) + ";";
    }

    private List<String> getDropTriggerScripts() {
        return Arrays.stream(this.getSupportedEvents()).map(this::getDropTriggerDdl).collect(Collectors.toList());
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void dropIndex(String indexName, String tableName) {
        if (this.indexExist(indexName, tableName)) {
            this.jdbcTemplate.execute(this.getDropIndexDDL(indexName, tableName));
        }
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public boolean indexExist(String indexName, String tableName) {
        Integer indexCount = (Integer)this.jdbcTemplate.queryForObject(this.getIndexExistDDL(indexName, tableName), Integer.class);
        return indexCount != null && indexCount > 0;
    }

    private String getIndexExistDDL(String indexName, String tableName) {
        return "SELECT COUNT(*)\nFROM INFORMATION_SCHEMA.STATISTICS\nWHERE TABLE_NAME = '" + tableName + "' AND INDEX_NAME = '" + indexName + "' AND TABLE_SCHEMA = '" + this.schemaName + "';";
    }

    private String getDropIndexDDL(String indexName, String tableName) {
        return "DROP INDEX " + indexName + " ON " + tableName + ";";
    }

    private String getSchemaName(JdbcTemplate jdbcTemplate) {
        try {
            String schemaName = (String)jdbcTemplate.queryForObject("SELECT DATABASE();", String.class);
            if (schemaName == null) {
                throw new IllegalStateException("Unable to determine schema name. Expected default schema name, but [SELECT DATABASE()] query returned NULL");
            }
            return schemaName;
        }
        catch (IncorrectResultSizeDataAccessException e) {
            throw new IllegalStateException("Unable to determine schema name. Expected one value, but [SELECT DATABASE()] query returned more than one record", e);
        }
    }
}

