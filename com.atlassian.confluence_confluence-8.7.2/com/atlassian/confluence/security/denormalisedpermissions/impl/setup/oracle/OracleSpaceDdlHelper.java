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

public class OracleSpaceDdlHelper
extends BaseOracleDdlHelper {
    private static final String SEQUENCE_NAME = "DENORMALISED_SPACE_CHANGE_LOG_SEQ";
    private static final String USE_SEQUENCE_FOR_DEFAULT_VALUE = "ALTER TABLE DENORMALISED_SPACE_CHANGE_LOG \nMODIFY (ID DEFAULT DENORMALISED_SPACE_CHANGE_LOG_SEQ.NEXTVAL)";
    private final String CREATE_TRIGGER = "CREATE OR REPLACE TRIGGER " + this.getTriggerName() + "\nAFTER INSERT OR UPDATE OR DELETE\nON " + "spaces".toUpperCase() + " FOR EACH ROW\nDISABLE\nDECLARE\nSPACE_ID NUMBER;\nBEGIN\nIF NOT (UPDATING AND :NEW.LOWERSPACEKEY = :OLD.LOWERSPACEKEY) THEN\nIF DELETING THEN\nSPACE_ID := :OLD.SPACEID;\nELSE\nSPACE_ID := :NEW.SPACEID;\nEND IF;\nINSERT INTO DENORMALISED_SPACE_CHANGE_LOG(SPACE_ID)\nVALUES (SPACE_ID);\nEND IF;END;";

    public OracleSpaceDdlHelper(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTriggerName() {
        return "denormalised_space_trigger";
    }

    @Override
    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    public void createTriggersAndFunctions() {
        this.jdbcTemplate.execute(USE_SEQUENCE_FOR_DEFAULT_VALUE);
        this.jdbcTemplate.execute(this.CREATE_TRIGGER);
    }
}

