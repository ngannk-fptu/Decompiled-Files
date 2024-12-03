/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AttributeMapper
extends MapperWrapper {
    private final Map fieldNameToTypeMap = new HashMap();
    private final Set typeSet = new HashSet();
    private ConverterLookup converterLookup;
    private ReflectionProvider reflectionProvider;
    private final Set fieldToUseAsAttribute = new HashSet();

    public AttributeMapper(Mapper wrapped) {
        this(wrapped, null, null);
    }

    public AttributeMapper(Mapper wrapped, ConverterLookup converterLookup, ReflectionProvider refProvider) {
        super(wrapped);
        this.converterLookup = converterLookup;
        this.reflectionProvider = refProvider;
    }

    public void setConverterLookup(ConverterLookup converterLookup) {
        this.converterLookup = converterLookup;
    }

    public void addAttributeFor(String fieldName, Class type) {
        this.fieldNameToTypeMap.put(fieldName, type);
    }

    public void addAttributeFor(Class type) {
        this.typeSet.add(type);
    }

    private SingleValueConverter getLocalConverterFromItemType(Class type) {
        Converter converter = this.converterLookup.lookupConverterForType(type);
        if (converter != null && converter instanceof SingleValueConverter) {
            return (SingleValueConverter)((Object)converter);
        }
        return null;
    }

    public SingleValueConverter getConverterFromItemType(String fieldName, Class type) {
        if (this.fieldNameToTypeMap.get(fieldName) == type) {
            return this.getLocalConverterFromItemType(type);
        }
        return null;
    }

    public SingleValueConverter getConverterFromItemType(String fieldName, Class type, Class definedIn) {
        SingleValueConverter converter;
        if (this.shouldLookForSingleValueConverter(fieldName, type, definedIn) && (converter = this.getLocalConverterFromItemType(type)) != null) {
            return converter;
        }
        return super.getConverterFromItemType(fieldName, type, definedIn);
    }

    public boolean shouldLookForSingleValueConverter(String fieldName, Class type, Class definedIn) {
        if (this.typeSet.contains(type)) {
            return true;
        }
        if (this.fieldNameToTypeMap.get(fieldName) == type) {
            return true;
        }
        if (fieldName != null && definedIn != null) {
            Field field = this.reflectionProvider.getFieldOrNull(definedIn, fieldName);
            return field != null && this.fieldToUseAsAttribute.contains(field);
        }
        return false;
    }

    public SingleValueConverter getConverterFromItemType(Class type) {
        if (this.typeSet.contains(type)) {
            return this.getLocalConverterFromItemType(type);
        }
        return null;
    }

    public SingleValueConverter getConverterFromAttribute(String attributeName) {
        SingleValueConverter converter = null;
        Class type = (Class)this.fieldNameToTypeMap.get(attributeName);
        if (type != null) {
            converter = this.getLocalConverterFromItemType(type);
        }
        return converter;
    }

    public SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute) {
        Field field = this.reflectionProvider.getFieldOrNull(definedIn, attribute);
        return field != null ? this.getConverterFromAttribute(definedIn, attribute, field.getType()) : null;
    }

    public SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute, Class type) {
        SingleValueConverter converter;
        if (this.shouldLookForSingleValueConverter(attribute, type, definedIn) && (converter = this.getLocalConverterFromItemType(type)) != null) {
            return converter;
        }
        return super.getConverterFromAttribute(definedIn, attribute, type);
    }

    public void addAttributeFor(Field field) {
        if (field != null) {
            this.fieldToUseAsAttribute.add(field);
        }
    }

    public void addAttributeFor(Class definedIn, String fieldName) {
        this.addAttributeFor(this.reflectionProvider.getField(definedIn, fieldName));
    }
}

