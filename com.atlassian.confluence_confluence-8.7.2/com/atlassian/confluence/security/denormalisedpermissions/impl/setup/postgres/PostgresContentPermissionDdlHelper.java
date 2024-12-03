/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup.postgres;

import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.postgres.BasePostgresDdlHelper;
import org.springframework.jdbc.core.JdbcTemplate;

public class PostgresContentPermissionDdlHelper
extends BasePostgresDdlHelper {
    public PostgresContentPermissionDdlHelper(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTriggerName() {
        return "denormalised_content_permission_trigger_on_";
    }

    @Override
    protected String getTriggerTableName() {
        return "content_perm";
    }

    @Override
    protected String getTriggerFunctionName() {
        return "content_permission_function_for_denormalised_permissions";
    }

    @Override
    protected String getCreateTriggerFunctionDdl() {
        return "CREATE OR REPLACE FUNCTION " + this.getTriggerFunctionName() + "()\n   RETURNS TRIGGER AS $BODY$\nDECLARE\n   CPS_ID BIGINT;\nBEGIN\n\n   IF (TG_OP = 'UPDATE' AND \n       NEW.CP_TYPE = OLD.CP_TYPE AND \n       NEW.CPS_ID = OLD.CPS_ID AND \n       ((NEW.USERNAME IS NULL AND OLD.USERNAME IS NULL) OR \n        (NEW.USERNAME IS NOT NULL AND OLD.USERNAME IS NOT NULL AND NEW.USERNAME = OLD.USERNAME)) AND \n       ((NEW.GROUPNAME IS NULL AND OLD.GROUPNAME IS NULL) OR \n        (NEW.GROUPNAME IS NOT NULL AND OLD.GROUPNAME IS NOT NULL AND NEW.GROUPNAME = OLD.GROUPNAME)) \n       ) THEN\n       RETURN NEW;\n   END IF;\n   IF (TG_OP = 'DELETE' OR TG_OP = 'TRUNCATE') THEN\n       CPS_ID = OLD.CPS_ID;\n   ELSE\n       CPS_ID = NEW.CPS_ID;\n   END IF;\n\n   INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG(CPS_ID)\n       VALUES (CPS_ID);\n   RETURN NEW;\nEND\n\n$BODY$\nLANGUAGE plpgsql;";
    }
}

