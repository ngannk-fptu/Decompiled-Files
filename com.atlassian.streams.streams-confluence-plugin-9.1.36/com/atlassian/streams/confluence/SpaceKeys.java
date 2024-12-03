/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ListBuilder
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpaceType
 *  com.atlassian.confluence.spaces.SpacesQuery
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.streams.spi.StreamsKeyProvider$StreamsKey
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Ordering
 */
package com.atlassian.streams.confluence;

import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.streams.spi.StreamsKeyProvider;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import java.util.Comparator;

public class SpaceKeys {
    private final SpaceManager spaceManager;
    private final TransactionTemplate transactionTemplate;
    private final Function<Space, StreamsKeyProvider.StreamsKey> toStreamsKey = space -> new StreamsKeyProvider.StreamsKey(space.getKey(), space.getName());
    private Comparator<Space> spaceSorter = (space1, space2) -> {
        if (space1.getSpaceType().equals((Object)SpaceType.PERSONAL) && space2.getSpaceType().equals((Object)SpaceType.GLOBAL)) {
            return 1;
        }
        if (space1.getSpaceType().equals((Object)SpaceType.GLOBAL) && space2.getSpaceType().equals((Object)SpaceType.PERSONAL)) {
            return -1;
        }
        return space1.getName().compareToIgnoreCase(space2.getName());
    };

    public SpaceKeys(SpaceManager spaceManager, TransactionTemplate transactionTemplate) {
        this.spaceManager = (SpaceManager)Preconditions.checkNotNull((Object)spaceManager, (Object)"spaceManager");
        this.transactionTemplate = (TransactionTemplate)Preconditions.checkNotNull((Object)transactionTemplate, (Object)"transactionTemplate");
    }

    public Iterable<StreamsKeyProvider.StreamsKey> get() {
        return ImmutableList.builder().addAll(this.spaceKeys()).build();
    }

    private Iterable<StreamsKeyProvider.StreamsKey> spaceKeys() {
        return Iterables.transform(this.fetchSpaces(), this.toStreamsKey());
    }

    private Function<Space, StreamsKeyProvider.StreamsKey> toStreamsKey() {
        return this.toStreamsKey;
    }

    private Iterable<Space> fetchSpaces() {
        return (Iterable)this.transactionTemplate.execute(() -> {
            SpacesQuery spacesQuery = SpacesQuery.newQuery().forUser(AuthenticatedUserThreadLocal.getUser()).build();
            ListBuilder spaces = this.spaceManager.getSpaces(spacesQuery);
            return Ordering.from(this.spaceSorter).sortedCopy((Iterable)spaces.getRange(0, spaces.getAvailableSize()));
        });
    }
}

