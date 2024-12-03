/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup.h2triggers;

import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.TriggerEvent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.h2triggers.BaseH2DdlHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.h2triggers.H2SpacePermissionTrigger;
import org.springframework.jdbc.core.JdbcTemplate;

public class H2SpacePermissionDdlHelper
extends BaseH2DdlHelper {
    public H2SpacePermissionDdlHelper(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTriggerName(TriggerEvent event) {
        return "denormalised_space_permission_trigger_on_" + event.name().toLowerCase();
    }

    @Override
    protected String getTableName() {
        return "spacepermissions";
    }

    @Override
    protected String getTriggerClassName() {
        return H2SpacePermissionTrigger.class.getName();
    }
}

