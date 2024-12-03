/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.extension.MetadataProperty
 *  com.atlassian.confluence.api.extension.ModelMetadataProvider
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Label
 *  com.atlassian.confluence.api.model.content.Label$Prefix
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.nav.Navigation$Builder
 *  com.atlassian.confluence.api.service.content.ContentLabelService
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.confluence.rest.api.services.RestNavigationService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.reflect.TypeToken
 */
package com.atlassian.confluence.plugins.restapi.metadata.content;

import com.atlassian.confluence.api.extension.MetadataProperty;
import com.atlassian.confluence.api.extension.ModelMetadataProvider;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.Label;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.service.content.ContentLabelService;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.confluence.rest.api.services.RestNavigationService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LabelsModelMetadataProvider
implements ModelMetadataProvider {
    private static final String LABELS_EXPAND = "labels";
    private final ContentLabelService contentLabelService;
    private final RestNavigationService navigationService;

    public LabelsModelMetadataProvider(@ComponentImport ContentLabelService contentLabelService, @ComponentImport RestNavigationService navigationService) {
        this.contentLabelService = contentLabelService;
        this.navigationService = navigationService;
    }

    private Map<String, ?> getMetadata(Object entity) throws NotFoundException {
        if (!(entity instanceof Content)) {
            return Collections.emptyMap();
        }
        Content content = (Content)entity;
        Navigation.Builder navBuilder = this.navigationService.createNavigation().content(content).label();
        RestPageRequest request = new RestPageRequest(navBuilder, 0, Integer.MAX_VALUE);
        PageResponse response = this.contentLabelService.getLabels(content.getId(), (Collection)ImmutableList.of((Object)Label.Prefix.global), (PageRequest)request);
        return Collections.singletonMap(LABELS_EXPAND, RestList.createRestList((PageRequest)request.copyWithLimits(response), (PageResponse)response));
    }

    public Map<Object, Map<String, ?>> getMetadataForAll(Iterable<Object> entities, Expansions expansions) {
        ImmutableMap.Builder mapBuilder = ImmutableMap.builder();
        for (Content content : Iterables.filter(entities, Content.class)) {
            try {
                mapBuilder.put((Object)content, this.getMetadata(content));
            }
            catch (NotFoundException notFoundException) {}
        }
        return mapBuilder.build();
    }

    public List<String> getMetadataProperties() {
        return ImmutableList.of((Object)LABELS_EXPAND);
    }

    public List<MetadataProperty> getProperties() {
        return Collections.singletonList(new MetadataProperty(LABELS_EXPAND, new TypeToken<List<Label>>(){}.getType()));
    }
}

