/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.SpringVersion
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.ldap.odm.core.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.LdapDataEntry;
import org.springframework.core.SpringVersion;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.core.ObjectDirectoryMapper;
import org.springframework.ldap.odm.core.impl.AttributeMetaData;
import org.springframework.ldap.odm.core.impl.CaseIgnoreString;
import org.springframework.ldap.odm.core.impl.InvalidEntryException;
import org.springframework.ldap.odm.core.impl.ObjectMetaData;
import org.springframework.ldap.odm.typeconversion.ConverterManager;
import org.springframework.ldap.odm.typeconversion.impl.ConversionServiceConverterManager;
import org.springframework.ldap.odm.typeconversion.impl.ConverterManagerImpl;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public class DefaultObjectDirectoryMapper
implements ObjectDirectoryMapper {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultObjectDirectoryMapper.class);
    private ConverterManager converterManager;
    private static final String OBJECT_CLASS_ATTRIBUTE = "objectclass";
    private static final CaseIgnoreString OBJECT_CLASS_ATTRIBUTE_CI = new CaseIgnoreString("objectclass");
    private final ConcurrentMap<Class<?>, EntityData> metaDataMap = new ConcurrentHashMap();

    public DefaultObjectDirectoryMapper() {
        this.converterManager = DefaultObjectDirectoryMapper.createDefaultConverterManager();
    }

    private static ConverterManager createDefaultConverterManager() {
        String springVersion = SpringVersion.getVersion();
        if (springVersion == null) {
            LOG.debug("Could not determine the Spring Version. Guessing > Spring 3.0. If this does not work, please ensure to explicitly set converterManager");
            return new ConversionServiceConverterManager();
        }
        if (springVersion.compareTo("3.0") > 0) {
            return new ConversionServiceConverterManager();
        }
        return new ConverterManagerImpl();
    }

    public void setConverterManager(ConverterManager converterManager) {
        this.converterManager = converterManager;
    }

    private EntityData getEntityData(Class<?> managedClass) {
        EntityData result = (EntityData)this.metaDataMap.get(managedClass);
        if (result == null) {
            return this.addManagedClass(managedClass);
        }
        return result;
    }

    @Override
    public String[] manageClass(Class<?> clazz) {
        EntityData entityData = this.getEntityData(clazz);
        HashSet<String> managedAttributeNames = new HashSet<String>();
        for (Field field : entityData.metaData) {
            AttributeMetaData attributeMetaData = entityData.metaData.getAttribute(field);
            if (attributeMetaData.isTransient()) continue;
            String[] attributesOfField = attributeMetaData.getAttributes();
            if (attributesOfField != null && attributesOfField.length > 0) {
                managedAttributeNames.addAll(Arrays.asList(attributesOfField));
                continue;
            }
            managedAttributeNames.add(field.getName());
        }
        managedAttributeNames.add(OBJECT_CLASS_ATTRIBUTE);
        return managedAttributeNames.toArray(new String[managedAttributeNames.size()]);
    }

    private EntityData addManagedClass(Class<?> managedClass) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Adding class %1$s to managed set", managedClass));
        }
        ObjectMetaData metaData = new ObjectMetaData(managedClass);
        try {
            managedClass.getConstructor(new Class[0]);
        }
        catch (NoSuchMethodException e) {
            throw new InvalidEntryException(String.format("The class %1$s must have a zero argument constructor to be an Entry", managedClass), e);
        }
        for (Object field : metaData) {
            AttributeMetaData attributeInfo = metaData.getAttribute((Field)field);
            if (attributeInfo.isTransient() || attributeInfo.isId() || attributeInfo.isObjectClass()) continue;
            this.verifyConversion(managedClass, (Field)field, attributeInfo);
        }
        AndFilter ocFilter = new AndFilter();
        for (CaseIgnoreString oc : metaData.getObjectClasses()) {
            ocFilter.and(new EqualsFilter(OBJECT_CLASS_ATTRIBUTE, oc.toString()));
        }
        EntityData newValue = new EntityData(metaData, ocFilter);
        EntityData previousValue = this.metaDataMap.putIfAbsent(managedClass, newValue);
        if (previousValue != null) {
            return previousValue;
        }
        return newValue;
    }

    private void verifyConversion(Class<?> managedClass, Field field, AttributeMetaData attributeInfo) {
        Class<?> jndiClass = attributeInfo.getJndiClass();
        Class<?> javaClass = attributeInfo.getValueClass();
        if (!this.converterManager.canConvert(jndiClass, attributeInfo.getSyntax(), javaClass)) {
            throw new InvalidEntryException(String.format("Missing converter from %1$s to %2$s, this is needed for field %3$s on Entry %4$s", jndiClass, javaClass, field.getName(), managedClass));
        }
        if (!attributeInfo.isReadOnly() && !this.converterManager.canConvert(javaClass, attributeInfo.getSyntax(), jndiClass)) {
            throw new InvalidEntryException(String.format("Missing converter from %1$s to %2$s, this is needed for field %3$s on Entry %4$s", javaClass, jndiClass, field.getName(), managedClass));
        }
    }

    @Override
    public void mapToLdapDataEntry(Object entry, LdapDataEntry context) {
        ObjectMetaData metaData = this.getEntityData(entry.getClass()).metaData;
        Attribute objectclassAttribute = context.getAttributes().get(OBJECT_CLASS_ATTRIBUTE);
        if (objectclassAttribute == null || objectclassAttribute.size() == 0) {
            int numOcs = metaData.getObjectClasses().size();
            CaseIgnoreString[] metaDataObjectClasses = metaData.getObjectClasses().toArray(new CaseIgnoreString[numOcs]);
            Object[] stringOcs = new String[numOcs];
            for (int ocIndex = 0; ocIndex < numOcs; ++ocIndex) {
                stringOcs[ocIndex] = metaDataObjectClasses[ocIndex].toString();
            }
            context.setAttributeValues(OBJECT_CLASS_ATTRIBUTE, stringOcs);
        }
        for (Field field : metaData) {
            AttributeMetaData attributeInfo = metaData.getAttribute(field);
            if (attributeInfo.isTransient() || attributeInfo.isId() || attributeInfo.isObjectClass() || attributeInfo.isReadOnly()) continue;
            try {
                Class<?> targetClass = attributeInfo.getJndiClass();
                if (!attributeInfo.isCollection()) {
                    this.populateSingleValueAttribute(entry, context, field, attributeInfo, targetClass);
                    continue;
                }
                this.populateMultiValueAttribute(entry, context, field, attributeInfo, targetClass);
            }
            catch (IllegalAccessException e) {
                throw new InvalidEntryException(String.format("Can't set attribute %1$s", attributeInfo.getName()), e);
            }
        }
    }

    private void populateMultiValueAttribute(Object entry, LdapDataEntry context, Field field, AttributeMetaData attributeInfo, Class<?> targetClass) throws IllegalAccessException {
        ArrayList attributeValues = new ArrayList();
        Collection fieldValues = (Collection)field.get(entry);
        if (fieldValues != null) {
            for (Object o : fieldValues) {
                if (o == null) continue;
                attributeValues.add(this.converterManager.convert(o, attributeInfo.getSyntax(), targetClass));
            }
            context.setAttributeValues(attributeInfo.getName().toString(), attributeValues.toArray());
        }
    }

    private void populateSingleValueAttribute(Object entry, LdapDataEntry context, Field field, AttributeMetaData attributeInfo, Class<?> targetClass) throws IllegalAccessException {
        Object fieldValue = field.get(entry);
        if (fieldValue != null) {
            context.setAttributeValue(attributeInfo.getName().toString(), this.converterManager.convert(fieldValue, attributeInfo.getSyntax(), targetClass));
        } else {
            context.setAttributeValue(attributeInfo.getName().toString(), null);
        }
    }

    @Override
    public <T> T mapFromLdapDataEntry(LdapDataEntry context, Class<T> clazz) {
        T result;
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Converting to Java Entry class %1$s from %2$s", clazz, context));
        }
        ObjectMetaData metaData = this.getEntityData(clazz).metaData;
        try {
            result = clazz.newInstance();
            HashMap<CaseIgnoreString, Attribute> attributeValueMap = new HashMap<CaseIgnoreString, Attribute>();
            Attributes attributes = context.getAttributes();
            NamingEnumeration<? extends Attribute> attributesEnumeration = attributes.getAll();
            while (attributesEnumeration.hasMoreElements()) {
                Attribute currentAttribute = (Attribute)attributesEnumeration.nextElement();
                attributeValueMap.put(new CaseIgnoreString(currentAttribute.getID()), currentAttribute);
            }
            Attribute ocAttribute = (Attribute)attributeValueMap.get(OBJECT_CLASS_ATTRIBUTE_CI);
            if (ocAttribute != null) {
                HashSet<CaseIgnoreString> objectClassesFromJndi = new HashSet<CaseIgnoreString>();
                NamingEnumeration<?> objectClassesFromJndiEnum = ocAttribute.getAll();
                while (objectClassesFromJndiEnum.hasMoreElements()) {
                    objectClassesFromJndi.add(new CaseIgnoreString((String)objectClassesFromJndiEnum.nextElement()));
                }
                if (!DefaultObjectDirectoryMapper.collectionContainsAll(objectClassesFromJndi, metaData.getObjectClasses())) {
                    return null;
                }
            } else {
                throw new InvalidEntryException(String.format("No object classes were returned for class %1$s", clazz.getName()));
            }
            for (Field field : metaData) {
                DnAttribute dnAttribute;
                AttributeMetaData attributeInfo = metaData.getAttribute(field);
                Name dn = context.getDn();
                if (!attributeInfo.isTransient() && !attributeInfo.isId()) {
                    if (!attributeInfo.isCollection()) {
                        this.populateSingleValueField(result, attributeValueMap, field, attributeInfo);
                    } else {
                        this.populateMultiValueField(result, attributeValueMap, field, attributeInfo);
                    }
                } else if (attributeInfo.isId()) {
                    field.set(result, this.converterManager.convert(dn, attributeInfo.getSyntax(), attributeInfo.getValueClass()));
                }
                if ((dnAttribute = attributeInfo.getDnAttribute()) == null) continue;
                int index = dnAttribute.index();
                String dnValue = index != -1 ? LdapUtils.getStringValue(dn, index) : LdapUtils.getStringValue(dn, dnAttribute.value());
                field.set(result, dnValue);
            }
        }
        catch (NamingException ne) {
            throw new InvalidEntryException(String.format("Problem creating %1$s from LDAP Entry %2$s", clazz, context), ne);
        }
        catch (IllegalAccessException iae) {
            throw new InvalidEntryException(String.format("Could not create an instance of %1$s could not access field", clazz.getName()), iae);
        }
        catch (InstantiationException ie) {
            throw new InvalidEntryException(String.format("Could not instantiate %1$s", clazz), ie);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Converted object - %1$s", result));
        }
        return result;
    }

    private <T> void populateMultiValueField(T result, Map<CaseIgnoreString, Attribute> attributeValueMap, Field field, AttributeMetaData attributeInfo) throws NamingException, IllegalAccessException {
        Collection<Object> fieldValues = attributeInfo.newCollectionInstance();
        Attribute currentAttribute = attributeValueMap.get(attributeInfo.getName());
        if (currentAttribute != null) {
            NamingEnumeration<?> valuesEmumeration = currentAttribute.getAll();
            while (valuesEmumeration.hasMore()) {
                Object value = valuesEmumeration.nextElement();
                if (value == null) continue;
                fieldValues.add(this.converterManager.convert(value, attributeInfo.getSyntax(), attributeInfo.getValueClass()));
            }
        }
        field.set(result, fieldValues);
    }

    private <T> void populateSingleValueField(T result, Map<CaseIgnoreString, Attribute> attributeValueMap, Field field, AttributeMetaData attributeInfo) throws NamingException, IllegalAccessException {
        Object value;
        Attribute attribute = attributeValueMap.get(attributeInfo.getName());
        if (attribute != null && (value = attribute.get()) != null) {
            Object convertedValue = this.converterManager.convert(value, attributeInfo.getSyntax(), attributeInfo.getValueClass());
            field.set(result, convertedValue);
        }
    }

    @Override
    public Name getId(Object entry) {
        try {
            return (Name)this.getIdField(entry).get(entry);
        }
        catch (Exception e) {
            throw new InvalidEntryException(String.format("Can't get Id field from Entry %1$s", entry), e);
        }
    }

    private Field getIdField(Object entry) {
        return this.getEntityData(entry.getClass()).metaData.getIdAttribute().getField();
    }

    @Override
    public void setId(Object entry, Name id) {
        try {
            this.getIdField(entry).set(entry, id);
        }
        catch (Exception e) {
            throw new InvalidEntryException(String.format("Can't set Id field on Entry %s to %s", entry, id), e);
        }
    }

    @Override
    public Name getCalculatedId(Object entry) {
        Assert.notNull((Object)entry, (String)"Entry must not be null");
        EntityData entityData = this.getEntityData(entry.getClass());
        if (entityData.metaData.canCalculateDn()) {
            Set<AttributeMetaData> dnAttributes = entityData.metaData.getDnAttributes();
            LdapNameBuilder ldapNameBuilder = LdapNameBuilder.newInstance(entityData.metaData.getBase());
            for (AttributeMetaData dnAttribute : dnAttributes) {
                Object dnFieldValue = ReflectionUtils.getField((Field)dnAttribute.getField(), (Object)entry);
                if (dnFieldValue == null) {
                    throw new IllegalStateException(String.format("DnAttribute for field %s on class %s is null; cannot build DN", dnAttribute.getField().getName(), entry.getClass().getName()));
                }
                ldapNameBuilder.add(dnAttribute.getDnAttribute().value(), dnFieldValue.toString());
            }
            return ldapNameBuilder.build();
        }
        return null;
    }

    @Override
    public Filter filterFor(Class<?> clazz, Filter baseFilter) {
        Filter ocFilter = this.getEntityData(clazz).ocFilter;
        if (baseFilter == null) {
            return ocFilter;
        }
        AndFilter andFilter = new AndFilter();
        return andFilter.append(ocFilter).append(baseFilter);
    }

    @Override
    public String attributeFor(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            AttributeMetaData attributeMetaData = this.getEntityData(clazz).metaData.getAttribute(field);
            return attributeMetaData.getName().toString();
        }
        catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(String.format("Field %s cannot be found in class %s", fieldName, clazz), e);
        }
    }

    ConcurrentMap<Class<?>, EntityData> getMetaDataMap() {
        return this.metaDataMap;
    }

    static boolean collectionContainsAll(Collection<?> collection, Set<?> shouldBePresent) {
        for (Object o : shouldBePresent) {
            if (collection.contains(o)) continue;
            return false;
        }
        return true;
    }

    static final class EntityData {
        final ObjectMetaData metaData;
        final Filter ocFilter;

        private EntityData(ObjectMetaData metaData, Filter ocFilter) {
            this.metaData = metaData;
            this.ocFilter = ocFilter;
        }
    }
}

