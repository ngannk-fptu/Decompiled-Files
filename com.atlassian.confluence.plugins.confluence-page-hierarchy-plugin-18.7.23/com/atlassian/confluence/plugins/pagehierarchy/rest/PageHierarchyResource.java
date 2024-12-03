/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.permissions.Operation
 *  com.atlassian.confluence.api.model.permissions.OperationKey
 *  com.atlassian.confluence.api.model.permissions.Target
 *  com.atlassian.confluence.api.service.content.ChildContentService
 *  com.atlassian.confluence.api.service.people.PersonService
 *  com.atlassian.confluence.api.service.permissions.OperationService
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.confluence.links.LinkManager
 *  com.atlassian.confluence.links.OutgoingLinkMeta
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.pages.persistence.dao.bulk.PageNameConflictResolver
 *  com.atlassian.confluence.pages.persistence.dao.bulk.copy.PageCopyOptions$Builder
 *  com.atlassian.confluence.pages.persistence.dao.bulk.delete.PageDeleteOptions
 *  com.atlassian.confluence.pages.persistence.dao.bulk.delete.PageDeleteOptions$Builder
 *  com.atlassian.confluence.pages.persistence.dao.bulk.impl.AggregateNameConflictResolver
 *  com.atlassian.confluence.pages.persistence.dao.bulk.impl.FindAndReplaceNameConflictResolver
 *  com.atlassian.confluence.pages.persistence.dao.bulk.impl.PrefixNameConflictResolver
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.longrunning.LongRunningTaskId
 *  com.atlassian.confluence.util.longrunning.LongRunningTaskManager
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableMap
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.pagehierarchy.rest;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.permissions.Operation;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.model.permissions.Target;
import com.atlassian.confluence.api.service.content.ChildContentService;
import com.atlassian.confluence.api.service.people.PersonService;
import com.atlassian.confluence.api.service.permissions.OperationService;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.links.LinkManager;
import com.atlassian.confluence.links.OutgoingLinkMeta;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.persistence.dao.bulk.PageNameConflictResolver;
import com.atlassian.confluence.pages.persistence.dao.bulk.copy.PageCopyOptions;
import com.atlassian.confluence.pages.persistence.dao.bulk.delete.PageDeleteOptions;
import com.atlassian.confluence.pages.persistence.dao.bulk.impl.AggregateNameConflictResolver;
import com.atlassian.confluence.pages.persistence.dao.bulk.impl.FindAndReplaceNameConflictResolver;
import com.atlassian.confluence.pages.persistence.dao.bulk.impl.PrefixNameConflictResolver;
import com.atlassian.confluence.plugins.bulk.tasks.BulkPageCopyLongRunningTask;
import com.atlassian.confluence.plugins.bulk.tasks.BulkPageDeleteLongRunningTask;
import com.atlassian.confluence.plugins.pagehierarchy.analytics.AnalyticsPublisher;
import com.atlassian.confluence.plugins.pagehierarchy.rest.CopyPageHierarchyRequest;
import com.atlassian.confluence.plugins.pagehierarchy.rest.DeletePageHierarchyRequest;
import com.atlassian.confluence.plugins.pagehierarchy.validation.CopyPageHierarchyValidator;
import com.atlassian.confluence.plugins.pagehierarchy.validation.DeletePageHierarchyValidator;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.confluence.util.longrunning.LongRunningTaskManager;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AnonymousAllowed
@Path(value="/")
@Consumes(value={"application/json"})
@Produces(value={"application/json;charset=UTF-8"})
public class PageHierarchyResource {
    private static final Logger logger = LoggerFactory.getLogger(PageHierarchyResource.class);
    private static final String CONTENT_LIMIT = "25";
    private static final String CONTENT_META_INCOMING_COUNT = "incomingCount";
    private static final String CONTENT_META_INCOMING_COUNT_FOR_PARENT = "incomingCountForParent";
    private static final String CONTENT_META_EDIT_PERMISSION = "editPermission";
    private final PageManager pageManager;
    private final LongRunningTaskManager longRunningTaskManager;
    private final TransactionTemplate transactionTemplate;
    private final CopyPageHierarchyValidator copyPageHierarchyValidator;
    private final DeletePageHierarchyValidator deletePageHierarchyValidator;
    private final AnalyticsPublisher analyticsPublisher;
    private final LinkManager linkManager;
    private final ChildContentService childContentService;
    private final OperationService operationService;
    private final PersonService personService;

