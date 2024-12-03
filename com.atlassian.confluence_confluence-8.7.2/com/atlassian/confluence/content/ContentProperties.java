/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 */
package com.atlassian.confluence.content;

import com.atlassian.confluence.content.ContentProperty;
import com.google.common.collect.Iterables;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContentProperties
implements Serializable {
    private final List<ContentProperty> properties;

    public static ContentProperties deepClone(ContentProperties properties) {
        List<ContentProperty> oldPropertyList = properties.asList();
        ArrayList<ContentProperty> newPropertyList = new ArrayList<ContentProperty>(oldPropertyList.size());
        for (ContentProperty contentProperty : oldPropertyList) {
            ContentProperty oldContentProperty = new ContentProperty(contentProperty);
            oldContentProperty.setId(0L);
            newPropertyList.add(oldContentProperty);
        }
        return new ContentProperties(newPropertyList);
    }

    public ContentProperties(List<ContentProperty> properties) {
        this.properties = properties;
    }

    public void setStringProperty(String name, String value) {
        Objects.requireNonNull(name, "Name must not be null");
        Objects.requireNonNull(value, "Value must not be null");
        ContentProperty existingProperty = this.find(name);
        if (existingProperty == null) {
            ContentProperty newProperty = new ContentProperty();
            newProperty.setName(name);
            newProperty.setStringValue(value);
            this.properties.add(newProperty);
        } else {
            if (existingProperty.getStringValue() == null) {
                throw new IllegalArgumentException("Attempt to change the type of property: " + existingProperty + " to a string");
            }
            existingProperty.setStringValue(value);
        }
    }

    public String getStringProperty(String name) {
        Objects.requireNonNull(name, "Name must not be null");
        ContentProperty property = this.find(name);
        if (property == null) {
            return null;
        }
        if (property.getStringValue() == null) {
            throw new IllegalArgumentException("Property with name " + name + " is not a String");
        }
        return property.getStringValue();
    }

    public void setLongProperty(String name, long value) {
        Objects.requireNonNull(name, "Name must not be null");
        ContentProperty existingProperty = this.find(name);
        if (existingProperty == null) {
            ContentProperty newProperty = new ContentProperty();
            newProperty.setName(name);
            newProperty.setLongValue(value);
            this.properties.add(newProperty);
        } else {
            if (existingProperty.getLongValue() == null) {
                throw new IllegalArgumentException("Attempt to change the type of property: " + existingProperty + " to a long");
            }
            existingProperty.setLongValue(value);
        }
    }

    public long getLongProperty(String name, long defaultValue) {
        Objects.requireNonNull(name, "Name must not be null");
        ContentProperty property = this.find(name);
        if (property == null) {
            return defaultValue;
        }
        return property.getLongValue();
    }

    public void removeProperty(String name) {
        Objects.requireNonNull(name, "Name must not be null");
        Iterables.removeIf(this.properties, input -> input.getName().equals(name));
    }

    private ContentProperty find(String key) {
        for (ContentProperty property : this.properties) {
            if (!property.getName().equals(key)) continue;
            return property;
        }
        return null;
    }

    public List<ContentProperty> asList() {
        return this.properties;
    }
}

