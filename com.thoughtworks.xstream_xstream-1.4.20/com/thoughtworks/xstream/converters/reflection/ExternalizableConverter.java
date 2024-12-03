/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.ReferencingMarshallingContext;
import com.thoughtworks.xstream.core.util.CustomObjectInputStream;
import com.thoughtworks.xstream.core.util.CustomObjectOutputStream;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.core.util.SerializationMembers;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.mapper.Mapper;
import java.io.Externalizable;
import java.io.IOException;
import java.io.NotActiveException;
import java.io.ObjectInputValidation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ExternalizableConverter
implements Converter {
    private Mapper mapper;
    private final ClassLoaderReference classLoaderReference;
    private transient SerializationMembers serializationMembers;

    public ExternalizableConverter(Mapper mapper, ClassLoaderReference classLoaderReference) {
        this.mapper = mapper;
        this.classLoaderReference = classLoaderReference;
        this.serializationMembers = new SerializationMembers();
    }

    public ExternalizableConverter(Mapper mapper, ClassLoader classLoader) {
        this(mapper, new ClassLoaderReference(classLoader));
    }

    public ExternalizableConverter(Mapper mapper) {
        this(mapper, ExternalizableConverter.class.getClassLoader());
    }

    public boolean canConvert(Class type) {
        return type != null && JVM.canCreateDerivedObjectOutputStream() && Externalizable.class.isAssignableFrom(type);
    }

    public void marshal(Object original, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        Object source = this.serializationMembers.callWriteReplace(original);
        if (source != original && context instanceof ReferencingMarshallingContext) {
            ((ReferencingMarshallingContext)context).replace(original, source);
        }
        if (source.getClass() != original.getClass()) {
            String attributeName = this.mapper.aliasForSystemAttribute("resolves-to");
            if (attributeName != null) {
                writer.addAttribute(attributeName, this.mapper.serializedClass(source.getClass()));
            }
            context.convertAnother(source);
        } else {
            try {
                Externalizable externalizable = (Externalizable)source;
                CustomObjectOutputStream.StreamCallback callback = new CustomObjectOutputStream.StreamCallback(){

                    public void writeToStream(Object object) {
                        if (object == null) {
                            writer.startNode("null");
                            writer.endNode();
                        } else {
                            ExtendedHierarchicalStreamWriterHelper.startNode(writer, ExternalizableConverter.this.mapper.serializedClass(object.getClass()), object.getClass());
                            context.convertAnother(object);
                            writer.endNode();
                        }
                    }

                    public void writeFieldsToStream(Map fields) {
                        throw new UnsupportedOperationException();
                    }

                    public void defaultWriteObject() {
                        throw new UnsupportedOperationException();
                    }

                    public void flush() {
                        writer.flush();
                    }

                    public void close() {
                        throw new UnsupportedOperationException("Objects are not allowed to call ObjectOutput.close() from writeExternal()");
                    }
                };
                CustomObjectOutputStream objectOutput = CustomObjectOutputStream.getInstance(context, callback);
                externalizable.writeExternal(objectOutput);
                objectOutput.popCallback();
            }
            catch (IOException e) {
                throw new StreamException("Cannot serialize " + source.getClass().getName() + " using Externalization", e);
            }
        }
    }

    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        Class type = context.getRequiredType();
        try {
            Constructor defaultConstructor = type.getDeclaredConstructor(null);
            if (!defaultConstructor.isAccessible()) {
                defaultConstructor.setAccessible(true);
            }
            final Externalizable externalizable = (Externalizable)defaultConstructor.newInstance(null);
            CustomObjectInputStream.StreamCallback callback = new CustomObjectInputStream.StreamCallback(){

                public Object readFromStream() {
                    reader.moveDown();
                    Class type = HierarchicalStreams.readClassType(reader, ExternalizableConverter.this.mapper);
                    Object streamItem = context.convertAnother(externalizable, type);
                    reader.moveUp();
                    return streamItem;
                }

                public Map readFieldsFromStream() {
                    throw new UnsupportedOperationException();
                }

                public void defaultReadObject() {
                    throw new UnsupportedOperationException();
                }

                public void registerValidation(ObjectInputValidation validation, int priority) throws NotActiveException {
                    throw new NotActiveException("stream inactive");
                }

                public void close() {
                    throw new UnsupportedOperationException("Objects are not allowed to call ObjectInput.close() from readExternal()");
                }
            };
            CustomObjectInputStream objectInput = CustomObjectInputStream.getInstance((DataHolder)context, callback, this.classLoaderReference);
            externalizable.readExternal(objectInput);
            objectInput.popCallback();
            return this.serializationMembers.callReadResolve(externalizable);
        }
        catch (NoSuchMethodException e) {
            throw new ConversionException("Missing default constructor of type", e);
        }
        catch (InvocationTargetException e) {
            throw new ConversionException("Cannot construct type", e);
        }
        catch (InstantiationException e) {
            throw new ConversionException("Cannot construct type", e);
        }
        catch (IllegalAccessException e) {
            throw new ObjectAccessException("Cannot construct type", e);
        }
        catch (IOException e) {
            throw new StreamException("Cannot externalize " + type.getClass(), e);
        }
        catch (ClassNotFoundException e) {
            throw new ConversionException("Cannot construct type", e);
        }
    }

    private Object readResolve() {
        this.serializationMembers = new SerializationMembers();
        return this;
    }
}

