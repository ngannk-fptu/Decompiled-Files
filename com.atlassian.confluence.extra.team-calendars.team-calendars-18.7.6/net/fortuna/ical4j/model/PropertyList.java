/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import net.fortuna.ical4j.model.Property;

public class PropertyList<T extends Property>
extends ArrayList<T>
implements Serializable {
    private static final long serialVersionUID = -8875923766224921031L;

    public PropertyList() {
    }

    public PropertyList(int initialCapacity) {
        super(initialCapacity);
    }

    public PropertyList(PropertyList<? extends T> properties) throws ParseException, IOException, URISyntaxException {
        for (Property p : properties) {
            this.add((T)p.copy());
        }
    }

    @Override
    public final String toString() {
        return this.stream().map(Property::toString).collect(Collectors.joining(""));
    }

    public final <R> R getProperty(String aName) {
        for (Property p : this) {
            if (!p.getName().equalsIgnoreCase(aName)) continue;
            return (R)p;
        }
        return null;
    }

    public final <C extends T> PropertyList<C> getProperties(String name) {
        PropertyList<Property> list = new PropertyList<Property>();
        for (Property p : this) {
            if (!p.getName().equalsIgnoreCase(name)) continue;
            list.add(p);
        }
        return list;
    }

    @Override
    public final boolean add(T property) {
        return super.add(property);
    }

    public final boolean remove(Property property) {
        return super.remove(property);
    }
}

