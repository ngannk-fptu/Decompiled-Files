/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup.postgres;

import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.postgres.BasePostgresDdlHelper;
import org.springframework.jdbc.core.JdbcTemplate;

public class PostgresSpaceDdlHelper
extends BasePostgresDdlHelper {
    public PostgresSpaceDdlHelper(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTriggerName() {
        return "denormalised_space_trigger_on_";
    }

    @Override
    protected String getTriggerTableName() {
        return "spaces";
    }

    @Override
    protected String getTriggerFunctionName() {
        return "space_function_for_denormalised_permissions";
    }

    @Override
    protected String getCreateTriggerFunctionDdl() {
        return "CREATE OR REPLACE FUNCTION " + this.getTriggerFunctionName() + "()\n  RETURNS TRIGGER\n  AS\n$BODY$\nDECLARE\n    SPACE_ID BIGINT;\nBEGIN\n\n  IF (TG_OP = 'UPDATE' AND NEW.LOWERSPACEKEY = OLD.LOWERSPACEKEY) THEN\n    RETURN NEW;\n  END IF;\n  IF (TG_OP = 'DELETE' OR TG_OP = 'TRUNCATE') THEN\n    SPACE_ID = OLD.SPACEID;\n  ELSE\n    SPACE_ID = NEW.SPACEID;\n  END IF;\n\n  INSERT INTO DENORMALISED_SPACE_CHANGE_LOG(SPACE_ID)\n    VALUES (SPACE_ID);\n  RETURN NEW;\nEND\n\n$BODY$\nLANGUAGE plpgsql;";
    }
}

