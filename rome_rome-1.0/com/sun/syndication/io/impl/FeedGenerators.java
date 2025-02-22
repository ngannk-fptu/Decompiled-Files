/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.io.impl;

import com.sun.syndication.io.WireFeedGenerator;
import com.sun.syndication.io.impl.PluginManager;
import java.util.List;

public class FeedGenerators
extends PluginManager {
    public static final String FEED_GENERATORS_KEY = "WireFeedGenerator.classes";

    public FeedGenerators() {
        super(FEED_GENERATORS_KEY);
    }

    public WireFeedGenerator getGenerator(String feedType) {
        return (WireFeedGenerator)this.getPlugin(feedType);
    }

    protected String getKey(Object obj) {
        return ((WireFeedGenerator)obj).getType();
    }

    public List getSupportedFeedTypes() {
        return this.getKeys();
    }
}

