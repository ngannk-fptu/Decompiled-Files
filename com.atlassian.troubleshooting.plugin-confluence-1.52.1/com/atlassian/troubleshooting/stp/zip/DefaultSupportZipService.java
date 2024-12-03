/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.zip;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.troubleshooting.api.ClusterService;
import com.atlassian.troubleshooting.stp.events.SupportZipOptionsAwareEvent;
import com.atlassian.troubleshooting.stp.request.SupportTaskFactory;
import com.atlassian.troubleshooting.stp.security.PermissionValidationService;
import com.atlassian.troubleshooting.stp.task.MonitoredTaskExecutor;
import com.atlassian.troubleshooting.stp.task.MonitoredTaskExecutorFactory;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import com.atlassian.troubleshooting.stp.task.TaskType;
import com.atlassian.troubleshooting.stp.zip.ClusterMessagingException;
import com.atlassian.troubleshooting.stp.zip.ClusteredSupportZipService;
import com.atlassian.troubleshooting.stp.zip.ClusteredZipTaskStart;
import com.atlassian.troubleshooting.stp.zip.CreateSupportZipMonitor;
import com.atlassian.troubleshooting.stp.zip.CreateSupportZipTask;
import com.atlassian.troubleshooting.stp.zip.NotClusteredException;
import com.atlassian.troubleshooting.stp.zip.SupportZipRequest;
import com.atlassian.troubleshooting.stp.zip.SupportZipService;
import com.atlassian.troubleshooting.stp.zip.TaskNotFoundException;
import com.atlassian.util.concurrent.Lazy;
import com.atlassian.util.concurrent.Supplier;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultSupportZipService
implements SupportZipService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSupportZipService.class);
    private static final int SUPPORT_ZIP_MAX_THREADS = Integer.getInteger("troubleshooting.zip.tasks.max.threads", 1);
    private final ClusterService clusterService;
    private final ClusteredSupportZipService clusteredSupportZipService;
    private final EventPublisher eventPublisher;
    private final Supplier<MonitoredTaskExecutor<File, CreateSupportZipMonitor>> executor;
    private final PermissionValidationService permissionValidationService;
    private final SupportTaskFactory supportTaskFactory;

    @Autowired
    public DefaultSupportZipService(ClusterService clusterService, EventPublisher eventPublisher, MonitoredTaskExecutorFactory taskExecutorFactory, PermissionValidationService permissionValidationService, ClusteredSupportZipService clusteredSupportZipService, SupportTaskFactory supportTaskFactory) {
        this.clusterService = Objects.requireNonNull(clusterService);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.permissionValidationService = Objects.requireNonNull(permissionValidationService);
        this.clusteredSupportZipService = Objects.requireNonNull(clusteredSupportZipService);
        Objects.requireNonNull(taskExecutorFactory);
        this.executor = Lazy.supplier(() -> taskExecutorFactory.create(TaskType.SUPPORT_ZIP, SUPPORT_ZIP_MAX_THREADS));
        this.supportTaskFactory = Objects.requireNonNull(supportTaskFactory);
    }

    @Override
    @Nonnull
    public CreateSupportZipMonitor createLocalSupportZipWithPermissionCheck(SupportZipRequest request) {
        this.permissionValidationService.validateIsSysadmin();
        this.eventPublisher.publish(this.getSupportZipCreationAnalyticsEvent(request));
        return this.createLocalSupportZip(request);
    }

    private Object getSupportZipCreationAnalyticsEvent(SupportZipRequest request) {
        return new SupportZipCreationAnalyticsEvent(request.isLimitFileSizes(), request.getFileConstraintSize(), request.getFileConstraintLastModified(), request.getItems(), this.getRequestedNodeCount(request), this.clusterService.getNodeCount().orElse(1), request.getSource());
    }

    private int getRequestedNodeCount(SupportZipRequest request) {
        if (!this.clusterService.isClustered()) {
            return 1;
        }
        Set<String> nodeIds = request.getNodeIds();
        if (nodeIds == null) {
            return this.clusterService.getNodeCount().orElseThrow(IllegalStateException::new);
        }
        return nodeIds.size();
    }

    @Override
    public CreateSupportZipMonitor createLocalSupportZipWithoutPermissionCheck(SupportZipRequest request) {
        if (this.appliesToThisNode(request)) {
            return this.createLocalSupportZip(request);
        }
        return null;
    }

    @Nonnull
    private CreateSupportZipMonitor createLocalSupportZip(SupportZipRequest request) {
        CreateSupportZipTask task = this.supportTaskFactory.createSupportZipTask(request);
        return Optional.ofNullable(request.getClusterTaskId()).map(clusterTaskId -> this.executor.get().submit(task, (String)clusterTaskId)).orElseGet(() -> this.executor.get().submit(task));
    }

    @Override
    public Optional<CreateSupportZipMonitor> getMonitor(String taskId) {
        this.permissionValidationService.validateIsSysadmin();
        return this.getMonitorWithoutPermissionCheck(taskId);
    }

    @Override
    public Optional<CreateSupportZipMonitor> getMonitorWithoutPermissionCheck(String taskId) {
        return Optional.ofNullable(this.executor.get().getMonitor(taskId));
    }

    @Override
    public Collection<CreateSupportZipMonitor> getMonitors(boolean includeRemote) {
        this.permissionValidationService.validateIsSysadmin();
        return this.executor.get().getMonitors().stream().filter(monitor -> this.isRelevantToThisNode((TaskMonitor<File>)monitor, includeRemote)).collect(Collectors.toList());
    }

    private boolean isRelevantToThisNode(TaskMonitor<File> taskMonitor, boolean includeRemote) {
        return includeRemote || !this.clusterService.isClustered() || this.clusterService.getCurrentNodeId().equals(taskMonitor.getNodeId());
    }

    @Override
    public Collection<CreateSupportZipMonitor> getClusteredMonitors(String clusterTaskId) {
        this.permissionValidationService.validateIsSysadmin();
        return ImmutableList.copyOf(this.executor.get().getTaskMonitorRepository().getRecentTaskMonitorsByClusteredTaskId(clusterTaskId));
    }

    @Override
    public void cancelSupportZipTask(String taskId) throws TaskNotFoundException {
        CreateSupportZipMonitor monitor = this.getMonitor(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        LOGGER.debug("Cancelling Support zip task {}", (Object)taskId);
        Optional<String> currentNodeId = this.clusterService.getCurrentNodeId();
        Optional<String> taskNodeId = monitor.getNodeId();
        if (!taskNodeId.isPresent() || taskNodeId.equals(currentNodeId)) {
            this.cancelSupportZipTaskOnThisNode(monitor);
        } else {
            this.clusteredSupportZipService.requestSupportZipCancellationOnOtherNodes(taskId);
        }
    }

    @Override
    public void cancelSupportZipTaskOnThisNode(CreateSupportZipMonitor monitor) {
        this.executor.get().getTaskMonitorRepository().deleteTaskMonitor(monitor);
        monitor.cancel(true);
    }

    @Override
    @Nonnull
    public final ClusteredZipTaskStart createSupportZipsForCluster(SupportZipRequest supportZipRequest) throws ClusterMessagingException, NotClusteredException {
        if (!this.clusterService.isClustered()) {
            throw new NotClusteredException();
        }
        Validate.notBlank((CharSequence)supportZipRequest.getClusterTaskId());
        Optional<String> maybeError = this.clusteredSupportZipService.requestSupportZipCreationOnOtherNodes(supportZipRequest);
        if (maybeError.isPresent()) {
            throw new ClusterMessagingException(maybeError.get());
        }
        if (this.appliesToThisNode(supportZipRequest)) {
            this.createLocalSupportZipWithPermissionCheck(supportZipRequest);
        }
        return new ClusteredZipTaskStart(supportZipRequest.getClusterTaskId(), this.clusterService.getNodeIds().stream().sorted().collect(Collectors.toList()));
    }

    @Override
    public boolean isClusterSupportZipSupported() {
        return this.clusterService.isClustered() && this.clusteredSupportZipService.isClusterSupportZipSupported();
    }

    private boolean appliesToThisNode(SupportZipRequest supportZipRequest) {
        return this.clusterService.getCurrentNodeId().map(supportZipRequest::appliesToNode).orElse(false);
    }

    @EventName(value="atst.create.support.zip.created")
    static class SupportZipCreationAnalyticsEvent
    extends SupportZipOptionsAwareEvent {
        private final Boolean limitFileSizes;
        private final Integer fileConstraintSize;
        private final Integer fileConstraintLastModified;
        private final int requestedNodes;
        private final int totalNodes;
        private final SupportZipRequest.Source source;

        private SupportZipCreationAnalyticsEvent(Boolean limitFileSizes, Integer fileConstraintSize, Integer fileConstraintLastModified, Collection<String> items, int requestedNodes, int totalNodes, SupportZipRequest.Source source) {
            super(items);
            this.limitFileSizes = limitFileSizes;
            this.fileConstraintSize = fileConstraintSize;
            this.fileConstraintLastModified = fileConstraintLastModified;
            this.requestedNodes = requestedNodes;
            this.totalNodes = totalNodes;
            this.source = source;
        }

        public int getRequestedNodes() {
            return this.requestedNodes;
        }

        public int getTotalNodes() {
            return this.totalNodes;
        }

        public String getUi() {
            return this.source.getKey();
        }

        public Boolean getLimitFileSizes() {
            return this.limitFileSizes;
        }

        public Integer getFileConstraintSize() {
            return this.fileConstraintSize;
        }

        public Integer getFileConstraintLastModified() {
            return this.fileConstraintLastModified;
        }
    }
}

