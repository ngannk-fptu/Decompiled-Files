/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.xmpbox.type.PropertyType;

public class PropertiesDescription {
    private final Map<String, PropertyType> types = new HashMap<String, PropertyType>();

    public List<String> getPropertiesName() {
        return new ArrayList<String>(this.types.keySet());
    }

    public void addNewProperty(String name, PropertyType type) {
        this.types.put(name, type);
    }

    public PropertyType getPropertyType(String name) {
        return this.types.get(name);
    }
}

