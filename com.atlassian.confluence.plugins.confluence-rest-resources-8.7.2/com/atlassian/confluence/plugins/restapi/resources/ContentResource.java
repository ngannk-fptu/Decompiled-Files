/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.History
 *  com.atlassian.confluence.api.model.content.MacroInstance
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.ContentCursor
 *  com.atlassian.confluence.api.model.pagination.Cursor
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.search.SearchContext
 *  com.atlassian.confluence.api.model.search.SearchPageResponse
 *  com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier
 *  com.atlassian.confluence.api.service.content.ContentDraftService
 *  com.atlassian.confluence.api.service.content.ContentDraftService$ConflictPolicy
 *  com.atlassian.confluence.api.service.content.ContentMacroService
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.content.ContentService$ContentFinder
 *  com.atlassian.confluence.api.service.content.ContentService$SingleContentFetcher
 *  com.atlassian.confluence.api.service.content.ContentTrashService
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.api.service.exceptions.ApiPreconditions
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ConflictException
 *  com.atlassian.confluence.api.service.exceptions.GoneException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.atlassian.confluence.api.service.pagination.CursorFactory
 *  com.atlassian.confluence.api.service.search.CQLSearchService
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.graphql.annotations.GraphQLName
 *  com.atlassian.graphql.annotations.GraphQLProvider
 *  com.atlassian.graphql.annotations.expansions.GraphQLExpansionParam
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.google.common.base.Strings
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.UriInfo
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.joda.time.LocalDate
 */
package com.atlassian.confluence.plugins.restapi.resources;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.History;
import com.atlassian.confluence.api.model.content.MacroInstance;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.ContentCursor;
import com.atlassian.confluence.api.model.pagination.Cursor;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.search.SearchContext;
import com.atlassian.confluence.api.model.search.SearchPageResponse;
import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.service.content.ContentDraftService;
import com.atlassian.confluence.api.service.content.ContentMacroService;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.content.ContentTrashService;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.api.service.exceptions.ApiPreconditions;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ConflictException;
import com.atlassian.confluence.api.service.exceptions.GoneException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.api.service.pagination.CursorFactory;
import com.atlassian.confluence.api.service.search.CQLSearchService;
import com.atlassian.confluence.plugins.restapi.annotations.LimitRequestSize;
import com.atlassian.confluence.plugins.restapi.graphql.GraphQLOffsetCursor;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.graphql.annotations.GraphQLName;
import com.atlassian.graphql.annotations.GraphQLProvider;
import com.atlassian.graphql.annotations.expansions.GraphQLExpansionParam;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.base.Strings;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.LocalDate;

@AnonymousAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/content")
@GraphQLProvider
@LimitRequestSize(value=0x500000L)
public class ContentResource {
    private final ContentService contentService;
    private final SpaceService spaceService;
    private final ContentMacroService contentMacroService;
    private final ContentTrashService contentTrashService;
    private final CQLSearchService searchService;
    private final ContentDraftService contentDraftService;

    public ContentResource(@ComponentImport ContentService contentService, @ComponentImport SpaceService spaceService, @ComponentImport ContentMacroService contentMacroService, @ComponentImport ContentTrashService contentTrashService, @ComponentImport CQLSearchService searchService, @ComponentImport ContentDraftService contentDraftService) {
        this.contentService = contentService;
        this.spaceService = spaceService;
        this.contentMacroService = contentMacroService;
        this.contentTrashService = contentTrashService;
        this.searchService = searchService;
        this.contentDraftService = contentDraftService;
    }

