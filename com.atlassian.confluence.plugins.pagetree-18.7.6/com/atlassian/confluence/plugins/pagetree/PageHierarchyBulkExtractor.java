/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ContentQuery
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor$Store
 *  com.atlassian.confluence.plugins.index.api.StringFieldDescriptor
 *  com.atlassian.confluence.search.v2.extractor.BulkExtractor
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Multimaps
 *  io.atlassian.fugue.Pair
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.pagetree;

import com.atlassian.confluence.content.ContentQuery;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.StringFieldDescriptor;
import com.atlassian.confluence.search.v2.extractor.BulkExtractor;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.atlassian.fugue.Pair;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public final class PageHierarchyBulkExtractor
implements BulkExtractor<ContentEntityObject> {
    static final String ANCESTORS_KEY = "ancestorIds";
    static final String POSITION_KEY = "position";
    static final int UNSPECIFIED_POSITION = -1;
    private final CustomContentManager customContentManager;

    public PageHierarchyBulkExtractor(CustomContentManager customContentManager) {
        this.customContentManager = Objects.requireNonNull(customContentManager);
    }

    public boolean canHandle(@Nonnull Class<?> entityType) {
        return Page.class.isAssignableFrom(entityType) || Attachment.class.isAssignableFrom(entityType);
    }

    public void extractAll(@Nonnull Collection<ContentEntityObject> searchables, @Nonnull Class<? extends ContentEntityObject> entityType, @Nonnull BiConsumer<ContentEntityObject, FieldDescriptor> sink) {
        Multimap<Long, ContentEntityObject> searchablesByPageId = this.mapByContainingPageId(searchables);
        if (searchablesByPageId.isEmpty()) {
            return;
        }
        Collection<Page> pages = this.queryForPages(searchablesByPageId.keySet());
        searchablesByPageId.forEach((pageId, searchable) -> pages.stream().filter(page -> Objects.equals(page.getId(), pageId)).findFirst().ifPresent(page -> this.extractFields((Page)page, (ContentEntityObject)searchable, sink)));
    }

    private Multimap<Long, ContentEntityObject> mapByContainingPageId(Collection<ContentEntityObject> searchables) {
        return (Multimap)searchables.stream().flatMap(searchable -> PageHierarchyBulkExtractor.getContainingPageId(searchable).map(page -> Pair.pair((Object)searchable, (Object)page))).collect(Multimaps.toMultimap(Pair::right, Pair::left, ArrayListMultimap::create));
    }

    private Collection<Page> queryForPages(Collection<Long> pageIds) {
        ContentQuery contentQuery = new ContentQuery("pagetree.bulkQueryPageAncestors", pageIds.toArray(new Object[0]));
        return this.customContentManager.queryForList(contentQuery);
    }

    private void extractFields(Page page, ContentEntityObject searchable, BiConsumer<ContentEntityObject, FieldDescriptor> sink) {
        Stream.concat(Stream.of(page), page.getAncestors().stream()).map(this::createAncestorField).forEach(field -> sink.accept(searchable, (FieldDescriptor)field));
        if (searchable instanceof Page) {
            sink.accept(searchable, (FieldDescriptor)this.createPositionField(page));
        }
    }

    private FieldDescriptor createAncestorField(Page page) {
        return new StringFieldDescriptor(ANCESTORS_KEY, String.valueOf(page.getId()), FieldDescriptor.Store.YES);
    }

    private StringFieldDescriptor createPositionField(Page page) {
        int position = Optional.ofNullable(page.getPosition()).orElse(-1);
        return new StringFieldDescriptor(POSITION_KEY, String.valueOf(position), FieldDescriptor.Store.YES);
    }

    private static Stream<Long> getContainingPageId(ContentEntityObject searchable) {
        Attachment attachment;
        ContentEntityObject container;
        if (searchable instanceof Page) {
            return Stream.of(Long.valueOf(searchable.getId()));
        }
        if (searchable instanceof Attachment && (container = (attachment = (Attachment)searchable).getContainer()) instanceof Page) {
            return Stream.of(Long.valueOf(container.getId()));
        }
        return Stream.empty();
    }
}

