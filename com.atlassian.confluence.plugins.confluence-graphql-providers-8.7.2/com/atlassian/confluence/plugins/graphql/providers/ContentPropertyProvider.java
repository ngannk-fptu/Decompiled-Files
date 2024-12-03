/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.JsonContentProperty
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.content.ContentPropertyService
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.graphql.annotations.GraphQLExtensions
 *  com.atlassian.graphql.annotations.GraphQLName
 *  com.atlassian.graphql.annotations.expansions.GraphQLExpansionParam
 *  com.atlassian.graphql.spi.GraphQLTypeBuilderContext
 *  com.atlassian.graphql.spi.GraphQLTypeContributor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  graphql.schema.DataFetchingEnvironment
 *  graphql.schema.GraphQLFieldDefinition
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.UriInfo
 */
package com.atlassian.confluence.plugins.graphql.providers;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.JsonContentProperty;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.content.ContentPropertyService;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.graphql.annotations.GraphQLExtensions;
import com.atlassian.graphql.annotations.GraphQLName;
import com.atlassian.graphql.annotations.expansions.GraphQLExpansionParam;
import com.atlassian.graphql.spi.GraphQLTypeBuilderContext;
import com.atlassian.graphql.spi.GraphQLTypeContributor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@GraphQLExtensions
public class ContentPropertyProvider
implements GraphQLTypeContributor {
    private static final String DEFAULT_LIMIT = "10";
    private final ContentPropertyService service;

    public ContentPropertyProvider(@ComponentImport ContentPropertyService service) {
        this.service = service;
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

    @GraphQLName(value="properties")
    public RestList<JsonContentProperty> properties(@GraphQLName(value="key") String key, @GraphQLExpansionParam String expand, @GraphQLName(value="start") int start, @GraphQLName(value="limit") @DefaultValue(value="10") int limit, @Context UriInfo uriInfo, DataFetchingEnvironment env) throws ServiceException {
        ContentId contentId = (ContentId)((Map)env.getSource()).get("id");
        if (key != null) {
            Expansion[] expansions = ExpansionsParser.parse((String)expand);
            Optional contentProperty = this.service.find(expansions).withContentId(contentId).withPropertyKey(key).fetch();
            return contentProperty.isPresent() ? RestList.newRestList().pageRequest((PageRequest)new SimplePageRequest(0, 1)).results(Collections.singletonList((JsonContentProperty)contentProperty.get()), false).build() : RestList.newRestList().pageRequest((PageRequest)new SimplePageRequest(0, 1)).build();
        }
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        RestPageRequest pageRequest = new RestPageRequest(uriInfo, start, limit);
        PageResponse response = this.service.find(expansions).withContentId(contentId).fetchMany((PageRequest)pageRequest);
        return RestList.createRestList((PageRequest)pageRequest, (PageResponse)response);
    }
}

