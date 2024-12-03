/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.extension.ModelMetadataProvider
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.JsonContentProperty
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.reference.ModelMapBuilder
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.nav.Navigation$Builder
 *  com.atlassian.confluence.api.nav.NavigationService
 *  com.atlassian.confluence.api.service.content.ContentPropertyService
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.plugins.restapi.metadata.content;

import com.atlassian.confluence.api.extension.ModelMetadataProvider;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.JsonContentProperty;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.reference.ModelMapBuilder;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationService;
import com.atlassian.confluence.api.service.content.ContentPropertyService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PropertiesModelMetadataProvider
implements ModelMetadataProvider {
    private static final String PROPERTIES_EXPAND = "properties";
    private static final int PAGE_SIZE = 200;
    private final ContentPropertyService contentPropertyService;
    private final NavigationService navigationService;

    public PropertiesModelMetadataProvider(ContentPropertyService contentPropertyService, NavigationService navigationService) {
        this.contentPropertyService = contentPropertyService;
        this.navigationService = navigationService;
    }

    public Map<Object, Map<String, ?>> getMetadataForAll(Iterable<Object> entities, Expansions expansions) {
        Expansions keyExpansions = expansions.getSubExpansions(PROPERTIES_EXPAND);
        keyExpansions.checkRecursiveExpansion(PROPERTIES_EXPAND);
        List expansionKeys = Lists.transform(Arrays.asList(keyExpansions.toArray()), Expansion::getPropertyName);
        Iterable contents = Iterables.filter(entities, Content.class);
        ArrayList<JsonContentProperty> allProperties = new ArrayList<JsonContentProperty>();
        if (!expansionKeys.isEmpty()) {
            PageResponse properties;
            int current = 0;
            do {
                properties = this.contentPropertyService.find(new Expansion[0]).withPropertyKeys(expansionKeys).withContentIds((List)Lists.newArrayList((Iterable)Iterables.transform((Iterable)contents, Content::getId))).fetchMany((PageRequest)new SimplePageRequest(current, 200));
                Iterables.addAll(allProperties, (Iterable)properties);
                current += properties.size();
            } while (properties.hasMore());
        }
        ImmutableMap.Builder mapBuilder = ImmutableMap.builder();
        for (Content content : contents) {
            Map<String, JsonContentProperty> propertyKeyMap = this.createPropertyKeyMap(keyExpansions, allProperties, content);
            mapBuilder.put((Object)content, (Object)ImmutableMap.of((Object)PROPERTIES_EXPAND, propertyKeyMap));
        }
        return mapBuilder.build();
    }

    private Map<String, JsonContentProperty> createPropertyKeyMap(Expansions keyExpansions, Iterable<JsonContentProperty> properties, Content content) {
        Navigation.Builder navBuilder = this.navigationService.createNavigation().content(content.getSelector()).properties();
        ModelMapBuilder propertyMapBuilder = ModelMapBuilder.newExpandedInstance().navigable(navBuilder);
        for (JsonContentProperty property : properties) {
            if (!content.getId().equals((Object)Content.getSelector((Reference)property.getContentRef()).getId())) continue;
            propertyMapBuilder.put((Object)property.getKey(), (Object)property);
        }
        ArrayList availableKeys = Lists.newArrayList((Iterator)this.contentPropertyService.find(new Expansion[0]).withContentId(content.getId()).fetchPropertyKeys());
        for (String key : availableKeys) {
            if (keyExpansions.canExpand(key)) continue;
            propertyMapBuilder.addCollapsedEntry((Object)key);
        }
        return propertyMapBuilder.build();
    }

    public List<String> getMetadataProperties() {
        return ImmutableList.of((Object)PROPERTIES_EXPAND);
    }
}

