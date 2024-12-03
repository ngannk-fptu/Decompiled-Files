/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.directory.spi.SubscribedGadgetFeed
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.gadgets.gadgetspecstore;

import com.atlassian.gadgets.directory.spi.SubscribedGadgetFeed;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class SubscribedGadgetFeedHelper {
    private static final String ID_KEY = "id";
    private static final String FEED_URI_KEY = "feedURI";

    static Function<Map<String, Object>, SubscribedGadgetFeed> toFeed() {
        return new MapToSubscribedGadgetFeed();
    }

    static Function<SubscribedGadgetFeed, Map<String, Object>> toMap() {
        return new SubscribedGadgetFeedToMap();
    }

    static Predicate<SubscribedGadgetFeed> findFeed(String feedId) {
        return new FindSubscribedGadgetFeed(feedId);
    }

    private static final class FindSubscribedGadgetFeed
    implements Predicate<SubscribedGadgetFeed> {
        private final String feedId;

        public FindSubscribedGadgetFeed(String feedId) {
            this.feedId = feedId;
        }

        public boolean apply(@Nullable SubscribedGadgetFeed subscribedGadgetFeed) {
            return subscribedGadgetFeed != null && this.feedId.equals(subscribedGadgetFeed.getId());
        }
    }

    private static final class SubscribedGadgetFeedToMap
    implements Function<SubscribedGadgetFeed, Map<String, Object>> {
        private SubscribedGadgetFeedToMap() {
        }

        public Map<String, Object> apply(@Nullable SubscribedGadgetFeed feed) {
            if (feed == null) {
                throw new IllegalArgumentException("feed cannot be null.");
            }
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(SubscribedGadgetFeedHelper.ID_KEY, feed.getId());
            map.put(SubscribedGadgetFeedHelper.FEED_URI_KEY, feed.getUri());
            return map;
        }
    }

    private static final class MapToSubscribedGadgetFeed
    implements Function<Map<String, Object>, SubscribedGadgetFeed> {
        private MapToSubscribedGadgetFeed() {
        }

        public SubscribedGadgetFeed apply(@Nullable Map<String, Object> map) {
            if (map == null) {
                throw new IllegalArgumentException("map cannot be null.");
            }
            String id = (String)map.get(SubscribedGadgetFeedHelper.ID_KEY);
            URI feedURI = (URI)map.get(SubscribedGadgetFeedHelper.FEED_URI_KEY);
            return new SubscribedGadgetFeed(id, feedURI);
        }
    }
}

