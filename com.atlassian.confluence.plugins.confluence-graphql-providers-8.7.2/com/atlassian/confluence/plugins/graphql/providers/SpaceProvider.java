/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.graphql.annotations.GraphQLBatchLoader
 *  com.atlassian.graphql.annotations.GraphQLName
 *  com.atlassian.graphql.annotations.GraphQLNonNull
 *  com.atlassian.graphql.annotations.GraphQLProvider
 *  com.atlassian.graphql.annotations.expansions.GraphQLExpansionParam
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.QueryParam
 *  org.dataloader.BatchLoader
 *  org.dataloader.DataLoaderRegistry
 */
package com.atlassian.confluence.plugins.graphql.providers;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.plugins.graphql.providers.ExpandableRequest;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.graphql.annotations.GraphQLBatchLoader;
import com.atlassian.graphql.annotations.GraphQLName;
import com.atlassian.graphql.annotations.GraphQLNonNull;
import com.atlassian.graphql.annotations.GraphQLProvider;
import com.atlassian.graphql.annotations.expansions.GraphQLExpansionParam;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import org.dataloader.BatchLoader;
import org.dataloader.DataLoaderRegistry;

@AnonymousAllowed
@GraphQLProvider
@GraphQLName(value="space")
public class SpaceProvider {
    private final SpaceService spaceService;

    public SpaceProvider(@ComponentImport SpaceService spaceService) {
        this.spaceService = Objects.requireNonNull(spaceService);
    }

    @GraphQLName
    public CompletableFuture<Space> space(@GraphQLName(value="key") @GraphQLNonNull String key, @GraphQLExpansionParam @QueryParam(value="expand") @DefaultValue(value="") String expand, DataLoaderRegistry registry) throws ServiceException {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        if (key != null) {
            return registry.getDataLoader("loadSpaceByKey").load(new ExpandableRequest<String>(key, expansions));
        }
        return null;
    }

    @GraphQLBatchLoader
    public BatchLoader<ExpandableRequest<String>, Space> loadSpaceByKey() {
        return keys -> ExpandableRequest.queryByExpansions(keys, Space::getKey, (expansions, ids) -> this.spaceService.find(expansions).withKeys((String[])keys.stream().map(ExpandableRequest::getKey).toArray(String[]::new)).fetchMany((PageRequest)new SimplePageRequest(0, keys.size())).getResults());
    }
}

