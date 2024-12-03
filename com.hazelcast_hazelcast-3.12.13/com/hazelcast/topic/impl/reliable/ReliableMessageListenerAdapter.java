/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.topic.impl.reliable;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.hazelcast.topic.ReliableMessageListener;

public class ReliableMessageListenerAdapter<E>
implements ReliableMessageListener<E> {
    final MessageListener<E> messageListener;

    public ReliableMessageListenerAdapter(MessageListener<E> messageListener) {
        this.messageListener = messageListener;
    }

    @Override
    public long retrieveInitialSequence() {
        return -1L;
    }

    @Override
    public void storeSequence(long sequence) {
    }

    @Override
    public boolean isLossTolerant() {
        return false;
    }

    @Override
    public void onMessage(Message<E> message) {
        this.messageListener.onMessage(message);
    }

    @Override
    public boolean isTerminal(Throwable failure) {
        return false;
    }

    public String toString() {
        return this.messageListener.toString();
    }
}

