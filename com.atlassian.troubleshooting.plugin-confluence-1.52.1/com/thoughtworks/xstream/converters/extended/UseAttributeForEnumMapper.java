/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.core.util.DependencyInjectionFactory;
import com.thoughtworks.xstream.mapper.AttributeMapper;
import com.thoughtworks.xstream.mapper.DefaultMapper;
import com.thoughtworks.xstream.mapper.Mapper;

class UseAttributeForEnumMapper
extends AttributeMapper {
    static /* synthetic */ Class class$java$lang$Object;

    public UseAttributeForEnumMapper(Mapper wrapped) {
        super(wrapped, null, null);
    }

    public static boolean isEnum(Class type) {
        while (type != null && type != (class$java$lang$Object == null ? UseAttributeForEnumMapper.class$("java.lang.Object") : class$java$lang$Object)) {
            if (type.getName().equals("java.lang.Enum")) {
                return true;
            }
            type = type.getSuperclass();
        }
        return false;
    }

    public boolean shouldLookForSingleValueConverter(String fieldName, Class type, Class definedIn) {
        return UseAttributeForEnumMapper.isEnum(type);
    }

    public SingleValueConverter getConverterFromItemType(String fieldName, Class type, Class definedIn) {
        return null;
    }

    public SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute, Class type) {
        return null;
    }

    static Mapper createEnumMapper(Mapper mapper) {
        try {
            Class<?> enumMapperClass = Class.forName("com.thoughtworks.xstream.mapper.EnumMapper", true, Mapper.class.getClassLoader());
            return (Mapper)DependencyInjectionFactory.newInstance(enumMapperClass, new Object[]{new UseAttributeForEnumMapper(mapper.lookupMapperOfType(DefaultMapper.class))});
        }
        catch (Exception e) {
            return null;
        }
    }
}

