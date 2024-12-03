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
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestriction
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.SetMultimap
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
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.NullRestriction;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.LongFunction;

public class OptimizedCanonicalityChecker
implements CanonicalityChecker {
    private final DirectoryManagerSearchWrapper directoryManagerSearchWrapper;
    private final List<Directory> directories;
    private final Map<EntityDescriptor, Function<String, Long>> entityToShadowingProviders = new HashMap<EntityDescriptor, Function<String, Long>>();

    public OptimizedCanonicalityChecker(DirectoryManager directoryManager, List<Directory> directories) {
        this.directoryManagerSearchWrapper = new DirectoryManagerSearchWrapper(directoryManager);
        this.directories = ImmutableList.copyOf(directories);
    }

    public OptimizedCanonicalityChecker(DirectoryManager directoryManager, List<Directory> directories, Map<EntityDescriptor, LongFunction<Collection<String>>> providers) {
        this.directoryManagerSearchWrapper = new DirectoryManagerSearchWrapper(directoryManager);
        this.directories = ImmutableList.copyOf(directories);
        providers.forEach((entity, provider) -> this.entityToShadowingProviders.put((EntityDescriptor)entity, this.createNameToShadowingDirProvider((EntityDescriptor)entity, (LongFunction<Collection<String>>)provider)));
    }

    @Override
    public void removeNonCanonicalEntities(Multimap<Long, String> allNames, EntityDescriptor entity) {
        Function nameToShadowingDirProvider = this.entityToShadowingProviders.computeIfAbsent(entity, this::createNameToShadowingDirProvider);
        for (Directory directory : this.directories) {
            Long dirId = directory.getId();
            Iterator it = allNames.get((Object)dirId).iterator();
            while (it.hasNext()) {
                Long shadowedBy = (Long)nameToShadowingDirProvider.apply(it.next());
                if (shadowedBy == null || shadowedBy.equals(dirId)) continue;
                it.remove();
            }
        }
    }

    @Override
    public SetMultimap<Long, String> groupByCanonicalId(Set<String> names, EntityDescriptor entity) {
        throw new UnsupportedOperationException();
    }

    private Function<String, Long> createNameToShadowingDirProvider(EntityDescriptor entity) {
        boolean isUserQuery = entity.equals((Object)EntityDescriptor.user());
        EntityQuery query = QueryBuilder.queryFor(String.class, (EntityDescriptor)entity, (SearchRestriction)NullRestriction.INSTANCE, (int)0, (int)-1);
        return this.createNameToShadowingDirProvider(entity, isUserQuery ? dirId -> this.directoryManagerSearchWrapper.searchUsers(dirId, query) : dirId -> this.directoryManagerSearchWrapper.searchGroups(dirId, query));
    }

    private Function<String, Long> createNameToShadowingDirProvider(EntityDescriptor entity, LongFunction<Collection<String>> searcher) {
        Preconditions.checkArgument((entity.equals((Object)EntityDescriptor.user()) || entity.equals((Object)EntityDescriptor.group()) ? 1 : 0) != 0);
        if (this.directories.size() <= 1) {
            return name -> null;
        }
        HashMap<String, Long> result = new HashMap<String, Long>();
        HashSet<String> seen = new HashSet<String>();
        HashSet<String> shadowed = new HashSet<String>();
        for (Directory directory : this.directories) {
            IdentifierSet lowerCasedNames = new IdentifierSet(searcher.apply(directory.getId()));
            for (String lowerCasedName : lowerCasedNames) {
                if (seen.add(lowerCasedName)) {
                    result.put(lowerCasedName, directory.getId());
                    continue;
                }
                shadowed.add(lowerCasedName);
            }
        }
        result.keySet().retainAll(shadowed);
        ImmutableMap immutableResult = ImmutableMap.copyOf(result);
        return arg_0 -> OptimizedCanonicalityChecker.lambda$createNameToShadowingDirProvider$4((Map)immutableResult, arg_0);
    }

    @Override
    public List<Directory> getDirectories() {
        return this.directories;
    }

    private static /* synthetic */ Long lambda$createNameToShadowingDirProvider$4(Map immutableResult, String name) {
        return (Long)immutableResult.get(IdentifierUtils.toLowerCase((String)name));
    }
}

