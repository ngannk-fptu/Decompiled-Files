/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ListMultimap
 *  com.google.common.collect.Multimaps
 *  io.atlassian.fugue.Pair
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.search.v2.extractor.ContentPermissionExtractorHelper;
import com.atlassian.confluence.impl.search.v2.extractor.IndexablePermissionSetFilter;
import com.atlassian.confluence.impl.search.v2.lucene.ContentPermissionSearchUtils;
import com.atlassian.confluence.internal.security.persistence.ContentPermissionSetDaoInternal;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.search.v2.extractor.BulkExtractor;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import io.atlassian.fugue.Pair;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public final class ContentPermissionsBulkExtractor
implements BulkExtractor<Searchable> {
    private final ContentPermissionSetDaoInternal contentPermissionSetDao;

    public ContentPermissionsBulkExtractor(ContentPermissionSetDaoInternal contentPermissionSetDao) {
        this.contentPermissionSetDao = contentPermissionSetDao;
    }

    @Override
    public boolean canHandle(@Nonnull Class<?> type) {
        return Searchable.class.isAssignableFrom(type);
    }

    @Override
    public void extractAll(@Nonnull Collection<Searchable> searchables, @Nonnull Class<? extends Searchable> entityType, @Nonnull BiConsumer<Searchable, FieldDescriptor> sink) {
        ListMultimap<Long, Pair<Searchable, ContentEntityObject>> searchablesByContainerId = this.mapByContainerId(searchables);
        Set containerIds = searchablesByContainerId.keySet();
        Map<Long, List<ContentPermissionSet>> directPermissions = this.contentPermissionSetDao.getExplicitPermissionSetsFor(containerIds);
        Map<Long, List<ContentPermissionSet>> inheritedPermissions = this.contentPermissionSetDao.getInheritedContentPermissionSets(containerIds);
        searchablesByContainerId.forEach((containerId, entityPair) -> {
            Searchable searchable = (Searchable)entityPair.left();
            ContentEntityObject container = (ContentEntityObject)entityPair.right();
            Collection<ContentPermissionSet> indexablePermissionSets = IndexablePermissionSetFilter.filterPermissionSets(container, directPermissions.getOrDefault(containerId, Collections.emptyList()), inheritedPermissions.getOrDefault(containerId, Collections.emptyList()));
            if (!indexablePermissionSets.isEmpty()) {
                indexablePermissionSets.stream().map(ContentPermissionSearchUtils::getEncodedPermissionsCollection).map(SearchFieldMappings.CONTENT_PERMISSION_SETS::createField).forEach(f -> sink.accept(searchable, (FieldDescriptor)f));
            }
        });
    }

    private ListMultimap<Long, Pair<Searchable, ContentEntityObject>> mapByContainerId(Collection<Searchable> searchables) {
        return (ListMultimap)searchables.stream().flatMap(this::withContainer).collect(Multimaps.toMultimap(pair -> ((ContentEntityObject)pair.right()).getId(), pair -> pair, ArrayListMultimap::create));
    }

    private Stream<Pair<Searchable, ContentEntityObject>> withContainer(Searchable searchable) {
        ContentEntityObject containerForPermissions = ContentPermissionExtractorHelper.getContainerForPermissions(searchable);
        if (containerForPermissions instanceof AbstractPage) {
            return Stream.of(Pair.pair((Object)searchable, (Object)containerForPermissions));
        }
        return Stream.empty();
    }
}

