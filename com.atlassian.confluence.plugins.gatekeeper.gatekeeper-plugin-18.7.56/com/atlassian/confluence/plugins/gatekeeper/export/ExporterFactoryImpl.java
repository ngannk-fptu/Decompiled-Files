/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.gatekeeper.export;

import com.atlassian.confluence.plugins.gatekeeper.export.CsvExporter;
import com.atlassian.confluence.plugins.gatekeeper.export.Exporter;
import com.atlassian.confluence.plugins.gatekeeper.export.ExporterFactory;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.ExportSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExporterFactoryImpl
implements ExporterFactory {
    private static final Logger logger = LoggerFactory.getLogger(ExporterFactoryImpl.class);

    @Override
    public Exporter createExporter(ExportSettings exportSettings) {
        String exportFormat = exportSettings.getExportFormat();
        logger.debug("EXPORT FORMAT: " + exportFormat);
        if ("csv".equals(exportFormat)) {
            return new CsvExporter(exportSettings);
        }
        throw new UnsupportedOperationException();
    }
}

