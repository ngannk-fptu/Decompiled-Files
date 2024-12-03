/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 *  org.hibernate.Session
 */
package com.atlassian.confluence.importexport.xmlimport;

import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.ImportProcessorSummary;
import com.atlassian.confluence.importexport.ImportedObjectPreProcessor;
import com.atlassian.confluence.importexport.xmlimport.InputStreamFactory;
import com.atlassian.core.util.ProgressMeter;
import org.hibernate.Session;

@Deprecated
public interface XmlImporter {
    public ImportProcessorSummary doImport(Session var1, InputStreamFactory var2, ProgressMeter var3, boolean var4, ImportedObjectPreProcessor var5) throws ImportExportException;

    public ImportProcessorSummary doImport(Session var1, InputStreamFactory var2, boolean var3, ImportContext var4) throws ImportExportException;
}