    public PageHierarchyResource(@ConfluenceImport PageManager pageManager, @ConfluenceImport LongRunningTaskManager longRunningTaskManager, @ConfluenceImport TransactionTemplate transactionTemplate, @ConfluenceImport LinkManager linkManager, @ComponentImport ChildContentService childContentService, @ComponentImport OperationService operationService, @ComponentImport PersonService personService, CopyPageHierarchyValidator copyPageHierarchyValidator, DeletePageHierarchyValidator deletePageHierarchyValidator, AnalyticsPublisher analyticsPublisher) {
        this.copyPageHierarchyValidator = copyPageHierarchyValidator;
        this.deletePageHierarchyValidator = deletePageHierarchyValidator;
        this.pageManager = pageManager;
        this.longRunningTaskManager = longRunningTaskManager;
        this.transactionTemplate = transactionTemplate;
        this.analyticsPublisher = analyticsPublisher;
        this.linkManager = linkManager;
        this.operationService = operationService;
        this.childContentService = childContentService;
        this.personService = personService;
    }

    @POST
    @Path(value="/copy")
    public Response copyPageHierarchy(CopyPageHierarchyRequest request) {
        this.copyPageHierarchyValidator.validate(request);
        String prefix = request.getTitleOptions().getPrefix();
        String search = request.getTitleOptions().getSearch();
        String replace = request.getTitleOptions().getReplace();
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        ArrayList<Object> conflictResolvers = new ArrayList<Object>(2);
        PageCopyOptions.Builder optionsBuilder = ((PageCopyOptions.Builder)((PageCopyOptions.Builder)((PageCopyOptions.Builder)new PageCopyOptions.Builder().withMaxProcessedEntries(CopyPageHierarchyValidator.MAX_PAGES)).withCopyAttachment(request.isCopyAttachments()).withCopyPermission(request.isCopyPermissions()).withCopyLabel(request.isCopyLabels()).withUser(user)).withBatchSize(20)).withPageContentTranformer((pageContent, originalPage, destinationPage) -> pageContent);
        if (StringUtils.isNotEmpty((CharSequence)prefix)) {
            optionsBuilder.withPrefixNameConflictResolver(true, prefix);
            conflictResolvers.add(new PrefixNameConflictResolver(true, prefix));
        }
        if (StringUtils.isNotBlank((CharSequence)search) && replace != null) {
            conflictResolvers.add(new FindAndReplaceNameConflictResolver(search, replace));
        }
        optionsBuilder.withNameConflictResolver((PageNameConflictResolver)new AggregateNameConflictResolver(conflictResolvers.toArray(new PageNameConflictResolver[conflictResolvers.size()])));
        BulkPageCopyLongRunningTask bulkPageCopylongRunningTask = ((BulkPageCopyLongRunningTask.Builder)((BulkPageCopyLongRunningTask.Builder)((BulkPageCopyLongRunningTask.Builder)new BulkPageCopyLongRunningTask.Builder().withOptionsBuilder(optionsBuilder)).withOriginalPage(request.getOriginalPageId()).withDestinationPage(request.getDestinationPageId()).withPageManager(this.pageManager)).withTransactionTemplate(this.transactionTemplate)).build();
        this.analyticsPublisher.publishCopyEvent(request);
        LongRunningTaskId taskId = this.longRunningTaskManager.startLongRunningTask((User)user, (LongRunningTask)bulkPageCopylongRunningTask);
        return Response.ok((Object)ImmutableMap.of((Object)"taskId", (Object)taskId.asLongTaskId())).build();
    }

