/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.plugin.PluginParseException
 */
package com.atlassian.confluence.plugins.createcontent.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.plugins.createcontent.SpaceUtils;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.PluginParseException;
import java.util.Map;

public class CreateContentCondition
extends BaseConfluenceCondition {
    private final SpaceManager spaceManager;
    private final SpacePermissionManager spacePermissionManager;
    private String contentType;

    public CreateContentCondition(SpaceManager spaceManager, SpacePermissionManager spacePermissionManager) {
        this.spaceManager = spaceManager;
        this.spacePermissionManager = spacePermissionManager;
    }

    public void init(Map<String, String> params) throws PluginParseException {
        this.contentType = params.get("contentType");
    }

    public boolean shouldDisplay(WebInterfaceContext context) {
        if (this.contentType != null && this.contentType.equals("page")) {
            return SpaceUtils.hasCreatePagePermission(context.getCurrentUser(), context.getSpace(), this.spaceManager, this.spacePermissionManager);
        }
        return SpaceUtils.hasEditableSpaces(context.getCurrentUser(), this.spaceManager);
    }
}

