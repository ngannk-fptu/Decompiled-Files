/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.UpgradeError
 *  com.atlassian.confluence.upgrade.UpgradeException
 *  com.atlassian.confluence.upgrade.UpgradeFinalizationManager
 *  com.atlassian.confluence.upgrade.UpgradeTask
 *  com.atlassian.spring.container.ContainerContext
 *  com.atlassian.spring.container.ContainerManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.security.core.session.SessionRegistry
 */
package com.atlassian.confluence.impl.cluster;

import com.atlassian.confluence.cluster.NodeZduInfo;
import com.atlassian.confluence.cluster.UpgradeFinalizationRun;
import com.atlassian.confluence.impl.cluster.NodeZduInfoImpl;
import com.atlassian.confluence.impl.cluster.UpgradeFinalizationRunImpl;
import com.atlassian.confluence.internal.longrunning.LongRunningTaskManagerInternal;
import com.atlassian.confluence.server.ApplicationState;
import com.atlassian.confluence.server.ApplicationStatusService;
import com.atlassian.confluence.server.DefaultApplicationStatusService;
import com.atlassian.confluence.upgrade.UpgradeError;
import com.atlassian.confluence.upgrade.UpgradeException;
import com.atlassian.confluence.upgrade.UpgradeFinalizationManager;
import com.atlassian.confluence.upgrade.UpgradeTask;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.spring.container.ContainerContext;
import com.atlassian.spring.container.ContainerManager;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.session.SessionRegistry;

public class CollectNodeZduInfo
implements Callable<NodeZduInfo>,
Serializable {
    private static final long serialVersionUID = -1023038254781166903L;
    private static final Logger logger = LoggerFactory.getLogger(CollectNodeZduInfo.class);

    @Override
    public NodeZduInfo call() throws Exception {
        ApplicationState state;
        String buildNumber = GeneralUtil.getBuildNumber();
        String version = GeneralUtil.getVersionNumber();
        int longRunningTasksCount = 0;
        int activeUserCount = 0;
        boolean isPendingFinalization = false;
        UpgradeFinalizationRun finalizationRun = null;
        if (ContainerManager.isContainerSetup()) {
            ContainerContext context = ContainerManager.getInstance().getContainerContext();
            UpgradeFinalizationManager finalizationManager = (UpgradeFinalizationManager)context.getComponent((Object)"upgradeFinalizationManager");
            state = this.getState(context);
            longRunningTasksCount = this.getLongRunningTasksCount(context);
            activeUserCount = this.getActiveUserCount(context);
            isPendingFinalization = finalizationManager.isPendingLocalFinalization();
            finalizationRun = this.getFinalizationRun(finalizationManager).orElse(null);
        } else {
            logger.info("Unable to obtain node status as container context is not yet set up");
            state = DefaultApplicationStatusService.isError() ? ApplicationState.ERROR : ApplicationState.STARTING;
        }
        return new NodeZduInfoImpl(state, buildNumber, version, longRunningTasksCount, activeUserCount, isPendingFinalization, finalizationRun);
    }

    private ApplicationState getState(ContainerContext context) {
        ApplicationStatusService statusService = (ApplicationStatusService)context.getComponent((Object)"applicationStatusService");
        return statusService != null ? statusService.getState() : ApplicationState.STARTING;
    }

    private int getLongRunningTasksCount(ContainerContext context) {
        LongRunningTaskManagerInternal longRunningTaskManager = (LongRunningTaskManagerInternal)context.getComponent((Object)"longRunningTaskManager");
        return longRunningTaskManager != null ? longRunningTaskManager.getTaskCount() : 0;
    }

    private int getActiveUserCount(ContainerContext context) {
        try {
            SessionRegistry sessionRegistry = (SessionRegistry)context.getComponent((Object)"sessionRegistry");
            return (int)sessionRegistry.getAllPrincipals().stream().filter(p -> !sessionRegistry.getAllSessions(p, false).isEmpty()).count();
        }
        catch (Exception t) {
            logger.warn("Failed to get active user count", (Throwable)t);
            return 0;
        }
    }

    private Optional<UpgradeFinalizationRun> getFinalizationRun(UpgradeFinalizationManager finalizationManager) {
        return finalizationManager.getLastRun().map(r -> new UpgradeFinalizationRunImpl(r.getRequestTimestamp(), r.completedTimestamp(), r.isDatabaseUpgrade(), this.transformToFinalizationTaskError(r.getException(), r.getLastTask())));
    }

    private List<UpgradeFinalizationRun.Error> transformToFinalizationTaskError(UpgradeException exception, UpgradeTask task) {
        if (exception == null) {
            return Collections.emptyList();
        }
        String taskName = null;
        String buildNumber = null;
        boolean databaseUpgrade = false;
        if (task != null) {
            taskName = task.getName();
            buildNumber = task.getBuildNumber();
            databaseUpgrade = task.isDatabaseUpgrade();
        }
        return Collections.singletonList(new UpgradeFinalizationRunImpl.ErrorImpl(taskName, buildNumber, databaseUpgrade, exception.getMessage(), exception.getUpgradeErrors().stream().map(UpgradeError::getMessage).collect(Collectors.toList())));
    }
}

