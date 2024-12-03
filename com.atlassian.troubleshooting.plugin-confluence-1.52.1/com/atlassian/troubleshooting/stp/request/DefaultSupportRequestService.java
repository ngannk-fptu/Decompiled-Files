/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.request;

import com.atlassian.troubleshooting.stp.request.SupportRequestCreationRequest;
import com.atlassian.troubleshooting.stp.request.SupportRequestService;
import com.atlassian.troubleshooting.stp.request.SupportTaskFactory;
import com.atlassian.troubleshooting.stp.security.PermissionValidationService;
import com.atlassian.troubleshooting.stp.task.MonitoredTaskExecutor;
import com.atlassian.troubleshooting.stp.task.MonitoredTaskExecutorFactory;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import com.atlassian.troubleshooting.stp.task.TaskType;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

public final class DefaultSupportRequestService
implements DisposableBean,
SupportRequestService {
    private final PermissionValidationService permissionValidationService;
    private final MonitoredTaskExecutor executor;
    private final SupportTaskFactory supportTaskFactory;

    @Autowired
    public DefaultSupportRequestService(PermissionValidationService permissionValidationService, MonitoredTaskExecutorFactory taskExecutorFactory, SupportTaskFactory supportTaskFactory) {
        this.permissionValidationService = permissionValidationService;
        this.supportTaskFactory = supportTaskFactory;
        this.executor = taskExecutorFactory.create(TaskType.SUPPORT_REQUEST, 1);
    }

    @Override
    @Nonnull
    public TaskMonitor<Void> createSupportRequest(@Nonnull SupportRequestCreationRequest request) {
        this.permissionValidationService.validateIsSysadmin();
        return this.executor.submit(this.supportTaskFactory.createSupportRequestTask(request));
    }

    public void destroy() {
        this.executor.shutdown();
    }

    @Override
    public <T> TaskMonitor<T> getMonitor(@Nonnull String id) {
        this.permissionValidationService.validateIsSysadmin();
        return this.executor.getMonitor(id);
    }
}

