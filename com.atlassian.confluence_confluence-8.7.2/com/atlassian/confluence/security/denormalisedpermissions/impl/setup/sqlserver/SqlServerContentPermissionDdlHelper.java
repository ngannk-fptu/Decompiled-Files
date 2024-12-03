/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup.sqlserver;

import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.sqlserver.BaseSqlServerDdlHelper;
import org.springframework.jdbc.core.JdbcTemplate;

public class SqlServerContentPermissionDdlHelper
extends BaseSqlServerDdlHelper {
    public SqlServerContentPermissionDdlHelper(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTriggerName() {
        return "denormalised_content_permission_trigger";
    }

    @Override
    protected String getFunctionName() {
        return "content_permission_function_for_denormalised_permissions";
    }

    @Override
    protected String getTableName() {
        return "content_perm".toUpperCase();
    }

    @Override
    protected String getCreateTriggerDdl() {
        return "CREATE TRIGGER " + this.getFullTriggerName() + "\nON " + this.getFullTableName() + "\nAFTER INSERT, UPDATE, DELETE\nAS\nBEGIN\n    SET NOCOUNT ON;\n    if (" + this.getFullFunctionName() + "() = 1)\n        BEGIN\n            INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG (cps_id) select CPS_ID from inserted;\n            INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG (cps_id) select CPS_ID from deleted;\n        END\nEND\n";
    }
}

