/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.impl.ConfluenceTempDirExportFileNameGenerator
 *  com.atlassian.confluence.importexport.impl.ExportFileNameGenerator
 *  com.atlassian.confluence.setup.settings.ConfluenceDirectories
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.util;

import com.atlassian.confluence.importexport.impl.ConfluenceTempDirExportFileNameGenerator;
import com.atlassian.confluence.importexport.impl.ExportFileNameGenerator;
import com.atlassian.confluence.setup.settings.ConfluenceDirectories;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.File;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class PdfExporterTempDirFileNameGenerator
implements ExportFileNameGenerator {
    private final ExportFileNameGenerator delegate;

    public PdfExporterTempDirFileNameGenerator(@ComponentImport ConfluenceDirectories confluenceDirectories) {
        this.delegate = ConfluenceTempDirExportFileNameGenerator.create((ConfluenceDirectories)confluenceDirectories, (String)"pdfexport", (String)"pdf", (String)"{0,date,yyyyMMdd}", (String)"{1,time,ddMMyy-HHmm}");
    }

    public File createExportDirectory() throws IOException {
        return this.delegate.createExportDirectory();
    }

    public String getExportFileName(String ... strings) {
        return this.delegate.getExportFileName(strings);
    }
}

