/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Label
 *  com.atlassian.confluence.api.model.content.Label$Prefix
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.service.content.ContentLabelService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.graphql.annotations.GraphQLExtensions
 *  com.atlassian.graphql.annotations.GraphQLName
 *  com.atlassian.graphql.spi.GraphQLTypeBuilderContext
 *  com.atlassian.graphql.spi.GraphQLTypeContributor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.ImmutableList
 *  graphql.schema.DataFetchingEnvironment
 *  graphql.schema.GraphQLFieldDefinition
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.UriInfo
 */
package com.atlassian.confluence.plugins.graphql.providers;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.Label;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.service.content.ContentLabelService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.plugins.graphql.providers.GraphQLOffsetCursor;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.graphql.annotations.GraphQLExtensions;
import com.atlassian.graphql.annotations.GraphQLName;
import com.atlassian.graphql.spi.GraphQLTypeBuilderContext;
import com.atlassian.graphql.spi.GraphQLTypeContributor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableList;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@GraphQLExtensions
public class ContentLabelsProvider
implements GraphQLTypeContributor {
    private static final String DEFAULT_LIMIT = "200";
    private final ContentLabelService contentLabelService;

    public ContentLabelsProvider(@ComponentImport ContentLabelService contentLabelService) {
        this.contentLabelService = contentLabelService;
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

    @GraphQLName(value="labels")
    public PageResponse<Label> labels(@GraphQLName(value="prefix") List<String> prefixes, @GraphQLName(value="offset") int offset, @GraphQLName(value="after") String afterOffset, @GraphQLName(value="first") @DefaultValue(value="200") int limit, @Context UriInfo uriInfo, DataFetchingEnvironment env) throws ServiceException {
        ContentId contentId = (ContentId)((Map)env.getSource()).get("id");
        Collection<Label.Prefix> requestPrefixes = this.convertLabelPrefixStrings(prefixes);
        RestPageRequest pageRequest = new RestPageRequest(uriInfo, GraphQLOffsetCursor.parseOffset(offset, afterOffset), limit);
        PageResponse labels = this.contentLabelService.getLabels(contentId, requestPrefixes, (PageRequest)pageRequest);
        return RestList.createRestList((PageRequest)pageRequest.copyWithLimits(labels), (PageResponse)labels);
    }

    private Collection<Label.Prefix> convertLabelPrefixStrings(List<String> prefixes) throws ServiceException {
        if (prefixes == null || prefixes.isEmpty()) {
            return ImmutableList.copyOf((Object[])Label.Prefix.values());
        }
        try {
            return ImmutableList.copyOf((Collection)prefixes.stream().map(Label.Prefix::valueOf).collect(Collectors.toList()));
        }
        catch (Exception ex) {
            throw new BadRequestException("Could not convert label prefixes :" + prefixes, (Throwable)ex);
        }
    }
}

