/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.spi.feature.DelegatedViewFeature
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.audit.frontend.conditions;

import com.atlassian.audit.permission.PermissionChecker;
import com.atlassian.audit.spi.feature.DelegatedViewFeature;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class JiraCanViewDelegatedUICondition
implements Condition {
    public static final String RESOURCE_TYPE = "PROJECT";
    private final PermissionChecker permissionChecker;
    private final DelegatedViewFeature delegatedViewFeature;

    public JiraCanViewDelegatedUICondition(PermissionChecker permissionChecker, DelegatedViewFeature delegatedViewFeature) {
        this.permissionChecker = permissionChecker;
        this.delegatedViewFeature = delegatedViewFeature;
    }

    public final void init(Map<String, String> params) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> stringObjectMap) {
        String resourceId = (String)stringObjectMap.get("projectKeyEncoded");
        if (resourceId == null) {
            return false;
        }
        return this.delegatedViewFeature.isEnabled() && this.permissionChecker.hasResourceAuditViewPermission(RESOURCE_TYPE, resourceId);
    }
}

