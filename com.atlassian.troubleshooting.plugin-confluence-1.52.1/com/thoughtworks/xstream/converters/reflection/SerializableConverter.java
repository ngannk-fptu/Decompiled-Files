/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.AbstractReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.MissingFieldException;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionProviderWrapper;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.util.CustomObjectInputStream;
import com.thoughtworks.xstream.core.util.CustomObjectOutputStream;
import com.thoughtworks.xstream.core.util.Fields;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.mapper.Mapper;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputValidation;
import java.io.ObjectStreamClass;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SerializableConverter
extends AbstractReflectionConverter {
    private static final String ELEMENT_NULL = "null";
    private static final String ELEMENT_DEFAULT = "default";
    private static final String ELEMENT_UNSERIALIZABLE_PARENTS = "unserializable-parents";
    private static final String ATTRIBUTE_CLASS = "class";
    private static final String ATTRIBUTE_SERIALIZATION = "serialization";
    private static final String ATTRIBUTE_VALUE_CUSTOM = "custom";
    private static final String ELEMENT_FIELDS = "fields";
    private static final String ELEMENT_FIELD = "field";
    private static final String ATTRIBUTE_NAME = "name";
    private final ClassLoaderReference classLoaderReference;
    static /* synthetic */ Class class$java$lang$Object;

    public SerializableConverter(Mapper mapper, ReflectionProvider reflectionProvider, ClassLoaderReference classLoaderReference) {
        super(mapper, new UnserializableParentsReflectionProvider(reflectionProvider));
        this.classLoaderReference = classLoaderReference;
    }

    public SerializableConverter(Mapper mapper, ReflectionProvider reflectionProvider, ClassLoader classLoader) {
        this(mapper, reflectionProvider, new ClassLoaderReference(classLoader));
    }

    public SerializableConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
        this(mapper, (ReflectionProvider)new UnserializableParentsReflectionProvider(reflectionProvider), new ClassLoaderReference(null));
    }

    public boolean canConvert(Class type) {
        return JVM.canCreateDerivedObjectOutputStream() && this.isSerializable(type);
    }

    private boolean isSerializable(Class type) {
        if (type != null && Serializable.class.isAssignableFrom(type) && !type.isInterface() && (this.serializationMembers.supportsReadObject(type, true) || this.serializationMembers.supportsWriteObject(type, true))) {
            Iterator iter = this.hierarchyFor(type).iterator();
            while (iter.hasNext()) {
                if ((class$java$io$Serializable == null ? SerializableConverter.class$("java.io.Serializable") : class$java$io$Serializable).isAssignableFrom((Class)iter.next())) continue;
                return this.canAccess(type);
            }
            return true;
        }
        return false;
    }

    public void doMarshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        String attributeName = this.mapper.aliasForSystemAttribute(ATTRIBUTE_SERIALIZATION);
        if (attributeName != null) {
            writer.addAttribute(attributeName, ATTRIBUTE_VALUE_CUSTOM);
        }
        final Class[] currentType = new Class[1];
        final boolean[] writtenClassWrapper = new boolean[]{false};
        CustomObjectOutputStream.StreamCallback callback = new CustomObjectOutputStream.StreamCallback(){

            public void writeToStream(Object object) {
                if (object == null) {
                    writer.startNode(SerializableConverter.ELEMENT_NULL);
                    writer.endNode();
                } else {
                    ExtendedHierarchicalStreamWriterHelper.startNode(writer, SerializableConverter.this.mapper.serializedClass(object.getClass()), object.getClass());
                    context.convertAnother(object);
                    writer.endNode();
                }
            }

            public void writeFieldsToStream(Map fields) {
                ObjectStreamClass objectStreamClass = ObjectStreamClass.lookup(currentType[0]);
                writer.startNode(SerializableConverter.ELEMENT_DEFAULT);
                Iterator iterator = fields.keySet().iterator();
                while (iterator.hasNext()) {
                    String attributeName;
                    String name = (String)iterator.next();
                    if (!SerializableConverter.this.mapper.shouldSerializeMember(currentType[0], name)) continue;
                    ObjectStreamField field = objectStreamClass.getField(name);
                    Object value = fields.get(name);
                    if (field == null) {
                        throw new MissingFieldException(value.getClass().getName(), name);
                    }
                    if (value == null) continue;
                    ExtendedHierarchicalStreamWriterHelper.startNode(writer, SerializableConverter.this.mapper.serializedMember(source.getClass(), name), value.getClass());
                    if (field.getType() != value.getClass() && !field.getType().isPrimitive() && (attributeName = SerializableConverter.this.mapper.aliasForSystemAttribute(SerializableConverter.ATTRIBUTE_CLASS)) != null) {
                        writer.addAttribute(attributeName, SerializableConverter.this.mapper.serializedClass(value.getClass()));
                    }
                    context.convertAnother(value);
                    writer.endNode();
                }
                writer.endNode();
            }

            public void defaultWriteObject() {
                boolean writtenDefaultFields = false;
                ObjectStreamClass objectStreamClass = ObjectStreamClass.lookup(currentType[0]);
                if (objectStreamClass == null) {
                    return;
                }
                ObjectStreamField[] fields = objectStreamClass.getFields();
                for (int i = 0; i < fields.length; ++i) {
                    String attributeName;
                    ObjectStreamField field = fields[i];
                    Object value = SerializableConverter.this.readField(field, currentType[0], source);
                    if (value == null) continue;
                    if (!writtenClassWrapper[0]) {
                        writer.startNode(SerializableConverter.this.mapper.serializedClass(currentType[0]));
                        writtenClassWrapper[0] = true;
                    }
                    if (!writtenDefaultFields) {
                        writer.startNode(SerializableConverter.ELEMENT_DEFAULT);
                        writtenDefaultFields = true;
                    }
                    if (!SerializableConverter.this.mapper.shouldSerializeMember(currentType[0], field.getName())) continue;
                    Class<?> actualType = value.getClass();
                    ExtendedHierarchicalStreamWriterHelper.startNode(writer, SerializableConverter.this.mapper.serializedMember(source.getClass(), field.getName()), actualType);
                    Class defaultType = SerializableConverter.this.mapper.defaultImplementationOf(field.getType());
                    if (!actualType.equals(defaultType) && (attributeName = SerializableConverter.this.mapper.aliasForSystemAttribute(SerializableConverter.ATTRIBUTE_CLASS)) != null) {
                        writer.addAttribute(attributeName, SerializableConverter.this.mapper.serializedClass(actualType));
                    }
                    context.convertAnother(value);
                    writer.endNode();
                }
                if (writtenClassWrapper[0] && !writtenDefaultFields) {
                    writer.startNode(SerializableConverter.ELEMENT_DEFAULT);
                    writer.endNode();
                } else if (writtenDefaultFields) {
                    writer.endNode();
                }
            }

            public void flush() {
                writer.flush();
            }

            public void close() {
                throw new UnsupportedOperationException("Objects are not allowed to call ObjectOutputStream.close() from writeObject()");
            }
        };
        try {
            boolean mustHandleUnserializableParent = false;
            Iterator classHieararchy = this.hierarchyFor(source.getClass()).iterator();
            while (classHieararchy.hasNext()) {
                String classAttributeName;
                currentType[0] = (Class)classHieararchy.next();
                if (!(class$java$io$Serializable == null ? SerializableConverter.class$("java.io.Serializable") : class$java$io$Serializable).isAssignableFrom(currentType[0])) {
                    mustHandleUnserializableParent = true;
                    continue;
                }
                if (mustHandleUnserializableParent) {
                    this.marshalUnserializableParent(writer, context, source);
                    mustHandleUnserializableParent = false;
                }
                if (this.serializationMembers.supportsWriteObject(currentType[0], false)) {
                    writtenClassWrapper[0] = true;
                    writer.startNode(this.mapper.serializedClass(currentType[0]));
                    if (currentType[0] != this.mapper.defaultImplementationOf(currentType[0]) && (classAttributeName = this.mapper.aliasForSystemAttribute(ATTRIBUTE_CLASS)) != null) {
                        writer.addAttribute(classAttributeName, currentType[0].getName());
                    }
                    CustomObjectOutputStream objectOutputStream = CustomObjectOutputStream.getInstance(context, callback);
                    this.serializationMembers.callWriteObject(currentType[0], source, objectOutputStream);
                    objectOutputStream.popCallback();
                    writer.endNode();
                    continue;
                }
                if (this.serializationMembers.supportsReadObject(currentType[0], false)) {
                    writtenClassWrapper[0] = true;
                    writer.startNode(this.mapper.serializedClass(currentType[0]));
                    if (currentType[0] != this.mapper.defaultImplementationOf(currentType[0]) && (classAttributeName = this.mapper.aliasForSystemAttribute(ATTRIBUTE_CLASS)) != null) {
                        writer.addAttribute(classAttributeName, currentType[0].getName());
                    }
                    callback.defaultWriteObject();
                    writer.endNode();
                    continue;
                }
                writtenClassWrapper[0] = false;
                callback.defaultWriteObject();
                if (!writtenClassWrapper[0]) continue;
                writer.endNode();
            }
        }
        catch (IOException e) {
            throw new StreamException("Cannot write defaults", e);
        }
    }

    protected void marshalUnserializableParent(HierarchicalStreamWriter writer, MarshallingContext context, Object replacedSource) {
        writer.startNode(ELEMENT_UNSERIALIZABLE_PARENTS);
        super.doMarshal(replacedSource, writer, context);
        writer.endNode();
    }

    private Object readField(ObjectStreamField field, Class type, Object instance) {
        Field javaField = Fields.find(type, field.getName());
        return Fields.read(javaField, instance);
    }

    protected List hierarchyFor(Class type) {
        ArrayList result = new ArrayList();
        while (type != (class$java$lang$Object == null ? SerializableConverter.class$("java.lang.Object") : class$java$lang$Object) && type != null) {
            result.add(type);
            type = type.getSuperclass();
        }
        Collections.reverse(result);
        return result;
    }

    public Object doUnmarshal(final Object result, final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final Class[] currentType = new Class[1];
        String attributeName = this.mapper.aliasForSystemAttribute(ATTRIBUTE_SERIALIZATION);
        if (attributeName != null && !ATTRIBUTE_VALUE_CUSTOM.equals(reader.getAttribute(attributeName))) {
            throw new ConversionException("Cannot deserialize object with new readObject()/writeObject() methods");
        }
        CustomObjectInputStream.StreamCallback callback = new CustomObjectInputStream.StreamCallback(){

            public Object readFromStream() {
                reader.moveDown();
                Class type = HierarchicalStreams.readClassType(reader, SerializableConverter.this.mapper);
                Object value = context.convertAnother(result, type);
                reader.moveUp();
                return value;
            }

            public Map readFieldsFromStream() {
                HashMap<String, Object> fields = new HashMap<String, Object>();
                reader.moveDown();
                if (reader.getNodeName().equals(SerializableConverter.ELEMENT_FIELDS)) {
                    while (reader.hasMoreChildren()) {
                        reader.moveDown();
                        if (!reader.getNodeName().equals(SerializableConverter.ELEMENT_FIELD)) {
                            throw new ConversionException("Expected <field/> element inside <field/>");
                        }
                        String name = reader.getAttribute(SerializableConverter.ATTRIBUTE_NAME);
                        Class type = SerializableConverter.this.mapper.realClass(reader.getAttribute(SerializableConverter.ATTRIBUTE_CLASS));
                        Object value = context.convertAnother(result, type);
                        fields.put(name, value);
                        reader.moveUp();
                    }
                } else if (reader.getNodeName().equals(SerializableConverter.ELEMENT_DEFAULT)) {
                    ObjectStreamClass objectStreamClass = ObjectStreamClass.lookup(currentType[0]);
                    while (reader.hasMoreChildren()) {
                        reader.moveDown();
                        String name = SerializableConverter.this.mapper.realMember(currentType[0], reader.getNodeName());
                        if (SerializableConverter.this.mapper.shouldSerializeMember(currentType[0], name)) {
                            Class<?> type;
                            String classAttribute = HierarchicalStreams.readClassAttribute(reader, SerializableConverter.this.mapper);
                            if (classAttribute != null) {
                                type = SerializableConverter.this.mapper.realClass(classAttribute);
                            } else {
                                ObjectStreamField field = objectStreamClass.getField(name);
                                if (field == null) {
                                    throw new MissingFieldException(currentType[0].getName(), name);
                                }
                                type = field.getType();
                            }
                            Object value = context.convertAnother(result, type);
                            fields.put(name, value);
                        }
                        reader.moveUp();
                    }
                } else {
                    throw new ConversionException("Expected <fields/> or <default/> element when calling ObjectInputStream.readFields()");
                }
                reader.moveUp();
                return fields;
            }

            public void defaultReadObject() {
                if (SerializableConverter.this.serializationMembers.getSerializablePersistentFields(currentType[0]) != null) {
                    this.readFieldsFromStream();
                    return;
                }
                if (!reader.hasMoreChildren()) {
                    return;
                }
                reader.moveDown();
                if (!reader.getNodeName().equals(SerializableConverter.ELEMENT_DEFAULT)) {
                    throw new ConversionException("Expected <default/> element in readObject() stream");
                }
                while (reader.hasMoreChildren()) {
                    reader.moveDown();
                    String fieldName = SerializableConverter.this.mapper.realMember(currentType[0], reader.getNodeName());
                    if (SerializableConverter.this.mapper.shouldSerializeMember(currentType[0], fieldName)) {
                        String classAttribute = HierarchicalStreams.readClassAttribute(reader, SerializableConverter.this.mapper);
                        Class type = classAttribute != null ? SerializableConverter.this.mapper.realClass(classAttribute) : SerializableConverter.this.mapper.defaultImplementationOf(SerializableConverter.this.reflectionProvider.getFieldType(result, fieldName, currentType[0]));
                        Object value = context.convertAnother(result, type);
                        SerializableConverter.this.reflectionProvider.writeField(result, fieldName, value, currentType[0]);
                    }
                    reader.moveUp();
                }
                reader.moveUp();
            }

            public void registerValidation(final ObjectInputValidation validation, int priority) {
                context.addCompletionCallback(new Runnable(){

                    public void run() {
                        try {
                            validation.validateObject();
                        }
                        catch (InvalidObjectException e) {
                            throw new ObjectAccessException("Cannot validate object", e);
                        }
                    }
                }, priority);
            }

            public void close() {
                throw new UnsupportedOperationException("Objects are not allowed to call ObjectInputStream.close() from readObject()");
            }
        };
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if (nodeName.equals(ELEMENT_UNSERIALIZABLE_PARENTS)) {
                super.doUnmarshal(result, reader, context);
            } else {
                String classAttribute = HierarchicalStreams.readClassAttribute(reader, this.mapper);
                currentType[0] = classAttribute == null ? this.mapper.defaultImplementationOf(this.mapper.realClass(nodeName)) : this.mapper.realClass(classAttribute);
                if (this.serializationMembers.supportsReadObject(currentType[0], false)) {
                    CustomObjectInputStream objectInputStream = CustomObjectInputStream.getInstance((DataHolder)context, callback, this.classLoaderReference);
                    this.serializationMembers.callReadObject(currentType[0], result, objectInputStream);
                    objectInputStream.popCallback();
                } else {
                    try {
                        callback.defaultReadObject();
                    }
                    catch (IOException e) {
                        throw new StreamException("Cannot read defaults", e);
                    }
                }
            }
            reader.moveUp();
        }
        return result;
    }

    protected void doMarshalConditionally(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        if (this.isSerializable(source.getClass())) {
            this.doMarshal(source, writer, context);
        } else {
            super.doMarshal(source, writer, context);
        }
    }

    protected Object doUnmarshalConditionally(Object result, HierarchicalStreamReader reader, UnmarshallingContext context) {
        return this.isSerializable(result.getClass()) ? this.doUnmarshal(result, reader, context) : super.doUnmarshal(result, reader, context);
    }

    private static class UnserializableParentsReflectionProvider
    extends ReflectionProviderWrapper {
        public UnserializableParentsReflectionProvider(ReflectionProvider reflectionProvider) {
            super(reflectionProvider);
        }

        public void visitSerializableFields(Object object, final ReflectionProvider.Visitor visitor) {
            this.wrapped.visitSerializableFields(object, new ReflectionProvider.Visitor(){

                public void visit(String name, Class type, Class definedIn, Object value) {
                    if (!(class$java$io$Serializable == null ? (class$java$io$Serializable = SerializableConverter.class$("java.io.Serializable")) : class$java$io$Serializable).isAssignableFrom(definedIn)) {
                        visitor.visit(name, type, definedIn, value);
                    }
                }
            });
        }
    }
}

