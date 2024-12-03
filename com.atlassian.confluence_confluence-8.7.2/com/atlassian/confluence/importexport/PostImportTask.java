/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport;

import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.confluence.importexport.ImportExportException;

@Deprecated
public interface PostImportTask {
    public void execute(ImportContext var1) throws ImportExportException;
}

