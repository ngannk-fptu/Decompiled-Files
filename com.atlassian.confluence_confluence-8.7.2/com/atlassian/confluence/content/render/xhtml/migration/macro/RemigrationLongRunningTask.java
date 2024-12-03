/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 */
package com.atlassian.confluence.content.render.xhtml.migration.macro;

import com.atlassian.confluence.content.render.xhtml.migration.BatchTask;
import com.atlassian.confluence.content.render.xhtml.migration.BatchableWorkSource;
import com.atlassian.confluence.content.render.xhtml.migration.WorkSourceBatchRunner;
import com.atlassian.confluence.content.render.xhtml.migration.macro.MacroMigrationService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;

class RemigrationLongRunningTask
extends ConfluenceAbstractLongRunningTask {
    private final Logger log = LoggerFactory.getLogger(RemigrationLongRunningTask.class);
    private final WorkSourceBatchRunner<ContentEntityObject> batchRunner;
    private final BatchableWorkSource<ContentEntityObject> workSource;
    private final BatchTask<ContentEntityObject> batchTask;
    private final MacroMigrationService remigrationService;
    private static final int DEFAULT_NUM_THREADS = 4;
    private LongRunningTaskId taskid;

    RemigrationLongRunningTask(BatchableWorkSource<ContentEntityObject> workSource, PlatformTransactionManager platformTransactionManager, BatchTask<ContentEntityObject> batchTask, MacroMigrationService remigrationService) {
        this(workSource, batchTask, new WorkSourceBatchRunner<ContentEntityObject>("macro-migration", Integer.getInteger("remigration.threads", 4), platformTransactionManager), remigrationService);
    }

    RemigrationLongRunningTask(BatchableWorkSource<ContentEntityObject> workSource, BatchTask<ContentEntityObject> batchTask, WorkSourceBatchRunner<ContentEntityObject> batchRunner, MacroMigrationService service) {
        this.workSource = workSource;
        this.batchRunner = batchRunner;
        this.batchTask = batchTask;
        batchRunner.setProgressWrapper(this.progress);
        this.remigrationService = service;
    }

    public String getName() {
        return "Unmigrated wiki markup migration Task";
    }

    void setTaskId(LongRunningTaskId id) {
        this.taskid = id;
    }

    public LongRunningTaskId getTaskId() {
        return this.taskid;
    }

    @Override
    protected void runInternal() {
        try {
            List<Exception> exceptions = this.batchRunner.run(this.workSource, this.batchTask);
            this.remigrationService.setMigrationRequired(false);
            if (!exceptions.isEmpty()) {
                this.log.warn("{} exceptions occurred during the migration of unmigrated wiki markup, an administrator can rerun this task from Migrate Macros.\nYou might want to set the {} logger to debug level first.", (Object)exceptions.size(), (Object)RemigrationLongRunningTask.class.getName());
                if (this.log.isDebugEnabled()) {
                    StringWriter underlyingWriter = new StringWriter();
                    PrintWriter writer = new PrintWriter(underlyingWriter);
                    for (Exception ex : exceptions) {
                        ex.printStackTrace(writer);
                        writer.append("\n");
                    }
                    writer.flush();
                    this.log.debug("The following exceptions occurred during wiki markup migration:\n{}", (Object)underlyingWriter.toString());
                }
            }
        }
        catch (Exception ex) {
            this.log.error("An error occurred during the long running macro migration task", (Throwable)ex);
        }
    }
}

