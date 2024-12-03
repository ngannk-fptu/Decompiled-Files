/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.ExportLinkFormatter
 *  com.atlassian.confluence.links.linktypes.PageLink
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.renderer.links.Link
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.flyingpdf.html;

import com.atlassian.confluence.importexport.ExportLinkFormatter;
import com.atlassian.confluence.links.linktypes.PageLink;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.renderer.links.Link;
import java.text.FieldPosition;
import java.text.MessageFormat;
import org.apache.commons.lang3.StringUtils;

public class PdfExportLinkFormatter
implements ExportLinkFormatter {
    private static final MessageFormat PAGE_FORMAT = new MessageFormat("#{0}");
    private static final PdfExportLinkFormatter INSTANCE = new PdfExportLinkFormatter();

    private PdfExportLinkFormatter() {
    }

    public static PdfExportLinkFormatter getInstance() {
        return INSTANCE;
    }

    public boolean isFormatSupported(Link link) {
        return link instanceof PageLink;
    }

    public String format(Link link, PageContext context) {
        PageLink pageLink = (PageLink)link;
        String anchor = pageLink.getAnchor(context);
        if (StringUtils.isNotBlank((CharSequence)anchor)) {
            return PAGE_FORMAT.format(new Object[]{anchor}, new StringBuffer(), (FieldPosition)null).toString();
        }
        String pageTitle = pageLink.getDestinationContent().getTitle();
        return PAGE_FORMAT.format(new Object[]{pageTitle}, new StringBuffer(), (FieldPosition)null).toString();
    }
}

