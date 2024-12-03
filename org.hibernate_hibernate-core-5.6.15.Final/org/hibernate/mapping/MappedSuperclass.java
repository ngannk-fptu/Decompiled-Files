/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;

public class MappedSuperclass {
    private final MappedSuperclass superMappedSuperclass;
    private final PersistentClass superPersistentClass;
    private final List declaredProperties;
    private Class mappedClass;
    private Property identifierProperty;
    private Property version;
    private Component identifierMapper;

    public MappedSuperclass(MappedSuperclass superMappedSuperclass, PersistentClass superPersistentClass) {
        this.superMappedSuperclass = superMappedSuperclass;
        this.superPersistentClass = superPersistentClass;
        this.declaredProperties = new ArrayList();
    }

    public MappedSuperclass getSuperMappedSuperclass() {
        return this.superMappedSuperclass;
    }

    public boolean hasIdentifierProperty() {
        return this.getIdentifierProperty() != null;
    }

    public boolean isVersioned() {
        return this.getVersion() != null;
    }

    public PersistentClass getSuperPersistentClass() {
        return this.superPersistentClass;
    }

    public Iterator getDeclaredPropertyIterator() {
        return this.declaredProperties.iterator();
    }

    public void addDeclaredProperty(Property p) {
        String name = p.getName();
        Iterator it = this.declaredProperties.iterator();
        while (it.hasNext()) {
            if (!name.equals(((Property)it.next()).getName())) continue;
            return;
        }
        this.declaredProperties.add(p);
    }

    public Class getMappedClass() {
        return this.mappedClass;
    }

    public void setMappedClass(Class mappedClass) {
        this.mappedClass = mappedClass;
    }

    public Property getIdentifierProperty() {
        Property propagatedIdentifierProp = this.identifierProperty;
        if (propagatedIdentifierProp == null) {
            if (this.superMappedSuperclass != null) {
                propagatedIdentifierProp = this.superMappedSuperclass.getIdentifierProperty();
            }
            if (propagatedIdentifierProp == null && this.superPersistentClass != null) {
                propagatedIdentifierProp = this.superPersistentClass.getIdentifierProperty();
            }
        }
        return propagatedIdentifierProp;
    }

    public Property getDeclaredIdentifierProperty() {
        return this.identifierProperty;
    }

    public void setDeclaredIdentifierProperty(Property prop) {
        this.identifierProperty = prop;
    }

    public Property getVersion() {
        Property propagatedVersion = this.version;
        if (propagatedVersion == null) {
            if (this.superMappedSuperclass != null) {
                propagatedVersion = this.superMappedSuperclass.getVersion();
            }
            if (propagatedVersion == null && this.superPersistentClass != null) {
                propagatedVersion = this.superPersistentClass.getVersion();
            }
        }
        return propagatedVersion;
    }

    public Property getDeclaredVersion() {
        return this.version;
    }

    public void setDeclaredVersion(Property prop) {
        this.version = prop;
    }

    public Component getIdentifierMapper() {
        Component propagatedMapper = this.identifierMapper;
        if (propagatedMapper == null) {
            if (this.superMappedSuperclass != null) {
                propagatedMapper = this.superMappedSuperclass.getIdentifierMapper();
            }
            if (propagatedMapper == null && this.superPersistentClass != null) {
                propagatedMapper = this.superPersistentClass.getIdentifierMapper();
            }
        }
        return propagatedMapper;
    }

    public Component getDeclaredIdentifierMapper() {
        return this.identifierMapper;
    }

    public void setDeclaredIdentifierMapper(Component identifierMapper) {
        this.identifierMapper = identifierMapper;
    }

    public boolean hasProperty(String name) {
        Iterator itr = this.getDeclaredPropertyIterator();
        while (itr.hasNext()) {
            Property property = (Property)itr.next();
            if (!property.getName().equals(name)) continue;
            return true;
        }
        return false;
    }

    public boolean isPropertyDefinedInHierarchy(String name) {
        if (this.hasProperty(name)) {
            return true;
        }
        if (this.getSuperMappedSuperclass() != null && this.getSuperMappedSuperclass().isPropertyDefinedInHierarchy(name)) {
            return true;
        }
        return this.getSuperPersistentClass() != null && this.getSuperPersistentClass().isPropertyDefinedInHierarchy(name);
    }
}

