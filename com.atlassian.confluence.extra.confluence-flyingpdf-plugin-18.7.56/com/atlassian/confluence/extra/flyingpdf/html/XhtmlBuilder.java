/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.ImportExportException
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.ContentTree
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.confluence.extra.flyingpdf.html;

import com.atlassian.confluence.extra.flyingpdf.PdfExportProgressMonitor;
import com.atlassian.confluence.extra.flyingpdf.html.DecorationPolicy;
import com.atlassian.confluence.extra.flyingpdf.html.LinkRenderingDetails;
import com.atlassian.confluence.extra.flyingpdf.html.TocBuilder;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.spaces.Space;
import org.w3c.dom.Document;

public interface XhtmlBuilder {
    public Document buildHtml(ContentTree var1, Space var2, LinkRenderingDetails var3, DecorationPolicy var4) throws ImportExportException;

    public Document buildHtml(ContentTree var1, Space var2, LinkRenderingDetails var3, DecorationPolicy var4, PdfExportProgressMonitor var5) throws ImportExportException;

    public Document buildHtml(BlogPost var1) throws ImportExportException;

    public Document generateTableOfContents(String var1, Space var2, TocBuilder var3) throws ImportExportException;
}

