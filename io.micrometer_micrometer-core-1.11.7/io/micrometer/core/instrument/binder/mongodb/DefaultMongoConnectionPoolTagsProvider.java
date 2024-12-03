/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mongodb.event.ConnectionPoolCreatedEvent
 */
package io.micrometer.core.instrument.binder.mongodb;

import com.mongodb.event.ConnectionPoolCreatedEvent;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.mongodb.MongoConnectionPoolTagsProvider;

public class DefaultMongoConnectionPoolTagsProvider
implements MongoConnectionPoolTagsProvider {
    @Override
    public Iterable<Tag> connectionPoolTags(ConnectionPoolCreatedEvent event) {
        return Tags.of(Tag.of("cluster.id", event.getServerId().getClusterId().getValue()), Tag.of("server.address", event.getServerId().getAddress().toString()));
    }
}

