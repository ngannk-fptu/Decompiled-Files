/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.confluence.security.denormalisedpermissions.BulkPermissionService
 *  com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionStateManager
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 */
package com.atlassian.confluence.plugins.tasklist.ao.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.plugins.tasklist.ao.dao.AOInlineTasksWithFastPermissionsDao;
import com.atlassian.confluence.security.denormalisedpermissions.BulkPermissionService;
import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionStateManager;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;

public class AOMySQL8InlineTasksWithFastPermissionsDao
extends AOInlineTasksWithFastPermissionsDao {
    public AOMySQL8InlineTasksWithFastPermissionsDao(ActiveObjects ao, BulkPermissionService bulkPermissionService, DarkFeaturesManager darkFeaturesManager, DenormalisedPermissionStateManager denormalisedPermissionStateManager) {
        super(ao, bulkPermissionService, darkFeaturesManager, denormalisedPermissionStateManager);
    }

    @Override
    protected String buildWithStatement(String name, String fields) {
        return String.format("%s (%s)", name, fields);
    }

    @Override
    protected String castFieldToText(String fieldName) {
        return fieldName;
    }

    @Override
    protected String buildLimitStatement(int limit) {
        return String.format("LIMIT %d", INITIAL_CONDITION_LIMIT);
    }

    @Override
    protected String addRecursiveKeywordIsApplicable() {
        return "RECURSIVE ";
    }

    @Override
    protected String postProcessQuery(String sqlQuery) {
        return sqlQuery.replace("\"", "");
    }
}

