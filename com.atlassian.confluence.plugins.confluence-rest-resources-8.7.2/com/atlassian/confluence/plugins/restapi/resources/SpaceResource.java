/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.Label
 *  com.atlassian.confluence.api.model.content.Label$Prefix
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.SpaceStatus
 *  com.atlassian.confluence.api.model.content.SpaceType
 *  com.atlassian.confluence.api.model.longtasks.LongTaskSubmission
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.reference.BuilderUtils
 *  com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier
 *  com.atlassian.confluence.api.nav.Navigation$Builder
 *  com.atlassian.confluence.api.nav.Navigation$SpaceContentNav
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.api.service.content.SpaceService$SpaceFinder
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.confluence.rest.api.services.RestNavigationService
 *  com.atlassian.graphql.annotations.GraphQLName
 *  com.atlassian.graphql.annotations.GraphQLProvider
 *  com.atlassian.graphql.annotations.expansions.GraphQLExpansionParam
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.google.common.base.Strings
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Maps$EntryTransformer
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
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.plugins.restapi.resources;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.Label;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.SpaceStatus;
import com.atlassian.confluence.api.model.content.SpaceType;
import com.atlassian.confluence.api.model.longtasks.LongTaskSubmission;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.reference.BuilderUtils;
import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.plugins.restapi.graphql.GraphQLOffsetCursor;
import com.atlassian.confluence.plugins.restapi.resources.LongTaskResource;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.confluence.rest.api.services.RestNavigationService;
import com.atlassian.graphql.annotations.GraphQLName;
import com.atlassian.graphql.annotations.GraphQLProvider;
import com.atlassian.graphql.annotations.expansions.GraphQLExpansionParam;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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
import org.checkerframework.checker.nullness.qual.Nullable;

@AnonymousAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/space")
@GraphQLProvider
public class SpaceResource {
    private final SpaceService spaceService;
    private final RestNavigationService navigationService;
    private static final String DEPTH_ALL = "all";

    public SpaceResource(@ComponentImport SpaceService spaceService, @ComponentImport RestNavigationService navigationService) {
        this.spaceService = spaceService;
        this.navigationService = navigationService;
    }

    @POST
    @PublicApi
    public Space createSpace(Space newSpace) throws ServiceException {
        return this.spaceService.create(newSpace, false);
    }

    @POST
    @Path(value="_private")
    @PublicApi
    public Space createPrivateSpace(Space newSpace) throws ServiceException {
        return this.spaceService.create(newSpace, true);
    }

    @PUT
    @Path(value="/{spaceKey}")
    @PublicApi
    public Space update(@PathParam(value="spaceKey") String spaceKey, Space space) throws ServiceException {
        String keyInObject = space.getKey();
        if (Strings.isNullOrEmpty((String)keyInObject)) {
            space = Space.builder((Space)space).key(spaceKey).build();
        } else if (!spaceKey.equals(keyInObject)) {
            throw new BadRequestException("Updated Space has incorrect key: " + keyInObject);
        }
        return this.spaceService.update(space);
    }

    @DELETE
    @Path(value="/{spaceKey}")
    @PublicApi
    public Response delete(@PathParam(value="spaceKey") String spaceKey) throws ServiceException, URISyntaxException {
        Space space = Space.builder().key(spaceKey).build();
        LongTaskSubmission submissionResult = this.spaceService.delete(space);
        return LongTaskResource.submissionResponse(submissionResult);
    }

    @GET
    @Path(value="{spaceKey}")
    @PublicApi
    public Space space(@PathParam(value="spaceKey") String spaceKey, @QueryParam(value="expand") @DefaultValue(value="") String expand) {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        Optional space = this.spaceService.find(expansions).withKeys(new String[]{spaceKey}).fetch();
        return (Space)space.orElseThrow(ServiceExceptionSupplier.notFound((String)("No space found with key : " + spaceKey)));
    }

