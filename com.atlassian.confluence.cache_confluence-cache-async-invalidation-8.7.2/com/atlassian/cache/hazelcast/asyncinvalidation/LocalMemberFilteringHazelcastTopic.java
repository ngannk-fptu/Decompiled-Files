/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.ITopic
 *  com.hazelcast.core.Message
 *  com.hazelcast.core.MessageListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.cache.hazelcast.asyncinvalidation;

import com.atlassian.cache.hazelcast.asyncinvalidation.ClusterNode;
import com.atlassian.cache.hazelcast.asyncinvalidation.Topic;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import java.io.Serializable;
import java.util.function.BiConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class LocalMemberFilteringHazelcastTopic<M extends Serializable>
implements Topic<M> {
    private static final Logger log = LoggerFactory.getLogger(LocalMemberFilteringHazelcastTopic.class);
    private final ITopic<M> hazelcastTopic;
    private final ClusterNode localMember;

    public LocalMemberFilteringHazelcastTopic(ITopic<M> hazelcastTopic, ClusterNode localMember) {
        this.hazelcastTopic = hazelcastTopic;
        this.localMember = localMember;
    }

    @Override
    public Topic.Registration addListener(Topic.MessageConsumer<M> consumer) {
        MessageListener<M> messageListener = this.createMessageListener(this.localMember, consumer);
        String registrationId = this.hazelcastTopic.addMessageListener(messageListener);
        return () -> this.hazelcastTopic.removeMessageListener(registrationId);
    }

    private MessageListener<M> createMessageListener(ClusterNode localMember, BiConsumer<ClusterNode, M> consumer) {
        return new LocalMemberFilteringMessageListener(localMember, message -> {
            log.debug("Received message on topic '{}' from member {}: {}", new Object[]{this.hazelcastTopic.getName(), message.getPublishingMember(), message.getMessageObject()});
            consumer.accept(ClusterNode.from(message.getPublishingMember()), (Serializable)message.getMessageObject());
        });
    }

    @Override
    public void publish(M message) {
        log.debug("Sending message on topic '{}': {}", (Object)this.hazelcastTopic.getName(), message);
        this.hazelcastTopic.publish(message);
    }

    private static class LocalMemberFilteringMessageListener<T>
    implements MessageListener<T> {
        private final ClusterNode localMember;
        private final MessageListener<T> delegate;

        public LocalMemberFilteringMessageListener(ClusterNode localMember, MessageListener<T> delegate) {
            this.localMember = localMember;
            this.delegate = delegate;
        }

        public void onMessage(Message<T> message) {
            if (ClusterNode.from(message.getPublishingMember()).equals(this.localMember)) {
                log.debug("Ignoring message on topic from self {}", (Object)this.localMember);
            } else {
                this.delegate.onMessage(message);
            }
        }
    }
}

