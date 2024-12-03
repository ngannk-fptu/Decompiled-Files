/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.builder.ConfigurationBuilder;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventType;

public class ConfigurationBuilderEvent
extends Event {
    private static final long serialVersionUID = -7488811456039315104L;
    public static final EventType<ConfigurationBuilderEvent> ANY = new EventType<Event>(Event.ANY, "BUILDER");
    public static final EventType<ConfigurationBuilderEvent> RESET = new EventType<ConfigurationBuilderEvent>(ANY, "RESET");
    public static final EventType<ConfigurationBuilderEvent> CONFIGURATION_REQUEST = new EventType<ConfigurationBuilderEvent>(ANY, "CONFIGURATION_REQUEST");

    public ConfigurationBuilderEvent(ConfigurationBuilder<?> source, EventType<? extends ConfigurationBuilderEvent> evType) {
        super(source, evType);
    }

    @Override
    public ConfigurationBuilder<?> getSource() {
        return (ConfigurationBuilder)super.getSource();
    }
}