    @GET
    @GraphQLName(value="spaces")
    @PublicApi
    public PageResponse<Space> spaces(@GraphQLName(value="spaceKey") String spaceKey, @GraphQLName(value="spaceKeys") @QueryParam(value="spaceKey") List<String> spaceKeys, @GraphQLName(value="type") @QueryParam(value="type") String type, @GraphQLName(value="status") @QueryParam(value="status") String status, @GraphQLName(value="label") @QueryParam(value="label") List<String> labelNames, @GraphQLName(value="favourite") @QueryParam(value="favourite") Boolean favourite, @GraphQLName(value="hasRetentionPolicy") @QueryParam(value="hasRetentionPolicy") Boolean hasRetentionPolicy, @GraphQLExpansionParam @QueryParam(value="expand") @DefaultValue(value="") String expand, @GraphQLName(value="offset") @QueryParam(value="start") int offset, @GraphQLName(value="after") String afterOffset, @GraphQLName(value="first") @QueryParam(value="limit") @DefaultValue(value="25") int limit, @Context UriInfo uriInfo) throws ServiceException {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        SpaceService.SpaceFinder finder = this.spaceService.find(expansions);
        if (!spaceKeys.isEmpty()) {
            finder.withKeys(spaceKeys.toArray(new String[spaceKeys.size()]));
        }
        if (spaceKey != null) {
            finder.withKeys(new String[]{spaceKey});
        }
        if (!Strings.isNullOrEmpty((String)type)) {
            finder.withType(SpaceType.forName((String)type));
        }
        if (!Strings.isNullOrEmpty((String)status)) {
            finder.withStatus(SpaceStatus.valueOf((String)status));
        }
        if (!labelNames.isEmpty()) {
            List<Label> labels = labelNames.stream().map(name -> Label.builder((String)name).prefix(Label.Prefix.team).build()).collect(Collectors.toList());
            finder.withLabels(labels.toArray(new Label[labels.size()]));
        }
        if (favourite != null) {
            finder.withIsFavourited(favourite.booleanValue());
        }
        if (hasRetentionPolicy != null) {
            finder.withHasRetentionPolicy(hasRetentionPolicy.booleanValue());
        }
        RestPageRequest request = new RestPageRequest(uriInfo, GraphQLOffsetCursor.parseOffset(offset, afterOffset), limit);
        PageResponse results = finder.fetchMany((PageRequest)request);
        return RestList.createRestList((PageRequest)request.copyWithLimits(results), (PageResponse)results);
    }

    @GET
    @Path(value="{spaceKey}/content")
    @PublicApi
    public Map<ContentType, RestList<Content>> contents(@PathParam(value="spaceKey") String spaceKey, @QueryParam(value="depth") @DefaultValue(value="all") String depth, @QueryParam(value="expand") @DefaultValue(value="") String expand, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="25") int limit) throws ServiceException {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        SimplePageRequest pageRequest = new SimplePageRequest(start, limit);
        Space space = this.space(spaceKey, "");
        Depth spaceContentDepth = Depth.valueOf((String)depth.toUpperCase());
        Map content = this.spaceService.findContent(space, expansions).withDepth(spaceContentDepth).fetchMappedByType((PageRequest)pageRequest);
        final Navigation.SpaceContentNav spaceContentNav = this.navigationService.createNavigation().space(space).content();
        return BuilderUtils.modelMap((Map)Maps.transformEntries((Map)content, (Maps.EntryTransformer)new Maps.EntryTransformer<ContentType, PageResponse<Content>, RestList<Content>>(){

            public RestList<Content> transformEntry(@Nullable ContentType key, @Nullable PageResponse<Content> pageResponse) {
                Navigation.Builder spaceContentTypeUri = spaceContentNav.type(key);
                return RestList.createRestList((PageRequest)new RestPageRequest(spaceContentTypeUri, pageResponse), pageResponse);
            }
        }));
    }

    @GET
    @Path(value="{spaceKey}/content/{type}")
    @PublicApi
    public RestList<Content> contentsWithType(@PathParam(value="spaceKey") String spaceKey, @PathParam(value="type") String type, @QueryParam(value="depth") @DefaultValue(value="all") String depth, @QueryParam(value="expand") @DefaultValue(value="") String expand, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="25") int limit, @Context UriInfo uriInfo) throws ServiceException {
        ContentType contentType = ContentType.valueOf((String)type);
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        RestPageRequest pageRequest = new RestPageRequest(uriInfo, start, limit);
        Space space = this.space(spaceKey, "");
        Depth spaceContentDepth = Depth.valueOf((String)depth.toUpperCase());
        PageResponse response = this.spaceService.findContent(space, expansions).withDepth(spaceContentDepth).fetchMany(contentType, (PageRequest)pageRequest);
        return RestList.createRestList((PageRequest)pageRequest.copyWithLimits(response), (PageResponse)response);
    }
}

