/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.themes.GlobalHelper;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public abstract class BaseConfluenceCondition
implements Condition {
    public static final String CONTEXT_KEY_HELPER = "helper";

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public boolean shouldDisplay(Map context) {
        Object helperObject;
        DefaultWebInterfaceContext webInterfaceContext = DefaultWebInterfaceContext.createFrom(context);
        if (webInterfaceContext.hasParameter(CONTEXT_KEY_HELPER) && webInterfaceContext.getPage() == null && (helperObject = webInterfaceContext.getParameter(CONTEXT_KEY_HELPER)) instanceof GlobalHelper) {
            GlobalHelper helper = (GlobalHelper)helperObject;
            webInterfaceContext.setPage(helper.getPage());
            webInterfaceContext.setSpace(helper.getSpace());
            webInterfaceContext.setLabel(helper.getLabel());
        }
        return this.shouldDisplay(webInterfaceContext);
    }

    protected abstract boolean shouldDisplay(WebInterfaceContext var1);
}

