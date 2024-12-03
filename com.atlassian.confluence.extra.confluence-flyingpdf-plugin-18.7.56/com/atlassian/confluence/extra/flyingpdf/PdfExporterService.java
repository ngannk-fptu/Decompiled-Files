/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.ImportExportException
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.ContentTree
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.extra.flyingpdf;

import com.atlassian.confluence.extra.flyingpdf.PdfExportProgressMonitor;
import com.atlassian.confluence.extra.flyingpdf.analytic.PageExportMetrics;
import com.atlassian.confluence.extra.flyingpdf.analytic.SpaceExportMetrics;
import com.atlassian.confluence.extra.flyingpdf.html.DecorationPolicy;
import com.atlassian.confluence.extra.flyingpdf.impl.DefaultProgressMonitor;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.user.User;
import java.io.File;

public interface PdfExporterService {
    default public File createPdfForSpace(User user, Space space, ContentTree contentTree, String contextPath, SpaceExportMetrics spaceExportMetrics) throws ImportExportException {
        return this.createPdfForSpace(user, space, contentTree, new DefaultProgressMonitor(), contextPath, spaceExportMetrics, DecorationPolicy.none());
    }

    default public File createPdfForSpace(User user, Space space, ContentTree contentTree, String contextPath) throws ImportExportException {
        return this.createPdfForSpace(user, space, contentTree, new DefaultProgressMonitor(), contextPath, new SpaceExportMetrics(), DecorationPolicy.none());
    }

    public File createPdfForSpace(User var1, Space var2, ContentTree var3, PdfExportProgressMonitor var4, String var5, SpaceExportMetrics var6, DecorationPolicy var7) throws ImportExportException;

    public File createPdfForPage(User var1, AbstractPage var2, String var3, PageExportMetrics var4) throws ImportExportException;

    default public File createPdfForPage(User user, AbstractPage page, String contextPath) throws ImportExportException {
        return this.createPdfForPage(user, page, contextPath, new PageExportMetrics());
    }

    public ContentTree getContentTree(User var1, Space var2);

    public boolean isPermitted(User var1, AbstractPage var2);

    public boolean isPermitted(User var1, Space var2);

    public boolean exportableContentExists(Space var1);

    public PdfExportProgressMonitor createProgressMonitor(ProgressMeter var1);
}

