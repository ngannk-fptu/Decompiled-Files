/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.supercsv.exception.SuperCsvReflectionException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ReflectionUtils {
    public static final String SET_PREFIX = "set";
    public static final String GET_PREFIX = "get";
    public static final String IS_PREFIX = "is";
    private static final Map<Class<?>, Class<?>> AUTOBOXING_CONVERTER = new HashMap();

    private ReflectionUtils() {
    }

    public static Method findGetter(Object object, String fieldName) {
        if (object == null) {
            throw new NullPointerException("object should not be null");
        }
        if (fieldName == null) {
            throw new NullPointerException("fieldName should not be null");
        }
        Class<?> clazz = object.getClass();
        String standardGetterName = ReflectionUtils.getMethodNameForField(GET_PREFIX, fieldName);
        Method getter = ReflectionUtils.findGetterWithCompatibleReturnType(standardGetterName, clazz, false);
        if (getter == null) {
            String booleanGetterName = ReflectionUtils.getMethodNameForField(IS_PREFIX, fieldName);
            getter = ReflectionUtils.findGetterWithCompatibleReturnType(booleanGetterName, clazz, true);
        }
        if (getter == null) {
            throw new SuperCsvReflectionException(String.format("unable to find getter for field %s in class %s - check that the corresponding nameMapping element matches the field name in the bean", fieldName, clazz.getName()));
        }
        return getter;
    }

    private static Method findGetterWithCompatibleReturnType(String getterName, Class<?> clazz, boolean enforceBooleanReturnType) {
        for (Method method : clazz.getMethods()) {
            if (!getterName.equals(method.getName()) || method.getParameterTypes().length != 0 || method.getReturnType().equals(Void.TYPE) || enforceBooleanReturnType && !Boolean.TYPE.equals(method.getReturnType()) && !Boolean.class.equals(method.getReturnType())) continue;
            return method;
        }
        return null;
    }

    public static Method findSetter(Object object, String fieldName, Class<?> argumentType) {
        if (object == null) {
            throw new NullPointerException("object should not be null");
        }
        if (fieldName == null) {
            throw new NullPointerException("fieldName should not be null");
        }
        if (argumentType == null) {
            throw new NullPointerException("argumentType should not be null");
        }
        String setterName = ReflectionUtils.getMethodNameForField(SET_PREFIX, fieldName);
        Class<?> clazz = object.getClass();
        Method setter = ReflectionUtils.findSetterWithCompatibleParamType(clazz, setterName, argumentType);
        if (setter == null && AUTOBOXING_CONVERTER.containsKey(argumentType)) {
            setter = ReflectionUtils.findSetterWithCompatibleParamType(clazz, setterName, AUTOBOXING_CONVERTER.get(argumentType));
        }
        if (setter == null) {
            throw new SuperCsvReflectionException(String.format("unable to find method %s(%s) in class %s - check that the corresponding nameMapping element matches the field name in the bean, and the cell processor returns a type compatible with the field", setterName, argumentType.getName(), clazz.getName()));
        }
        return setter;
    }

    private static Method findSetterWithCompatibleParamType(Class<?> clazz, String setterName, Class<?> argumentType) {
        Method compatibleSetter = null;
        for (Method method : clazz.getMethods()) {
            if (!setterName.equals(method.getName()) || method.getParameterTypes().length != 1) continue;
            Class<?> parameterType = method.getParameterTypes()[0];
            if (parameterType.equals(argumentType)) {
                compatibleSetter = method;
                break;
            }
            if (!parameterType.isAssignableFrom(argumentType)) continue;
            compatibleSetter = method;
        }
        return compatibleSetter;
    }

    private static String getMethodNameForField(String prefix, String fieldName) {
        return prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    static {
        AUTOBOXING_CONVERTER.put(Long.TYPE, Long.class);
        AUTOBOXING_CONVERTER.put(Long.class, Long.TYPE);
        AUTOBOXING_CONVERTER.put(Integer.TYPE, Integer.class);
        AUTOBOXING_CONVERTER.put(Integer.class, Integer.TYPE);
        AUTOBOXING_CONVERTER.put(Character.TYPE, Character.class);
        AUTOBOXING_CONVERTER.put(Character.class, Character.TYPE);
        AUTOBOXING_CONVERTER.put(Byte.TYPE, Byte.class);
        AUTOBOXING_CONVERTER.put(Byte.class, Byte.TYPE);
        AUTOBOXING_CONVERTER.put(Short.TYPE, Short.class);
        AUTOBOXING_CONVERTER.put(Short.class, Short.TYPE);
        AUTOBOXING_CONVERTER.put(Boolean.TYPE, Boolean.class);
        AUTOBOXING_CONVERTER.put(Boolean.class, Boolean.TYPE);
        AUTOBOXING_CONVERTER.put(Double.TYPE, Double.class);
        AUTOBOXING_CONVERTER.put(Double.class, Double.TYPE);
        AUTOBOXING_CONVERTER.put(Float.TYPE, Float.class);
        AUTOBOXING_CONVERTER.put(Float.class, Float.TYPE);
    }
}

