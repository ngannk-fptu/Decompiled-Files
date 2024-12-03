/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeMap;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

abstract class PropertyDescriptorUtils {
    public static final PropertyDescriptor[] EMPTY_PROPERTY_DESCRIPTOR_ARRAY = new PropertyDescriptor[0];

    PropertyDescriptorUtils() {
    }

    public static Collection<? extends PropertyDescriptor> determineBasicProperties(Class<?> beanClass) throws IntrospectionException {
        TreeMap<String, BasicPropertyDescriptor> pdMap = new TreeMap<String, BasicPropertyDescriptor>();
        for (Method method : beanClass.getMethods()) {
            int nameIndex;
            boolean setter;
            String methodName = method.getName();
            if (methodName.startsWith("set") && method.getParameterCount() == 1) {
                setter = true;
                nameIndex = 3;
            } else if (methodName.startsWith("get") && method.getParameterCount() == 0 && method.getReturnType() != Void.TYPE) {
                setter = false;
                nameIndex = 3;
            } else {
                if (!methodName.startsWith("is") || method.getParameterCount() != 0 || method.getReturnType() != Boolean.TYPE) continue;
                setter = false;
                nameIndex = 2;
            }
            String propertyName = Introspector.decapitalize(methodName.substring(nameIndex));
            if (propertyName.isEmpty()) continue;
            BasicPropertyDescriptor pd = (BasicPropertyDescriptor)pdMap.get(propertyName);
            if (pd != null) {
                if (setter) {
                    if (pd.getWriteMethod() == null || pd.getWriteMethod().getParameterTypes()[0].isAssignableFrom(method.getParameterTypes()[0])) {
                        pd.setWriteMethod(method);
                        continue;
                    }
                    pd.addWriteMethod(method);
                    continue;
                }
                if (pd.getReadMethod() != null && (pd.getReadMethod().getReturnType() != method.getReturnType() || !method.getName().startsWith("is"))) continue;
                pd.setReadMethod(method);
                continue;
            }
            pd = new BasicPropertyDescriptor(propertyName, !setter ? method : null, setter ? method : null);
            pdMap.put(propertyName, pd);
        }
        return pdMap.values();
    }

