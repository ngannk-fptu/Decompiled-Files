/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.plugin.PluginParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;

public class SpaceFunctionPermissionCondition
extends BaseConfluenceCondition {
    static final String PERMISSION = "permission";
    private SpacePermissionManager spacePermissionManager;
    private Set<String> permissions;

    @Override
    public void init(Map<String, String> params) throws PluginParseException {
        String permissionsString = null;
        try {
            permissionsString = params.get(PERMISSION);
            if (StringUtils.isBlank((CharSequence)permissionsString)) {
                throw new IllegalArgumentException("The permission parameter must not be blank.");
            }
            this.permissions = SpaceFunctionPermissionCondition.parsePermissions(permissionsString);
            if (this.permissions.isEmpty()) {
                throw new IllegalArgumentException("No permissions could be parsed from the permission parameter.");
            }
        }
        catch (Exception e) {
            throw new PluginParseException("Could not determine permissions for condition. " + e.getMessage(), (Throwable)e);
        }
        super.init(params);
    }

    Set<String> getPermissions() {
        return this.permissions;
    }

    private static Set<String> parsePermissions(String permissions) {
        HashSet<String> permissionsSet = new HashSet<String>();
        StringTokenizer tokenizer = new StringTokenizer(permissions, ", ");
        while (tokenizer.hasMoreTokens()) {
            permissionsSet.add(tokenizer.nextToken());
        }
        return permissionsSet;
    }

    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        return this.spacePermissionManager.hasAllPermissions(new ArrayList<String>(this.permissions), context.getSpace(), context.getCurrentUser());
    }

    public void setSpacePermissionManager(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }
}

