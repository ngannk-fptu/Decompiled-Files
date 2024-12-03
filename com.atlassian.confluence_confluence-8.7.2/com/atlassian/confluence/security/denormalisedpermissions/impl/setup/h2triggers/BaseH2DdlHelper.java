/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup.h2triggers;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.TriggerEvent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.h2triggers.H2EmptyTrigger;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class BaseH2DdlHelper {
    protected final JdbcTemplate jdbcTemplate;

    public BaseH2DdlHelper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected abstract String getTriggerName(TriggerEvent var1);

    protected abstract String getTableName();

    protected abstract String getTriggerClassName();

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void createTriggersAndFunctions(boolean enableService) {
        for (TriggerEvent event : this.getSupportedEvents()) {
            this.jdbcTemplate.execute("DROP TRIGGER IF EXISTS " + this.getTriggerName(event));
            String triggerClassName = enableService ? this.getTriggerClassName() : H2EmptyTrigger.class.getName();
            String createTriggerDdl = "CREATE TRIGGER " + this.getTriggerName(event) + "\nAFTER " + event.name() + "\nON " + this.getTableName() + "\nFOR EACH ROW\nCALL \"" + triggerClassName + "\"";
            this.jdbcTemplate.execute(createTriggerDdl);
        }
    }

    protected TriggerEvent[] getSupportedEvents() {
        return TriggerEvent.values();
    }

    public void enableService() {
        this.createTriggersAndFunctions(true);
    }

    public void disableService() {
        this.createTriggersAndFunctions(false);
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    @VisibleForTesting
    public void dropTriggersAndFunctions() {
        for (TriggerEvent event : this.getSupportedEvents()) {
            String dropTriggerDdl = "DROP TRIGGER IF EXISTS " + this.getTriggerName(event);
            this.jdbcTemplate.execute(dropTriggerDdl);
        }
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void dropIndex(String indexName) {
        String dropIndexDdl = "DROP INDEX IF EXISTS " + indexName;
        this.jdbcTemplate.execute(dropIndexDdl);
    }

    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public boolean indexExist(String indexName, String tableName) {
        Integer indexCount = (Integer)this.jdbcTemplate.queryForObject(this.getIndexExistDDL(indexName, tableName), Integer.class);
        return indexCount != null && indexCount > 0;
    }

    private String getIndexExistDDL(String indexName, String tableName) {
        return "SELECT COUNT(*) FROM INFORMATION_SCHEMA.INDEXES\nWHERE INDEX_NAME = '" + indexName.toUpperCase() + "' \nAND TABLE_NAME = '" + tableName.toUpperCase() + "';";
    }
}

