/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.model;

import com.atlassian.confluence.importexport.xmlimport.model.ImportedProperty;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveProperty;
import java.util.List;

@Deprecated
public class ComponentProperty
extends ImportedProperty {
    private final List<PrimitiveProperty> properties;

    public ComponentProperty(String name, List<PrimitiveProperty> properties) {
        super(name);
        this.properties = properties;
    }

    public List<PrimitiveProperty> getProperties() {
        return this.properties;
    }

    @Override
    public String toString() {
        return super.toString() + this.properties;
    }

    public String getPropertyStringValue(String propertyName) {
        for (PrimitiveProperty property : this.properties) {
            if (!propertyName.equals(property.getName())) continue;
            return property.getValue();
        }
        return null;
    }
}

