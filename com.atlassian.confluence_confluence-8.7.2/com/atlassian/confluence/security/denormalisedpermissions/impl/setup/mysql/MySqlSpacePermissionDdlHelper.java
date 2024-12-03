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

public class MySqlSpacePermissionDdlHelper
extends BaseMySqlDdlHelper {
    public MySqlSpacePermissionDdlHelper(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getProcedureName() {
        return "space_permission_procedure_for_denormalised_permissions";
    }

    @Override
    protected String getTriggerName() {
        return "denormalised_space_permission_trigger_on_";
    }

    @Override
    protected String getCreateUpdateTriggerDdl() {
        return "CREATE TRIGGER " + this.getTriggerName(TriggerEvent.UPDATE) + "\nAFTER UPDATE \nON " + "spacepermissions".toUpperCase() + " FOR EACH ROW\nsp: BEGIN \n   DECLARE isServiceDisabled BOOL DEFAULT TRUE;\n   CALL " + this.getProcedureName() + "(isServiceDisabled);\n   IF (isServiceDisabled) THEN \n       LEAVE sp; \n   END IF; \n   INSERT INTO DENORMALISED_SPACE_CHANGE_LOG(SPACE_ID) \n       VALUES (NEW.SPACEID); \nEND;";
    }

    @Override
    protected String getCreateInsertTriggerDdl() {
        return "CREATE TRIGGER " + this.getTriggerName(TriggerEvent.INSERT) + "\nAFTER INSERT \nON " + "spacepermissions".toUpperCase() + " FOR EACH ROW\nsp: BEGIN \n   DECLARE isServiceDisabled BOOL DEFAULT TRUE;\n   CALL " + this.getProcedureName() + "(isServiceDisabled);\n   IF (isServiceDisabled) THEN \n       LEAVE sp; \n   END IF; \n   INSERT INTO DENORMALISED_SPACE_CHANGE_LOG(SPACE_ID) \n       VALUES (NEW.SPACEID); \nEND;";
    }

    @Override
    protected String getCreateDeleteTriggerDdl() {
        return "CREATE TRIGGER " + this.getTriggerName(TriggerEvent.DELETE) + "\nAFTER DELETE \nON " + "spacepermissions".toUpperCase() + " FOR EACH ROW\nsp: BEGIN \n   DECLARE isServiceDisabled BOOL DEFAULT TRUE;\n   CALL " + this.getProcedureName() + "(isServiceDisabled);\n   IF (isServiceDisabled) THEN \n       LEAVE sp; \n   END IF; \n   INSERT INTO DENORMALISED_SPACE_CHANGE_LOG(SPACE_ID) \n       VALUES (OLD.SPACEID); \nEND;";
    }
}

