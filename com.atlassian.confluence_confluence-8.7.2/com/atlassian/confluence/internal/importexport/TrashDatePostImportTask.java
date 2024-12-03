/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.importexport;

import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.PostImportTask;
import com.atlassian.confluence.internal.pages.TrashManagerInternal;
import java.time.Instant;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrashDatePostImportTask
implements PostImportTask {
    private static final Logger log = LoggerFactory.getLogger(TrashDatePostImportTask.class);
    private final TrashManagerInternal trashManagerInternal;

    public TrashDatePostImportTask(TrashManagerInternal trashManagerInternal) {
        this.trashManagerInternal = Objects.requireNonNull(trashManagerInternal);
    }

    @Override
    public void execute(ImportContext context) throws ImportExportException {
        String spaceKey = context.getSpaceKeyOfSpaceImport();
        Instant importTime = Instant.now();
        if (spaceKey == null) {
            log.info("Setting {} as default trash date", (Object)importTime);
            this.trashManagerInternal.migrateTrashDate(importTime);
        } else {
            log.info("Setting {} as default trash date for space {}", (Object)importTime, (Object)spaceKey);
            this.trashManagerInternal.migrateTrashDate(spaceKey, importTime);
        }
    }
}

