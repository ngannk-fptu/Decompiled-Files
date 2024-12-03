/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterMatcher;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.extended.UseAttributeForEnumMapper;
import com.thoughtworks.xstream.converters.reflection.AbstractReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.util.FastField;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.core.util.Primitives;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class ToAttributedValueConverter
implements Converter {
    private static final String STRUCTURE_MARKER = "";
    private final Class type;
    private final Mapper mapper;
    private final Mapper enumMapper;
    private final ReflectionProvider reflectionProvider;
    private final ConverterLookup lookup;
    private final Field valueField;

    public ToAttributedValueConverter(Class type, Mapper mapper, ReflectionProvider reflectionProvider, ConverterLookup lookup) {
        this(type, mapper, reflectionProvider, lookup, null, null);
    }

    public ToAttributedValueConverter(Class type, Mapper mapper, ReflectionProvider reflectionProvider, ConverterLookup lookup, String valueFieldName) {
        this(type, mapper, reflectionProvider, lookup, valueFieldName, null);
    }

    public ToAttributedValueConverter(Class type, Mapper mapper, ReflectionProvider reflectionProvider, ConverterLookup lookup, String valueFieldName, Class valueDefinedIn) {
        this.type = type;
        this.mapper = mapper;
        this.reflectionProvider = reflectionProvider;
        this.lookup = lookup;
        if (valueFieldName == null) {
            this.valueField = null;
        } else {
            Field field = null;
            try {
                field = (valueDefinedIn != null ? valueDefinedIn : type).getDeclaredField(valueFieldName);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
            }
            catch (NoSuchFieldException e) {
                throw new IllegalArgumentException(e.getMessage() + ": " + valueFieldName);
            }
            this.valueField = field;
        }
        this.enumMapper = JVM.isVersion(5) ? UseAttributeForEnumMapper.createEnumMapper(mapper) : null;
    }

    public boolean canConvert(Class type) {
        return this.type == type;
    }

    public void marshal(Object source, final HierarchicalStreamWriter writer, MarshallingContext context) {
        final Class<?> sourceType = source.getClass();
        final HashMap defaultFieldDefinition = new HashMap();
        final String[] tagValue = new String[1];
        final Object[] realValue = new Object[1];
        final Class[] fieldType = new Class[1];
        final Class[] definingType = new Class[1];
        this.reflectionProvider.visitSerializableFields(source, new ReflectionProvider.Visitor(){

            public void visit(String fieldName, Class type, Class definedIn, Object value) {
                ConverterMatcher converter;
                if (!ToAttributedValueConverter.this.mapper.shouldSerializeMember(definedIn, fieldName)) {
                    return;
                }
                FastField field = new FastField(definedIn, fieldName);
                String alias = ToAttributedValueConverter.this.mapper.serializedMember(definedIn, fieldName);
                if (!defaultFieldDefinition.containsKey(alias)) {
                    Class lookupType = sourceType;
                    defaultFieldDefinition.put(alias, ToAttributedValueConverter.this.reflectionProvider.getField(lookupType, fieldName));
                } else if (!ToAttributedValueConverter.this.fieldIsEqual(field)) {
                    ConversionException exception = new ConversionException("Cannot write attribute twice for object");
                    exception.add("alias", alias);
                    exception.add("type", sourceType.getName());
                    throw exception;
                }
                ConverterMatcher converterMatcher = converter = UseAttributeForEnumMapper.isEnum(type) ? ToAttributedValueConverter.this.enumMapper.getConverterFromItemType(null, type, null) : ToAttributedValueConverter.this.mapper.getLocalConverter(definedIn, fieldName);
                if (converter == null) {
                    converter = ToAttributedValueConverter.this.lookup.lookupConverterForType(type);
                }
                if (value != null) {
                    boolean isValueField;
                    boolean bl = isValueField = ToAttributedValueConverter.this.valueField != null && ToAttributedValueConverter.this.fieldIsEqual(field);
                    if (isValueField) {
                        definingType[0] = definedIn;
                        fieldType[0] = type;
                        realValue[0] = value;
                        tagValue[0] = ToAttributedValueConverter.STRUCTURE_MARKER;
                    }
                    if (converter instanceof SingleValueConverter) {
                        String str = ((SingleValueConverter)converter).toString(value);
                        if (isValueField) {
                            tagValue[0] = str;
                        } else if (str != null) {
                            writer.addAttribute(alias, str);
                        }
                    } else if (!isValueField) {
                        ConversionException exception = new ConversionException("Cannot write element as attribute");
                        exception.add("alias", alias);
                        exception.add("type", sourceType.getName());
                        throw exception;
                    }
                }
            }
        });
        if (tagValue[0] != null) {
            String attributeName;
            String serializedClassName;
            Class defaultType;
            Class<?> actualType = realValue[0].getClass();
            if (!actualType.equals(defaultType = this.mapper.defaultImplementationOf(fieldType[0])) && !(serializedClassName = this.mapper.serializedClass(actualType)).equals(this.mapper.serializedClass(defaultType)) && (attributeName = this.mapper.aliasForSystemAttribute("class")) != null) {
                writer.addAttribute(attributeName, serializedClassName);
            }
            if (tagValue[0] == STRUCTURE_MARKER) {
                context.convertAnother(realValue[0]);
            } else {
                writer.setValue(tagValue[0]);
            }
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Class type;
        String fieldName;
        Field field;
        Object result = this.reflectionProvider.newInstance(context.getRequiredType());
        Class<?> resultType = result.getClass();
        HashSet<FastField> seenFields = new HashSet<FastField>();
        Iterator it = reader.getAttributeNames();
        HashSet<String> systemAttributes = new HashSet<String>();
        systemAttributes.add(this.mapper.aliasForSystemAttribute("class"));
        while (it.hasNext()) {
            ConverterMatcher converter;
            String attrName = (String)it.next();
            if (systemAttributes.contains(attrName) || (field = this.reflectionProvider.getFieldOrNull(resultType, fieldName = this.mapper.realMember(resultType, attrName))) == null || Modifier.isTransient(field.getModifiers())) continue;
            type = field.getType();
            Class<?> declaringClass = field.getDeclaringClass();
            ConverterMatcher converterMatcher = converter = UseAttributeForEnumMapper.isEnum(type) ? this.enumMapper.getConverterFromItemType(null, type, null) : this.mapper.getLocalConverter(declaringClass, fieldName);
            if (converter == null) {
                converter = this.lookup.lookupConverterForType(type);
            }
            if (!(converter instanceof SingleValueConverter)) {
                ConversionException exception = new ConversionException("Cannot read field as a single value for object");
                exception.add("field", fieldName);
                exception.add("type", resultType.getName());
                throw exception;
            }
            if (converter == null) continue;
            Object value = ((SingleValueConverter)converter).fromString(reader.getAttribute(attrName));
            if (type.isPrimitive()) {
                type = Primitives.box(type);
            }
            if (value != null && !type.isAssignableFrom(value.getClass())) {
                ConversionException exception = new ConversionException("Cannot assign object to type");
                exception.add("object type", value.getClass().getName());
                exception.add("target type", type.getName());
                throw exception;
            }
            this.reflectionProvider.writeField(result, fieldName, value, declaringClass);
            if (seenFields.add(new FastField(declaringClass, fieldName))) continue;
            throw new AbstractReflectionConverter.DuplicateFieldException(fieldName + " [" + declaringClass.getName() + "]");
        }
        if (this.valueField != null) {
            Class<?> classDefiningField = this.valueField.getDeclaringClass();
            fieldName = this.valueField.getName();
            Field field2 = field = fieldName == null ? null : this.reflectionProvider.getField(classDefiningField, fieldName);
            if (fieldName == null || field == null) {
                ConversionException exception = new ConversionException("Cannot assign value to field of type");
                exception.add("element", reader.getNodeName());
                exception.add("field", fieldName);
                exception.add("target type", context.getRequiredType().getName());
                throw exception;
            }
            String classAttribute = HierarchicalStreams.readClassAttribute(reader, this.mapper);
            type = classAttribute != null ? this.mapper.realClass(classAttribute) : this.mapper.defaultImplementationOf(this.reflectionProvider.getFieldType(result, fieldName, classDefiningField));
            Object value = context.convertAnother(result, type, this.mapper.getLocalConverter(field.getDeclaringClass(), field.getName()));
            Class definedType = this.reflectionProvider.getFieldType(result, fieldName, classDefiningField);
            if (!definedType.isPrimitive()) {
                type = definedType;
            }
            if (value != null && !type.isAssignableFrom(value.getClass())) {
                ConversionException exception = new ConversionException("Cannot assign object to type");
                exception.add("object type", value.getClass().getName());
                exception.add("target type", type.getName());
                throw exception;
            }
            this.reflectionProvider.writeField(result, fieldName, value, classDefiningField);
            if (!seenFields.add(new FastField(classDefiningField, fieldName))) {
                throw new AbstractReflectionConverter.DuplicateFieldException(fieldName + " [" + classDefiningField.getName() + "]");
            }
        }
        return result;
    }

    private boolean fieldIsEqual(FastField field) {
        return this.valueField.getName().equals(field.getName()) && this.valueField.getDeclaringClass().getName().equals(field.getDeclaringClass());
    }
}

