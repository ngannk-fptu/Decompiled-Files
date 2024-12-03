/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.web.UrlBuilder
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.velocity.htmlsafe.HtmlFragment
 *  com.atlassian.xwork.XsrfTokenGenerator
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.plugins.createcontent.contextproviders;

import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.velocity.htmlsafe.HtmlFragment;
import com.atlassian.xwork.XsrfTokenGenerator;
import java.util.Map;
import org.apache.struts2.ServletActionContext;

public class QuickCreateUrlContextProvider
implements ContextProvider {
    private final ContextPathHolder contextPathHolder;
    private final XsrfTokenGenerator simpleXsrfTokenGenerator;

    public QuickCreateUrlContextProvider(ContextPathHolder contextPathHolder, @ComponentImport XsrfTokenGenerator simpleXsrfTokenGenerator) {
        this.contextPathHolder = contextPathHolder;
        this.simpleXsrfTokenGenerator = simpleXsrfTokenGenerator;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        UrlBuilder urlBuilder;
        Object renderingQuickURL = context.get("renderingQuickURL");
        if (renderingQuickURL == null || !Boolean.parseBoolean(renderingQuickURL.toString())) {
            return context;
        }
        Space space = (Space)context.get("space");
        if (space != null) {
            urlBuilder = new UrlBuilder(this.contextPathHolder.getContextPath() + "/pages/createpage.action");
            AbstractPage page = (AbstractPage)context.get("page");
            urlBuilder.add("spaceKey", space.getKey());
            urlBuilder.add("atl_token", this.getAtlToken());
            if (page != null) {
                urlBuilder.add("fromPageId", page.getId());
            }
        } else {
            urlBuilder = new UrlBuilder(this.contextPathHolder.getContextPath() + "/plugins/createcontent/createpage-defaultspace.action");
        }
        urlBuilder.add("src", "quick-create");
        context.put("quickUrlHtml", new HtmlFragment((Object)urlBuilder.toUrl()));
        return context;
    }

    private String getAtlToken() {
        String token = this.simpleXsrfTokenGenerator.getToken(ServletActionContext.getRequest(), true);
        return token;
    }
}

