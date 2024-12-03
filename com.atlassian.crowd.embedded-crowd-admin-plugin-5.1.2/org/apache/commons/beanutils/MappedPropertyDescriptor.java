/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.apache.commons.beanutils.MethodUtils;

public class MappedPropertyDescriptor
extends PropertyDescriptor {
    private Reference<Class<?>> mappedPropertyTypeRef;
    private MappedMethodReference mappedReadMethodRef;
    private MappedMethodReference mappedWriteMethodRef;
    private static final Class<?>[] STRING_CLASS_PARAMETER = new Class[]{String.class};

    public MappedPropertyDescriptor(String propertyName, Class<?> beanClass) throws IntrospectionException {
        super(propertyName, null, null);
        if (propertyName == null || propertyName.length() == 0) {
            throw new IntrospectionException("bad property name: " + propertyName + " on class: " + beanClass.getClass().getName());
        }
        this.setName(propertyName);
        String base = MappedPropertyDescriptor.capitalizePropertyName(propertyName);
        Method mappedReadMethod = null;
        Method mappedWriteMethod = null;
        try {
            try {
                mappedReadMethod = MappedPropertyDescriptor.getMethod(beanClass, "get" + base, STRING_CLASS_PARAMETER);
            }
            catch (IntrospectionException e) {
                mappedReadMethod = MappedPropertyDescriptor.getMethod(beanClass, "is" + base, STRING_CLASS_PARAMETER);
            }
            Class[] params = new Class[]{String.class, mappedReadMethod.getReturnType()};
            mappedWriteMethod = MappedPropertyDescriptor.getMethod(beanClass, "set" + base, params);
        }
        catch (IntrospectionException introspectionException) {
            // empty catch block
        }
        if (mappedReadMethod == null) {
            mappedWriteMethod = MappedPropertyDescriptor.getMethod(beanClass, "set" + base, 2);
        }
        if (mappedReadMethod == null && mappedWriteMethod == null) {
            throw new IntrospectionException("Property '" + propertyName + "' not found on " + beanClass.getName());
        }
        this.mappedReadMethodRef = new MappedMethodReference(mappedReadMethod);
        this.mappedWriteMethodRef = new MappedMethodReference(mappedWriteMethod);
        this.findMappedPropertyType();
    }

    public MappedPropertyDescriptor(String propertyName, Class<?> beanClass, String mappedGetterName, String mappedSetterName) throws IntrospectionException {
        super(propertyName, null, null);
        if (propertyName == null || propertyName.length() == 0) {
            throw new IntrospectionException("bad property name: " + propertyName);
        }
        this.setName(propertyName);
        Method mappedReadMethod = null;
        Method mappedWriteMethod = null;
        mappedReadMethod = MappedPropertyDescriptor.getMethod(beanClass, mappedGetterName, STRING_CLASS_PARAMETER);
        if (mappedReadMethod != null) {
            Class[] params = new Class[]{String.class, mappedReadMethod.getReturnType()};
            mappedWriteMethod = MappedPropertyDescriptor.getMethod(beanClass, mappedSetterName, params);
        } else {
            mappedWriteMethod = MappedPropertyDescriptor.getMethod(beanClass, mappedSetterName, 2);
        }
        this.mappedReadMethodRef = new MappedMethodReference(mappedReadMethod);
        this.mappedWriteMethodRef = new MappedMethodReference(mappedWriteMethod);
        this.findMappedPropertyType();
    }

    public MappedPropertyDescriptor(String propertyName, Method mappedGetter, Method mappedSetter) throws IntrospectionException {
        super(propertyName, mappedGetter, mappedSetter);
        if (propertyName == null || propertyName.length() == 0) {
            throw new IntrospectionException("bad property name: " + propertyName);
        }
        this.setName(propertyName);
        this.mappedReadMethodRef = new MappedMethodReference(mappedGetter);
        this.mappedWriteMethodRef = new MappedMethodReference(mappedSetter);
        this.findMappedPropertyType();
    }

    public Class<?> getMappedPropertyType() {
        return this.mappedPropertyTypeRef.get();
    }

    public Method getMappedReadMethod() {
        return this.mappedReadMethodRef.get();
    }

    public void setMappedReadMethod(Method mappedGetter) throws IntrospectionException {
        this.mappedReadMethodRef = new MappedMethodReference(mappedGetter);
        this.findMappedPropertyType();
    }

    public Method getMappedWriteMethod() {
        return this.mappedWriteMethodRef.get();
    }

    public void setMappedWriteMethod(Method mappedSetter) throws IntrospectionException {
        this.mappedWriteMethodRef = new MappedMethodReference(mappedSetter);
        this.findMappedPropertyType();
    }

    private void findMappedPropertyType() throws IntrospectionException {
        Method mappedReadMethod = this.getMappedReadMethod();
        Method mappedWriteMethod = this.getMappedWriteMethod();
        Class<?> mappedPropertyType = null;
        if (mappedReadMethod != null) {
            if (mappedReadMethod.getParameterTypes().length != 1) {
                throw new IntrospectionException("bad mapped read method arg count");
            }
            mappedPropertyType = mappedReadMethod.getReturnType();
            if (mappedPropertyType == Void.TYPE) {
                throw new IntrospectionException("mapped read method " + mappedReadMethod.getName() + " returns void");
            }
        }
        if (mappedWriteMethod != null) {
            Class<?>[] params = mappedWriteMethod.getParameterTypes();
            if (params.length != 2) {
                throw new IntrospectionException("bad mapped write method arg count");
            }
            if (mappedPropertyType != null && mappedPropertyType != params[1]) {
                throw new IntrospectionException("type mismatch between mapped read and write methods");
            }
            mappedPropertyType = params[1];
        }
        this.mappedPropertyTypeRef = new SoftReference<Object>(mappedPropertyType);
    }

    private static String capitalizePropertyName(String s) {
        if (s.length() == 0) {
            return s;
        }
        char[] chars = s.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    private static Method internalGetMethod(Class<?> initial, String methodName, int parameterCount) {
        Class<?>[] interfaces;
        Method method;
        for (Class<?> clazz = initial; clazz != null; clazz = clazz.getSuperclass()) {
            Method[] methods;
            Method[] methodArray = methods = clazz.getDeclaredMethods();
            int n = methodArray.length;
            for (int i = 0; i < n; ++i) {
                int mods;
                method = methodArray[i];
                if (method == null || !Modifier.isPublic(mods = method.getModifiers()) || Modifier.isStatic(mods) || !method.getName().equals(methodName) || method.getParameterTypes().length != parameterCount) continue;
                return method;
            }
        }
        for (Class<?> interface1 : interfaces = initial.getInterfaces()) {
            method = MappedPropertyDescriptor.internalGetMethod(interface1, methodName, parameterCount);
            if (method == null) continue;
            return method;
        }
        return null;
    }

    private static Method getMethod(Class<?> clazz, String methodName, int parameterCount) throws IntrospectionException {
        if (methodName == null) {
            return null;
        }
        Method method = MappedPropertyDescriptor.internalGetMethod(clazz, methodName, parameterCount);
        if (method != null) {
            return method;
        }
        throw new IntrospectionException("No method \"" + methodName + "\" with " + parameterCount + " parameter(s)");
    }

    private static Method getMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) throws IntrospectionException {
        if (methodName == null) {
            return null;
        }
        Method method = MethodUtils.getMatchingAccessibleMethod(clazz, methodName, parameterTypes);
        if (method != null) {
            return method;
        }
        int parameterCount = parameterTypes == null ? 0 : parameterTypes.length;
        throw new IntrospectionException("No method \"" + methodName + "\" with " + parameterCount + " parameter(s) of matching types.");
    }

    private static class MappedMethodReference {
        private String className;
        private String methodName;
        private Reference<Method> methodRef;
        private Reference<Class<?>> classRef;
        private Reference<Class<?>> writeParamTypeRef0;
        private Reference<Class<?>> writeParamTypeRef1;
        private String[] writeParamClassNames;

        MappedMethodReference(Method m) {
            if (m != null) {
                this.className = m.getDeclaringClass().getName();
                this.methodName = m.getName();
                this.methodRef = new SoftReference<Method>(m);
                this.classRef = new WeakReference(m.getDeclaringClass());
                Class<?>[] types = m.getParameterTypes();
                if (types.length == 2) {
                    this.writeParamTypeRef0 = new WeakReference(types[0]);
                    this.writeParamTypeRef1 = new WeakReference(types[1]);
                    this.writeParamClassNames = new String[2];
                    this.writeParamClassNames[0] = types[0].getName();
                    this.writeParamClassNames[1] = types[1].getName();
                }
            }
        }

        private Method get() {
            if (this.methodRef == null) {
                return null;
            }
            Method m = this.methodRef.get();
            if (m == null) {
                Class<?> clazz = this.classRef.get();
                if (clazz == null && (clazz = this.reLoadClass()) != null) {
                    this.classRef = new WeakReference(clazz);
                }
                if (clazz == null) {
                    throw new RuntimeException("Method " + this.methodName + " for " + this.className + " could not be reconstructed - class reference has gone");
                }
                Class[] paramTypes = null;
                if (this.writeParamClassNames != null) {
                    paramTypes = new Class[2];
                    paramTypes[0] = this.writeParamTypeRef0.get();
                    if (paramTypes[0] == null) {
                        paramTypes[0] = this.reLoadClass(this.writeParamClassNames[0]);
                        if (paramTypes[0] != null) {
                            this.writeParamTypeRef0 = new WeakReference<Class>(paramTypes[0]);
                        }
                    }
                    paramTypes[1] = this.writeParamTypeRef1.get();
                    if (paramTypes[1] == null) {
                        paramTypes[1] = this.reLoadClass(this.writeParamClassNames[1]);
                        if (paramTypes[1] != null) {
                            this.writeParamTypeRef1 = new WeakReference<Class>(paramTypes[1]);
                        }
                    }
                } else {
                    paramTypes = STRING_CLASS_PARAMETER;
                }
                try {
                    m = clazz.getMethod(this.methodName, paramTypes);
                }
                catch (NoSuchMethodException e) {
                    throw new RuntimeException("Method " + this.methodName + " for " + this.className + " could not be reconstructed - method not found");
                }
                this.methodRef = new SoftReference<Method>(m);
            }
            return m;
        }

        private Class<?> reLoadClass() {
            return this.reLoadClass(this.className);
        }

        private Class<?> reLoadClass(String name) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader != null) {
                try {
                    return classLoader.loadClass(name);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    // empty catch block
                }
            }
            classLoader = MappedPropertyDescriptor.class.getClassLoader();
            try {
                return classLoader.loadClass(name);
            }
            catch (ClassNotFoundException e) {
                return null;
            }
        }
    }
}

