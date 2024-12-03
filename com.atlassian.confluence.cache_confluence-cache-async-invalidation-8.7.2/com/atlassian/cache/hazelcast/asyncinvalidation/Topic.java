/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.cache.hazelcast.asyncinvalidation;

import com.atlassian.cache.hazelcast.asyncinvalidation.ClusterNode;
import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

interface Topic<T extends Serializable> {
    public Registration addListener(MessageConsumer<T> var1);

    default public void addListener(MessageConsumer<T> messageConsumer, Consumer<Registration> registrationConsumer) {
        Registration registration = this.addListener(messageConsumer);
        registrationConsumer.accept(registration);
    }

    public void publish(T var1);

    @FunctionalInterface
    public static interface MessageConsumer<T>
    extends BiConsumer<ClusterNode, T> {
    }

    @FunctionalInterface
    public static interface Registration
    extends AutoCloseable {
        @Override
        public void close();
    }
}

