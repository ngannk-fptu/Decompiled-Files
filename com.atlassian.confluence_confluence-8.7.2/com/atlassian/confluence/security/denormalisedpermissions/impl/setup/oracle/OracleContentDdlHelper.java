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

public class OracleContentDdlHelper
extends BaseOracleDdlHelper {
    private static final String SEQUENCE_NAME = "DENORMALISED_CONTENT_CHANGE_LOG_SEQ";
    private static final String USE_SEQUENCE_FOR_DEFAULT_VALUE = "ALTER TABLE DENORMALISED_CONTENT_CHANGE_LOG \nMODIFY (ID DEFAULT DENORMALISED_CONTENT_CHANGE_LOG_SEQ.NEXTVAL)";
    private final String CREATE_TRIGGER = "CREATE OR REPLACE TRIGGER " + this.getTriggerName() + "\nAFTER INSERT OR UPDATE OR DELETE\nON " + "content".toUpperCase() + " FOR EACH ROW\nDISABLE\nBEGIN\n   IF (UPDATING OR INSERTING) THEN \n       IF (:NEW.CONTENTTYPE = 'PAGE' AND \n           :NEW.PREVVER IS NULL) THEN\n          INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG(CONTENT_ID)\n              VALUES (:NEW.CONTENTID);\n       END IF; \n   END IF; \n   IF DELETING THEN\n       IF (:OLD.CONTENTTYPE = 'PAGE' AND \n           :OLD.PREVVER IS NULL) THEN \n          INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG(CONTENT_ID)\n              VALUES (:OLD.CONTENTID);\n       END IF; \n   END IF; \nEND;";

    public OracleContentDdlHelper(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTriggerName() {
        return "denormalised_content_trigger";
    }

    @Override
    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void createTriggersAndFunctions() {
        this.jdbcTemplate.execute(USE_SEQUENCE_FOR_DEFAULT_VALUE);
        this.jdbcTemplate.execute(this.CREATE_TRIGGER);
    }
}

