/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 */
package com.atlassian.confluence.importexport;

import com.atlassian.confluence.importexport.ExportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.core.util.ProgressMeter;

@Deprecated
public abstract class Exporter {
    protected ExportContext context;

    public void setContext(ExportContext context) {
        this.context = context;
    }

    public ExportContext getContext() {
        return this.context;
    }

    public abstract String doExport(ProgressMeter var1) throws ImportExportException;
}

