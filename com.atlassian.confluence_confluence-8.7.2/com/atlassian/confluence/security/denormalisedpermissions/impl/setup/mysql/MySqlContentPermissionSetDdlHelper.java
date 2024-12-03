/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup.mysql;

import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.TriggerEvent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.mysql.BaseMySqlDdlHelper;
import java.util.Collections;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

public class MySqlContentPermissionSetDdlHelper
extends BaseMySqlDdlHelper {
    public MySqlContentPermissionSetDdlHelper(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getProcedureName() {
        return "content_perm_set_procedure_for_denormalised_permissions";
    }

    @Override
    protected String getTriggerName() {
        return "denormalised_content_perm_set_trigger_on_";
    }

    @Override
    protected String getCreateUpdateTriggerDdl() {
        throw new UnsupportedOperationException("Trigger on UPDATE operation is not supported for content_perm_set table");
    }

    @Override
    protected String getCreateInsertTriggerDdl() {
        throw new UnsupportedOperationException("Trigger on INSERT operation is not supported for content_perm_set table");
    }

    @Override
    protected String getCreateDeleteTriggerDdl() {
        return "CREATE TRIGGER " + this.getTriggerName(TriggerEvent.DELETE) + "\nAFTER DELETE \nON " + "content_perm_set".toUpperCase() + " FOR EACH ROW\nsp: BEGIN \n   DECLARE isServiceDisabled BOOL DEFAULT TRUE;\n   CALL " + this.getProcedureName() + "(isServiceDisabled);\n   IF (isServiceDisabled) THEN \n       LEAVE sp; \n   END IF; \n   INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG(CONTENT_ID, CPS_ID)\n       VALUES (OLD.CONTENT_ID, OLD.ID); \nEND;";
    }

    @Override
    protected TriggerEvent[] getSupportedEvents() {
        return new TriggerEvent[]{TriggerEvent.DELETE};
    }

    @Override
    protected List<String> getCreateTriggerScripts() {
        return Collections.singletonList(this.getCreateDeleteTriggerDdl());
    }
}

