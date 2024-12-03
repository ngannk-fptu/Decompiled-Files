/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.model;

import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveProperty;
import java.util.List;

@Deprecated
public class CompositeId {
    private final List<PrimitiveProperty> properties;

    public CompositeId(List<PrimitiveProperty> properties) {
        this.properties = properties;
    }

    public List<PrimitiveProperty> getProperties() {
        return this.properties;
    }

    public String getPropertyValue(String propertyName) {
        for (PrimitiveProperty property : this.properties) {
            if (!propertyName.equals(property.getName())) continue;
            return property.getValue();
        }
        return null;
    }
}

