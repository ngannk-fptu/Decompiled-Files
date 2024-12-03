/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.ImportExportException
 */
package com.atlassian.confluence.extra.flyingpdf;

import com.atlassian.confluence.extra.flyingpdf.PdfExportProgressMonitor;
import com.atlassian.confluence.importexport.ImportExportException;
import java.io.File;
import org.w3c.dom.Document;

public interface XmlToPdfConverter {
    public File convertXhtmlToPdf(String var1, Document var2, String var3) throws ImportExportException;

    public File convertXhtmlToPdf(String var1, Document var2, PdfExportProgressMonitor var3, String var4) throws ImportExportException;
}