    @GraphQLName(value="content")
    public PageResponse<Content> getContentByGraph(@GraphQLName(value="id") ContentId id, @GraphQLExpansionParam String expand, @GraphQLName(value="type") @DefaultValue(value="page") String type, @GraphQLName(value="spaceKey") String spaceKey, @GraphQLName(value="title") String title, @GraphQLName(value="postingDay") String postingDay, @GraphQLName(value="status") List<ContentStatus> statuses, @GraphQLName(value="version") Integer version, @GraphQLName(value="offset") int offset, @GraphQLName(value="after") String afterOffset, @GraphQLName(value="first") @DefaultValue(value="25") int limit, UriInfo uriInfo) {
        expand = expand.replace(".edges.nodes.", ".");
        expand = expand.replace(".nodes.", ".");
        if (id != null) {
            try {
                Content content = this.getContentById(id, statuses, version, expand);
                return RestList.newRestList().pageRequest((PageRequest)new SimplePageRequest(0, 1)).results(Collections.singletonList(content), false).build();
            }
            catch (NotFoundException ex) {
                return RestList.newRestList().pageRequest((PageRequest)new SimplePageRequest(0, 1)).build();
            }
        }
        return this.getContent(type, spaceKey, title, statuses, postingDay, expand, GraphQLOffsetCursor.parseOffset(offset, afterOffset), limit, uriInfo);
    }

    @GET
    @Path(value="/{id}")
    @PublicApi
    public Content getContentById(@GraphQLName(value="id") @PathParam(value="id") ContentId id, @QueryParam(value="status") List<ContentStatus> statuses, @QueryParam(value="version") Integer version, @QueryParam(value="expand") @DefaultValue(value="history,space,version") String expand) throws ServiceException {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        ContentService.ContentFinder contentFinder = this.contentService.find(expansions);
        if (!statuses.isEmpty()) {
            contentFinder = statuses.size() == 1 && statuses.get(0).getValue().equals("any") ? contentFinder.withAnyStatus() : contentFinder.withStatus(statuses);
        }
        ContentService.SingleContentFetcher fetcher = version == null ? contentFinder.withId(id) : contentFinder.withIdAndVersion(id, version.intValue());
        Optional content = fetcher.fetch();
        return (Content)content.orElseThrow(ServiceExceptionSupplier.notFound((String)("No content found with id: " + id)));
    }

    @POST
    @PublicApi
    public Content createContent(Content content, @QueryParam(value="status") @DefaultValue(value="current") ContentStatus status, @QueryParam(value="expand") @DefaultValue(value="body.storage,history,space,container.history,container.version,version,ancestors") String expand) throws ServiceException {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        if (ContentStatus.DRAFT.equals((Object)status) && ContentStatus.CURRENT.equals((Object)content.getStatus()) && content.getId() != null) {
            return this.contentDraftService.publishNewDraft(content, expansions);
        }
        return this.contentService.create(content, expansions);
    }

