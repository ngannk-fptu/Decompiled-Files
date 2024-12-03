/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.javabean;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.javabean.BeanProvider;
import com.thoughtworks.xstream.converters.javabean.JavaBeanProvider;
import com.thoughtworks.xstream.converters.reflection.MissingFieldException;
import com.thoughtworks.xstream.core.util.FastField;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import java.util.HashSet;

public class JavaBeanConverter
implements Converter {
    protected final Mapper mapper;
    protected final JavaBeanProvider beanProvider;
    private final Class type;
    private String classAttributeIdentifier;
    static /* synthetic */ Class class$com$thoughtworks$xstream$mapper$Mapper$Null;

    public JavaBeanConverter(Mapper mapper) {
        this(mapper, (Class)null);
    }

    public JavaBeanConverter(Mapper mapper, Class type) {
        this(mapper, new BeanProvider(), type);
    }

    public JavaBeanConverter(Mapper mapper, JavaBeanProvider beanProvider) {
        this(mapper, beanProvider, null);
    }

    public JavaBeanConverter(Mapper mapper, JavaBeanProvider beanProvider, Class type) {
        this.mapper = mapper;
        this.beanProvider = beanProvider;
        this.type = type;
    }

    public JavaBeanConverter(Mapper mapper, String classAttributeIdentifier) {
        this(mapper, new BeanProvider());
        this.classAttributeIdentifier = classAttributeIdentifier;
    }

    public boolean canConvert(Class type) {
        return (this.type == null || this.type == type) && this.beanProvider.canInstantiate(type);
    }

    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final String classAttributeName = this.mapper.aliasForSystemAttribute("class");
        this.beanProvider.visitSerializableProperties(source, new JavaBeanProvider.Visitor(){

            public boolean shouldVisit(String name, Class definedIn) {
                return JavaBeanConverter.this.mapper.shouldSerializeMember(definedIn, name);
            }

            public void visit(String propertyName, Class fieldType, Class definedIn, Object newObj) {
                if (newObj != null) {
                    this.writeField(propertyName, fieldType, newObj);
                } else {
                    this.writeNullField(propertyName);
                }
            }

            private void writeField(String propertyName, Class fieldType, Object newObj) {
                Class<?> actualType = newObj.getClass();
                Class defaultType = JavaBeanConverter.this.mapper.defaultImplementationOf(fieldType);
                String serializedMember = JavaBeanConverter.this.mapper.serializedMember(source.getClass(), propertyName);
                ExtendedHierarchicalStreamWriterHelper.startNode(writer, serializedMember, actualType);
                if (!actualType.equals(defaultType) && classAttributeName != null) {
                    writer.addAttribute(classAttributeName, JavaBeanConverter.this.mapper.serializedClass(actualType));
                }
                context.convertAnother(newObj);
                writer.endNode();
            }

            private void writeNullField(String propertyName) {
                String serializedMember = JavaBeanConverter.this.mapper.serializedMember(source.getClass(), propertyName);
                ExtendedHierarchicalStreamWriterHelper.startNode(writer, serializedMember, class$com$thoughtworks$xstream$mapper$Mapper$Null == null ? (class$com$thoughtworks$xstream$mapper$Mapper$Null = JavaBeanConverter.class$("com.thoughtworks.xstream.mapper.Mapper$Null")) : class$com$thoughtworks$xstream$mapper$Mapper$Null);
                writer.addAttribute(classAttributeName, JavaBeanConverter.this.mapper.serializedClass(class$com$thoughtworks$xstream$mapper$Mapper$Null == null ? (class$com$thoughtworks$xstream$mapper$Mapper$Null = JavaBeanConverter.class$("com.thoughtworks.xstream.mapper.Mapper$Null")) : class$com$thoughtworks$xstream$mapper$Mapper$Null));
                writer.endNode();
            }
        });
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Object result = this.instantiateNewInstance(context);
        HashSet seenProperties = new HashSet(){

            public boolean add(Object e) {
                if (!super.add(e)) {
                    throw new DuplicatePropertyException(((FastField)e).getName());
                }
                return true;
            }
        };
        Class<?> resultType = result.getClass();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String propertyName = this.mapper.realMember(resultType, reader.getNodeName());
            if (this.mapper.shouldSerializeMember(resultType, propertyName)) {
                boolean propertyExistsInClass = this.beanProvider.propertyDefinedInClass(propertyName, resultType);
                if (propertyExistsInClass) {
                    Class type = this.determineType(reader, result, propertyName);
                    Object value = context.convertAnother(result, type);
                    this.beanProvider.writeProperty(result, propertyName, value);
                    seenProperties.add(new FastField(resultType, propertyName));
                } else if (!this.mapper.isIgnoredElement(propertyName)) {
                    throw new MissingFieldException(resultType.getName(), propertyName);
                }
            }
            reader.moveUp();
        }
        return result;
    }

    private Object instantiateNewInstance(UnmarshallingContext context) {
        Object result = context.currentObject();
        if (result == null) {
            result = this.beanProvider.newInstance(context.getRequiredType());
        }
        return result;
    }

    private Class determineType(HierarchicalStreamReader reader, Object result, String fieldName) {
        String classAttribute;
        String classAttributeName = this.classAttributeIdentifier != null ? this.classAttributeIdentifier : this.mapper.aliasForSystemAttribute("class");
        String string = classAttribute = classAttributeName == null ? null : reader.getAttribute(classAttributeName);
        if (classAttribute != null) {
            return this.mapper.realClass(classAttribute);
        }
        return this.mapper.defaultImplementationOf(this.beanProvider.getPropertyType(result, fieldName));
    }

    public static class DuplicatePropertyException
    extends ConversionException {
        public DuplicatePropertyException(String msg) {
            super("Duplicate property " + msg);
            this.add("property", msg);
        }
    }

    public static class DuplicateFieldException
    extends ConversionException {
        public DuplicateFieldException(String msg) {
            super(msg);
        }
    }
}

