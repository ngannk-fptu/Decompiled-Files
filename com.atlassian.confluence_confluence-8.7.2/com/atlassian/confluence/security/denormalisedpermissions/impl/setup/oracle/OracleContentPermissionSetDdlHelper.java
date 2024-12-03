/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup.oracle;

import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.oracle.BaseOracleDdlHelper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.jdbc.core.JdbcTemplate;

public class OracleContentPermissionSetDdlHelper
extends BaseOracleDdlHelper {
    private final String CREATE_TRIGGER = "CREATE OR REPLACE TRIGGER " + this.getTriggerName() + "\nAFTER DELETE\nON " + "content_perm_set".toUpperCase() + " FOR EACH ROW\nDISABLE\nBEGIN\n   INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG(CONTENT_ID, CPS_ID)\n   VALUES (:OLD.CONTENT_ID, :OLD.ID);\nEND;";

    public OracleContentPermissionSetDdlHelper(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTriggerName() {
        return "denormalised_content_perm_set_trigger";
    }

    @Override
    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void createTriggersAndFunctions() {
        this.jdbcTemplate.execute(this.CREATE_TRIGGER);
    }
}

