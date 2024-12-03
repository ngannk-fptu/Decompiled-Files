/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mongodb.event.CommandEvent
 *  com.mongodb.event.CommandFailedEvent
 *  com.mongodb.event.CommandListener
 *  com.mongodb.event.CommandStartedEvent
 *  com.mongodb.event.CommandSucceededEvent
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 */
package io.micrometer.core.instrument.binder.mongodb;

import com.mongodb.event.CommandEvent;
import com.mongodb.event.CommandFailedEvent;
import com.mongodb.event.CommandListener;
import com.mongodb.event.CommandStartedEvent;
import com.mongodb.event.CommandSucceededEvent;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.mongodb.DefaultMongoCommandTagsProvider;
import io.micrometer.core.instrument.binder.mongodb.MongoCommandTagsProvider;
import java.util.concurrent.TimeUnit;

@NonNullApi
@NonNullFields
@Incubating(since="1.2.0")
public class MongoMetricsCommandListener
implements CommandListener {
    private final MeterRegistry registry;
    private final MongoCommandTagsProvider tagsProvider;

    public MongoMetricsCommandListener(MeterRegistry registry) {
        this(registry, new DefaultMongoCommandTagsProvider());
    }

    public MongoMetricsCommandListener(MeterRegistry registry, MongoCommandTagsProvider tagsProvider) {
        this.registry = registry;
        this.tagsProvider = tagsProvider;
    }

    public void commandStarted(CommandStartedEvent commandStartedEvent) {
        this.tagsProvider.commandStarted(commandStartedEvent);
    }

    public void commandSucceeded(CommandSucceededEvent event) {
        this.timeCommand((CommandEvent)event, event.getElapsedTime(TimeUnit.NANOSECONDS));
    }

    public void commandFailed(CommandFailedEvent event) {
        this.timeCommand((CommandEvent)event, event.getElapsedTime(TimeUnit.NANOSECONDS));
    }

    private void timeCommand(CommandEvent event, long elapsedTimeInNanoseconds) {
        ((Timer.Builder)Timer.builder("mongodb.driver.commands").description("Timer of mongodb commands").tags((Iterable)this.tagsProvider.commandTags(event))).register(this.registry).record(elapsedTimeInNanoseconds, TimeUnit.NANOSECONDS);
    }
}

