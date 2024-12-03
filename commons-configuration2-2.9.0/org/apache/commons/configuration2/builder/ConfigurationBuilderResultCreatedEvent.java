/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilder;
import org.apache.commons.configuration2.builder.ConfigurationBuilderEvent;
import org.apache.commons.configuration2.event.EventType;

public class ConfigurationBuilderResultCreatedEvent
extends ConfigurationBuilderEvent {
    public static final EventType<ConfigurationBuilderResultCreatedEvent> RESULT_CREATED = new EventType(ANY, "RESULT_CREATED");
    private final ImmutableConfiguration configuration;

    public ConfigurationBuilderResultCreatedEvent(ConfigurationBuilder<?> source, EventType<? extends ConfigurationBuilderResultCreatedEvent> evType, ImmutableConfiguration createdConfiguration) {
        super(source, evType);
        if (createdConfiguration == null) {
            throw new IllegalArgumentException("Configuration must not be null!");
        }
        this.configuration = createdConfiguration;
    }

    public ImmutableConfiguration getConfiguration() {
        return this.configuration;
    }
}

