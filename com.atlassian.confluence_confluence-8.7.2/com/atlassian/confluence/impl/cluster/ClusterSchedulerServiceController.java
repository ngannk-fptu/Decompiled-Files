/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.core.LifecycleAwareSchedulerService$State
 *  com.atlassian.scheduler.core.RunningJob
 *  com.atlassian.scheduler.core.SchedulerServiceController
 *  com.atlassian.spring.container.ContainerManager
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.impl.cluster;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeExecution;
import com.atlassian.confluence.impl.cluster.SchedulerServiceControllerException;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.core.LifecycleAwareSchedulerService;
import com.atlassian.scheduler.core.RunningJob;
import com.atlassian.scheduler.core.SchedulerServiceController;
import com.atlassian.spring.container.ContainerManager;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

class ClusterSchedulerServiceController
implements SchedulerServiceController {
    static final String SCHEDULER_SERVICE_CONTROLLER = "scheduler-service-controller";
    private final ClusterManager clusterManager;

    public ClusterSchedulerServiceController(ClusterManager clusterManager) {
        this.clusterManager = Objects.requireNonNull(clusterManager);
    }

    public void start() throws SchedulerServiceException {
        List<ClusterNodeExecution<Boolean>> executions = this.clusterManager.submitToAllNodes(new StartTask(), SCHEDULER_SERVICE_CONTROLLER);
        try {
            ClusterSchedulerServiceController.waitForCompletion(executions);
        }
        catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new SchedulerServiceException("Starting scheduler service is interrupted", (Throwable)e);
        }
    }

    public void standby() throws SchedulerServiceException {
        List<ClusterNodeExecution<Boolean>> executions = this.clusterManager.submitToAllNodes(new StandbyTask(), SCHEDULER_SERVICE_CONTROLLER);
        try {
            ClusterSchedulerServiceController.waitForCompletion(executions);
        }
        catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new SchedulerServiceException("Pausing scheduler service is interrupted", (Throwable)e);
        }
    }

    public void shutdown() {
        List<ClusterNodeExecution<Boolean>> executions = this.clusterManager.submitToAllNodes(new ShutdownTask(), SCHEDULER_SERVICE_CONTROLLER);
        try {
            ClusterSchedulerServiceController.waitForCompletion(executions);
        }
        catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new SchedulerServiceControllerException(e);
        }
    }

    @Nonnull
    public Collection<RunningJob> getLocallyRunningJobs() {
        return ClusterSchedulerServiceController.getLocalServiceController().getLocallyRunningJobs();
    }

    public boolean waitUntilIdle(long timeout, TimeUnit timeUnit) throws InterruptedException {
        List<ClusterNodeExecution<Boolean>> executions = this.clusterManager.submitToAllNodes(new WaitUntilIdleTask(timeout, timeUnit), SCHEDULER_SERVICE_CONTROLLER);
        try {
            return ClusterSchedulerServiceController.waitForCompletion(executions);
        }
        catch (ExecutionException e) {
            throw new SchedulerServiceControllerException(e);
        }
    }

    @Nonnull
    public LifecycleAwareSchedulerService.State getState() {
        return ClusterSchedulerServiceController.getLocalServiceController().getState();
    }

    private static SchedulerServiceController getLocalServiceController() {
        return (SchedulerServiceController)ContainerManager.getInstance().getContainerContext().getComponent((Object)"schedulerService");
    }

    private static boolean waitForCompletion(List<ClusterNodeExecution<Boolean>> executions) throws ExecutionException, InterruptedException {
        boolean result = true;
        for (ClusterNodeExecution<Boolean> execution : executions) {
            result &= execution.getCompletionStage().toCompletableFuture().get().booleanValue();
        }
        return result;
    }

    static class WaitUntilIdleTask
    implements Callable<Boolean>,
    Serializable {
        private static final long serialVersionUID = 5749156671414528445L;
        private final long timeout;
        private final TimeUnit timeUnit;

        WaitUntilIdleTask(long timeout, TimeUnit timeUnit) {
            this.timeout = timeout;
            this.timeUnit = timeUnit;
        }

        @Override
        public Boolean call() {
            try {
                return ClusterSchedulerServiceController.getLocalServiceController().waitUntilIdle(this.timeout, this.timeUnit);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new SchedulerServiceControllerException(e);
            }
        }
    }

    static class ShutdownTask
    implements Callable<Boolean>,
    Serializable {
        private static final long serialVersionUID = -7830601487412920731L;

        ShutdownTask() {
        }

        @Override
        public Boolean call() {
            ClusterSchedulerServiceController.getLocalServiceController().shutdown();
            return true;
        }
    }

    static class StandbyTask
    implements Callable<Boolean>,
    Serializable {
        private static final long serialVersionUID = 4892933634050719496L;

        StandbyTask() {
        }

        @Override
        public Boolean call() {
            try {
                ClusterSchedulerServiceController.getLocalServiceController().standby();
            }
            catch (SchedulerServiceException e) {
                throw new SchedulerServiceControllerException(e);
            }
            return true;
        }
    }

    static class StartTask
    implements Callable<Boolean>,
    Serializable {
        private static final long serialVersionUID = 1814113090129656513L;

        StartTask() {
        }

        @Override
        public Boolean call() {
            try {
                ClusterSchedulerServiceController.getLocalServiceController().start();
            }
            catch (SchedulerServiceException e) {
                throw new SchedulerServiceControllerException(e);
            }
            return true;
        }
    }
}

