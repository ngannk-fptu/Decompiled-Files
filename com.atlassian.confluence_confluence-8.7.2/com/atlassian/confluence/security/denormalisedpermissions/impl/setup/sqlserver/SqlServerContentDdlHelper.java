/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup.sqlserver;

import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.sqlserver.BaseSqlServerDdlHelper;
import org.springframework.jdbc.core.JdbcTemplate;

public class SqlServerContentDdlHelper
extends BaseSqlServerDdlHelper {
    public SqlServerContentDdlHelper(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTriggerName() {
        return "denormalised_content_trigger";
    }

    @Override
    protected String getFunctionName() {
        return "content_function_for_denormalised_permissions";
    }

    @Override
    protected String getTableName() {
        return "content".toUpperCase();
    }

    @Override
    protected String getCreateTriggerDdl() {
        return "CREATE TRIGGER " + this.getFullTriggerName() + "\nON " + this.getFullTableName() + "\nAFTER INSERT, UPDATE, DELETE\nAS\nBEGIN\n    SET NOCOUNT ON;\n    if (" + this.getFullFunctionName() + "() = 1)\n        BEGIN\n            INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG (content_id) \n               SELECT CONTENTID \n               FROM inserted \n               WHERE CONTENTTYPE = 'PAGE' AND PREVVER IS NULL; \n            INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG (content_id) \n               SELECT CONTENTID \n               FROM deleted \n               WHERE CONTENTTYPE = 'PAGE' AND PREVVER IS NULL; \n        END\nEND\n";
    }
}

