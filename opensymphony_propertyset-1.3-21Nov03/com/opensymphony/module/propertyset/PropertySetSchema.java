/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset;

import com.opensymphony.module.propertyset.PropertySchema;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PropertySetSchema
implements Serializable {
    private Map propertySchemas = new HashMap();
    private boolean restricted;

    public void setPropertySchema(String key, PropertySchema ps) {
        if (ps.getPropertyName() == null) {
            ps.setPropertyName(key);
        }
        this.propertySchemas.put(key, ps);
    }

    public PropertySchema getPropertySchema(String key) {
        return (PropertySchema)this.propertySchemas.get(key);
    }

    public void setRestricted(boolean b) {
        this.restricted = b;
    }

    public boolean isRestricted() {
        return this.restricted;
    }

    public void addPropertySchema(PropertySchema ps) {
        this.propertySchemas.put(ps.getPropertyName(), ps);
    }
}

