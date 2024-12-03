/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.event.configuration;

import java.util.Objects;

public class ConfigurationPropertyUpdatedEvent {
    private final String propertyName;
    private final String oldValue;
    private final String newValue;

    public ConfigurationPropertyUpdatedEvent(String propertyName, String oldValue, String newValue) {
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public String getOldValue() {
        return this.oldValue;
    }

    public String getNewValue() {
        return this.newValue;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ConfigurationPropertyUpdatedEvent that = (ConfigurationPropertyUpdatedEvent)o;
        return Objects.equals(this.propertyName, that.propertyName) && Objects.equals(this.oldValue, that.oldValue) && Objects.equals(this.newValue, that.newValue);
    }

    public int hashCode() {
        return Objects.hash(this.propertyName, this.oldValue, this.newValue);
    }
}

