/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.model.group.BaseImmutableGroup
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.ImmutableGroup
 *  com.google.common.base.Throwables
 *  com.google.common.cache.Cache
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ListMultimap
 *  org.apache.commons.lang3.ArrayUtils
 */
package com.atlassian.crowd.manager.directory.nestedgroups;

import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.directory.nestedgroups.MultipleGroupsProvider;
import com.atlassian.crowd.model.group.BaseImmutableGroup;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.ImmutableGroup;
import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.ArrayUtils;

public class CachedMultipleGroupsProvider
implements MultipleGroupsProvider {
    private final Cache<String, String[]> subgroupsCache;
    private final Cache<String, Group> groupsCache;
    private final Function<String, String> idNormalizer;
    private final MultipleGroupsProvider provider;

    protected CachedMultipleGroupsProvider(Cache<String, String[]> subgroupsCache, Cache<String, Group> groupsCache, Function<String, String> idNormalizer, MultipleGroupsProvider provider) {
        this.subgroupsCache = subgroupsCache;
        this.groupsCache = groupsCache;
        this.idNormalizer = idNormalizer;
        this.provider = provider;
    }

    private List<Group> get(String groupName) {
        String[] names = (String[])this.subgroupsCache.getIfPresent((Object)this.idNormalizer.apply(groupName));
        if (names == null) {
            return null;
        }
        List<Group> result = Stream.of(names).map(arg_0 -> this.groupsCache.getIfPresent(arg_0)).collect(Collectors.toList());
        return result.contains(null) ? null : result;
    }

    private void addToCache(Set<String> names, ListMultimap<String, Group> results) {
        Set normalizedMissingNames = names.stream().map(this.idNormalizer).collect(Collectors.toSet());
        HashMap<String, Group> groupMap = new HashMap<String, Group>();
        for (Map.Entry entry : results.asMap().entrySet()) {
            ArrayList<String> subgroupNames = new ArrayList<String>();
            for (Group group : (Collection)entry.getValue()) {
                String normalizedName = this.idNormalizer.apply(group.getName());
                subgroupNames.add(normalizedName);
                groupMap.computeIfAbsent(normalizedName, ignore -> this.createImmutableGroup(group));
            }
            String normalizedParentName = this.idNormalizer.apply((String)entry.getKey());
            this.subgroupsCache.put((Object)normalizedParentName, (Object)subgroupNames.toArray(ArrayUtils.EMPTY_STRING_ARRAY));
            normalizedMissingNames.remove(normalizedParentName);
        }
        for (String normalizedName : normalizedMissingNames) {
            this.subgroupsCache.put((Object)normalizedName, (Object)ArrayUtils.EMPTY_STRING_ARRAY);
        }
        this.groupsCache.putAll(groupMap);
    }

    private Group createImmutableGroup(Group group) {
        if (group instanceof BaseImmutableGroup) {
            return group;
        }
        return ImmutableGroup.from((Group)group);
    }

    @Override
    public ListMultimap<String, Group> getDirectlyRelatedGroups(Collection<String> names) throws OperationFailedException {
        try {
            ArrayListMultimap results = ArrayListMultimap.create();
            HashSet<String> missingNames = new HashSet<String>();
            for (String name : names) {
                List<Group> entry = this.get(name);
                if (entry != null) {
                    results.putAll((Object)name, entry);
                    continue;
                }
                missingNames.add(name);
            }
            if (missingNames.isEmpty()) {
                return results;
            }
            ListMultimap<String, Group> fetchedResults = this.provider.getDirectlyRelatedGroups(missingNames);
            this.addToCache(missingNames, fetchedResults);
            results.putAll(fetchedResults);
            return results;
        }
        catch (ExecutionException e) {
            Throwables.propagateIfPossible((Throwable)e.getCause(), OperationFailedException.class);
            throw new OperationFailedException(e.getCause());
        }
        catch (Exception e) {
            Throwables.propagateIfPossible((Throwable)e, OperationFailedException.class);
            throw new OperationFailedException((Throwable)e);
        }
    }
}

