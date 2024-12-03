/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.ITopic
 */
package com.atlassian.cache.hazelcast.asyncinvalidation;

import com.atlassian.cache.Cache;
import com.atlassian.cache.hazelcast.asyncinvalidation.ClusterNode;
import com.atlassian.cache.hazelcast.asyncinvalidation.LocalMemberFilteringHazelcastTopic;
import com.atlassian.cache.hazelcast.asyncinvalidation.SequenceSnapshot;
import com.atlassian.cache.hazelcast.asyncinvalidation.Topic;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

final class Topics
implements AutoCloseable {
    private final TopicLookup topicLookup;
    private final Collection<Topic.Registration> topicRegistrations = new ArrayList<Topic.Registration>();

    static Topics from(HazelcastInstance hazelcast, Function<String, String> topicNamePrefixer) {
        return new Topics(TopicLookup.fromHazelcast(hazelcast, topicNamePrefixer));
    }

    private Topics(TopicLookup topicLookup) {
        this.topicLookup = topicLookup;
    }

    <K extends Serializable> Topic<SequenceSnapshot<K>> sequenceSnapshot() {
        return this.topicLookup.findTopic("SequenceSnapshot");
    }

    <T extends Serializable> Topic<T> cacheInvalidation(Cache<?, ?> cache) {
        return this.topicLookup.findTopic("Invalidation." + cache.getName());
    }

    <T extends Serializable> Topic<T> cacheReplication(Cache<?, ?> cache) {
        return this.topicLookup.findTopic("Replication." + cache.getName());
    }

    public void addRegistration(Topic.Registration registration) {
        this.topicRegistrations.add(registration);
    }

    @Override
    public void close() {
        this.topicRegistrations.forEach(Topic.Registration::close);
    }

    @FunctionalInterface
    private static interface TopicLookup {
        public <T extends Serializable> Topic<T> findTopic(String var1);

        public static TopicLookup fromHazelcast(final HazelcastInstance hazelcast, final Function<String, String> topicNamePrefixer) {
            final ClusterNode localMember = ClusterNode.from(hazelcast.getCluster().getLocalMember());
            return new TopicLookup(){

                @Override
                public <T extends Serializable> Topic<T> findTopic(String name) {
                    ITopic hazelcastTopic = hazelcast.getTopic((String)topicNamePrefixer.apply(name));
                    return new LocalMemberFilteringHazelcastTopic(hazelcastTopic, localMember);
                }
            };
        }
    }
}

