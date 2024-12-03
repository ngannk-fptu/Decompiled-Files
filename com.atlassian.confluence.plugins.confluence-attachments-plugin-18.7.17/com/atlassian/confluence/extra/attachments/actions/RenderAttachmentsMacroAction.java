/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.actions.PageAware
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.attachments.actions;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.actions.PageAware;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.ArrayList;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.lang3.StringUtils;

public class RenderAttachmentsMacroAction
extends ConfluenceActionSupport
implements PageAware {
    private AbstractPage page;
    private String sortBy;
    private String patterns;
    private String labels;
    private boolean old;
    @ComponentImport
    private XhtmlContent xhtmlContent;

    public AbstractPage getPage() {
        return this.page;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public void setPage(AbstractPage page) {
        this.page = page;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public void setPatterns(String patterns) {
        this.patterns = patterns;
    }

    public void setOld(boolean old) {
        this.old = old;
    }

    public void setXhtmlContent(XhtmlContent xhtmlContent) {
        this.xhtmlContent = xhtmlContent;
    }

    private String buildMacroMarkup() {
        StringBuilder macroMarkupBuilder = new StringBuilder("{attachments").append(":old=").append(this.old).append("|upload=false");
        if (null != this.sortBy) {
            macroMarkupBuilder.append("|sortBy=").append(this.sortBy);
        }
        if (null != this.patterns) {
            macroMarkupBuilder.append("|patterns=").append(this.patterns);
        }
        if (!StringUtils.isBlank((CharSequence)this.labels)) {
            macroMarkupBuilder.append("|labels=").append(this.labels);
        }
        macroMarkupBuilder.append("}");
        return macroMarkupBuilder.toString();
    }

    public String getRenderedMacroHtml() throws XMLStreamException, XhtmlException {
        return this.xhtmlContent.convertWikiToView(this.buildMacroMarkup(), (ConversionContext)new DefaultConversionContext((RenderContext)this.getPage().toPageContext()), new ArrayList());
    }

    public void validate() {
        super.validate();
        if (!this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.VIEW, (Object)this.getPage())) {
            this.addActionError(this.getText("not.permitted.description"));
        }
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    public boolean isPageRequired() {
        return true;
    }

    public boolean isLatestVersionRequired() {
        return true;
    }

    public boolean isViewPermissionRequired() {
        return true;
    }

    public Space getSpace() {
        if (this.page != null) {
            return this.page.getSpace();
        }
        return null;
    }
}

