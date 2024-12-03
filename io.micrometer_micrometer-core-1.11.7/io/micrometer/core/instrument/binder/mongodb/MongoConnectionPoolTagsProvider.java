/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mongodb.event.ConnectionPoolCreatedEvent
 */
package io.micrometer.core.instrument.binder.mongodb;

import com.mongodb.event.ConnectionPoolCreatedEvent;
import io.micrometer.core.instrument.Tag;

@FunctionalInterface
public interface MongoConnectionPoolTagsProvider {
    public Iterable<Tag> connectionPoolTags(ConnectionPoolCreatedEvent var1);
}

