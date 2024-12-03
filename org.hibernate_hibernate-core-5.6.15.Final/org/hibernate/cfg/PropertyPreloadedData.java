/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.annotations.common.reflection.XClass
 *  org.hibernate.annotations.common.reflection.XProperty
 */
package org.hibernate.cfg;

import org.hibernate.MappingException;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.cfg.AccessType;
import org.hibernate.cfg.PropertyData;

public class PropertyPreloadedData
implements PropertyData {
    private final AccessType defaultAccess;
    private final String propertyName;
    private final XClass returnedClass;

    public PropertyPreloadedData(AccessType defaultAccess, String propertyName, XClass returnedClass) {
        this.defaultAccess = defaultAccess;
        this.propertyName = propertyName;
        this.returnedClass = returnedClass;
    }

    @Override
    public AccessType getDefaultAccess() throws MappingException {
        return this.defaultAccess;
    }

    @Override
    public String getPropertyName() throws MappingException {
        return this.propertyName;
    }

    @Override
    public XClass getClassOrElement() throws MappingException {
        return this.getPropertyClass();
    }

    @Override
    public XClass getPropertyClass() throws MappingException {
        return this.returnedClass;
    }

    @Override
    public String getClassOrElementName() throws MappingException {
        return this.getTypeName();
    }

    @Override
    public String getTypeName() throws MappingException {
        return this.returnedClass == null ? null : this.returnedClass.getName();
    }

    @Override
    public XProperty getProperty() {
        return null;
    }

    @Override
    public XClass getDeclaringClass() {
        return null;
    }
}

