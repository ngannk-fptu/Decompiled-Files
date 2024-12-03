/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.ErrorWritingException;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.core.Caching;
import com.thoughtworks.xstream.core.util.FastField;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SerializationMembers
implements Caching {
    private static final Method NO_METHOD = new Object(){

        private void noMethod() {
        }
    }.getClass().getDeclaredMethods()[0];
    private static final Object[] EMPTY_ARGS = new Object[0];
    private static final Class[] EMPTY_CLASSES = new Class[0];
    private static final Map NO_FIELDS = Collections.EMPTY_MAP;
    private static final int PERSISTENT_FIELDS_MODIFIER = 26;
    private static final FastField[] OBJECT_TYPE_FIELDS = new FastField[]{new FastField(Object.class, "readResolve"), new FastField(Object.class, "writeReplace"), new FastField(Object.class, "readObject"), new FastField(Object.class, "writeObject")};
    private Map declaredCache = Collections.synchronizedMap(new HashMap());
    private Map resRepCache = Collections.synchronizedMap(new HashMap());
    private final Map fieldCache = Collections.synchronizedMap(new HashMap());

    public SerializationMembers() {
        int i;
        for (i = 0; i < OBJECT_TYPE_FIELDS.length; ++i) {
            this.declaredCache.put(OBJECT_TYPE_FIELDS[i], NO_METHOD);
        }
        for (i = 0; i < 2; ++i) {
            this.resRepCache.put(OBJECT_TYPE_FIELDS[i], NO_METHOD);
        }
    }

    public Object callReadResolve(Object result) {
        if (result == null) {
            return null;
        }
        Class<?> resultType = result.getClass();
        Method readResolveMethod = this.getRRMethod(resultType, "readResolve");
        if (readResolveMethod != null) {
            ErrorWritingException ex = null;
            try {
                return readResolveMethod.invoke(result, EMPTY_ARGS);
            }
            catch (IllegalAccessException e) {
                ex = new ObjectAccessException("Cannot access method", e);
            }
            catch (InvocationTargetException e) {
                ex = new ConversionException("Failed calling method", e.getTargetException());
            }
            ex.add("method", resultType.getName() + ".readResolve()");
            throw ex;
        }
        return result;
    }

    public Object callWriteReplace(Object object) {
        if (object == null) {
            return null;
        }
        Class<?> objectType = object.getClass();
        Method writeReplaceMethod = this.getRRMethod(objectType, "writeReplace");
        if (writeReplaceMethod != null) {
            ErrorWritingException ex = null;
            try {
                Object replaced = writeReplaceMethod.invoke(object, EMPTY_ARGS);
                if (replaced != null && !object.getClass().equals(replaced.getClass())) {
                    replaced = this.callWriteReplace(replaced);
                }
                return replaced;
            }
            catch (IllegalAccessException e) {
                ex = new ObjectAccessException("Cannot access method", e);
            }
            catch (InvocationTargetException e) {
                ex = new ConversionException("Failed calling method", e.getTargetException());
            }
            catch (ErrorWritingException e) {
                ex = e;
            }
            ex.add("method", objectType.getName() + ".writeReplace()");
            throw ex;
        }
        return object;
    }

    public boolean supportsReadObject(Class type, boolean includeBaseClasses) {
        return this.getMethod(type, "readObject", new Class[]{ObjectInputStream.class}, includeBaseClasses) != null;
    }

    public void callReadObject(Class type, Object object, ObjectInputStream stream) {
        ErrorWritingException ex = null;
        try {
            Method readObjectMethod = this.getMethod(type, "readObject", new Class[]{ObjectInputStream.class}, false);
            readObjectMethod.invoke(object, stream);
        }
        catch (IllegalAccessException e) {
            ex = new ObjectAccessException("Cannot access method", e);
        }
        catch (InvocationTargetException e) {
            ex = new ConversionException("Failed calling method", e.getTargetException());
        }
        if (ex != null) {
            ex.add("method", object.getClass().getName() + ".readObject()");
            throw ex;
        }
    }

    public boolean supportsWriteObject(Class type, boolean includeBaseClasses) {
        return this.getMethod(type, "writeObject", new Class[]{ObjectOutputStream.class}, includeBaseClasses) != null;
    }

    public void callWriteObject(Class type, Object instance, ObjectOutputStream stream) {
        ErrorWritingException ex = null;
        try {
            Method readObjectMethod = this.getMethod(type, "writeObject", new Class[]{ObjectOutputStream.class}, false);
            readObjectMethod.invoke(instance, stream);
        }
        catch (IllegalAccessException e) {
            ex = new ObjectAccessException("Cannot access method", e);
        }
        catch (InvocationTargetException e) {
            Throwable cause = e.getTargetException();
            if (cause instanceof ConversionException) {
                throw (ConversionException)cause;
            }
            ex = new ConversionException("Failed calling method", e.getTargetException());
        }
        if (ex != null) {
            ex.add("method", instance.getClass().getName() + ".writeObject()");
            throw ex;
        }
    }

    private Method getMethod(Class type, String name, Class[] parameterTypes, boolean includeBaseclasses) {
        Method method = this.getMethod(type, name, parameterTypes);
        return method == NO_METHOD || !includeBaseclasses && !method.getDeclaringClass().equals(type) ? null : method;
    }

    private Method getMethod(Class type, String name, Class[] parameterTypes) {
        if (type == null) {
            return null;
        }
        FastField method = new FastField(type, name);
        Method result = (Method)this.declaredCache.get(method);
        if (result == null) {
            try {
                result = type.getDeclaredMethod(name, parameterTypes);
                if (!result.isAccessible()) {
                    result.setAccessible(true);
                }
            }
            catch (NoSuchMethodException e) {
                result = this.getMethod(type.getSuperclass(), name, parameterTypes);
            }
            this.declaredCache.put(method, result);
        }
        return result;
    }

    private Method getRRMethod(Class type, String name) {
        FastField method = new FastField(type, name);
        Method result = (Method)this.resRepCache.get(method);
        if (result == null) {
            result = this.getMethod(type, name, EMPTY_CLASSES, true);
            if (result != null && result.getDeclaringClass() != type) {
                if ((result.getModifiers() & 5) == 0 && ((result.getModifiers() & 2) > 0 || type.getPackage() != result.getDeclaringClass().getPackage())) {
                    result = NO_METHOD;
                }
            } else if (result == null) {
                result = NO_METHOD;
            }
            this.resRepCache.put(method, result);
        }
        return result == NO_METHOD ? null : result;
    }

    public Map getSerializablePersistentFields(Class type) {
        if (type == null) {
            return null;
        }
        HashMap<String, ObjectStreamField> result = (HashMap<String, ObjectStreamField>)this.fieldCache.get(type.getName());
        if (result == null) {
            ErrorWritingException ex = null;
            try {
                Field field = type.getDeclaredField("serialPersistentFields");
                if ((field.getModifiers() & 0x1A) == 26) {
                    field.setAccessible(true);
                    ObjectStreamField[] fields = (ObjectStreamField[])field.get(null);
                    if (fields != null) {
                        result = new HashMap<String, ObjectStreamField>();
                        for (int i = 0; i < fields.length; ++i) {
                            result.put(fields[i].getName(), fields[i]);
                        }
                    }
                }
            }
            catch (NoSuchFieldException field) {
            }
            catch (IllegalAccessException e) {
                ex = new ObjectAccessException("Cannot get field", e);
            }
            catch (ClassCastException e) {
                ex = new ConversionException("Incompatible field type", e);
            }
            if (ex != null) {
                ex.add("field", type.getName() + ".serialPersistentFields");
                throw ex;
            }
            if (result == null) {
                result = NO_FIELDS;
            }
            this.fieldCache.put(type.getName(), result);
        }
        return result == NO_FIELDS ? null : result;
    }

    public void flushCache() {
        this.declaredCache.keySet().retainAll(Arrays.asList(OBJECT_TYPE_FIELDS));
        this.resRepCache.keySet().retainAll(Arrays.asList(OBJECT_TYPE_FIELDS));
    }
}

