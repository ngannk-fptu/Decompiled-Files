/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.TextUtils
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.renderer.v2;

import com.atlassian.renderer.Icon;
import com.atlassian.renderer.IconManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.RenderContextHelper;
import com.atlassian.renderer.RendererConfiguration;
import com.atlassian.renderer.escaper.RenderEscapers;
import com.atlassian.renderer.links.Link;
import com.atlassian.renderer.links.LinkRenderer;
import com.atlassian.renderer.links.UnpermittedLink;
import com.atlassian.renderer.links.UnresolvedLink;
import com.atlassian.renderer.links.UrlLink;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import com.atlassian.renderer.wysiwyg.WysiwygLinkHelper;
import com.opensymphony.util.TextUtils;
import org.apache.commons.lang.StringUtils;

public class V2LinkRenderer
implements LinkRenderer {
    SubRenderer subRenderer;
    IconManager iconManager;
    RendererConfiguration rendererConfiguration;

    public V2LinkRenderer() {
    }

    public V2LinkRenderer(SubRenderer subRenderer, IconManager iconManager, RendererConfiguration rendererConfiguration) {
        this.subRenderer = subRenderer;
        this.iconManager = iconManager;
        this.rendererConfiguration = rendererConfiguration;
    }

    @Override
    public String renderLink(Link link, RenderContext renderContext) {
        StringBuffer buffer = new StringBuffer();
        if ((link instanceof UnresolvedLink || link instanceof UnpermittedLink) && !renderContext.isRenderingForWysiwyg()) {
            buffer.append("<strike>").append(link.getLinkBody()).append("</strike>");
            return buffer.toString();
        }
        Icon icon = this.iconManager.getLinkDecoration(link.getIconName());
        if (icon.position != 0) {
            buffer.append("<span class=\"nobr\">");
        }
        buffer.append("<a href=\"");
        if (link.isRelativeUrl() && renderContext.getSiteRoot() != null) {
            buffer.append(renderContext.getSiteRoot());
        }
        String linkUrl = RenderContextHelper.storeEscaperAndCreateTokensIfNeeded(link.getUrl(), renderContext, RenderEscapers.LINK_RENDERER_ESCAPER);
        buffer.append(this.unescapeEscapeSequences(HtmlEscaper.escapeAmpersands(linkUrl, true)));
        buffer.append("\"");
        if (renderContext.isRenderingForWysiwyg()) {
            buffer.append("\n");
        }
        if (StringUtils.isNotEmpty((String)link.getTitle())) {
            String title = TextUtils.htmlEncode((String)this.getLinkTitle(link));
            title = RenderContextHelper.storeEscaperAndCreateTokensIfNeeded(title, renderContext, RenderEscapers.ATTRIBUTE_RENDERER_ESCAPER);
            buffer.append(" title=\"").append(title).append("\"");
        }
        buffer.append(link.getLinkAttributes());
        if (link instanceof UrlLink && this.rendererConfiguration.isNofollowExternalLinks()) {
            buffer.append(" rel=\"nofollow\"");
        }
        if (renderContext.isRenderingForWysiwyg()) {
            buffer.append(WysiwygLinkHelper.getLinkInfoAttributes(link));
        }
        buffer.append(">");
        if (icon.position == -1) {
            buffer.append(icon.toHtml(renderContext.getImagePath()));
        }
        RenderMode linkBodyRenderMode = link.getLinkBody().equals(link.getUrl()) || link.getLinkBody().equals(link.getOriginalLinkText()) ? RenderMode.allow(4224L) : RenderMode.PHRASES_IMAGES;
        String linkBody = RenderContextHelper.storeEscaperAndCreateTokensIfNeeded(link.getLinkBody(), renderContext, RenderEscapers.LINK_TEXT_RENDERER_ESCAPER);
        linkBody = this.subRenderer.render(linkBody, renderContext, linkBodyRenderMode);
        buffer.append(linkBody);
        if (icon.position == 1) {
            buffer.append(icon.toHtml(renderContext.getImagePath()));
        }
        buffer.append("</a>");
        if (icon.position != 0) {
            buffer.append("</span>");
        }
        return renderContext.getEscaper().escape(buffer.toString(), renderContext.getCharacterEncoding());
    }

    protected String getLinkTitle(Link link) {
        return link.getTitle();
    }

    private String unescapeEscapeSequences(String s) {
        if (s == null) {
            return "";
        }
        StringBuffer result = new StringBuffer(s.length());
        char[] chars = s.toCharArray();
        int prev = 0;
        for (int n : chars) {
            if (n != 92 || prev == 92) {
                result.append((char)n);
            }
            prev = n;
        }
        return result.toString();
    }

    public void setSubRenderer(SubRenderer subRenderer) {
        this.subRenderer = subRenderer;
    }

    public void setIconManager(IconManager iconManager) {
        this.iconManager = iconManager;
    }

    public void setRendererConfiguration(RendererConfiguration rendererConfiguration) {
        this.rendererConfiguration = rendererConfiguration;
    }
}

