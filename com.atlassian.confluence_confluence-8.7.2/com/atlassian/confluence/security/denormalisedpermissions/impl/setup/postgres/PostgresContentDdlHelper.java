/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup.postgres;

import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.postgres.BasePostgresDdlHelper;
import org.springframework.jdbc.core.JdbcTemplate;

public class PostgresContentDdlHelper
extends BasePostgresDdlHelper {
    public PostgresContentDdlHelper(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTriggerName() {
        return "denormalised_content_trigger_on_";
    }

    @Override
    protected String getTriggerTableName() {
        return "content";
    }

    @Override
    protected String getTriggerFunctionName() {
        return "content_function_for_denormalised_permissions";
    }

    @Override
    protected String getCreateTriggerFunctionDdl() {
        return "CREATE OR REPLACE FUNCTION " + this.getTriggerFunctionName() + "()\n   RETURNS TRIGGER AS $BODY$\nDECLARE\n   CONTENT_ID BIGINT;\nBEGIN\n\n   IF (TG_OP = 'UPDATE' OR TG_OP = 'INSERT') THEN \n       IF (NEW.CONTENTTYPE != 'PAGE' OR \n           NEW.PREVVER IS NOT NULL) THEN \n           RETURN NEW;\n       END IF;\n       CONTENT_ID = NEW.CONTENTID;\n   END IF;\n   IF (TG_OP = 'DELETE') THEN\n       IF (OLD.CONTENTTYPE != 'PAGE' OR \n           OLD.PREVVER IS NOT NULL) THEN \n           RETURN NEW;\n       END IF;\n       CONTENT_ID = OLD.CONTENTID;\n   END IF;\n\n   INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG(CONTENT_ID)\n       VALUES (CONTENT_ID);\n   RETURN NEW;\nEND\n\n$BODY$\nLANGUAGE plpgsql;";
    }
}

