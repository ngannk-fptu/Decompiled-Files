/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.index.status.importexport;

import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.PostImportTask;
import com.atlassian.confluence.internal.index.status.ReIndexJobPersister;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClearReIndexJobPostImportTask
implements PostImportTask {
    private static final Logger log = LoggerFactory.getLogger(ClearReIndexJobPostImportTask.class);
    private final ReIndexJobPersister persister;

    public ClearReIndexJobPostImportTask(ReIndexJobPersister persister) {
        this.persister = Objects.requireNonNull(persister);
    }

    @Override
    public void execute(ImportContext context) throws ImportExportException {
        log.info("Deleting re-index history of previous instance");
        this.persister.clear();
    }
}

