/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.model.group.Group
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Throwables
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.crowd.manager.directory.nestedgroups;

import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.directory.nestedgroups.MultipleGroupsProvider;
import com.atlassian.crowd.manager.directory.nestedgroups.NestedGroupsProvider;
import com.atlassian.crowd.model.group.Group;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

class NestedGroupsProviderImpl
implements NestedGroupsProvider {
    private final MultipleGroupsProvider provider;
    private final Function<String, String> idNormalizer;
    private final Function<Group, String> idProvider;
    private final int batchSize;

    @Override
    public List<Group> getDirectlyRelatedGroups(Collection<String> ids) throws OperationFailedException {
        try {
            return ImmutableList.copyOf((Collection)this.provider.getDirectlyRelatedGroups(ids).values());
        }
        catch (Exception e) {
            Throwables.propagateIfPossible((Throwable)e, OperationFailedException.class);
            throw new OperationFailedException((Throwable)e);
        }
    }

    @Override
    public String getIdentifierForSubGroupsQuery(Group group) {
        return this.idProvider.apply(group);
    }

    @Override
    public String normalizeIdentifier(String id) {
        return this.idNormalizer.apply(id);
    }

    @Override
    public int getMaxBatchSize() {
        return this.batchSize;
    }

    protected NestedGroupsProviderImpl(MultipleGroupsProvider provider, Function<String, String> idNormalizer, Function<Group, String> idProvider, int batchSize) {
        Preconditions.checkArgument((batchSize > 0 ? 1 : 0) != 0, (Object)"Batch size must be greater than 0.");
        this.provider = Objects.requireNonNull(provider);
        this.idNormalizer = Objects.requireNonNull(idNormalizer);
        this.idProvider = Objects.requireNonNull(idProvider);
        this.batchSize = batchSize;
    }
}

