/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.odm.core.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.naming.Name;
import org.springframework.ldap.UncategorizedLdapException;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Transient;
import org.springframework.ldap.odm.core.impl.CaseIgnoreString;
import org.springframework.ldap.odm.core.impl.MetaDataException;

final class AttributeMetaData {
    private static final CaseIgnoreString OBJECT_CLASS_ATTRIBUTE_CI = new CaseIgnoreString("objectclass");
    private CaseIgnoreString name;
    private String syntax;
    private boolean isBinary;
    private final Field field;
    private Class<?> valueClass;
    private boolean isId;
    private boolean isCollection;
    private Class<? extends Collection> collectionClass;
    private boolean isObjectClass;
    private boolean isTransient = false;
    private boolean isReadOnly = false;
    private String[] attributes;
    private DnAttribute dnAttribute;

    private boolean processAttributeAnnotation(Field field) {
        this.syntax = "";
        this.isBinary = false;
        this.name = new CaseIgnoreString(field.getName());
        boolean foundAnnotation = false;
        Attribute attribute = field.getAnnotation(Attribute.class);
        ArrayList<String> attrList = new ArrayList<String>();
        if (attribute != null) {
            foundAnnotation = true;
            String localAttributeName = attribute.name();
            if (localAttributeName != null && localAttributeName.length() > 0) {
                this.name = new CaseIgnoreString(localAttributeName);
                attrList.add(localAttributeName);
            }
            this.syntax = attribute.syntax();
            this.isBinary = attribute.type() == Attribute.Type.BINARY;
            this.isReadOnly = attribute.readonly();
        }
        this.attributes = attrList.toArray(new String[attrList.size()]);
        this.isObjectClass = this.name.equals(OBJECT_CLASS_ATTRIBUTE_CI);
        return foundAnnotation;
    }

    private void determineFieldType(Field field) {
        Class<?> fieldType = field.getType();
        this.isCollection = Collection.class.isAssignableFrom(fieldType);
        this.valueClass = null;
        if (!this.isCollection) {
            this.valueClass = fieldType;
        } else {
            ParameterizedType paramType;
            this.determineCollectionClass(fieldType);
            try {
                paramType = (ParameterizedType)field.getGenericType();
            }
            catch (ClassCastException e) {
                throw new MetaDataException(String.format("Can't determine destination type for field %1$s in Entry class %2$s", field, field.getDeclaringClass()), e);
            }
            Type[] actualParamArguments = paramType.getActualTypeArguments();
            if (actualParamArguments.length == 1) {
                Type type;
                if (actualParamArguments[0] instanceof Class) {
                    this.valueClass = (Class)actualParamArguments[0];
                } else if (actualParamArguments[0] instanceof GenericArrayType && (type = ((GenericArrayType)actualParamArguments[0]).getGenericComponentType()) instanceof Class) {
                    this.valueClass = Array.newInstance((Class)type, 0).getClass();
                }
            }
        }
        if (this.valueClass == null) {
            throw new MetaDataException(String.format("Can't determine destination type for field %1$s in class %2$s", field, field.getDeclaringClass()));
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void determineCollectionClass(Class<?> fieldType) {
        if (fieldType.isInterface()) {
            if (Collection.class.equals(fieldType) || List.class.equals(fieldType)) {
                this.collectionClass = ArrayList.class;
                return;
            } else if (SortedSet.class.equals(fieldType)) {
                this.collectionClass = TreeSet.class;
                return;
            } else {
                if (!Set.class.isAssignableFrom(fieldType)) throw new MetaDataException(String.format("Collection class %s is not supported", fieldType));
                this.collectionClass = LinkedHashSet.class;
            }
            return;
        } else {
            this.collectionClass = fieldType;
        }
    }

    public Collection<Object> newCollectionInstance() {
        try {
            return this.collectionClass.newInstance();
        }
        catch (Exception e) {
            throw new UncategorizedLdapException("Failed to instantiate collection class", e);
        }
    }

    private boolean processIdAnnotation(Field field, Class<?> fieldType) {
        boolean bl = this.isId = field.getAnnotation(Id.class) != null;
        if (this.isId && !Name.class.isAssignableFrom(fieldType)) {
            throw new MetaDataException(String.format("The id field must be of type javax.naming.Name or a subclass that of in Entry class %1$s", field.getDeclaringClass()));
        }
        return this.isId;
    }

    public AttributeMetaData(Field field) {
        this.field = field;
        this.dnAttribute = field.getAnnotation(DnAttribute.class);
        if (this.dnAttribute != null && !field.getType().equals(String.class)) {
            throw new MetaDataException(String.format("%s is of type %s, but only String attributes can be declared as @DnAttributes", field.toString(), field.getType().toString()));
        }
        Transient transientAnnotation = field.getAnnotation(Transient.class);
        if (transientAnnotation != null) {
            this.isTransient = true;
            return;
        }
        this.determineFieldType(field);
        boolean foundAttributeAnnotation = this.processAttributeAnnotation(field);
        boolean foundIdAnnoation = this.processIdAnnotation(field, this.valueClass);
        if (foundAttributeAnnotation && foundIdAnnoation) {
            throw new MetaDataException(String.format("You may not specifiy an %1$s annoation and an %2$s annotation on the same field, error in field %3$s in Entry class %4$s", Id.class, Attribute.class, field.getName(), field.getDeclaringClass()));
        }
        if (this.isObjectClass() && (!this.isCollection() || this.valueClass != String.class)) {
            throw new MetaDataException(String.format("The type of the objectclass attribute must be List<String> in classs %1$s", field.getDeclaringClass()));
        }
    }

    public String getSyntax() {
        return this.syntax;
    }

    public boolean isBinary() {
        return this.isBinary;
    }

    public Field getField() {
        return this.field;
    }

    public CaseIgnoreString getName() {
        return this.name;
    }

    public boolean isCollection() {
        return this.isCollection;
    }

    public boolean isId() {
        return this.isId;
    }

    public boolean isReadOnly() {
        return this.isReadOnly;
    }

    public boolean isTransient() {
        return this.isTransient;
    }

    public DnAttribute getDnAttribute() {
        return this.dnAttribute;
    }

    public boolean isDnAttribute() {
        return this.dnAttribute != null;
    }

    public boolean isObjectClass() {
        return this.isObjectClass;
    }

    public Class<?> getValueClass() {
        return this.valueClass;
    }

    public String[] getAttributes() {
        return this.attributes;
    }

    public Class<?> getJndiClass() {
        if (this.isBinary()) {
            return byte[].class;
        }
        if (Name.class.isAssignableFrom(this.valueClass)) {
            return Name.class;
        }
        return String.class;
    }

    public String toString() {
        return String.format("name=%1$s | field=%2$s | valueClass=%3$s | syntax=%4$s| isBinary=%5$s | isId=%6$s | isReadOnly=%7$s |  isList=%8$s | isObjectClass=%9$s", this.getName(), this.getField(), this.getValueClass(), this.getSyntax(), this.isBinary(), this.isId(), this.isReadOnly(), this.isCollection(), this.isObjectClass());
    }
}

