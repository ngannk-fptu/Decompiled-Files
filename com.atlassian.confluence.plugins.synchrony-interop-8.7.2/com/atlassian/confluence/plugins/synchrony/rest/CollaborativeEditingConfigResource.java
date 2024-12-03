/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.api.service.eviction.SynchronyDataService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager
 *  com.atlassian.confluence.util.longrunning.LongRunningTaskId
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.google.common.collect.ImmutableMap
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.synchrony.rest;

import com.atlassian.cache.CacheManager;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.api.service.eviction.SynchronyDataService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyMonitor;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyProcessManager;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.confluence.plugins.synchrony.rest.CollaborativeEditingConfigResponse;
import com.atlassian.confluence.plugins.synchrony.service.CollaborativeEditingModeDuration;
import com.atlassian.confluence.plugins.synchrony.tasks.AbstractConfigLongRunningTask;
import com.atlassian.confluence.plugins.synchrony.tasks.DisableTask;
import com.atlassian.confluence.plugins.synchrony.tasks.EnableTask;
import com.atlassian.confluence.plugins.synchrony.tasks.RestartSynchronyTask;
import com.atlassian.confluence.plugins.synchrony.tasks.SynchronyConfigTaskTracker;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/")
@ReadOnlyAccessAllowed
@WebSudoRequired
@ResourceFilters(value={SysadminOnlyResourceFilter.class})
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
public class CollaborativeEditingConfigResource {
    private final SynchronyConfigurationManager configManager;
    private final SynchronyConfigTaskTracker taskTracker;
    private final SynchronyProcessManager processManager;
    private final EventPublisher eventPublisher;
    private final CacheManager cacheManager;
    private final SynchronyMonitor synchronyMonitor;
    private final TransactionTemplate transactionTemplate;
    private final PageManager pageManager;
    private final CollaborativeEditingModeDuration collaborativeEditingModeDuration;
    private final SynchronyDataService synchronyDataService;

    public CollaborativeEditingConfigResource(@ComponentImport SynchronyConfigurationManager configManager, SynchronyConfigTaskTracker taskTracker, @ComponentImport CacheManager cacheManager, @ComponentImport TransactionTemplate transactionTemplate, SynchronyMonitor synchronyMonitor, SynchronyProcessManager processManager, @ComponentImport EventPublisher eventPublisher, @ComponentImport PageManager pageManager, CollaborativeEditingModeDuration collaborativeEditingModeDuration, @ComponentImport SynchronyDataService synchronyDataService) {
        this.configManager = Objects.requireNonNull(configManager);
        this.cacheManager = Objects.requireNonNull(cacheManager);
        this.transactionTemplate = Objects.requireNonNull(transactionTemplate);
        this.synchronyMonitor = Objects.requireNonNull(synchronyMonitor);
        this.taskTracker = Objects.requireNonNull(taskTracker);
        this.processManager = Objects.requireNonNull(processManager);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.pageManager = Objects.requireNonNull(pageManager);
        this.collaborativeEditingModeDuration = Objects.requireNonNull(collaborativeEditingModeDuration);
        this.synchronyDataService = Objects.requireNonNull(synchronyDataService);
    }

    @GET
    @Path(value="/status")
    public Response status() {
        CollaborativeEditingConfigResponse response = new CollaborativeEditingConfigResponse.Builder().setSharedDraftsEnabled(this.configManager.isSharedDraftsEnabled()).setLongRunningTaskId(this.taskTracker.getTaskId()).setLongRunningTaskName(this.taskTracker.getTaskName()).build();
        return Response.ok().entity((Object)response).build();
    }

    @POST
    @Path(value="/enable")
    public Response enable() {
        if (this.configManager.isSharedDraftsEnabled()) {
            ValidationResult validationResult = SimpleValidationResult.builder().addError("synchrony.config.can.not.transition.from.on.to.on", new Object[0]).build();
            throw new BadRequestException("Cannot transition from on to on.", validationResult);
        }
        EnableTask task = new EnableTask(this.configManager, this.processManager, this.synchronyMonitor, this.cacheManager, this.taskTracker, this.eventPublisher, this.pageManager, this.collaborativeEditingModeDuration);
        return this.buildResponseFromTask(task);
    }

    @POST
    @Path(value="/disable")
    public Response disable() {
        DisableTask task = new DisableTask(this.configManager, this.processManager, this.synchronyMonitor, this.cacheManager, this.taskTracker, this.transactionTemplate, this.eventPublisher, this.collaborativeEditingModeDuration, this.synchronyDataService);
        return this.buildResponseFromTask(task);
    }

    @POST
    @Path(value="/restart")
    public Response restart() {
        if (this.processManager.isSynchronyClusterManuallyManaged()) {
            ValidationResult validationResult = SimpleValidationResult.builder().addError("synchrony.config.can.not.restart.while.in.external.mode", new Object[0]).build();
            throw new BadRequestException("Cannot restart Synchrony when in externally managed mode.", validationResult);
        }
        RestartSynchronyTask task = new RestartSynchronyTask(this.configManager, this.processManager, this.synchronyMonitor, this.cacheManager, this.taskTracker, this.eventPublisher, this.collaborativeEditingModeDuration);
        return this.buildResponseFromTask(task);
    }

    private Response buildResponseFromTask(AbstractConfigLongRunningTask task) {
        try {
            LongRunningTaskId longRunningTaskId = this.taskTracker.startTask(task);
            return Response.ok().entity((Object)ImmutableMap.of((Object)"taskId", (Object)longRunningTaskId.toString())).build();
        }
        catch (IllegalStateException | RejectedExecutionException e) {
            ValidationResult validationResult = SimpleValidationResult.builder().addError("synchrony.config.could.not.start.task", new Object[0]).build();
            throw new BadRequestException("Could not start the requested task.", validationResult);
        }
    }

    @GET
    @Path(value="/configuration")
    public Response getConfiguration() {
        return Response.ok().entity(this.processManager.getConfiguration()).build();
    }

    @GET
    @Path(value="/synchrony-status")
    public Response synchronyStatus() {
        return Response.ok().entity(Collections.singletonMap("status", this.synchronyMonitor.isSynchronyUp() ? "running" : "stopped")).build();
    }
}

