/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.longrunning.LongRunningTaskId
 *  com.atlassian.confluence.util.longrunning.LongRunningTaskManager
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.tasks;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.plugins.synchrony.tasks.AbstractConfigLongRunningTask;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.confluence.util.longrunning.LongRunningTaskManager;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.io.Serializable;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SynchronyConfigTaskTracker {
    private static final Logger log = LoggerFactory.getLogger(SynchronyConfigTaskTracker.class);
    static final BandanaContext BANDANA_CONTEXT = new ConfluenceBandanaContext("com.atlassian.confluence.plugins.synchrony.tasks");
    static final String BANDANA_KEY = "taskTrackingMetadata";
    private final LongRunningTaskManager taskManager;
    private final BandanaManager bandanaManager;

    @Autowired
    public SynchronyConfigTaskTracker(@ComponentImport BandanaManager bandanaManager, @ComponentImport LongRunningTaskManager taskManager) {
        this.bandanaManager = bandanaManager;
        this.taskManager = taskManager;
    }

    public @Nullable String getTaskId() {
        return this.getCachedMetadata().map(SynchronyTaskMetadata::getTaskId).orElse(null);
    }

    public @Nullable String getTaskName() {
        return this.getCachedMetadata().map(SynchronyTaskMetadata::getTaskName).orElse(null);
    }

    private Optional<SynchronyTaskMetadata> getCachedMetadata() {
        try {
            return Optional.ofNullable((SynchronyTaskMetadata)this.bandanaManager.getValue(BANDANA_CONTEXT, BANDANA_KEY));
        }
        catch (RuntimeException ex) {
            log.error("Failed to get status of Synchrony task cache", (Throwable)ex);
            return Optional.empty();
        }
    }

    public LongRunningTaskId startTask(AbstractConfigLongRunningTask task) {
        LongRunningTaskId taskId = this.taskManager.startLongRunningTask((User)AuthenticatedUserThreadLocal.get(), (LongRunningTask)task);
        SynchronyTaskMetadata taskMetadata = new SynchronyTaskMetadata(taskId.asLongTaskId().serialise(), task.getName());
        try {
            this.bandanaManager.setValue(BANDANA_CONTEXT, BANDANA_KEY, (Object)taskMetadata);
        }
        catch (RuntimeException ex) {
            log.error("Failed to get status of Synchrony task cache", (Throwable)ex);
        }
        return taskId;
    }

    public void taskDone() {
        this.bandanaManager.removeValue(BANDANA_CONTEXT, BANDANA_KEY);
    }

    static class SynchronyTaskMetadata
    implements Serializable {
        private static final long serialVersionUID = 8327482342734829L;
        private final String taskId;
        private final String taskName;

        SynchronyTaskMetadata(String taskId, String taskName) {
            this.taskId = taskId;
            this.taskName = taskName;
        }

        String getTaskId() {
            return this.taskId;
        }

        String getTaskName() {
            return this.taskName;
        }
    }
}

