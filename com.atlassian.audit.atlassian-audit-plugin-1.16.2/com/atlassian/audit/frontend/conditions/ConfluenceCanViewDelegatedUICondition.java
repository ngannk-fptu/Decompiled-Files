/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.spi.feature.DelegatedViewFeature
 *  com.atlassian.audit.spi.permission.ResourceContextPermissionChecker
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.audit.frontend.conditions;

import com.atlassian.audit.spi.feature.DelegatedViewFeature;
import com.atlassian.audit.spi.permission.ResourceContextPermissionChecker;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Qualifier;

public class ConfluenceCanViewDelegatedUICondition
implements Condition {
    private static final String SPACE_ID_KEY = "spaceid";
    private static final String SPACE_RESOURCE_TYPE = "Space";
    private final ResourceContextPermissionChecker permissionChecker;
    private final DelegatedViewFeature delegatedViewFeature;

    public ConfluenceCanViewDelegatedUICondition(@Qualifier(value="resourceContextPermissionChecker") ResourceContextPermissionChecker permissionChecker, DelegatedViewFeature delegatedViewFeature) {
        this.permissionChecker = Objects.requireNonNull(permissionChecker);
        this.delegatedViewFeature = Objects.requireNonNull(delegatedViewFeature);
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        Object spaceId = context.get(SPACE_ID_KEY);
        return spaceId instanceof Long && this.delegatedViewFeature.isEnabled() && this.permissionChecker.hasResourceAuditViewPermission(SPACE_RESOURCE_TYPE, String.valueOf(spaceId));
    }
}

