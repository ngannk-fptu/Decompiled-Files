/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.velocity.tools.config.Property;

public class Configuration
implements Comparable<Configuration> {
    private final SortedSet<Property> properties = new TreeSet<Property>();

    public void addProperty(Property property) {
        if (property.getName() == null) {
            throw new IllegalArgumentException("All properties must be named before they can be added to the configuration.");
        }
        this.properties.remove(property);
        this.properties.add(property);
    }

    public boolean removeProperty(Property property) {
        return this.properties.remove(property);
    }

    public void setProperty(String name, Object value) {
        if (name == null) {
            throw new NullPointerException("Property name cannot be null");
        }
        Property prop = new Property();
        prop.setName(name);
        prop.setValue(value);
        this.addProperty(prop);
    }

    public boolean removeProperty(String name) {
        Property prop = this.getProperty(name);
        return this.properties.remove(prop);
    }

    public boolean hasProperties() {
        return !this.properties.isEmpty();
    }

    public Property getProperty(String name) {
        for (Property prop : this.properties) {
            if (!name.equals(prop.getName())) continue;
            return prop;
        }
        return null;
    }

    public SortedSet<Property> getProperties() {
        return new TreeSet<Property>(this.properties);
    }

    public Map<String, Object> getPropertyMap() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        for (Property prop : this.properties) {
            map.put(prop.getName(), prop.getConvertedValue());
        }
        return map;
    }

    public void setPropertyMap(Map<String, Object> props) {
        for (Map.Entry<String, Object> entry : props.entrySet()) {
            this.setProperty(entry.getKey(), entry.getValue());
        }
    }

    public void setProperties(Collection<Property> props) {
        for (Property newProp : props) {
            this.addProperty(newProp);
        }
    }

    public void addConfiguration(Configuration config) {
        this.setProperties(config.getProperties());
    }

    public void validate() {
        for (Property property : this.properties) {
            property.validate();
        }
    }

    @Override
    public int compareTo(Configuration config) {
        throw new UnsupportedOperationException("Configuration is abstract and cannot be compared.");
    }

    public int hashCode() {
        return this.properties.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Configuration)) {
            return false;
        }
        Configuration that = (Configuration)obj;
        return this.properties.equals(that.properties);
    }

    protected void appendProperties(StringBuilder out) {
        if (this.hasProperties()) {
            out.append("with ");
            out.append(this.properties.size());
            out.append(" properties [");
            for (Property prop : this.properties) {
                out.append(prop.getKey());
                out.append(" -");
                out.append(prop.getType());
                out.append("-> ");
                out.append(prop.getValue());
                out.append("; ");
            }
            out.append("]");
        }
    }
}

