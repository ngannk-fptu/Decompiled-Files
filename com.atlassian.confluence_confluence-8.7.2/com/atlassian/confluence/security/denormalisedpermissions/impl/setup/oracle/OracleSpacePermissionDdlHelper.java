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

public class OracleSpacePermissionDdlHelper
extends BaseOracleDdlHelper {
    private final String CREATE_TRIGGER = "CREATE OR REPLACE TRIGGER " + this.getTriggerName() + "\nAFTER INSERT OR UPDATE OR DELETE\nON " + "spacepermissions".toUpperCase() + " FOR EACH ROW\nDISABLE\nDECLARE\n    SPACE_ID NUMBER;\nBEGIN\n    IF DELETING THEN\n        SPACE_ID := :OLD.SPACEID;\n    ELSE\n        SPACE_ID := :NEW.SPACEID;\n    END IF;\nINSERT INTO DENORMALISED_SPACE_CHANGE_LOG(SPACE_ID)\n    VALUES (SPACE_ID);\nEND;";

    public OracleSpacePermissionDdlHelper(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTriggerName() {
        return "denormalised_space_permission_trigger";
    }

    @Override
    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void createTriggersAndFunctions() {
        this.jdbcTemplate.execute(this.CREATE_TRIGGER);
    }
}

