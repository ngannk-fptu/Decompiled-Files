/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.links.Link
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.importexport.ExportLinkFormatter;
import com.atlassian.confluence.links.linktypes.AttachmentLink;
import com.atlassian.confluence.links.linktypes.PageLink;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.renderer.links.Link;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class HtmlExportLinkFormatter
implements ExportLinkFormatter {
    private static final HtmlExportLinkFormatter INSTANCE = new HtmlExportLinkFormatter();

    private HtmlExportLinkFormatter() {
    }

    public static HtmlExportLinkFormatter getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isFormatSupported(Link link) {
        return link instanceof AttachmentLink || link instanceof PageLink;
    }

    @Override
    public String format(Link link, PageContext context) {
        if (link instanceof AttachmentLink) {
            return this.format((AttachmentLink)link);
        }
        return this.format((PageLink)link, context);
    }

    private String format(AttachmentLink link) {
        Attachment attachment = Objects.requireNonNull(link.getAttachment());
        return attachment.getExportPath();
    }

    private String format(PageLink link, PageContext context) {
        StringBuilder buffer = new StringBuilder();
        String anchor = link.getAnchor(context);
        if (!link.isOnSamePage(context)) {
            ContentEntityObject destinationContent = Objects.requireNonNull(link.getDestinationContent());
            String pageTitle = destinationContent.getTitle();
            if (GeneralUtil.isSafeTitleForUrl(pageTitle)) {
                buffer.append(pageTitle);
            } else {
                buffer.append(destinationContent.getId());
            }
            buffer.append(".html");
        }
        if (StringUtils.isNotEmpty((CharSequence)anchor)) {
            buffer.append("#").append(anchor);
        }
        return buffer.toString();
    }
}