    @POST
    @Path(value="/delete")
    public Response deletePageHierarchy(DeletePageHierarchyRequest request) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        this.deletePageHierarchyValidator.validateRequest(request);
        PageDeleteOptions.Builder optionsBuilder = ((PageDeleteOptions.Builder)((PageDeleteOptions.Builder)((PageDeleteOptions.Builder)PageDeleteOptions.builder().withUser(user)).withMaxProcessedEntries(DeletePageHierarchyValidator.MAX_PAGES)).withProgressMeter(new ProgressMeter())).withPageId(new Long[]{request.getTargetPageId().asLong()});
        if (request.isDeleteHierarchy() && request.getTargetIds() != null) {
            optionsBuilder.withPageIds(request.getTargetIds());
        }
        BulkPageDeleteLongRunningTask bulkPageDeleteLongRunningTask = ((BulkPageDeleteLongRunningTask.Builder)((BulkPageDeleteLongRunningTask.Builder)((BulkPageDeleteLongRunningTask.Builder)new BulkPageDeleteLongRunningTask.Builder().withOptionsBuilder(optionsBuilder)).withTargetPageId(request.getTargetPageId()).withPageManager(this.pageManager)).withTransactionTemplate(this.transactionTemplate)).build();
        this.analyticsPublisher.publishDeleteEvent(request);
        LongRunningTaskId taskId = this.longRunningTaskManager.startLongRunningTask((User)user, (LongRunningTask)bulkPageDeleteLongRunningTask);
        return Response.ok((Object)ImmutableMap.of((Object)"taskId", (Object)taskId.asLongTaskId())).build();
    }

    @GET
    @Path(value="/incoming/count")
    public RestList<Content> countIncomingLinks(@QueryParam(value="rootContentId") ContentId rootContentId, @QueryParam(value="parentContentId") ContentId parentContentId, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="25") int limit) {
        if (rootContentId == null || parentContentId == null) {
            logger.error("Invalid parameters for rootContentId and/or parentContentId");
            return RestList.newRestList().build();
        }
        Page rootPage = this.pageManager.getPage(rootContentId.asLong());
        Page parentPage = this.pageManager.getPage(parentContentId.asLong());
        if (rootPage == null || parentPage == null) {
            logger.error("Invalid parameters for rootContentId and/or parentContentId");
            return RestList.newRestList().build();
        }
        ConfluenceUser loginUser = AuthenticatedUserThreadLocal.get();
        UserKey loginUserKey = loginUser == null ? null : loginUser.getKey();
        Person loginPerson = (Person)this.personService.find(new Expansion[0]).withUserKey(loginUserKey).fetchOne().get();
        Expansions expansions = ExpansionsParser.parseAsExpansions((String)"children.page");
        SimplePageRequest pageRequest = new SimplePageRequest(start, limit);
        PageResponse childContents = this.childContentService.findContent(parentContentId, expansions.toArray()).withParentVersion(0).fetchMany(ContentType.PAGE, (PageRequest)pageRequest);
        Map<Long, Long> outgoingLinkMetaList = this.linkManager.countIncomingLinksForContents((SpaceContentEntityObject)rootPage, (SpaceContentEntityObject)parentPage).collect(Collectors.toMap(OutgoingLinkMeta::getContentId, OutgoingLinkMeta::getIncomingLinkCount));
        List decoratedResult = childContents.getResults().stream().map(content -> {
            Long incomingCountForParent = (Long)outgoingLinkMetaList.get(parentContentId.asLong());
            Long incomingCount = (Long)outgoingLinkMetaList.get(content.getId().asLong());
            ImmutableMap metadataMap = ImmutableMap.of((Object)CONTENT_META_INCOMING_COUNT, (Object)(incomingCount == null ? 0L : incomingCount), (Object)CONTENT_META_INCOMING_COUNT_FOR_PARENT, (Object)(incomingCountForParent == null ? 0L : incomingCountForParent), (Object)CONTENT_META_EDIT_PERMISSION, (Object)this.operationService.canPerform(loginPerson, (Operation)OperationKey.DELETE, Target.forModelObject((Object)content)).isAuthorized());
            return Content.builder((Content)content).metadata((Map)metadataMap).build();
        }).collect(Collectors.toList());
        PageResponseImpl decoratedChildContents = PageResponseImpl.builder().addAll(decoratedResult).hasMore(childContents.hasMore()).pageRequest(childContents.getPageRequest()).build();
        return RestList.newRestList((PageRequest)pageRequest).results((PageResponse)decoratedChildContents).build();
    }
}

