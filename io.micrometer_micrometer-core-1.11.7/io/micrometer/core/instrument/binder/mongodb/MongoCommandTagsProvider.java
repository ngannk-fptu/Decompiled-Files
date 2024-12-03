/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mongodb.event.CommandEvent
 *  com.mongodb.event.CommandStartedEvent
 */
package io.micrometer.core.instrument.binder.mongodb;

import com.mongodb.event.CommandEvent;
import com.mongodb.event.CommandStartedEvent;
import io.micrometer.core.instrument.Tag;

@FunctionalInterface
public interface MongoCommandTagsProvider {
    default public void commandStarted(CommandStartedEvent commandStartedEvent) {
    }

    public Iterable<Tag> commandTags(CommandEvent var1);
}

