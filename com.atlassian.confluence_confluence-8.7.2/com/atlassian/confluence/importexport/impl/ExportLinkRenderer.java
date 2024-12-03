/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.Icon
 *  com.atlassian.renderer.IconManager
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.links.BaseLink
 *  com.atlassian.renderer.links.Link
 *  com.atlassian.renderer.links.LinkRenderer
 *  com.atlassian.renderer.links.UnpermittedLink
 *  com.atlassian.renderer.links.UnresolvedLink
 *  com.atlassian.renderer.util.UrlUtil
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.renderer.v2.SubRenderer
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.importexport.ExportContext;
import com.atlassian.confluence.importexport.ExportLinkFormatter;
import com.atlassian.confluence.links.linktypes.AbstractPageLink;
import com.atlassian.confluence.links.linktypes.AttachmentLink;
import com.atlassian.confluence.links.linktypes.PageCreateLink;
import com.atlassian.confluence.links.linktypes.PageLink;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.renderer.Icon;
import com.atlassian.renderer.IconManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.links.BaseLink;
import com.atlassian.renderer.links.Link;
import com.atlassian.renderer.links.LinkRenderer;
import com.atlassian.renderer.links.UnpermittedLink;
import com.atlassian.renderer.links.UnresolvedLink;
import com.atlassian.renderer.util.UrlUtil;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.SubRenderer;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public class ExportLinkRenderer
implements LinkRenderer {
    private ExportContext exportContext;
    private IconManager iconManager;
    private PageManager pageManager;
    private SubRenderer subRenderer;
    private ExportLinkFormatter exportLinkFormatter;

    public ExportLinkRenderer(SubRenderer subRenderer, ExportContext exportContext, IconManager iconManager, PageManager pageManager, ExportLinkFormatter exportLinkFormatter) {
        this.subRenderer = subRenderer;
        this.exportContext = exportContext;
        this.iconManager = iconManager;
        this.pageManager = pageManager;
        this.exportLinkFormatter = exportLinkFormatter;
    }

    public String renderLink(Link link) {
        return this.renderLink(link, new PageContext());
    }

    public String renderLink(Link link, RenderContext context) {
        if (!(context instanceof PageContext)) {
            return RenderUtils.error((RenderContext)context, (String)"[Unknown context for drawing a link]", null, (boolean)false);
        }
        PageContext pageContext = (PageContext)context;
        StringBuilder buffer = new StringBuilder();
        if (link instanceof UnresolvedLink || link instanceof UnpermittedLink || link instanceof PageCreateLink) {
            buffer.append(this.subRenderer.render(link.getLinkBody(), context, RenderMode.PHRASES_IMAGES));
            return buffer.toString();
        }
        buffer.append("<a href=\"");
        buffer.append(this.resolve(link, pageContext));
        buffer.append("\"");
        if (StringUtils.isNotEmpty((CharSequence)link.getTitle())) {
            buffer.append(" title=\"").append(link.getTitle()).append("\"");
        }
        buffer.append(">");
        Icon icon = this.iconManager.getLinkDecoration(link.getIconName());
        if (icon.position == -1) {
            icon.toHtml(pageContext.getImagePath());
        }
        if (link.getLinkBody().equals(link.getUrl())) {
            buffer.append(this.subRenderer.render(link.getLinkBody(), context, RenderMode.allow((long)4224L)));
        } else {
            buffer.append(this.subRenderer.render(link.getLinkBody(), context, RenderMode.PHRASES_IMAGES));
        }
        if (icon.position == 1) {
            icon.toHtml(pageContext.getImagePath());
        }
        buffer.append("</a>");
        return buffer.toString();
    }

    private String resolve(Link link, PageContext context) {
        if (link instanceof AttachmentLink) {
            return this.resolve((AttachmentLink)link, context);
        }
        if (link instanceof PageLink) {
            return this.resolve((PageLink)link, context);
        }
        StringBuilder buffer = new StringBuilder();
        if (link.isRelativeUrl()) {
            buffer.append(context.getBaseUrl());
        }
        buffer.append(UrlUtil.escapeSpecialCharacters((String)link.getUrl()));
        return buffer.toString();
    }

    private String resolve(AttachmentLink link, PageContext context) {
        if (this.exportLinkFormatter.isFormatSupported((Link)link) && this.isExportedPage(link.getAbstractPageLink())) {
            return this.exportLinkFormatter.format((Link)link, context);
        }
        return this.renderExternalLink(link, context);
    }

    private String resolve(PageLink link, PageContext context) {
        if (this.exportLinkFormatter.isFormatSupported((Link)link) && this.isExportedPage(link)) {
            return this.exportLinkFormatter.format((Link)link, context);
        }
        return this.renderExternalLink(link, context);
    }

    private boolean isExportedPage(AbstractPageLink link) {
        ContentEntityObject destination = link.getDestinationContent();
        if (!(destination instanceof Page)) {
            return false;
        }
        return this.exportContext.isPageInExport((Page)destination, this.pageManager);
    }

    private String renderExternalLink(BaseLink link, PageContext context) {
        StringBuilder externalLink = new StringBuilder();
        if (link.isRelativeUrl()) {
            externalLink.append(context.getBaseUrl());
        }
        externalLink.append(UrlUtil.escapeSpecialCharacters((String)link.getUrl()));
        return externalLink.toString();
    }
}

