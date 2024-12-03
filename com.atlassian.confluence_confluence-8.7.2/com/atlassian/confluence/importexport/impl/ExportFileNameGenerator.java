/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.ThreadSafe
 */
package com.atlassian.confluence.importexport.impl;

import java.io.File;
import java.io.IOException;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface ExportFileNameGenerator {
    public File createExportDirectory() throws IOException;

    public String getExportFileName(String ... var1);

    default public File getExportFile(String ... differentiators) throws IOException {
        String filename;
        File exportDir = this.createExportDirectory();
        File exportFile = new File(exportDir, filename = this.getExportFileName(differentiators));
        if (!exportFile.getCanonicalFile().getParentFile().equals(exportDir)) {
            exportFile = new File(exportDir, exportFile.getName());
        }
        return exportFile;
    }
}

