/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.impl;

import com.rometools.rome.feed.impl.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class BeanIntrospector {
    private static final Map<Class<?>, PropertyDescriptor[]> introspected = new HashMap();
    private static final String SETTER = "set";
    private static final String GETTER = "get";
    private static final String BOOLEAN_GETTER = "is";

    private BeanIntrospector() {
    }

    private static synchronized PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) {
        PropertyDescriptor[] descriptors = introspected.get(clazz);
        if (descriptors == null) {
            descriptors = BeanIntrospector.getPDs(clazz);
            introspected.put(clazz, descriptors);
        }
        return descriptors;
    }

    public static List<PropertyDescriptor> getPropertyDescriptorsWithGetters(Class<?> clazz) {
        ArrayList<PropertyDescriptor> relevantDescriptors = new ArrayList<PropertyDescriptor>();
        PropertyDescriptor[] propertyDescriptors = BeanIntrospector.getPropertyDescriptors(clazz);
        if (propertyDescriptors != null) {
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                boolean getterWithoutParams;
                boolean getterExists;
                Method getter = propertyDescriptor.getReadMethod();
                boolean bl = getterExists = getter != null;
                if (!getterExists) continue;
                boolean getterFromObject = getter.getDeclaringClass() == Object.class;
                boolean bl2 = getterWithoutParams = getter.getParameterTypes().length == 0;
                if (getterFromObject || !getterWithoutParams) continue;
                relevantDescriptors.add(propertyDescriptor);
            }
        }
        return relevantDescriptors;
    }

    public static List<PropertyDescriptor> getPropertyDescriptorsWithGettersAndSetters(Class<?> clazz) {
        ArrayList<PropertyDescriptor> relevantDescriptors = new ArrayList<PropertyDescriptor>();
        List<PropertyDescriptor> propertyDescriptors = BeanIntrospector.getPropertyDescriptorsWithGetters(clazz);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Method setter = propertyDescriptor.getWriteMethod();
            boolean setterExists = setter != null;
            if (!setterExists) continue;
            relevantDescriptors.add(propertyDescriptor);
        }
        return relevantDescriptors;
    }

    private static PropertyDescriptor[] getPDs(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        Map<String, PropertyDescriptor> getters = BeanIntrospector.getPDs(methods, false);
        Map<String, PropertyDescriptor> setters = BeanIntrospector.getPDs(methods, true);
        List<PropertyDescriptor> propertyDescriptors = BeanIntrospector.merge(getters, setters);
        return propertyDescriptors.toArray(new PropertyDescriptor[propertyDescriptors.size()]);
    }

    private static Map<String, PropertyDescriptor> getPDs(Method[] methods, boolean setters) {
        HashMap<String, PropertyDescriptor> pds = new HashMap<String, PropertyDescriptor>();
        for (Method method : methods) {
            String propertyName = null;
            PropertyDescriptor propertyDescriptor = null;
            int modifiers = method.getModifiers();
            if ((modifiers & 1) != 0) {
                String methodName = method.getName();
                Class<?> returnType = method.getReturnType();
                int nrOfParameters = method.getParameterTypes().length;
                if (setters) {
                    if (methodName.startsWith(SETTER) && returnType == Void.TYPE && nrOfParameters == 1) {
                        propertyName = BeanIntrospector.decapitalize(methodName.substring(3));
                        propertyDescriptor = new PropertyDescriptor(propertyName, null, method);
                    }
                } else if (methodName.startsWith(GETTER) && returnType != Void.TYPE && nrOfParameters == 0) {
                    propertyName = BeanIntrospector.decapitalize(methodName.substring(3));
                    propertyDescriptor = new PropertyDescriptor(propertyName, method, null);
                } else if (methodName.startsWith(BOOLEAN_GETTER) && returnType == Boolean.TYPE && nrOfParameters == 0) {
                    propertyName = BeanIntrospector.decapitalize(methodName.substring(2));
                    propertyDescriptor = new PropertyDescriptor(propertyName, method, null);
                }
            }
            if (propertyName == null) continue;
            pds.put(propertyName, propertyDescriptor);
        }
        return pds;
    }

    private static List<PropertyDescriptor> merge(Map<String, PropertyDescriptor> getters, Map<String, PropertyDescriptor> setters) {
        PropertyDescriptor setter;
        ArrayList<PropertyDescriptor> props = new ArrayList<PropertyDescriptor>();
        HashSet<String> processedProps = new HashSet<String>();
        for (String propertyName : getters.keySet()) {
            PropertyDescriptor getter = getters.get(propertyName);
            setter = setters.get(propertyName);
            if (setter != null) {
                processedProps.add(propertyName);
                PropertyDescriptor prop = new PropertyDescriptor(propertyName, getter.getReadMethod(), setter.getWriteMethod());
                props.add(prop);
                continue;
            }
            props.add(getter);
        }
        HashSet writeOnlyProperties = new HashSet();
        writeOnlyProperties.removeAll(processedProps);
        for (String propertyName : writeOnlyProperties) {
            setter = setters.get(propertyName);
            props.add(setter);
        }
        return props;
    }

    private static String decapitalize(String name) {
        if (name.isEmpty() || name.length() > 1 && Character.isUpperCase(name.charAt(1))) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }
}

