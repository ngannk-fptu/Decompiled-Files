/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.event;

import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventType;

public class ConfigurationEvent
extends Event {
    public static final EventType<ConfigurationEvent> ANY = new EventType<Event>(Event.ANY, "CONFIGURATION_UPDATE");
    public static final EventType<ConfigurationEvent> ADD_PROPERTY = new EventType<ConfigurationEvent>(ANY, "ADD_PROPERTY");
    public static final EventType<ConfigurationEvent> SET_PROPERTY = new EventType<ConfigurationEvent>(ANY, "SET_PROPERTY");
    public static final EventType<ConfigurationEvent> CLEAR_PROPERTY = new EventType<ConfigurationEvent>(ANY, "CLEAR_PROPERTY");
    public static final EventType<ConfigurationEvent> CLEAR = new EventType<ConfigurationEvent>(ANY, "CLEAR");
    public static final EventType<ConfigurationEvent> ANY_HIERARCHICAL = new EventType<ConfigurationEvent>(ANY, "HIERARCHICAL");
    public static final EventType<ConfigurationEvent> ADD_NODES = new EventType<ConfigurationEvent>(ANY_HIERARCHICAL, "ADD_NODES");
    public static final EventType<ConfigurationEvent> CLEAR_TREE = new EventType<ConfigurationEvent>(ANY_HIERARCHICAL, "CLEAR_TREE");
    public static final EventType<ConfigurationEvent> SUBNODE_CHANGED = new EventType<ConfigurationEvent>(ANY_HIERARCHICAL, "SUBNODE_CHANGED");
    private static final long serialVersionUID = 20140703L;
    private final String propertyName;
    private final Object propertyValue;
    private final boolean beforeUpdate;

    public ConfigurationEvent(Object source, EventType<? extends ConfigurationEvent> type, String propertyName, Object propertyValue, boolean beforeUpdate) {
        super(source, type);
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
        this.beforeUpdate = beforeUpdate;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public Object getPropertyValue() {
        return this.propertyValue;
    }

    public boolean isBeforeUpdate() {
        return this.beforeUpdate;
    }
}

