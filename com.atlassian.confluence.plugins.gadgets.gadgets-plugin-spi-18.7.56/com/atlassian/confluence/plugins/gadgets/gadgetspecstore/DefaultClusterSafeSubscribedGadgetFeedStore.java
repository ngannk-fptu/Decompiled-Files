/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.gadgets.directory.spi.SubscribedGadgetFeed
 *  com.atlassian.gadgets.directory.spi.SubscribedGadgetFeedStore
 *  com.google.common.collect.Iterables
 */
package com.atlassian.confluence.plugins.gadgets.gadgetspecstore;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.confluence.plugins.gadgets.gadgetspecstore.SubscribedGadgetFeedHelper;
import com.atlassian.confluence.plugins.gadgets.refimpl.SubscribedGadgetFeedIdGenerator;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.gadgets.directory.spi.SubscribedGadgetFeed;
import com.atlassian.gadgets.directory.spi.SubscribedGadgetFeedStore;
import com.google.common.collect.Iterables;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class DefaultClusterSafeSubscribedGadgetFeedStore
implements SubscribedGadgetFeedStore {
    private final ClusterLockService clusterLockService;
    private final BandanaManager bandanaManager;
    private final SubscribedGadgetFeedIdGenerator subscribedGadgetFeedIdGenerator;
    private static final String BANDANA_KEY = "confluence.SubscribedGadgetFeedStore.feeds";

    public DefaultClusterSafeSubscribedGadgetFeedStore(ClusterLockService clusterLockService, BandanaManager bandanaManager, SubscribedGadgetFeedIdGenerator subscribedGadgetFeedIdGenerator) {
        this.clusterLockService = clusterLockService;
        this.bandanaManager = bandanaManager;
        this.subscribedGadgetFeedIdGenerator = subscribedGadgetFeedIdGenerator;
    }

    private Iterable<SubscribedGadgetFeed> getEntries() {
        Iterable existingEntries = (Iterable)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, BANDANA_KEY);
        if (existingEntries == null) {
            return Collections.emptyList();
        }
        return Iterables.transform((Iterable)existingEntries, SubscribedGadgetFeedHelper.toFeed());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SubscribedGadgetFeed addFeed(URI feedUri) {
        if (feedUri == null) {
            throw new IllegalArgumentException("Cannot add null feedUri");
        }
        ClusterLock lock = this.clusterLockService.getLockForName(DefaultClusterSafeSubscribedGadgetFeedStore.class.getName() + ".executionlock");
        lock.lock();
        try {
            Iterable<SubscribedGadgetFeed> feeds = this.getEntries();
            for (SubscribedGadgetFeed feed : feeds) {
                if (!feed.getUri().equals(feedUri)) continue;
                SubscribedGadgetFeed subscribedGadgetFeed = feed;
                return subscribedGadgetFeed;
            }
            SubscribedGadgetFeed gadgetSpec = new SubscribedGadgetFeed(this.subscribedGadgetFeedIdGenerator.newSubscribedGadgetFeedId(), feedUri);
            this.saveSpecs(Iterables.concat(feeds, Collections.singletonList(gadgetSpec)));
            SubscribedGadgetFeed subscribedGadgetFeed = gadgetSpec;
            return subscribedGadgetFeed;
        }
        finally {
            lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeFeed(String feedId) {
        if (feedId == null) {
            throw new IllegalArgumentException("feedId cannot be null");
        }
        ClusterLock lock = this.clusterLockService.getLockForName(DefaultClusterSafeSubscribedGadgetFeedStore.class.getName() + ".executionlock");
        lock.lock();
        try {
            Iterable<SubscribedGadgetFeed> feeds = this.getEntries();
            Iterables.removeIf(feeds, SubscribedGadgetFeedHelper.findFeed(feedId));
            this.saveSpecs(feeds);
        }
        finally {
            lock.unlock();
        }
    }

    private void saveSpecs(Iterable<SubscribedGadgetFeed> list) {
        ArrayList<Map> feeds = new ArrayList<Map>();
        for (Map feed : Iterables.transform(list, SubscribedGadgetFeedHelper.toMap())) {
            feeds.add(feed);
        }
        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, BANDANA_KEY, feeds);
    }

    public boolean containsFeed(String feedId) {
        return Iterables.any(this.getEntries(), SubscribedGadgetFeedHelper.findFeed(feedId));
    }

    public SubscribedGadgetFeed getFeed(String feedId) {
        return (SubscribedGadgetFeed)Iterables.find(this.getEntries(), SubscribedGadgetFeedHelper.findFeed(feedId));
    }

    public Iterable<SubscribedGadgetFeed> getAllFeeds() {
        return this.getEntries();
    }
}

