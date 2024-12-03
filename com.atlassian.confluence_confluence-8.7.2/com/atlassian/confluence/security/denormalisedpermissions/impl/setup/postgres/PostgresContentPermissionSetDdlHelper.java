/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup.postgres;

import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.TriggerEvent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.postgres.BasePostgresDdlHelper;
import java.util.Collections;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

public class PostgresContentPermissionSetDdlHelper
extends BasePostgresDdlHelper {
    public PostgresContentPermissionSetDdlHelper(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTriggerName() {
        return "denormalised_content_perm_set_trigger_on_";
    }

    @Override
    protected String getTriggerTableName() {
        return "content_perm_set";
    }

    @Override
    protected String getTriggerFunctionName() {
        return "content_perm_set_function_for_denormalised_permissions";
    }

    @Override
    protected String getCreateTriggerFunctionDdl() {
        return "CREATE OR REPLACE FUNCTION " + this.getTriggerFunctionName() + "()\n   RETURNS TRIGGER AS $BODY$\nBEGIN\n\n   IF (TG_OP = 'DELETE') THEN\n       INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG(CONTENT_ID, CPS_ID)\n           VALUES (OLD.CONTENT_ID, OLD.ID);\n   END IF;\n\n   RETURN NEW;\nEND\n\n$BODY$\nLANGUAGE plpgsql;";
    }

    @Override
    protected List<String> getCreateTriggerScripts() {
        return Collections.singletonList(this.getCreateTriggerDdl(TriggerEvent.DELETE));
    }
}

