/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.ErrorWritingException;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.ImmutableFieldKeySorter;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.util.Fields;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

public class PureJavaReflectionProvider
implements ReflectionProvider {
    private transient Map objectStreamClassCache;
    private transient Map serializedDataCache;
    protected FieldDictionary fieldDictionary;
    static /* synthetic */ Class class$java$io$ObjectStreamClass;

    public PureJavaReflectionProvider() {
        this(new FieldDictionary(new ImmutableFieldKeySorter()));
    }

    public PureJavaReflectionProvider(FieldDictionary fieldDictionary) {
        this.fieldDictionary = fieldDictionary;
        this.init();
    }

    public Object newInstance(Class type) {
        ErrorWritingException ex = null;
        if (type == Void.TYPE || type == Void.class) {
            ex = new ConversionException("Security alert: Marshalling rejected");
        } else {
            try {
                Constructor<?>[] constructors = type.getDeclaredConstructors();
                for (int i = 0; i < constructors.length; ++i) {
                    Constructor<?> constructor = constructors[i];
                    if (constructor.getParameterTypes().length != 0) continue;
                    if (!constructor.isAccessible()) {
                        constructor.setAccessible(true);
                    }
                    return constructor.newInstance(new Object[0]);
                }
                if (Serializable.class.isAssignableFrom(type)) {
                    return this.instantiateUsingSerialization(type);
                }
                ex = new ObjectAccessException("Cannot construct type as it does not have a no-args constructor");
            }
            catch (InstantiationException e) {
                ex = new ObjectAccessException("Cannot construct type", e);
            }
            catch (IllegalAccessException e) {
                ex = new ObjectAccessException("Cannot construct type", e);
            }
            catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof RuntimeException) {
                    throw (RuntimeException)e.getTargetException();
                }
                if (e.getTargetException() instanceof Error) {
                    throw (Error)e.getTargetException();
                }
                ex = new ObjectAccessException("Constructor for type threw an exception", e.getTargetException());
            }
        }
        ex.add("construction-type", type.getName());
        throw ex;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object instantiateUsingSerialization(final Class type) {
        ObjectAccessException oaex = null;
        try {
            byte[] data;
            if (Reflections.newInstance != null) {
                Map map = this.objectStreamClassCache;
                synchronized (map) {
                    ObjectStreamClass osClass = (ObjectStreamClass)this.objectStreamClassCache.get(type);
                    if (osClass == null) {
                        osClass = ObjectStreamClass.lookup(type);
                        this.objectStreamClassCache.put(type, osClass);
                    }
                    return Reflections.newInstance.invoke((Object)osClass, new Object[0]);
                }
            }
            Map osClass = this.serializedDataCache;
            synchronized (osClass) {
                data = (byte[])this.serializedDataCache.get(type);
                if (data == null) {
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    DataOutputStream stream = new DataOutputStream(bytes);
                    stream.writeShort(-21267);
                    stream.writeShort(5);
                    stream.writeByte(115);
                    stream.writeByte(114);
                    stream.writeUTF(type.getName());
                    stream.writeLong(ObjectStreamClass.lookup(type).getSerialVersionUID());
                    stream.writeByte(2);
                    stream.writeShort(0);
                    stream.writeByte(120);
                    stream.writeByte(112);
                    data = bytes.toByteArray();
                    this.serializedDataCache.put(type, data);
                }
            }
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data)){

                protected Class resolveClass(ObjectStreamClass desc) throws ClassNotFoundException {
                    return Class.forName(desc.getName(), false, type.getClassLoader());
                }
            };
            return in.readObject();
        }
        catch (ObjectAccessException e) {
            oaex = e;
        }
        catch (IOException e) {
            oaex = new ObjectAccessException("Cannot create type by JDK serialization", e);
        }
        catch (ClassNotFoundException e) {
            oaex = new ObjectAccessException("Cannot find class", e);
        }
        catch (IllegalAccessException e) {
            oaex = new ObjectAccessException("Cannot create type by JDK object stream data", e);
        }
        catch (IllegalArgumentException e) {
            oaex = new ObjectAccessException("Cannot create type by JDK object stream data", e);
        }
        catch (InvocationTargetException e) {
            oaex = new ObjectAccessException("Cannot create type by JDK object stream data", e);
        }
        oaex.add("construction-type", type.getName());
        throw oaex;
    }

    public void visitSerializableFields(Object object, ReflectionProvider.Visitor visitor) {
        Iterator iterator = this.fieldDictionary.fieldsFor(object.getClass());
        while (iterator.hasNext()) {
            Field field = (Field)iterator.next();
            if (!this.fieldModifiersSupported(field)) continue;
            this.validateFieldAccess(field);
            Object value = Fields.read(field, object);
            visitor.visit(field.getName(), field.getType(), field.getDeclaringClass(), value);
        }
    }

    public void writeField(Object object, String fieldName, Object value, Class definedIn) {
        Field field = this.fieldDictionary.field(object.getClass(), fieldName, definedIn);
        this.validateFieldAccess(field);
        Fields.write(field, object, value);
    }

    public Class getFieldType(Object object, String fieldName, Class definedIn) {
        return this.fieldDictionary.field(object.getClass(), fieldName, definedIn).getType();
    }

    public boolean fieldDefinedInClass(String fieldName, Class type) {
        Field field = this.fieldDictionary.fieldOrNull(type, fieldName, null);
        return field != null && this.fieldModifiersSupported(field);
    }

    protected boolean fieldModifiersSupported(Field field) {
        int modifiers = field.getModifiers();
        return !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers);
    }

    protected void validateFieldAccess(Field field) {
        if (Modifier.isFinal(field.getModifiers())) {
            if (JVM.isVersion(5)) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
            } else {
                throw new ObjectAccessException("Invalid final field " + field.getDeclaringClass().getName() + "." + field.getName());
            }
        }
    }

    public Field getField(Class definedIn, String fieldName) {
        return this.fieldDictionary.field(definedIn, fieldName, null);
    }

    public Field getFieldOrNull(Class definedIn, String fieldName) {
        return this.fieldDictionary.fieldOrNull(definedIn, fieldName, null);
    }

    public void setFieldDictionary(FieldDictionary dictionary) {
        this.fieldDictionary = dictionary;
    }

    private Object readResolve() {
        this.init();
        return this;
    }

    protected void init() {
        this.objectStreamClassCache = new WeakHashMap();
        this.serializedDataCache = new WeakHashMap();
    }

    private static class Reflections {
        private static final Method newInstance;

        private Reflections() {
        }

        static {
            Method method = null;
            try {
                method = (class$java$io$ObjectStreamClass == null ? (class$java$io$ObjectStreamClass = PureJavaReflectionProvider.class$("java.io.ObjectStreamClass")) : class$java$io$ObjectStreamClass).getDeclaredMethod("newInstance", new Class[0]);
                method.setAccessible(true);
            }
            catch (NoSuchMethodException noSuchMethodException) {
            }
            catch (SecurityException securityException) {
                // empty catch block
            }
            newInstance = method;
        }
    }
}

