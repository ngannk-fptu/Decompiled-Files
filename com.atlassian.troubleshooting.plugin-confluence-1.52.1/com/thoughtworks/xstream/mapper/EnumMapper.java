/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.enums.EnumSingleValueConverter;
import com.thoughtworks.xstream.core.Caching;
import com.thoughtworks.xstream.mapper.AttributeMapper;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class EnumMapper
extends MapperWrapper
implements Caching {
    private transient AttributeMapper attributeMapper;
    private transient Map<Class, SingleValueConverter> enumConverterMap;

    @Deprecated
    public EnumMapper(Mapper wrapped, ConverterLookup lookup) {
        super(wrapped);
        this.readResolve();
    }

    public EnumMapper(Mapper wrapped) {
        super(wrapped);
        this.readResolve();
    }

    public String serializedClass(Class type) {
        if (type == null) {
            return super.serializedClass(type);
        }
        if (Enum.class.isAssignableFrom(type) && type.getSuperclass() != Enum.class) {
            return super.serializedClass(type.getSuperclass());
        }
        if (EnumSet.class.isAssignableFrom(type)) {
            return super.serializedClass(EnumSet.class);
        }
        return super.serializedClass(type);
    }

    public boolean isImmutableValueType(Class type) {
        return Enum.class.isAssignableFrom(type) || super.isImmutableValueType(type);
    }

    public boolean isReferenceable(Class type) {
        if (type != null && Enum.class.isAssignableFrom(type)) {
            return false;
        }
        return super.isReferenceable(type);
    }

    public SingleValueConverter getConverterFromItemType(String fieldName, Class type, Class definedIn) {
        SingleValueConverter converter = this.getLocalConverter(fieldName, type, definedIn);
        return converter == null ? super.getConverterFromItemType(fieldName, type, definedIn) : converter;
    }

    public SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute, Class type) {
        SingleValueConverter converter = this.getLocalConverter(attribute, type, definedIn);
        return converter == null ? super.getConverterFromAttribute(definedIn, attribute, type) : converter;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private SingleValueConverter getLocalConverter(String fieldName, Class type, Class definedIn) {
        if (this.attributeMapper != null && Enum.class.isAssignableFrom(type) && this.attributeMapper.shouldLookForSingleValueConverter(fieldName, type, definedIn)) {
            Map<Class, SingleValueConverter> map = this.enumConverterMap;
            synchronized (map) {
                SingleValueConverter singleValueConverter = this.enumConverterMap.get(type);
                if (singleValueConverter == null) {
                    singleValueConverter = super.getConverterFromItemType(fieldName, type, definedIn);
                    if (singleValueConverter == null) {
                        Class enumType = type;
                        singleValueConverter = new EnumSingleValueConverter(enumType);
                    }
                    this.enumConverterMap.put(type, singleValueConverter);
                }
                return singleValueConverter;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void flushCache() {
        if (this.enumConverterMap.size() > 0) {
            Map<Class, SingleValueConverter> map = this.enumConverterMap;
            synchronized (map) {
                this.enumConverterMap.clear();
            }
        }
    }

    private Object readResolve() {
        this.enumConverterMap = new HashMap<Class, SingleValueConverter>();
        this.attributeMapper = (AttributeMapper)this.lookupMapperOfType(AttributeMapper.class);
        return this;
    }
}

