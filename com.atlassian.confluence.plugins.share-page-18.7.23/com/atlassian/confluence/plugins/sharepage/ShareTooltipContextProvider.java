/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.confluence.plugins.sharepage;

import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.Map;

public class ShareTooltipContextProvider
implements ContextProvider {
    private final I18nResolver i18nResolver;

    public ShareTooltipContextProvider(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    public void init(Map<String, String> map) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        if (context.get("page") == null) {
            return context;
        }
        String contentType = context.get("page").getClass().equals(BlogPost.class) ? "blogpost" : "page";
        String tooltip = this.i18nResolver.getText("share.button.tooltip2." + contentType);
        context.put("tooltip", tooltip);
        return context;
    }
}

