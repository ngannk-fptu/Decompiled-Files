/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.impl.IdentifierSet
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Multimaps
 *  com.google.common.collect.SetMultimap
 *  com.google.common.collect.Table
 */
package com.atlassian.crowd.manager.application.canonicality;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.impl.IdentifierSet;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.manager.application.canonicality.CanonicalityChecker;
import com.atlassian.crowd.manager.application.search.DirectoryManagerSearchWrapper;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.NullRestriction;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Table;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class SimpleCanonicalityChecker
implements CanonicalityChecker {
    protected static final int BATCH_SIZE = 1000;
    private final DirectoryManagerSearchWrapper directoryManagerSearchWrapper;
    private final List<Directory> directories;
    private final Table<EntityDescriptor, String, Long> nameAndEntityToCanonicalDirId = HashBasedTable.create();
    private final Table<EntityDescriptor, Long, IdentifierSet> allEntities = HashBasedTable.create();
    private final int batchSize;

    public SimpleCanonicalityChecker(DirectoryManager directoryManager, List<Directory> directories) {
        this(directoryManager, directories, 1000);
    }

    @VisibleForTesting
    SimpleCanonicalityChecker(DirectoryManager directoryManager, List<Directory> directories, int batchSize) {
        this.directoryManagerSearchWrapper = new DirectoryManagerSearchWrapper(directoryManager);
        this.directories = ImmutableList.copyOf(directories);
        this.batchSize = batchSize;
    }

    @Override
    public void removeNonCanonicalEntities(Multimap<Long, String> allNames, EntityDescriptor entityDescriptor) {
        Map nameToCanonicalDirId = this.nameAndEntityToCanonicalDirId.row((Object)entityDescriptor);
        this.computeMissing(allNames.values(), entityDescriptor, (directory, unknown) -> {
            long dirId = directory.getId();
            Iterator it = allNames.get((Object)dirId).iterator();
            while (it.hasNext()) {
                String lowerName = IdentifierUtils.toLowerCase((String)((String)it.next()));
                unknown.removeAll((Object)lowerName);
                long canonicalDirId = nameToCanonicalDirId.computeIfAbsent(lowerName, ignore -> dirId);
                if (canonicalDirId == dirId) continue;
                it.remove();
            }
        });
    }

    @Override
    public SetMultimap<Long, String> groupByCanonicalId(Set<String> names, EntityDescriptor entityDescriptor) {
        this.computeMissing(names, entityDescriptor, (dir, unknown) -> {});
        Map nameToCanonicalDirId = this.nameAndEntityToCanonicalDirId.row((Object)entityDescriptor);
        HashMultimap result = HashMultimap.create();
        for (String name : names) {
            Long dirId = (Long)nameToCanonicalDirId.get(IdentifierUtils.toLowerCase((String)name));
            if (dirId == null) continue;
            result.put((Object)dirId, (Object)name);
        }
        return result;
    }

    private void computeMissing(Collection<String> names, EntityDescriptor entityDescriptor, BiConsumer<Directory, SetMultimap<String, String>> consumer) {
        Map nameToCanonicalDirId = this.nameAndEntityToCanonicalDirId.row((Object)entityDescriptor);
        HashMultimap unknown = HashMultimap.create((Multimap)Multimaps.index(names, IdentifierUtils::toLowerCase));
        unknown.keySet().removeAll(nameToCanonicalDirId.keySet());
        for (Directory directory : this.directories) {
            consumer.accept(directory, (SetMultimap<String, String>)unknown);
            IdentifierSet found = this.findEntitiesInternal(unknown.values(), entityDescriptor, directory);
            unknown.keySet().removeAll((Collection<?>)found);
            found.forEach(name -> nameToCanonicalDirId.put(name, directory.getId()));
        }
    }

    private IdentifierSet findEntitiesInternal(Collection<String> candidates, EntityDescriptor entity, Directory directory) {
        Preconditions.checkArgument((entity.equals((Object)EntityDescriptor.user()) || entity.equals((Object)EntityDescriptor.group()) ? 1 : 0) != 0);
        if (candidates.isEmpty()) {
            return IdentifierSet.EMPTY;
        }
        EntityQuery allNamesQuery = QueryBuilder.queryFor(String.class, (EntityDescriptor)entity, (SearchRestriction)NullRestriction.INSTANCE, (int)0, (int)-1);
        IdentifierSet allEntities = (IdentifierSet)this.allEntities.get((Object)entity, (Object)directory.getId());
        if (allEntities == null && candidates.size() > this.batchSize) {
            allEntities = new IdentifierSet(this.directoryManagerSearchWrapper.search(directory.getId(), allNamesQuery));
            this.allEntities.put((Object)entity, (Object)directory.getId(), (Object)allEntities);
        }
        if (allEntities != null) {
            return IdentifierSet.intersection((Collection)allEntities, candidates);
        }
        boolean isUserQuery = entity.equals((Object)EntityDescriptor.user());
        SearchRestriction restriction = Restriction.on((Property)(isUserQuery ? UserTermKeys.USERNAME : GroupTermKeys.NAME)).exactlyMatchingAny(candidates);
        return new IdentifierSet(this.directoryManagerSearchWrapper.search(directory.getId(), allNamesQuery.withSearchRestriction(restriction)));
    }

    @Override
    public List<Directory> getDirectories() {
        return this.directories;
    }
}