    @GET
    @PublicApi
    public PageResponse<Content> getContent(@QueryParam(value="type") @DefaultValue(value="page") String type, @QueryParam(value="spaceKey") String spaceKey, @QueryParam(value="title") String title, @QueryParam(value="status") List<ContentStatus> statuses, @QueryParam(value="postingDay") String postingDay, @QueryParam(value="expand") @DefaultValue(value="") String expand, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="25") int limit, @Context UriInfo uriInfo) throws ServiceException {
        RestPageRequest pageRequest = new RestPageRequest(uriInfo, start, limit);
        ContentService.ContentFinder contentFinder = this.getContentFinder(spaceKey, title, statuses, postingDay, expand);
        PageResponse contents = contentFinder.fetchMany(ContentType.valueOf((String)type), (PageRequest)pageRequest);
        return RestList.newRestList((PageResponse)contents).pageRequest((PageRequest)pageRequest.copyWithLimits(contents)).build();
    }

    @GET
    @Path(value="/scan")
    @PublicApi
    public PageResponse<Content> scanContent(@QueryParam(value="spaceKey") String spaceKey, @QueryParam(value="status") List<ContentStatus> statuses, @QueryParam(value="expand") @DefaultValue(value="") String expand, @QueryParam(value="limit") @DefaultValue(value="25") int limit, @QueryParam(value="cursor") String cursor, @Context UriInfo uriInfo) throws ServiceException {
        ContentCursor contentCursor;
        Object object = contentCursor = StringUtils.isEmpty((CharSequence)cursor) ? ContentCursor.EMPTY_CURSOR : CursorFactory.buildFrom((String)cursor);
        if (!(contentCursor instanceof ContentCursor)) {
            throw new IllegalArgumentException("Cursor type is incorrect");
        }
        RestPageRequest pageRequest = new RestPageRequest(uriInfo, (Cursor)contentCursor, limit);
        ContentService.ContentFinder contentFinder = this.getContentFinder(spaceKey, null, statuses, null, expand);
        PageResponse contents = contentFinder.fetchMany(ContentType.PAGE, (PageRequest)pageRequest);
        return RestList.newRestList((PageResponse)contents).pageRequest((PageRequest)pageRequest.copyWithLimits(contents)).build();
    }

    private ContentService.ContentFinder getContentFinder(String spaceKey, String title, List<ContentStatus> statuses, String postingDay, String expand) {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        ContentService.ContentFinder contentFinder = this.contentService.find(expansions);
        if (!statuses.isEmpty()) {
            if (statuses.size() == 1 && statuses.get(0).getValue().equals("any")) {
                contentFinder.withAnyStatus();
            } else {
                contentFinder.withStatus(statuses);
            }
        }
        if (!Strings.isNullOrEmpty((String)spaceKey)) {
            Optional space = this.spaceService.find(new Expansion[0]).withKeys(new String[]{spaceKey}).fetch();
            if (space.isPresent()) {
                contentFinder.withSpace(new Space[]{(Space)space.get()});
            } else {
                throw new NotFoundException("No space with key : " + spaceKey);
            }
        }
        if (!Strings.isNullOrEmpty((String)title)) {
            contentFinder.withTitle(title);
        }
        if (!Strings.isNullOrEmpty((String)postingDay)) {
            contentFinder.withCreatedDate(this.convertDate(postingDay));
        }
        return contentFinder;
    }

    private LocalDate convertDate(String postingDayStr) {
        String[] dateParts = postingDayStr.split("-");
        int year = Integer.parseInt(dateParts[0]);
        int monthOfYear = Integer.parseInt(dateParts[1]);
        int dayOfMonth = Integer.parseInt(dateParts[2]);
        return new LocalDate(year, monthOfYear, dayOfMonth);
    }

    @GET
    @Path(value="/search")
    @PublicApi
    public PageResponse<Content> search(@QueryParam(value="cql") String cql, @QueryParam(value="cqlcontext") String cqlcontext, @QueryParam(value="expand") @DefaultValue(value="") String expand, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="25") int limit, @Context UriInfo uriInfo) {
        if (Strings.isNullOrEmpty((String)cql)) {
            throw new BadRequestException("CQL query parameter is required but was empty");
        }
        SearchContext cqlContextObj = this.deserializeSearchContext(cqlcontext);
        RestPageRequest pageRequest = new RestPageRequest(uriInfo, start, limit);
        if (cqlContextObj == null) {
            cqlContextObj = SearchContext.EMPTY;
        }
        SearchPageResponse results = (SearchPageResponse)this.searchService.searchContent(cql, cqlContextObj, (PageRequest)pageRequest, ExpansionsParser.parse((String)expand));
        RestList restList = RestList.newRestList((PageResponse)results).pageRequest((PageRequest)pageRequest.copyWithLimits((PageResponse)results)).build();
        restList.putProperty("cqlQuery", (Object)results.getCqlQuery());
        restList.putProperty("searchDuration", (Object)results.getSearchDuration());
        restList.putProperty("totalSize", (Object)results.totalSize());
        if (results.archivedResultCount().isPresent()) {
            restList.putProperty("archivedResultCount", results.archivedResultCount().get());
        }
        return restList;
    }

    private SearchContext deserializeSearchContext(String searchContextJson) {
        ObjectMapper mapper = new ObjectMapper();
        return SearchContext.deserialize((String)searchContextJson, (ObjectMapper)mapper);
    }

    @GET
    @Path(value="/{id}/history")
    @PublicApi
    public History getHistory(@PathParam(value="id") ContentId contentId, @QueryParam(value="expand") @DefaultValue(value="previousVersion,nextVersion,lastUpdated") String expand) throws ServiceException {
        Expansions expansions = ExpansionsParser.parseWithPrefix((String)"history", (String)expand);
        Optional contentOption = this.contentService.find(expansions.toArray()).withId(contentId).fetch();
        Content content = (Content)contentOption.orElseThrow(ServiceExceptionSupplier.notFound((String)("No content with id : " + contentId)));
        return (History)content.getHistoryRef().get();
    }

    @Deprecated
    @GET
    @Path(value="/{id}/history/{version}/macro/hash/{hash}")
    @PublicApi
    public MacroInstance getMacroBodyByHash(@PathParam(value="id") ContentId contentId, @PathParam(value="version") int versionId, @PathParam(value="hash") String hash) throws ServiceException {
        return this.getMacroBodyByMacroId(contentId, versionId, hash);
    }

    @GET
    @Path(value="/{id}/history/{version}/macro/id/{macroId}")
    @PublicApi
    public MacroInstance getMacroBodyByMacroId(@PathParam(value="id") ContentId contentId, @PathParam(value="version") int versionId, @PathParam(value="macroId") String macroId) throws ServiceException {
        return (MacroInstance)this.contentMacroService.findInContent(contentId, new Expansion[0]).withMacroId(macroId).withContentVersion(versionId).fetch().orElseThrow(ServiceExceptionSupplier.notFound((String)("No macro found on content id : " + contentId + " with version: " + versionId + " and macroId: " + macroId)));
    }

    @PUT
    @Path(value="/{contentId}")
    @PublicApi
    public Content update(@PathParam(value="contentId") ContentId contentId, Content content, @QueryParam(value="status") ContentStatus status, @QueryParam(value="conflictPolicy") @DefaultValue(value="abort") ContentDraftService.ConflictPolicy conflictPolicy) throws ServiceException {
        ApiPreconditions.checkRequestArgs((content.getId() == null || content.getId().equals((Object)contentId) ? 1 : 0) != 0, (String)"content id mismatch");
        try {
            Content contentToUpdate;
            Content content2 = contentToUpdate = content.getId() == null ? Content.builder((Content)content).id(contentId).build() : content;
            if (status != null) {
                if (ContentStatus.DRAFT.equals((Object)status) && !ContentStatus.CURRENT.equals((Object)contentToUpdate.getStatus())) {
                    throw new NotImplementedServiceException("Updating a draft without publishing is not supported");
                }
                if (ContentStatus.DRAFT.equals((Object)status)) {
                    return this.contentDraftService.publishEditDraft(contentToUpdate, conflictPolicy);
                }
                Optional existingContent = this.contentService.find(new Expansion[0]).withStatus(new ContentStatus[]{status}).withId(contentId).fetch();
                if (!existingContent.isPresent()) {
                    Optional trashedContent = this.contentService.find(new Expansion[0]).withStatus(new ContentStatus[]{ContentStatus.TRASHED}).withId(contentId).fetch();
                    if (trashedContent.isPresent()) {
                        throw new GoneException("Content was trashed.");
                    }
                    throw new NotFoundException("Content can't be found.");
                }
            }
            return this.contentService.update(contentToUpdate);
        }
        catch (RuntimeException e) {
            if (e.getClass().getSimpleName().contains("HibernateOptimisticLockingFailureException")) {
                throw new ConflictException("Editor might be busy. Unable to update", (Throwable)e);
            }
            throw e;
        }
    }

    @DELETE
    @Path(value="/{id}")
    @PublicApi
    public Response delete(@PathParam(value="id") ContentId contentId, @QueryParam(value="status") ContentStatus status) throws ServiceException {
        try {
            Content content = Content.builder().id(contentId).status(status).build();
            if (ContentStatus.TRASHED.equals((Object)status)) {
                this.contentTrashService.purge(content);
                return Response.noContent().build();
            }
            if (ContentStatus.DRAFT.equals((Object)status)) {
                this.contentDraftService.deleteDraft(contentId);
                return Response.noContent().build();
            }
            if (status != null && !status.equals((Object)ContentStatus.CURRENT)) {
                throw new BadRequestException("Specified status for Content DELETE can only be 'current', 'draft' or 'trashed'");
            }
            this.contentService.delete(content);
            return Response.noContent().build();
        }
        catch (RuntimeException e) {
            if (e.getClass().getSimpleName().contains("HibernateOptimisticLockingFailureException")) {
                throw new ConflictException("Editor might be busy. Unable to discard to last published version", (Throwable)e);
            }
            throw e;
        }
    }
}

