/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;

public interface Mapper {
    public String serializedClass(Class var1);

    public Class realClass(String var1);

    public String serializedMember(Class var1, String var2);

    public String realMember(Class var1, String var2);

    public boolean isImmutableValueType(Class var1);

    public boolean isReferenceable(Class var1);

    public Class defaultImplementationOf(Class var1);

    public String aliasForAttribute(String var1);

    public String attributeForAlias(String var1);

    public String aliasForSystemAttribute(String var1);

    public String getFieldNameForItemTypeAndName(Class var1, Class var2, String var3);

    public Class getItemTypeForItemFieldName(Class var1, String var2);

    public ImplicitCollectionMapping getImplicitCollectionDefForFieldName(Class var1, String var2);

    public boolean shouldSerializeMember(Class var1, String var2);

    public boolean isIgnoredElement(String var1);

    public SingleValueConverter getConverterFromItemType(String var1, Class var2);

    public SingleValueConverter getConverterFromItemType(Class var1);

    public SingleValueConverter getConverterFromAttribute(String var1);

    public Converter getLocalConverter(Class var1, String var2);

    public Mapper lookupMapperOfType(Class var1);

    public SingleValueConverter getConverterFromItemType(String var1, Class var2, Class var3);

    public String aliasForAttribute(Class var1, String var2);

    public String attributeForAlias(Class var1, String var2);

    public SingleValueConverter getConverterFromAttribute(Class var1, String var2);

    public SingleValueConverter getConverterFromAttribute(Class var1, String var2, Class var3);

    public static interface ImplicitCollectionMapping {
        public String getFieldName();

        public String getItemFieldName();

        public Class getItemType();

        public String getKeyFieldName();
    }

    public static class Null {
    }
}

