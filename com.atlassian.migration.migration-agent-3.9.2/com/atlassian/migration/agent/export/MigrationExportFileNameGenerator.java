/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.impl.ExportFileNameGenerator
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.export;

import com.atlassian.confluence.importexport.impl.ExportFileNameGenerator;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.ExportDirManager;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;

class MigrationExportFileNameGenerator
implements ExportFileNameGenerator {
    private static final Logger log = ContextLoggerFactory.getLogger(MigrationExportFileNameGenerator.class);
    private final ExportDirManager exportDirManager;
    private final String fileId;

    MigrationExportFileNameGenerator(ExportDirManager exportDirManager, String fileId) {
        this.exportDirManager = exportDirManager;
        this.fileId = fileId;
    }

    public File createExportDirectory() {
        File exportDir = this.exportDirManager.getExportFilePath(UUID.randomUUID().toString()).toFile();
        boolean dirHasMade = exportDir.mkdirs();
        log.debug("Tried to created Export directory, result = {}", (Object)dirHasMade);
        return exportDir;
    }

    public String getExportFileName(String ... differentiators) {
        return this.fileId;
    }

    public File getExportFile(String ... differentiators) throws IOException {
        return new File(this.createExportDirectory(), this.getExportFileName(differentiators));
    }
}

