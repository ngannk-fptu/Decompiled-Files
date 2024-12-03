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
import org.hibernate.internal.util.StringHelper;

public class WrappedInferredData
implements PropertyData {
    private PropertyData wrappedInferredData;
    private String propertyName;

    @Override
    public XClass getClassOrElement() throws MappingException {
        return this.wrappedInferredData.getClassOrElement();
    }

    @Override
    public String getClassOrElementName() throws MappingException {
        return this.wrappedInferredData.getClassOrElementName();
    }

    @Override
    public AccessType getDefaultAccess() {
        return this.wrappedInferredData.getDefaultAccess();
    }

    @Override
    public XProperty getProperty() {
        return this.wrappedInferredData.getProperty();
    }

    @Override
    public XClass getDeclaringClass() {
        return this.wrappedInferredData.getDeclaringClass();
    }

    @Override
    public XClass getPropertyClass() throws MappingException {
        return this.wrappedInferredData.getPropertyClass();
    }

    @Override
    public String getPropertyName() throws MappingException {
        return this.propertyName;
    }

    @Override
    public String getTypeName() throws MappingException {
        return this.wrappedInferredData.getTypeName();
    }

    public WrappedInferredData(PropertyData inferredData, String suffix) {
        this.wrappedInferredData = inferredData;
        this.propertyName = StringHelper.qualify(inferredData.getPropertyName(), suffix);
    }
}

