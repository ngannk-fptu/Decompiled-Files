/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupType
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ArrayListMultimap
 */
package com.atlassian.crowd.manager.directory.nestedgroups;

import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.manager.directory.nestedgroups.CachedMultipleGroupsProvider;
import com.atlassian.crowd.manager.directory.nestedgroups.MultipleGroupsProvider;
import com.atlassian.crowd.manager.directory.nestedgroups.NestedGroupsCacheProvider;
import com.atlassian.crowd.manager.directory.nestedgroups.NestedGroupsProvider;
import com.atlassian.crowd.manager.directory.nestedgroups.NestedGroupsProviderImpl;
import com.atlassian.crowd.manager.directory.nestedgroups.SingleGroupProvider;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupType;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import java.util.function.Function;

public class NestedGroupsProviderBuilder {
    private MultipleGroupsProvider provider;
    private Function<String, String> idNormalizer;
    private Function<Group, String> idProvider;
    private int batchSize;

    private NestedGroupsProviderBuilder() {
    }

    public static NestedGroupsProviderBuilder create() {
        return new NestedGroupsProviderBuilder();
    }

    public NestedGroupsProviderBuilder setProvider(MultipleGroupsProvider provider) {
        this.provider = provider;
        return this;
    }

    public NestedGroupsProviderBuilder setSingleGroupProvider(SingleGroupProvider provider) {
        this.setProvider(names -> {
            ArrayListMultimap results = ArrayListMultimap.create();
            for (String name : names) {
                results.putAll((Object)name, provider.getDirectlyRelatedGroups(name));
            }
            return results;
        });
        this.setBatchSize(1);
        return this;
    }

    public NestedGroupsProviderBuilder useGroupName() {
        return this.setIdProvider(DirectoryEntity::getName).setIdNormalizer(IdentifierUtils::toLowerCase);
    }

    public NestedGroupsProviderBuilder useExternalId() {
        return this.setIdProvider(Group::getExternalId).setIdNormalizer(Function.identity());
    }

    public NestedGroupsProviderBuilder setBatchSize(int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public NestedGroupsProvider build() {
        return new NestedGroupsProviderImpl(this.provider, this.idNormalizer, this.idProvider, this.batchSize);
    }

    private NestedGroupsProviderBuilder setIdNormalizer(Function<String, String> idNormalizer) {
        this.idNormalizer = idNormalizer;
        return this;
    }

    private NestedGroupsProviderBuilder setIdProvider(Function<Group, String> idProvider) {
        this.idProvider = idProvider;
        return this;
    }

    public void useCache(NestedGroupsCacheProvider cacheProvider, long directoryId, boolean isChildrenQuery, GroupType groupType) {
        Preconditions.checkNotNull(this.idNormalizer, (Object)"All fields should be set before calling useCache");
        Preconditions.checkNotNull((Object)this.provider, (Object)"All fields should be set before calling useCache");
        this.provider = new CachedMultipleGroupsProvider(cacheProvider.getSubgroupsCache(directoryId, isChildrenQuery, groupType), cacheProvider.getGroupsCache(directoryId, groupType), this.idNormalizer, this.provider);
    }
}

