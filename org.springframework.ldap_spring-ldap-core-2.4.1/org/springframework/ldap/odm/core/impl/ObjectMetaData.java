/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.StringUtils
 */
package org.springframework.ldap.odm.core.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.naming.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.core.impl.AttributeMetaData;
import org.springframework.ldap.odm.core.impl.CaseIgnoreString;
import org.springframework.ldap.odm.core.impl.MetaDataException;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.util.StringUtils;

final class ObjectMetaData
implements Iterable<Field> {
    private static final Logger LOG = LoggerFactory.getLogger(ObjectMetaData.class);
    private AttributeMetaData idAttribute;
    private Map<Field, AttributeMetaData> fieldToAttribute = new HashMap<Field, AttributeMetaData>();
    private Set<AttributeMetaData> dnAttributes = new TreeSet<AttributeMetaData>(new Comparator<AttributeMetaData>(){

        @Override
        public int compare(AttributeMetaData a1, AttributeMetaData a2) {
            if (!a1.isDnAttribute() || !a2.isDnAttribute()) {
                return 0;
            }
            return Integer.valueOf(a1.getDnAttribute().index()).compareTo(a2.getDnAttribute().index());
        }
    });
    private boolean indexedDnAttributes = false;
    private Set<CaseIgnoreString> objectClasses = new LinkedHashSet<CaseIgnoreString>();
    private Name base = LdapUtils.emptyLdapName();

    public Set<CaseIgnoreString> getObjectClasses() {
        return this.objectClasses;
    }

    public AttributeMetaData getIdAttribute() {
        return this.idAttribute;
    }

    @Override
    public Iterator<Field> iterator() {
        return this.fieldToAttribute.keySet().iterator();
    }

    public AttributeMetaData getAttribute(Field field) {
        return this.fieldToAttribute.get(field);
    }

    public ObjectMetaData(Class<?> clazz) {
        Entry entity;
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Extracting metadata from %1$s", clazz));
        }
        if ((entity = clazz.getAnnotation(Entry.class)) != null) {
            String[] localObjectClasses = entity.objectClasses();
            if (localObjectClasses != null && localObjectClasses.length > 0 && localObjectClasses[0].length() > 0) {
                for (String localObjectClass : localObjectClasses) {
                    this.objectClasses.add(new CaseIgnoreString(localObjectClass));
                }
            } else {
                this.objectClasses.add(new CaseIgnoreString(clazz.getSimpleName()));
            }
            String base = entity.base();
            if (StringUtils.hasText((String)base)) {
                this.base = LdapUtils.newLdapName(base);
            }
        } else {
            throw new MetaDataException(String.format("Class %1$s must have a class level %2$s annotation", clazz, Entry.class));
        }
        if (!Modifier.isFinal(clazz.getModifiers())) {
            LOG.warn(String.format("The Entry class %1$s should be declared final", clazz.getSimpleName()));
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) continue;
            AttributeMetaData currentAttributeMetaData = new AttributeMetaData(field);
            if (currentAttributeMetaData.isId()) {
                if (this.idAttribute != null) {
                    throw new MetaDataException(String.format("You man have only one field with the %1$s annotation in class %2$s", Id.class, clazz));
                }
                this.idAttribute = currentAttributeMetaData;
            }
            this.fieldToAttribute.put(field, currentAttributeMetaData);
            if (!currentAttributeMetaData.isDnAttribute()) continue;
            this.dnAttributes.add(currentAttributeMetaData);
        }
        if (this.idAttribute == null) {
            throw new MetaDataException(String.format("All Entry classes must define a field with the %1$s annotation, error in class %2$s", Id.class, clazz));
        }
        this.postProcessDnAttributes(clazz);
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Extracted metadata from %1$s as %2$s", clazz, this));
        }
    }

    private void postProcessDnAttributes(Class<?> clazz) {
        boolean hasIndexed = false;
        boolean hasNonIndexed = false;
        for (AttributeMetaData dnAttribute : this.dnAttributes) {
            int declaredIndex = dnAttribute.getDnAttribute().index();
            if (declaredIndex != -1) {
                hasIndexed = true;
            }
            if (declaredIndex != -1) continue;
            hasNonIndexed = true;
        }
        if (hasIndexed && hasNonIndexed) {
            throw new MetaDataException(String.format("At least one DnAttribute declared on class %s is indexed, which means that all DnAttributes must be indexed", clazz.toString()));
        }
        this.indexedDnAttributes = hasIndexed;
    }

    int size() {
        return this.fieldToAttribute.size();
    }

    boolean canCalculateDn() {
        return this.dnAttributes.size() > 0 && this.indexedDnAttributes;
    }

    public Set<AttributeMetaData> getDnAttributes() {
        return this.dnAttributes;
    }

    Name getBase() {
        return this.base;
    }

    public String toString() {
        return String.format("objectsClasses=%1$s | idField=%2$s | attributes=%3$s", this.objectClasses, this.idAttribute.getName(), this.fieldToAttribute);
    }
}

