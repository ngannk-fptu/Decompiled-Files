/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.cluster.event.TopicEventCluster
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.ITopic
 *  com.hazelcast.core.Member
 *  com.hazelcast.core.MembershipAdapter
 *  com.hazelcast.core.MembershipEvent
 *  com.hazelcast.core.MembershipListener
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.cluster.hazelcast.event;

import com.atlassian.confluence.impl.cluster.event.TopicEventCluster;
import com.atlassian.confluence.impl.cluster.hazelcast.event.HazelcastTopicEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipAdapter;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HazelcastTopicEventCluster
implements TopicEventCluster<HazelcastTopicEvent, Member> {
    static final String TOPIC_PREFIX = HazelcastTopicEventCluster.class.getName();
    static final String AVAILABILITY_ATTRIBUTE = HazelcastTopicEventCluster.class.getName();
    private static final Logger log = LoggerFactory.getLogger(HazelcastTopicEventCluster.class);
    private final HazelcastInstance hazelcast;
    private final Collection<Runnable> shutdownTasks = new ArrayList<Runnable>();

    public HazelcastTopicEventCluster(HazelcastInstance hazelcast) {
        this.hazelcast = hazelcast;
    }

    public boolean allNodesInitialised() {
        return this.hazelcast.getCluster().getMembers().stream().filter(member -> !this.isAvailable((Member)member)).peek(member -> log.warn("{} is not available on cluster event topic", member)).findAny().isEmpty();
    }

    private boolean isAvailable(Member member) {
        return Boolean.TRUE.equals(member.getBooleanAttribute(AVAILABILITY_ATTRIBUTE));
    }

    public void initialise(BiConsumer<Member, HazelcastTopicEvent> eventListener, BiConsumer<Member, UUID> ackListener, Consumer<Member> nodeRemovedListener) {
        this.registerTopicListener(this.eventTopic(), eventListener);
        this.registerTopicListener(this.ackTopic(), ackListener);
        this.registerClusterNodeRemovedListener(nodeRemovedListener);
        this.hazelcast.getCluster().getLocalMember().setBooleanAttribute(AVAILABILITY_ATTRIBUTE, true);
    }

    <T> void registerTopicListener(ITopic<T> topic, BiConsumer<Member, T> listener) {
        log.debug("Registering listener for topic {}", (Object)topic.getName());
        String listenerId = topic.addMessageListener(message -> {
            if (!message.getPublishingMember().localMember()) {
                listener.accept(message.getPublishingMember(), message.getMessageObject());
            }
        });
        this.shutdownTasks.add(() -> topic.removeMessageListener(listenerId));
    }

    void registerClusterNodeRemovedListener(final Consumer<Member> action) {
        String id = this.hazelcast.getCluster().addMembershipListener((MembershipListener)new MembershipAdapter(){

            public void memberRemoved(MembershipEvent event) {
                action.accept(event.getMember());
            }
        });
        this.shutdownTasks.add(() -> this.hazelcast.getCluster().removeMembershipListener(id));
    }

    public Set<Member> getOtherClusterMembers() {
        return this.hazelcast.getCluster().getMembers().stream().filter(member -> !member.localMember()).collect(Collectors.toSet());
    }

    public void publishEvent(HazelcastTopicEvent event) {
        this.eventTopic().publish((Object)event);
    }

    public void publishAck(UUID ack) {
        this.ackTopic().publish((Object)ack);
    }

    public HazelcastTopicEvent wrapEvent(Object event) {
        return new HazelcastTopicEvent(event);
    }

    @PreDestroy
    void dispose() {
        this.shutdownTasks.forEach(Runnable::run);
    }

    private ITopic<HazelcastTopicEvent> eventTopic() {
        return this.hazelcast.getTopic(TOPIC_PREFIX + ".events");
    }

    private ITopic<UUID> ackTopic() {
        return this.hazelcast.getTopic(TOPIC_PREFIX + ".acks");
    }
}

