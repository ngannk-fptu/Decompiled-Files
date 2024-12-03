/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.model;

import com.atlassian.confluence.importexport.xmlimport.model.CompositeId;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedProperty;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveId;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveProperty;
import java.util.Collection;

@Deprecated
public class ImportedObject {
    private final String className;
    private final String packageName;
    private final Collection<ImportedProperty> properties;
    private final CompositeId compositeId;

    public ImportedObject(String className, String packageName, Collection<ImportedProperty> properties, CompositeId compositeId) {
        this.className = className;
        this.packageName = packageName;
        this.properties = properties;
        this.compositeId = compositeId;
    }

    public String getClassName() {
        return this.className;
    }

    public Collection<ImportedProperty> getProperties() {
        return this.properties;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public CompositeId getCompositeId() {
        return this.compositeId;
    }

    public String getStringProperty(String propertyName) {
        for (ImportedProperty property : this.properties) {
            if (!(property instanceof PrimitiveProperty) || !propertyName.equals(property.getName())) continue;
            return ((PrimitiveProperty)property).getValue();
        }
        return null;
    }

    public String getIdPropertyStr() {
        PrimitiveId idProperty = this.getIdProperty();
        return idProperty == null ? null : idProperty.getValue();
    }

    public String toString() {
        return "Imported[" + this.packageName + "." + this.className + "] " + this.properties;
    }

    public PrimitiveId getIdProperty() {
        for (ImportedProperty property : this.properties) {
            if (!(property instanceof PrimitiveId)) continue;
            return (PrimitiveId)property;
        }
        return null;
    }
}

