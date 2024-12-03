/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.renderer.RenderContext;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import org.apache.struts2.ServletActionContext;

@RequiresAnyConfluenceAccess
public class ViewSpaceAction
extends AbstractSpaceAction {
    public Page getPage() {
        return this.getSpace().getHomePage();
    }

    @HtmlSafe
    public String getPageXHtmlContent(Page page) {
        if (page == null) {
            return "";
        }
        return this.wikiStyleRenderer.convertWikiToXHtml((RenderContext)this.getPage().toPageContext(), this.getPage().getBodyContent().getBody());
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        if (this.getSpace() == null) {
            ServletActionContext.getResponse().sendError(404);
            return "success";
        }
        if (this.getSpace().getHomePage() == null) {
            return this.hasSpaceIA() ? "collector" : "browse";
        }
        return "homepage";
    }
}

