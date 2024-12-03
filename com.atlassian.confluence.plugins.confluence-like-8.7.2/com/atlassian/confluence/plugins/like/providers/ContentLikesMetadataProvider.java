/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.api.extension.ModelMetadataProvider
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.like.LikeManager
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.plugins.like.providers;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.extension.ModelMetadataProvider;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.like.LikeManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ContentLikesMetadataProvider
implements ModelMetadataProvider {
    private static final String LIKES_EXPAND = "likesCount";
    private final LikeManager likeManager;

    public ContentLikesMetadataProvider(LikeManager likeManager) {
        this.likeManager = likeManager;
    }

    public Map<Object, Map<String, ?>> getMetadataForAll(Iterable<Object> entities, Expansions expansions) {
        Iterable content = Iterables.filter(entities, Content.class);
        ImmutableList searchables = ImmutableList.copyOf((Iterable)Iterables.transform((Iterable)content, this::makeSearchable));
        return this.convertToContentMap(content, this.likeManager.countLikes((Collection)searchables));
    }

    public List<String> getMetadataProperties() {
        return ImmutableList.of((Object)LIKES_EXPAND);
    }

    private Map<String, Object> createLikesMetadata(Object likes) {
        return ImmutableMap.of((Object)LIKES_EXPAND, (Object)likes);
    }

    private Map<Object, Map<String, ?>> convertToContentMap(Iterable<Content> contentList, Map<Searchable, ?> searchableIntegerMap) {
        ImmutableMap contentById = Maps.uniqueIndex(contentList, input -> input.getId().asLong());
        ImmutableMap.Builder mapBuilder = ImmutableMap.builder();
        for (Map.Entry<Searchable, ?> likeCount : searchableIntegerMap.entrySet()) {
            Content content = (Content)contentById.get(likeCount.getKey().getId());
            if (content == null) continue;
            mapBuilder.put((Object)content, this.createLikesMetadata(likeCount.getValue()));
        }
        return mapBuilder.build();
    }

    private Searchable makeSearchable(final Content content) {
        return new Searchable(){

            public long getId() {
                return content.getId().asLong();
            }

            public Collection getSearchableDependants() {
                return null;
            }

            public boolean isIndexable() {
                return false;
            }
        };
    }
}

