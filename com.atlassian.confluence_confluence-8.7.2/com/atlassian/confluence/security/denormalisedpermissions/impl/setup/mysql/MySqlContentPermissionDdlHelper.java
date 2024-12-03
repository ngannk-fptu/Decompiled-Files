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

public class MySqlContentPermissionDdlHelper
extends BaseMySqlDdlHelper {
    public MySqlContentPermissionDdlHelper(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getProcedureName() {
        return "content_permission_procedure_for_denormalised_permissions";
    }

    @Override
    protected String getTriggerName() {
        return "denormalised_content_permission_trigger_on_";
    }

    @Override
    protected String getCreateUpdateTriggerDdl() {
        return "CREATE TRIGGER " + this.getTriggerName(TriggerEvent.UPDATE) + "\nAFTER UPDATE \nON " + "content_perm".toUpperCase() + " FOR EACH ROW\nsp: BEGIN \n   DECLARE isServiceDisabled BOOL DEFAULT TRUE;\n   CALL " + this.getProcedureName() + "(isServiceDisabled);\n   IF (isServiceDisabled) THEN \n       LEAVE sp; \n   END IF; \n   IF (NEW.CP_TYPE = OLD.CP_TYPE AND \n       NEW.CPS_ID = OLD.CPS_ID AND \n       ((NEW.USERNAME IS NULL AND OLD.USERNAME IS NULL) OR \n        (NEW.USERNAME IS NOT NULL AND OLD.USERNAME IS NOT NULL AND NEW.USERNAME = OLD.USERNAME)) AND \n       ((NEW.GROUPNAME IS NULL AND OLD.GROUPNAME IS NULL) OR \n        (NEW.GROUPNAME IS NOT NULL AND OLD.GROUPNAME IS NOT NULL AND NEW.GROUPNAME = OLD.GROUPNAME)) \n       ) THEN \n       LEAVE sp; \n   END IF; \n   INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG(CPS_ID)\n       VALUES (NEW.CPS_ID); \nEND;";
    }

    @Override
    protected String getCreateInsertTriggerDdl() {
        return "CREATE TRIGGER " + this.getTriggerName(TriggerEvent.INSERT) + "\nAFTER INSERT \nON " + "content_perm".toUpperCase() + " FOR EACH ROW\nsp: BEGIN \n   DECLARE isServiceDisabled BOOL DEFAULT TRUE;\n   CALL " + this.getProcedureName() + "(isServiceDisabled);\n   IF (isServiceDisabled) THEN \n       LEAVE sp; \n   END IF; \n   INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG(CPS_ID)\n       VALUES (NEW.CPS_ID); \nEND;";
    }

    @Override
    protected String getCreateDeleteTriggerDdl() {
        return "CREATE TRIGGER " + this.getTriggerName(TriggerEvent.DELETE) + "\nAFTER DELETE \nON " + "content_perm".toUpperCase() + " FOR EACH ROW\nsp: BEGIN \n   DECLARE isServiceDisabled BOOL DEFAULT TRUE;\n   CALL " + this.getProcedureName() + "(isServiceDisabled);\n   IF (isServiceDisabled) THEN \n       LEAVE sp; \n   END IF; \n   INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG(CPS_ID)\n       VALUES (OLD.CPS_ID); \nEND;";
    }
}

