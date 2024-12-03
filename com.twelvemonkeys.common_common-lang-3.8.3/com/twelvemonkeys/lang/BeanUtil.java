/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.lang;

import com.twelvemonkeys.lang.ReflectUtil;
import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.util.convert.ConversionException;
import com.twelvemonkeys.util.convert.Converter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;

public final class BeanUtil {
    private BeanUtil() {
    }

    public static Object getPropertyValue(Object object, String string) {
        int n;
        if (object == null || string == null || string.length() < 1) {
            return null;
        }
        Class<?> clazz = object.getClass();
        Object object2 = object;
        int n2 = n = 0;
        while (n < string.length() && n >= 0) {
            Object object3;
            String string2;
            if ((n2 = string.indexOf(46, n2 + 1)) > 0) {
                string2 = string.substring(n, n2);
                n = n2 + 1;
            } else {
                string2 = string.substring(n);
                n = -1;
            }
            Object[] objectArray = null;
            Class[] classArray = new Class[]{};
            int n3 = string2.indexOf(91);
            if (n3 > 0) {
                if (!string2.endsWith("]")) {
                    return null;
                }
                object3 = string2.substring(n3 + 1, string2.length() - 1);
                string2 = string2.substring(0, n3);
                objectArray = new Object[1];
                classArray = new Class[1];
                if (StringUtil.isNumber((String)object3)) {
                    try {
                        objectArray[0] = Integer.valueOf((String)object3);
                        classArray[0] = Integer.TYPE;
                    }
                    catch (NumberFormatException numberFormatException) {}
                } else {
                    objectArray[0] = ((String)object3).toLowerCase();
                    classArray[0] = String.class;
                }
            }
            String string3 = "get" + StringUtil.capitalize(string2);
            try {
                object3 = clazz.getMethod(string3, classArray);
            }
            catch (NoSuchMethodException noSuchMethodException) {
                System.err.print("No method named \"" + string3 + "()\"");
                if (classArray.length > 0 && classArray[0] != null) {
                    System.err.print(" with the parameter " + classArray[0].getName());
                }
                System.err.println(" in class " + clazz.getName() + "!");
                return null;
            }
            if (object3 == null) {
                return null;
            }
            try {
                object2 = ((Method)object3).invoke(object2, objectArray);
            }
            catch (InvocationTargetException invocationTargetException) {
                System.err.println("property=" + string + " & result=" + object2 + " & param=" + Arrays.toString(objectArray));
                invocationTargetException.getTargetException().printStackTrace();
                invocationTargetException.printStackTrace();
                return null;
            }
            catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
                return null;
            }
            catch (NullPointerException nullPointerException) {
                System.err.println(clazz.getName() + "." + ((Method)object3).getName() + "(" + (classArray.length > 0 && classArray[0] != null ? classArray[0].getName() : "") + ")");
                nullPointerException.printStackTrace();
                return null;
            }
            if (object2 != null) {
                clazz = object2.getClass();
                continue;
            }
            return null;
        }
        return object2;
    }

    public static void setPropertyValue(Object object, String string, Object object2) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class clazz = object2 != null ? object2.getClass() : Object.class;
        Object object3 = object;
        String string2 = string;
        int n = string2.indexOf(46);
        if (n >= 0) {
            object3 = BeanUtil.getPropertyValue(object3, string2.substring(0, n));
            string2 = string2.substring(n + 1);
        }
        Object[] objectArray = new Object[]{object2};
        Method method = BeanUtil.getMethodMayModifyParams(object3, "set" + StringUtil.capitalize(string2), new Class[]{clazz}, objectArray);
        method.invoke(object3, objectArray);
    }

    private static Method getMethodMayModifyParams(Object object, String string, Class[] classArray, Object[] objectArray) throws NoSuchMethodException {
        Method method;
        block13: {
            method = null;
            Class clazz = classArray[0];
            try {
                method = object.getClass().getMethod(string, classArray);
            }
            catch (NoSuchMethodException noSuchMethodException) {
                if (ReflectUtil.isPrimitiveWrapper(clazz)) {
                    classArray[0] = ReflectUtil.unwrapType(clazz);
                }
                try {
                    method = object.getClass().getMethod(string, classArray);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                if (method == null) {
                    while ((clazz = clazz.getSuperclass()) != null) {
                        classArray[0] = clazz;
                        try {
                            method = object.getClass().getMethod(string, classArray);
                            break;
                        }
                        catch (Throwable throwable) {
                        }
                    }
                }
                if (method == null) {
                    Method[] methodArray;
                    for (Method method2 : methodArray = object.getClass().getMethods()) {
                        if (!Modifier.isPublic(method2.getModifiers()) || !method2.getName().equals(string) || method2.getReturnType() != Void.TYPE || method2.getParameterTypes().length != 1) continue;
                        Class<?> clazz2 = method2.getParameterTypes()[0];
                        try {
                            objectArray[0] = BeanUtil.convertValueToType(objectArray[0], clazz2);
                        }
                        catch (Throwable throwable) {
                            continue;
                        }
                        method = method2;
                        break;
                    }
                }
                if (method != null) break block13;
                throw noSuchMethodException;
            }
        }
        return method;
    }

    private static Object convertValueToType(Object object, Class<?> clazz) throws ConversionException {
        if (clazz.isPrimitive()) {
            if (clazz == Boolean.TYPE && object instanceof Boolean) {
                return object;
            }
            if (clazz == Byte.TYPE && object instanceof Byte) {
                return object;
            }
            if (clazz == Character.TYPE && object instanceof Character) {
                return object;
            }
            if (clazz == Double.TYPE && object instanceof Double) {
                return object;
            }
            if (clazz == Float.TYPE && object instanceof Float) {
                return object;
            }
            if (clazz == Integer.TYPE && object instanceof Integer) {
                return object;
            }
            if (clazz == Long.TYPE && object instanceof Long) {
                return object;
            }
            if (clazz == Short.TYPE && object instanceof Short) {
                return object;
            }
        }
        if (object instanceof String) {
            Converter converter = Converter.getInstance();
            return converter.toObject((String)object, clazz);
        }
        if (clazz == String.class) {
            Converter converter = Converter.getInstance();
            return converter.toString(object);
        }
        throw new ConversionException("Cannot convert " + object.getClass().getName() + " to " + clazz.getName());
    }

    public static <T> T createInstance(Class<T> clazz, Object object) throws InvocationTargetException {
        return BeanUtil.createInstance(clazz, new Object[]{object});
    }

    public static <T> T createInstance(Class<T> clazz, Object ... objectArray) throws InvocationTargetException {
        T t;
        try {
            Class[] classArray = null;
            if (objectArray != null && objectArray.length > 0) {
                classArray = new Class[objectArray.length];
                for (int i = 0; i < objectArray.length; ++i) {
                    classArray[i] = objectArray[i].getClass();
                }
            }
            Constructor<T> constructor = clazz.getConstructor(classArray);
            t = constructor.newInstance(objectArray);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            return null;
        }
        catch (IllegalAccessException illegalAccessException) {
            return null;
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
        catch (InstantiationException instantiationException) {
            return null;
        }
        catch (ExceptionInInitializerError exceptionInInitializerError) {
            return null;
        }
        return t;
    }

    public static Object invokeStaticMethod(Class<?> clazz, String string, Object object) throws InvocationTargetException {
        return BeanUtil.invokeStaticMethod(clazz, string, new Object[]{object});
    }

    public static Object invokeStaticMethod(Class<?> clazz, String string, Object ... objectArray) throws InvocationTargetException {
        Object object = null;
        try {
            Class[] classArray = new Class[objectArray.length];
            for (int i = 0; i < objectArray.length; ++i) {
                classArray[i] = objectArray[i].getClass();
            }
            Method method = clazz.getMethod(string, classArray);
            if (Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers())) {
                object = method.invoke(null, objectArray);
            }
        }
        catch (NoSuchMethodException noSuchMethodException) {
            return null;
        }
        catch (IllegalAccessException illegalAccessException) {
            return null;
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
        return object;
    }

    public static void configure(Object object, Map<String, ?> map) throws InvocationTargetException {
        BeanUtil.configure(object, map, false);
    }

    public static void configure(Object object, Map<String, ?> map, boolean bl) throws InvocationTargetException {
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            try {
                String string = StringUtil.valueOf(entry.getKey());
                try {
                    BeanUtil.setPropertyValue(object, string, entry.getValue());
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    if (!bl || string.indexOf(45) <= 0) continue;
                    BeanUtil.setPropertyValue(object, StringUtil.lispToCamel(string, false), entry.getValue());
                }
            }
            catch (NoSuchMethodException noSuchMethodException) {
            }
            catch (IllegalAccessException illegalAccessException) {}
        }
    }
}

