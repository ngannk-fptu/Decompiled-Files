/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.spi.StreamsKeyProvider
 *  com.atlassian.streams.spi.StreamsKeyProvider$StreamsKey
 *  com.google.common.base.Preconditions
 */
package com.atlassian.streams.confluence;

import com.atlassian.streams.confluence.SpaceKeys;
import com.atlassian.streams.spi.StreamsKeyProvider;
import com.google.common.base.Preconditions;

public class ConfluenceStreamsKeyProvider
implements StreamsKeyProvider {
    private final SpaceKeys spaceKeys;

    public ConfluenceStreamsKeyProvider(SpaceKeys spaceKeys) {
        this.spaceKeys = (SpaceKeys)Preconditions.checkNotNull((Object)spaceKeys, (Object)"spaceKeys");
    }

    public Iterable<StreamsKeyProvider.StreamsKey> getKeys() {
        return this.spaceKeys.get();
    }
}

