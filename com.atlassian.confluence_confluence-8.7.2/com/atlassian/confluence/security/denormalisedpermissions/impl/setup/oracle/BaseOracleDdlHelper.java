/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.springframework.dao.IncorrectResultSizeDataAccessException
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup.oracle;

import com.atlassian.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class BaseOracleDdlHelper {
    protected final JdbcTemplate jdbcTemplate;
    protected final String schemaName;
    private final String DROP_TRIGGER = "DROP TRIGGER " + this.getTriggerName();
    private final String TRIGGER_EXIST_QUERY = "SELECT COUNT(*)\nFROM user_triggers\nWHERE trigger_name = '" + this.getTriggerName().toUpperCase() + "'";
    private final String DISABLE_TRIGGER = "ALTER TRIGGER " + this.getTriggerName() + " DISABLE";
    private final String ENABLE_TRIGGER = "ALTER TRIGGER " + this.getTriggerName() + " ENABLE";

    public BaseOracleDdlHelper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.schemaName = this.getSchemaName(jdbcTemplate);
    }

    protected abstract String getTriggerName();

    public abstract void createTriggersAndFunctions();

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void enableService() {
        this.jdbcTemplate.execute(this.ENABLE_TRIGGER);
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void disableService() {
        this.jdbcTemplate.execute(this.DISABLE_TRIGGER);
    }

    @VisibleForTesting
    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void dropTriggersAndFunctions() {
        Integer triggerCount = (Integer)this.jdbcTemplate.queryForObject(this.TRIGGER_EXIST_QUERY, Integer.class);
        if (triggerCount != null && triggerCount > 0) {
            this.jdbcTemplate.execute(this.DROP_TRIGGER);
        }
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void dropIndex(String indexName, String tableName) {
        if (this.indexExist(indexName, tableName)) {
            this.jdbcTemplate.execute(this.getDropIndexDDL(indexName));
        }
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public boolean indexExist(String indexName, String tableName) {
        Integer indexCount = (Integer)this.jdbcTemplate.queryForObject(this.getIndexExistDDL(indexName, tableName), Integer.class);
        return indexCount != null && indexCount == 1;
    }

    private String getIndexExistDDL(String indexName, String tableName) {
        return "SELECT COUNT(*)\nFROM user_indexes\nWHERE index_name = '" + indexName.toUpperCase() + "' AND table_name = '" + tableName.toUpperCase() + "' AND table_owner = '" + this.schemaName + "'";
    }

    private String getDropIndexDDL(String indexName) {
        return "DROP INDEX " + indexName;
    }

    private String getSchemaName(JdbcTemplate jdbcTemplate) {
        try {
            String schemaName = (String)jdbcTemplate.queryForObject("SELECT SYS_CONTEXT('USERENV','CURRENT_SCHEMA') FROM DUAL", String.class);
            if (schemaName == null) {
                throw new IllegalStateException("Unable to determine schema name. Expected default schema name, but [SELECT SYS_CONTEXT('USERENV','CURRENT_SCHEMA') FROM DUAL] query returned NULL");
            }
            return schemaName;
        }
        catch (IncorrectResultSizeDataAccessException e) {
            throw new IllegalStateException("Unable to determine schema name. Expected one value, but [SELECT SYS_CONTEXT('USERENV','CURRENT_SCHEMA') FROM DUAL] query returned more than one record", e);
        }
    }
}

