/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.UserChecker
 *  com.atlassian.confluence.util.longrunning.LongRunningTaskId
 *  com.atlassian.confluence.util.longrunning.LongRunningTaskManager
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableMap
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugin.copyspace.rest;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.plugin.copyspace.entity.CopySpaceProgressBarData;
import com.atlassian.confluence.plugin.copyspace.rest.CopySpaceRequest;
import com.atlassian.confluence.plugin.copyspace.service.BlogPostService;
import com.atlassian.confluence.plugin.copyspace.service.ConfluenceUtilService;
import com.atlassian.confluence.plugin.copyspace.service.ContextHolder;
import com.atlassian.confluence.plugin.copyspace.service.CopySpaceContextService;
import com.atlassian.confluence.plugin.copyspace.service.CopySpaceProgressBarCacheService;
import com.atlassian.confluence.plugin.copyspace.service.PageService;
import com.atlassian.confluence.plugin.copyspace.service.PermissionService;
import com.atlassian.confluence.plugin.copyspace.service.ProgressMeterService;
import com.atlassian.confluence.plugin.copyspace.service.SidebarLinkCopier;
import com.atlassian.confluence.plugin.copyspace.service.SpaceService;
import com.atlassian.confluence.plugin.copyspace.service.StatisticsService;
import com.atlassian.confluence.plugin.copyspace.service.ValidationService;
import com.atlassian.confluence.plugin.copyspace.task.BulkCopySpaceLongRunningTask;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.confluence.util.longrunning.LongRunningTaskManager;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableMap;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="/")
@Consumes(value={"application/json"})
@Produces(value={"application/json;charset=UTF-8"})
public class CopySpaceResource {
    private final TransactionTemplate transactionTemplate;
    private final LongRunningTaskManager longRunningTaskManager;
    private final BlogPostService blogPostService;
    private final PageService pageService;
    private final ContextHolder contextHolder;
    private final CopySpaceContextService copySpaceContextService;
    private final EventPublisher eventPublisher;
    private final SidebarLinkCopier sidebarLinkCopier;
    private final SpaceService spaceService;
    private final ValidationService validationService;
    private final BandanaManager bandanaManager;
    private final StatisticsService statisticsService;
    private final UserChecker userChecker;
    private final ConfluenceUtilService utilService;
    private final PermissionService permissionService;
    private final ProgressMeterService progressMeterService;
    private final CopySpaceProgressBarCacheService copySpaceProgressBarCacheService;

    public CopySpaceResource(@ComponentImport TransactionTemplate transactionTemplate, @ComponentImport LongRunningTaskManager longRunningTaskManager, BlogPostService blogPostService, PageService pageService, ContextHolder contextHolder, CopySpaceContextService copySpaceContextService, @ComponentImport(value="eventPublisher") EventPublisher eventPublisher, SidebarLinkCopier sidebarLinkCopier, SpaceService spaceService, ValidationService validationService, @ComponentImport BandanaManager bandanaManager, StatisticsService statisticsService, UserChecker userChecker, ConfluenceUtilService utilService, PermissionService permissionService, ProgressMeterService progressMeterService, CopySpaceProgressBarCacheService copySpaceProgressBarCacheService) {
        this.transactionTemplate = transactionTemplate;
        this.longRunningTaskManager = longRunningTaskManager;
        this.blogPostService = blogPostService;
        this.pageService = pageService;
        this.contextHolder = contextHolder;
        this.copySpaceContextService = copySpaceContextService;
        this.eventPublisher = eventPublisher;
        this.sidebarLinkCopier = sidebarLinkCopier;
        this.spaceService = spaceService;
        this.validationService = validationService;
        this.bandanaManager = bandanaManager;
        this.statisticsService = statisticsService;
        this.userChecker = userChecker;
        this.utilService = utilService;
        this.permissionService = permissionService;
        this.progressMeterService = progressMeterService;
        this.copySpaceProgressBarCacheService = copySpaceProgressBarCacheService;
    }

    @POST
    @Path(value="copy")
    public Response copySpace(CopySpaceRequest request) throws ServiceException {
        this.validationService.validate(request);
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        BulkCopySpaceLongRunningTask bulkCopySpaceLongRunningTask = new BulkCopySpaceLongRunningTask(request, this.transactionTemplate, this.blogPostService, this.pageService, this.spaceService, this.contextHolder, this.copySpaceContextService, this.eventPublisher, this.sidebarLinkCopier, this.bandanaManager, this.permissionService, this.progressMeterService, this.copySpaceProgressBarCacheService);
        LongRunningTaskId taskId = this.longRunningTaskManager.startLongRunningTask((User)user, (LongRunningTask)bulkCopySpaceLongRunningTask);
        CopySpaceProgressBarData copySpaceProgressBarData = new CopySpaceProgressBarData(request.getOldKey(), taskId.toString(), request.getNewKey(), request.getNewName(), request.isCopyPages(), request.isCopyBlogPosts());
        this.copySpaceProgressBarCacheService.putProgressBarData(copySpaceProgressBarData);
        return Response.ok((Object)ImmutableMap.of((Object)"taskId", (Object)taskId.asLongTaskId())).build();
    }

    @GET
    @Path(value="copy/space-content-count")
    public Response getContentCount(@QueryParam(value="key") String spaceKey) {
        return Response.ok((Object)ImmutableMap.builder().put((Object)"pagesCount", (Object)this.statisticsService.getTotalAmountOfPages(spaceKey)).put((Object)"blogPostsCount", (Object)this.statisticsService.getTotalAmountOfBlogs(spaceKey)).build()).build();
    }

    @GET
    @Path(value="license/expired")
    public Response isLicenseActive() {
        return Response.ok((Object)ImmutableMap.builder().put((Object)"isLicenseExpired", (Object)(this.utilService.isLicenseExpired() || this.userChecker != null && this.userChecker.hasTooManyUsers() ? 1 : 0)).build()).build();
    }

    @GET
    @Path(value="copy/task")
    public Response getCopySpaceTaskInProgress(@QueryParam(value="key") String spaceKey) {
        return Response.ok((Object)this.copySpaceProgressBarCacheService.getProgressBarData(spaceKey)).build();
    }
}

