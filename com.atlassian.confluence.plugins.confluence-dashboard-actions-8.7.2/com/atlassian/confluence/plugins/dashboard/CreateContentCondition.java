/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpaceType
 *  com.atlassian.confluence.spaces.SpacesQuery
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.dashboard;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.user.User;
import java.util.Map;

@Deprecated
public class CreateContentCondition
extends BaseConfluenceCondition {
    private SpaceManager spaceManager;
    private String contentType;

    public void init(Map<String, String> params) throws PluginParseException {
        this.contentType = params.get("content");
        if (!"page".equalsIgnoreCase(this.contentType) && !"blogpost".equalsIgnoreCase(this.contentType)) {
            throw new PluginParseException("Invalid 'content' parameter specified: '" + this.contentType + "'. Legal values are: 'page' and 'blogpost'.");
        }
        super.init(params);
    }

    public boolean shouldDisplay(WebInterfaceContext context) {
        return this.spaceManager.getSpaces(SpacesQuery.newQuery().forUser((User)context.getCurrentUser()).withSpaceType(SpaceType.GLOBAL).withPermission("page".equalsIgnoreCase(this.contentType) ? "EDITSPACE" : "EDITBLOG").build()).getAvailableSize() > 0;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }
}

