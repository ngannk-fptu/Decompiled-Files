/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;

public class IndexedPropertyList {
    private static final PropertyList<Property> EMPTY_LIST = new PropertyList();
    private Map<String, PropertyList<Property>> index;

    public IndexedPropertyList(PropertyList<Property> list, String parameterName) {
        HashMap indexedProperties = new HashMap();
        list.forEach(property -> property.getParameters(parameterName).forEach(parameter -> {
            PropertyList<Property> properties = (PropertyList<Property>)indexedProperties.get(parameter.getValue());
            if (properties == null) {
                properties = new PropertyList<Property>();
                indexedProperties.put(parameter.getValue(), properties);
            }
            properties.add((Property)property);
        }));
        this.index = Collections.unmodifiableMap(indexedProperties);
    }

    public PropertyList<Property> getProperties(String paramValue) {
        PropertyList<Property> properties = this.index.get(paramValue);
        if (properties == null) {
            properties = EMPTY_LIST;
        }
        return properties;
    }

    public Property getProperty(String paramValue) {
        PropertyList<Property> properties = this.getProperties(paramValue);
        if (!properties.isEmpty()) {
            return (Property)properties.iterator().next();
        }
        return null;
    }
}

