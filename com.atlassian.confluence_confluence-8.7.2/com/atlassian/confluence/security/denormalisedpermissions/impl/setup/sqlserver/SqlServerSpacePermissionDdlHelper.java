/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup.sqlserver;

import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.sqlserver.BaseSqlServerDdlHelper;
import org.springframework.jdbc.core.JdbcTemplate;

public class SqlServerSpacePermissionDdlHelper
extends BaseSqlServerDdlHelper {
    public SqlServerSpacePermissionDdlHelper(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTriggerName() {
        return "denormalised_space_permission_trigger";
    }

    @Override
    protected String getFunctionName() {
        return "space_permission_function_for_denormalised_permissions";
    }

    @Override
    protected String getTableName() {
        return "spacepermissions".toUpperCase();
    }

    @Override
    protected String getCreateTriggerDdl() {
        return "CREATE TRIGGER " + this.getFullTriggerName() + "\nON " + this.getFullTableName() + "\nAFTER INSERT, UPDATE, DELETE\nAS\nBEGIN\n    SET NOCOUNT ON;\n    if (" + this.getFullFunctionName() + "() = 1)\n        BEGIN\n            INSERT INTO DENORMALISED_SPACE_CHANGE_LOG (space_id) select SPACEID from inserted;\n            INSERT INTO DENORMALISED_SPACE_CHANGE_LOG (space_id) select SPACEID from deleted;\n        END\nEND\n";
    }
}

