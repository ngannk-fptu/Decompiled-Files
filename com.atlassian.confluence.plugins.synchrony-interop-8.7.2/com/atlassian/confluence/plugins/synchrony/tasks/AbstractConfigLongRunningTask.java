/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager
 *  com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.synchrony.tasks;

import com.atlassian.cache.CacheManager;
import com.atlassian.confluence.plugins.synchrony.api.CollaborativeEditingMode;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyMonitor;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyProcessManager;
import com.atlassian.confluence.plugins.synchrony.api.events.CollaborativeEditingModeChangeEvent;
import com.atlassian.confluence.plugins.synchrony.api.events.CollaborativeEditingOffEvent;
import com.atlassian.confluence.plugins.synchrony.api.events.CollaborativeEditingOnEvent;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.confluence.plugins.synchrony.service.CollaborativeEditingModeDuration;
import com.atlassian.confluence.plugins.synchrony.tasks.SynchronyConfigTaskTracker;
import com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask;
import com.atlassian.event.api.EventPublisher;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConfigLongRunningTask
extends ConfluenceAbstractLongRunningTask {
    protected static final Logger log = LoggerFactory.getLogger(AbstractConfigLongRunningTask.class);
    protected final SynchronyConfigurationManager configManager;
    protected final SynchronyProcessManager processManager;
    protected final SynchronyMonitor processMonitor;
    protected final CacheManager cacheManager;
    private final SynchronyConfigTaskTracker taskTracker;
    protected final EventPublisher eventPublisher;
    private final CollaborativeEditingModeDuration collaborativeEditingModeDuration;

    public AbstractConfigLongRunningTask(SynchronyConfigurationManager configManager, SynchronyProcessManager processManager, SynchronyMonitor processMonitor, CacheManager cacheManager, SynchronyConfigTaskTracker taskTracker, EventPublisher eventPublisher, CollaborativeEditingModeDuration collaborativeEditingModeDuration) {
        this.configManager = Objects.requireNonNull(configManager);
        this.processManager = Objects.requireNonNull(processManager);
        this.processMonitor = Objects.requireNonNull(processMonitor);
        this.cacheManager = Objects.requireNonNull(cacheManager);
        this.taskTracker = Objects.requireNonNull(taskTracker);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.collaborativeEditingModeDuration = Objects.requireNonNull(collaborativeEditingModeDuration);
    }

    protected abstract void execute() throws Exception;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void runInternal() {
        CollaborativeEditingMode previousMode = AbstractConfigLongRunningTask.getCollabEditingMode(this.configManager);
        boolean wasSynchronyUp = this.processMonitor.isSynchronyUp();
        long modeDurationInSeconds = this.collaborativeEditingModeDuration.currentModeDuration(TimeUnit.SECONDS);
        try {
            log.debug("Starting {} (Collaborative editing mode is currently: {})", (Object)this.getName(), (Object)previousMode);
            this.execute();
            this.progress.setCompletedSuccessfully(true);
        }
        catch (Exception e) {
            log.error("An error occurred when running a Synchrony ConfigLongRunningTask", (Throwable)e);
            this.progress.setCompletedSuccessfully(false);
        }
        finally {
            this.progress.setPercentage(100);
            this.taskTracker.taskDone();
            Optional<CollaborativeEditingModeChangeEvent> event = this.getModeChangeEvent(previousMode, wasSynchronyUp, modeDurationInSeconds);
            event.ifPresent(arg_0 -> ((EventPublisher)this.eventPublisher).publish(arg_0));
            this.storeModeChangeTimeWhenModeChanged(previousMode);
            log.debug("Finished task {}", (Object)this.getName());
        }
    }

    private void storeModeChangeTimeWhenModeChanged(CollaborativeEditingMode previousMode) {
        CollaborativeEditingMode newMode = AbstractConfigLongRunningTask.getCollabEditingMode(this.configManager);
        if (!Objects.equals((Object)previousMode, (Object)newMode)) {
            this.collaborativeEditingModeDuration.storeModeChangeTime();
        }
    }

    private Optional<CollaborativeEditingModeChangeEvent> getModeChangeEvent(CollaborativeEditingMode previousMode, boolean wasSynchronyUp, long modeDurationInSeconds) {
        CollaborativeEditingModeChangeEvent modeChangeEvent;
        CollaborativeEditingMode newMode = AbstractConfigLongRunningTask.getCollabEditingMode(this.configManager);
        if (Objects.equals((Object)previousMode, (Object)newMode)) {
            return Optional.empty();
        }
        if (CollaborativeEditingMode.ENABLED.equals((Object)newMode)) {
            modeChangeEvent = new CollaborativeEditingOnEvent(previousMode, wasSynchronyUp, modeDurationInSeconds);
        } else if (CollaborativeEditingMode.DISABLED.equals((Object)newMode)) {
            modeChangeEvent = new CollaborativeEditingOffEvent(previousMode, wasSynchronyUp, modeDurationInSeconds);
        } else {
            log.warn("{} mode is not handled. Not publishing event.", (Object)newMode);
            return Optional.empty();
        }
        return Optional.of(modeChangeEvent);
    }

    private static CollaborativeEditingMode getCollabEditingMode(SynchronyConfigurationManager configManager) {
        return CollaborativeEditingMode.fromStatus(configManager.isSharedDraftsEnabled());
    }
}

