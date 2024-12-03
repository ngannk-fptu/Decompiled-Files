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

public interface PropertyData {
    public AccessType getDefaultAccess();

    public String getPropertyName() throws MappingException;

    public XClass getClassOrElement() throws MappingException;

    public XClass getPropertyClass() throws MappingException;

    public String getClassOrElementName() throws MappingException;

    public String getTypeName() throws MappingException;

    public XProperty getProperty();

    public XClass getDeclaringClass();
}