    public static void copyNonMethodProperties(PropertyDescriptor source, PropertyDescriptor target) {
        target.setExpert(source.isExpert());
        target.setHidden(source.isHidden());
        target.setPreferred(source.isPreferred());
        target.setName(source.getName());
        target.setShortDescription(source.getShortDescription());
        target.setDisplayName(source.getDisplayName());
        Enumeration<String> keys = source.attributeNames();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            target.setValue(key, source.getValue(key));
        }
        target.setPropertyEditorClass(source.getPropertyEditorClass());
        target.setBound(source.isBound());
        target.setConstrained(source.isConstrained());
    }

    @Nullable
    public static Class<?> findPropertyType(@Nullable Method readMethod, @Nullable Method writeMethod) throws IntrospectionException {
        Class<?> propertyType = null;
        if (readMethod != null) {
            if (readMethod.getParameterCount() != 0) {
                throw new IntrospectionException("Bad read method arg count: " + readMethod);
            }
            propertyType = readMethod.getReturnType();
            if (propertyType == Void.TYPE) {
                throw new IntrospectionException("Read method returns void: " + readMethod);
            }
        }
        if (writeMethod != null) {
            Class<?>[] params = writeMethod.getParameterTypes();
            if (params.length != 1) {
                throw new IntrospectionException("Bad write method arg count: " + writeMethod);
            }
            if (propertyType != null) {
                if (propertyType.isAssignableFrom(params[0])) {
                    propertyType = params[0];
                } else if (!params[0].isAssignableFrom(propertyType)) {
                    throw new IntrospectionException("Type mismatch between read and write methods: " + readMethod + " - " + writeMethod);
                }
            } else {
                propertyType = params[0];
            }
        }
        return propertyType;
    }

    @Nullable
    public static Class<?> findIndexedPropertyType(String name, @Nullable Class<?> propertyType, @Nullable Method indexedReadMethod, @Nullable Method indexedWriteMethod) throws IntrospectionException {
        Class<?>[] params;
        Class<?> indexedPropertyType = null;
        if (indexedReadMethod != null) {
            params = indexedReadMethod.getParameterTypes();
            if (params.length != 1) {
                throw new IntrospectionException("Bad indexed read method arg count: " + indexedReadMethod);
            }
            if (params[0] != Integer.TYPE) {
                throw new IntrospectionException("Non int index to indexed read method: " + indexedReadMethod);
            }
            indexedPropertyType = indexedReadMethod.getReturnType();
            if (indexedPropertyType == Void.TYPE) {
                throw new IntrospectionException("Indexed read method returns void: " + indexedReadMethod);
            }
        }
        if (indexedWriteMethod != null) {
            params = indexedWriteMethod.getParameterTypes();
            if (params.length != 2) {
                throw new IntrospectionException("Bad indexed write method arg count: " + indexedWriteMethod);
            }
            if (params[0] != Integer.TYPE) {
                throw new IntrospectionException("Non int index to indexed write method: " + indexedWriteMethod);
            }
            if (indexedPropertyType != null) {
                if (indexedPropertyType.isAssignableFrom(params[1])) {
                    indexedPropertyType = params[1];
                } else if (!params[1].isAssignableFrom(indexedPropertyType)) {
                    throw new IntrospectionException("Type mismatch between indexed read and write methods: " + indexedReadMethod + " - " + indexedWriteMethod);
                }
            } else {
                indexedPropertyType = params[1];
            }
        }
        if (!(propertyType == null || propertyType.isArray() && propertyType.getComponentType() == indexedPropertyType)) {
            throw new IntrospectionException("Type mismatch between indexed and non-indexed methods: " + indexedReadMethod + " - " + indexedWriteMethod);
        }
        return indexedPropertyType;
    }

    public static boolean equals(PropertyDescriptor pd, PropertyDescriptor otherPd) {
        return ObjectUtils.nullSafeEquals(pd.getReadMethod(), otherPd.getReadMethod()) && ObjectUtils.nullSafeEquals(pd.getWriteMethod(), otherPd.getWriteMethod()) && ObjectUtils.nullSafeEquals(pd.getPropertyType(), otherPd.getPropertyType()) && ObjectUtils.nullSafeEquals(pd.getPropertyEditorClass(), otherPd.getPropertyEditorClass()) && pd.isBound() == otherPd.isBound() && pd.isConstrained() == otherPd.isConstrained();
    }

    private static class BasicPropertyDescriptor
    extends PropertyDescriptor {
        @Nullable
        private Method readMethod;
        @Nullable
        private Method writeMethod;
        private final List<Method> alternativeWriteMethods = new ArrayList<Method>();

        public BasicPropertyDescriptor(String propertyName, @Nullable Method readMethod, @Nullable Method writeMethod) throws IntrospectionException {
            super(propertyName, readMethod, writeMethod);
        }

        @Override
        public void setReadMethod(@Nullable Method readMethod) {
            this.readMethod = readMethod;
        }

        @Override
        @Nullable
        public Method getReadMethod() {
            return this.readMethod;
        }

        @Override
        public void setWriteMethod(@Nullable Method writeMethod) {
            this.writeMethod = writeMethod;
        }

        public void addWriteMethod(Method writeMethod) {
            if (this.writeMethod != null) {
                this.alternativeWriteMethods.add(this.writeMethod);
                this.writeMethod = null;
            }
            this.alternativeWriteMethods.add(writeMethod);
        }

        @Override
        @Nullable
        public Method getWriteMethod() {
            if (this.writeMethod == null && !this.alternativeWriteMethods.isEmpty()) {
                if (this.readMethod == null) {
                    return this.alternativeWriteMethods.get(0);
                }
                for (Method method : this.alternativeWriteMethods) {
                    if (!this.readMethod.getReturnType().isAssignableFrom(method.getParameterTypes()[0])) continue;
                    this.writeMethod = method;
                    break;
                }
            }
            return this.writeMethod;
        }
    }
}

