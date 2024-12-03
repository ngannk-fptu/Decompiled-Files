/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup.postgres;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.TriggerEvent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.postgres.BasePostgresDdlHelper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;

public class PostgresSpacePermissionDdlHelper
extends BasePostgresDdlHelper {
    public PostgresSpacePermissionDdlHelper(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    @SuppressFBWarnings(value={"SQL_INJECTION_SPRING_JDBC"}, justification="All sql queries are created from private variables and private methods, so SQL injection is not possible")
    @VisibleForTesting
    public void dropTriggersAndFunctions() {
        this.getDropTriggerWithOldNameScripts().forEach(arg_0 -> ((JdbcTemplate)this.jdbcTemplate).execute(arg_0));
        super.dropTriggersAndFunctions();
    }

    @Override
    protected String getTriggerName() {
        return "denormalised_space_permission_trigger_on_";
    }

    private String getOldTriggerName(TriggerEvent event) {
        return "denormalised_space_trigger_on_" + event.name().toLowerCase();
    }

    @Override
    protected String getTriggerTableName() {
        return "spacepermissions";
    }

    @Override
    protected String getTriggerFunctionName() {
        return "space_permission_function_for_denormalised_permissions";
    }

    @Override
    protected String getCreateTriggerFunctionDdl() {
        return "CREATE OR REPLACE FUNCTION " + this.getTriggerFunctionName() + "()\n  RETURNS TRIGGER\n  AS\n$BODY$\nDECLARE\n    SPACE_ID BIGINT;\nBEGIN\n\n  IF (TG_OP = 'DELETE' OR TG_OP = 'TRUNCATE') THEN\n    SPACE_ID = OLD.SPACEID;\n  ELSE\n    SPACE_ID = NEW.SPACEID;\n  END IF;\n\n  INSERT INTO DENORMALISED_SPACE_CHANGE_LOG(SPACE_ID)\n    VALUES (SPACE_ID);\n  RETURN NEW;\nEND\n\n$BODY$\nLANGUAGE plpgsql;";
    }

    private List<String> getDropTriggerWithOldNameScripts() {
        return Arrays.stream(TriggerEvent.values()).map(this::getOldTriggerName).map(this::getDropTriggerDdl).collect(Collectors.toList());
    }
}

