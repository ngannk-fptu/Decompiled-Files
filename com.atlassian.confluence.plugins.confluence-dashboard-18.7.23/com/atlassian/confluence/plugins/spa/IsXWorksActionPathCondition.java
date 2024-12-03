/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 *  com.atlassian.plugin.PluginParseException
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.plugins.spa;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.plugin.PluginParseException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class IsXWorksActionPathCondition
extends BaseConfluenceCondition {
    public void init(Map<String, String> params) throws PluginParseException {
    }

    protected boolean shouldDisplay(WebInterfaceContext context) {
        HttpServletRequest request = (HttpServletRequest)context.getParameter("request");
        return request.getServletPath() != null && request.getServletPath().endsWith(".action");
    }
}

