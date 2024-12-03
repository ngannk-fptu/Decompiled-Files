/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup.mysql;

import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.TriggerEvent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.mysql.BaseMySqlDdlHelper;
import org.springframework.jdbc.core.JdbcTemplate;

public class MySqlContentDdlHelper
extends BaseMySqlDdlHelper {
    public MySqlContentDdlHelper(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getProcedureName() {
        return "content_procedure_for_denormalised_permissions";
    }

    @Override
    protected String getTriggerName() {
        return "denormalised_content_trigger_on_";
    }

    @Override
    protected String getCreateUpdateTriggerDdl() {
        return "CREATE TRIGGER " + this.getTriggerName(TriggerEvent.UPDATE) + "\nAFTER UPDATE \nON " + "content".toUpperCase() + " FOR EACH ROW\nsp: BEGIN \n   DECLARE isServiceDisabled BOOL DEFAULT TRUE;\n   CALL " + this.getProcedureName() + "(isServiceDisabled);\n   IF (isServiceDisabled) THEN \n       LEAVE sp; \n   END IF; \n   IF (NEW.CONTENTTYPE != 'PAGE' OR \n       NEW.PREVVER IS NOT NULL) THEN \n       LEAVE sp; \n   END IF; \n   INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG(CONTENT_ID)\n       VALUES (NEW.CONTENTID); \nEND;";
    }

    @Override
    protected String getCreateInsertTriggerDdl() {
        return "CREATE TRIGGER " + this.getTriggerName(TriggerEvent.INSERT) + "\nAFTER INSERT \nON " + "content".toUpperCase() + " FOR EACH ROW\nsp: BEGIN \n   DECLARE isServiceDisabled BOOL DEFAULT TRUE;\n   CALL " + this.getProcedureName() + "(isServiceDisabled);\n   IF (isServiceDisabled) THEN \n       LEAVE sp; \n   END IF; \n   IF (NEW.CONTENTTYPE != 'PAGE' OR \n       NEW.PREVVER IS NOT NULL) THEN \n       LEAVE sp; \n   END IF; \n   INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG(CONTENT_ID)\n       VALUES (NEW.CONTENTID); \nEND;";
    }

    @Override
    protected String getCreateDeleteTriggerDdl() {
        return "CREATE TRIGGER " + this.getTriggerName(TriggerEvent.DELETE) + "\nAFTER DELETE \nON " + "content".toUpperCase() + " FOR EACH ROW\nsp: BEGIN \n   DECLARE isServiceDisabled BOOL DEFAULT TRUE;\n   CALL " + this.getProcedureName() + "(isServiceDisabled);\n   IF (isServiceDisabled) THEN \n       LEAVE sp; \n   END IF; \n   IF (OLD.CONTENTTYPE != 'PAGE' OR \n       OLD.PREVVER IS NOT NULL) THEN \n       LEAVE sp; \n   END IF; \n   INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG(CONTENT_ID)\n       VALUES (OLD.CONTENTID); \nEND;";
    }
}

