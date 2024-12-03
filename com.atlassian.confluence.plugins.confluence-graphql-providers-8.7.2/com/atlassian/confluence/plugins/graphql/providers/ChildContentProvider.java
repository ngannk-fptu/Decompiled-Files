/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.service.content.ChildContentService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.confluence.rest.serialization.graphql.GraphQLPagination
 *  com.atlassian.confluence.rest.serialization.graphql.GraphQLPaginationInfo
 *  com.atlassian.graphql.annotations.GraphQLExtensions
 *  com.atlassian.graphql.annotations.GraphQLName
 *  com.atlassian.graphql.annotations.expansions.GraphQLExpansionParam
 *  com.atlassian.graphql.spi.GraphQLTypeBuilderContext
 *  com.atlassian.graphql.spi.GraphQLTypeContributor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.google.common.collect.Lists
 *  graphql.schema.DataFetchingEnvironment
 *  graphql.schema.GraphQLFieldDefinition
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.core.UriInfo
 */
package com.atlassian.confluence.plugins.graphql.providers;

import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.service.content.ChildContentService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.plugins.graphql.providers.GraphQLOffsetCursor;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.confluence.rest.serialization.graphql.GraphQLPagination;
import com.atlassian.confluence.rest.serialization.graphql.GraphQLPaginationInfo;
import com.atlassian.graphql.annotations.GraphQLExtensions;
import com.atlassian.graphql.annotations.GraphQLName;
import com.atlassian.graphql.annotations.expansions.GraphQLExpansionParam;
import com.atlassian.graphql.spi.GraphQLTypeBuilderContext;
import com.atlassian.graphql.spi.GraphQLTypeContributor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.Lists;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.UriInfo;

@AnonymousAllowed
@GraphQLExtensions
public class ChildContentProvider
implements GraphQLTypeContributor {
    private final ChildContentService childContentService;

    public ChildContentProvider(@ComponentImport ChildContentService childContentService) {
        this.childContentService = childContentService;
    }

    public String contributeTypeName(String typeName, Type type, GraphQLTypeBuilderContext context) {
        return null;
    }

    public void contributeFields(String typeName, Type type, List<GraphQLFieldDefinition> fields, GraphQLTypeBuilderContext context) {
        if (!context.isCurrentType(Content.class)) {
            return;
        }
        fields.addAll(context.buildProviderGraphQLType("query", (Object)this).getFieldDefinitions());
    }

    @GraphQLName(value="children")
    public GraphQLPagination<Content> children(DataFetchingEnvironment env, @GraphQLExpansionParam @DefaultValue(value="") String expand, @GraphQLName(value="type") @DefaultValue(value="page") String type, @GraphQLName(value="parentVersion") @DefaultValue(value="0") Integer parentVersion, @GraphQLName(value="offset") int offset, @GraphQLName(value="after") String afterOffset, @GraphQLName(value="first") @DefaultValue(value="25") int limit, UriInfo uriInfo) throws ServiceException {
        return this.childrenOfType(env, ContentType.valueOf((String)type), expand, parentVersion, offset, afterOffset, limit, uriInfo);
    }

    @GraphQLName(value="attachments")
    public GraphQLPagination<Content> attachments(DataFetchingEnvironment env, @GraphQLExpansionParam @DefaultValue(value="") String expand, @GraphQLName(value="parentVersion") @DefaultValue(value="0") Integer parentVersion, @GraphQLName(value="offset") int offset, @GraphQLName(value="after") String afterOffset, @GraphQLName(value="first") @DefaultValue(value="25") int limit, UriInfo uriInfo) throws ServiceException {
        return this.childrenOfType(env, ContentType.ATTACHMENT, expand, parentVersion, offset, afterOffset, limit, uriInfo);
    }

    @GraphQLName(value="comments")
    public GraphQLPagination<Content> comments(final DataFetchingEnvironment env, final @GraphQLExpansionParam @DefaultValue(value="") String expand, final @GraphQLName(value="parentVersion") @DefaultValue(value="0") Integer parentVersion, final @GraphQLName(value="offset") int offset, final @GraphQLName(value="after") String afterOffset, final @GraphQLName(value="first") @DefaultValue(value="25") int limit, final @GraphQLName(value="location") Set<String> location, final @GraphQLName(value="depth") @DefaultValue(value="") String depth, final UriInfo uriInfo) throws ServiceException {
        return new GraphQLPagination<Content>(){

            protected void load() {
                ContentId parentId = ChildContentProvider.getContentIdFromSource(env);
                Expansion[] expansions = ExpansionsParser.parse((String)expand);
                RestPageRequest pageRequest = new RestPageRequest(uriInfo, GraphQLOffsetCursor.parseOffset(offset, afterOffset), limit);
                Depth fetchDepth = depth.equalsIgnoreCase("all") ? Depth.ALL : Depth.ROOT;
                PageResponse pageResponse = ChildContentProvider.this.childContentService.findContent(parentId, expansions).withDepth(fetchDepth).withLocation((Collection)location).withParentVersion(parentVersion.intValue()).fetchMany(ContentType.COMMENT, (PageRequest)pageRequest);
                ArrayList nodes = Lists.newArrayList((Iterable)pageResponse);
                this.setNodes(nodes);
                this.setCount(nodes.size());
                this.setEdges(1.buildEdges((List)nodes, (node, index) -> index.toString()));
                this.setPageInfo(new GraphQLPaginationInfo(pageResponse.hasMore()));
            }
        };
    }

    private GraphQLPagination<Content> childrenOfType(final DataFetchingEnvironment env, final ContentType type, final String expand, final Integer parentVersion, final int offset, final String afterOffset, final int limit, final UriInfo uriInfo) throws ServiceException {
        if (type.equals((Object)ContentType.COMMENT)) {
            return this.comments(env, expand, parentVersion, offset, afterOffset, limit, Collections.emptySet(), "", uriInfo);
        }
        return new GraphQLPagination<Content>(){

            protected void load() {
                ContentId parentId = ChildContentProvider.getContentIdFromSource(env);
                if (parentId == null) {
                    throw new BadRequestException("parentId must be specified");
                }
                Expansion[] expansions = ExpansionsParser.parse((String)expand);
                RestPageRequest pageRequest = new RestPageRequest(uriInfo, GraphQLOffsetCursor.parseOffset(offset, afterOffset), limit);
                PageResponse pageResponse = ChildContentProvider.this.childContentService.findContent(parentId, expansions).withParentVersion(parentVersion.intValue()).fetchMany(type, (PageRequest)pageRequest);
                ArrayList nodes = Lists.newArrayList((Iterable)pageResponse);
                this.setNodes(nodes);
                this.setCount(nodes.size());
                this.setEdges(2.buildEdges((List)nodes, (node, index) -> index.toString()));
                this.setPageInfo(new GraphQLPaginationInfo(pageResponse.hasMore()));
            }
        };
    }

    private static ContentId getContentIdFromSource(DataFetchingEnvironment env) {
        if (env.getSource() instanceof Map) {
            return (ContentId)((Map)env.getSource()).get("id");
        }
        if (env.getSource() instanceof Content) {
            return ((Content)env.getSource()).getId();
        }
        throw new IllegalArgumentException("Unexpected source type: " + env.getSource());
    }
}

