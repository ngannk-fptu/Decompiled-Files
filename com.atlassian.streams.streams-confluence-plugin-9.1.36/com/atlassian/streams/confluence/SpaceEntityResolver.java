/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.spi.EntityResolver
 *  com.google.common.base.Preconditions
 */
package com.atlassian.streams.confluence;

import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.spi.EntityResolver;
import com.google.common.base.Preconditions;

public class SpaceEntityResolver
implements EntityResolver {
    private final SpaceManager spaceManager;

    public SpaceEntityResolver(SpaceManager spaceManager) {
        this.spaceManager = (SpaceManager)Preconditions.checkNotNull((Object)spaceManager, (Object)"spaceManager");
    }

    public Option<Object> apply(String key) {
        return Option.option((Object)this.spaceManager.getSpace(key));
    }
}

