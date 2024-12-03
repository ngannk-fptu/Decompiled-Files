/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.core.util.FastField;
import com.thoughtworks.xstream.mapper.AttributeMapper;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import java.util.HashMap;
import java.util.Map;

public class LocalConversionMapper
extends MapperWrapper {
    private final Map localConverters = new HashMap();
    private transient AttributeMapper attributeMapper;

    public LocalConversionMapper(Mapper wrapped) {
        super(wrapped);
        this.readResolve();
    }

    public void registerLocalConverter(Class definedIn, String fieldName, Converter converter) {
        this.localConverters.put(new FastField(definedIn, fieldName), converter);
    }

    public Converter getLocalConverter(Class definedIn, String fieldName) {
        return (Converter)this.localConverters.get(new FastField(definedIn, fieldName));
    }

    public SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute, Class type) {
        SingleValueConverter converter = this.getLocalSingleValueConverter(definedIn, attribute, type);
        return converter == null ? super.getConverterFromAttribute(definedIn, attribute, type) : converter;
    }

    public SingleValueConverter getConverterFromItemType(String fieldName, Class type, Class definedIn) {
        SingleValueConverter converter = this.getLocalSingleValueConverter(definedIn, fieldName, type);
        return converter == null ? super.getConverterFromItemType(fieldName, type, definedIn) : converter;
    }

    private SingleValueConverter getLocalSingleValueConverter(Class definedIn, String fieldName, Class type) {
        Converter converter;
        if (this.attributeMapper != null && this.attributeMapper.shouldLookForSingleValueConverter(fieldName, type, definedIn) && (converter = this.getLocalConverter(definedIn, fieldName)) != null && converter instanceof SingleValueConverter) {
            return (SingleValueConverter)((Object)converter);
        }
        return null;
    }

    private Object readResolve() {
        this.attributeMapper = (AttributeMapper)this.lookupMapperOfType(AttributeMapper.class);
        return this;
    }
}

