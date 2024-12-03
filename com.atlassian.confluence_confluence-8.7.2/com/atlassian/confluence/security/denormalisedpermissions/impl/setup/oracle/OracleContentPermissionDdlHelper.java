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

public class OracleContentPermissionDdlHelper
extends BaseOracleDdlHelper {
    private final String CREATE_TRIGGER = "CREATE OR REPLACE TRIGGER " + this.getTriggerName() + "\nAFTER INSERT OR UPDATE OR DELETE\nON " + "content_perm".toUpperCase() + " FOR EACH ROW\nDISABLE\nDECLARE\n   CPS_ID NUMBER;\nBEGIN\n   IF NOT (UPDATING AND \n           :NEW.CP_TYPE = :OLD.CP_TYPE AND \n           :NEW.CPS_ID = :OLD.CPS_ID AND \n           ((:NEW.USERNAME IS NULL AND :OLD.USERNAME IS NULL) OR \n            (:NEW.USERNAME IS NOT NULL AND :OLD.USERNAME IS NOT NULL AND :NEW.USERNAME = :OLD.USERNAME)) AND \n           ((:NEW.GROUPNAME IS NULL AND :OLD.GROUPNAME IS NULL) OR \n            (:NEW.GROUPNAME IS NOT NULL AND :OLD.GROUPNAME IS NOT NULL AND :NEW.GROUPNAME = :OLD.GROUPNAME)) \n           ) THEN\n       IF DELETING THEN\n           CPS_ID := :OLD.CPS_ID;\n       ELSE\n           CPS_ID := :NEW.CPS_ID;\n       END IF;\n       INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG(CPS_ID)\n           VALUES (CPS_ID);\n   END IF;END;";

    public OracleContentPermissionDdlHelper(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTriggerName() {
        return "denormalised_content_permission_trigger";
    }

    @Override
    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void createTriggersAndFunctions() {
        this.jdbcTemplate.execute(this.CREATE_TRIGGER);
    }
}

