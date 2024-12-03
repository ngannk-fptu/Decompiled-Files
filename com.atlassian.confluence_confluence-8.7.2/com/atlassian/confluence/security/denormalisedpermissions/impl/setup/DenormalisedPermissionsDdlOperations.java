/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.domain.DenormalisedServiceStateRecord;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class DenormalisedPermissionsDdlOperations {
    public static final String SPACE_TABLE_NAME = "spaces";
    public static final String SPACE_PERMISSION_TABLE_NAME = "spacepermissions";
    public static final String CONTENT_TABLE_NAME = "content";
    public static final String CONTENT_PERMISSION_TABLE_NAME = "content_perm";
    public static final String CONTENT_PERMISSION_SET_TABLE_NAME = "content_perm_set";

    abstract void createSpaceTriggersAndFunctions(JdbcTemplate var1);

    @VisibleForTesting
    abstract void dropAllSpaceTriggersAndFunctions(JdbcTemplate var1);

    abstract void enableSpaceServiceTriggers(JdbcTemplate var1);

    abstract void disableSpaceServiceTriggers(JdbcTemplate var1);

    abstract void createSpacePermissionTriggersAndFunctions(JdbcTemplate var1);

    @VisibleForTesting
    abstract void dropAllSpacePermissionTriggersAndFunctions(JdbcTemplate var1);

    abstract void enableSpacePermissionServiceTriggers(JdbcTemplate var1);

    abstract void disableSpacePermissionServiceTriggers(JdbcTemplate var1);

    abstract void createContentTriggersAndFunctions(JdbcTemplate var1);

    abstract void dropAllContentTriggersAndFunctions(JdbcTemplate var1);

    abstract void enableContentServiceTriggers(JdbcTemplate var1);

    abstract void disableContentServiceTriggers(JdbcTemplate var1);

    abstract void createContentPermissionTriggersAndFunctions(JdbcTemplate var1);

    abstract void dropAllContentPermissionTriggersAndFunctions(JdbcTemplate var1);

    abstract void enableContentPermissionServiceTriggers(JdbcTemplate var1);

    abstract void disableContentPermissionServiceTriggers(JdbcTemplate var1);

    abstract void createContentPermissionSetTriggersAndFunctions(JdbcTemplate var1);

    abstract void dropAllContentPermissionSetTriggersAndFunctions(JdbcTemplate var1);

    abstract void enableContentPermissionSetServiceTriggers(JdbcTemplate var1);

    abstract void disableContentPermissionSetServiceTriggers(JdbcTemplate var1);

    abstract void dropIndex(JdbcTemplate var1, DenormalisedServiceStateRecord.ServiceType var2, String var3, String var4);

    abstract boolean indexExist(JdbcTemplate var1, DenormalisedServiceStateRecord.ServiceType var2, String var3, String var4);
}

