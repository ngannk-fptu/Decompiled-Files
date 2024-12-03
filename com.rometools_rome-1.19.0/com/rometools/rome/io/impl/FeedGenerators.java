/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.io.WireFeedGenerator;
import com.rometools.rome.io.impl.PluginManager;
import java.util.List;

public class FeedGenerators
extends PluginManager<WireFeedGenerator> {
    public static final String FEED_GENERATORS_KEY = "WireFeedGenerator.classes";

    public FeedGenerators() {
        super(FEED_GENERATORS_KEY);
    }

    public WireFeedGenerator getGenerator(String feedType) {
        return (WireFeedGenerator)this.getPlugin(feedType);
    }

    @Override
    protected String getKey(WireFeedGenerator obj) {
        return obj.getType();
    }

    public List<String> getSupportedFeedTypes() {
        return this.getKeys();
    }
}

