/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.json;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.concurrent.Callable;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;

public final class JSONUtils {
    private static Method isAccessibleMethod;
    private static Method setAccessibleMethod;
    private static Method trySetAccessibleMethod;
    static final String ID_KEY = "__ID";
    static final String ID_PREFIX = "[#";
    static final String ID_SUFFIX = "]";
    private static final String[] JSON_CHAR_REPLACEMENTS;
    private static final String[] INDENT_LEVELS;

    private static boolean isAccessible(AccessibleObject obj) {
        if (isAccessibleMethod != null) {
            try {
                if (((Boolean)isAccessibleMethod.invoke((Object)obj, new Object[0])).booleanValue()) {
                    return true;
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return false;
    }

    private static boolean tryMakeAccessible(AccessibleObject obj) {
        if (setAccessibleMethod != null) {
            try {
                setAccessibleMethod.invoke((Object)obj, true);
                return true;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (trySetAccessibleMethod != null) {
            try {
                if (((Boolean)trySetAccessibleMethod.invoke((Object)obj, new Object[0])).booleanValue()) {
                    return true;
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return false;
    }

    public static boolean makeAccessible(final AccessibleObject obj, ReflectionUtils reflectionUtils) {
        if (JSONUtils.isAccessible(obj) || JSONUtils.tryMakeAccessible(obj)) {
            return true;
        }
        try {
            return reflectionUtils.doPrivileged(new Callable<Boolean>(){

                @Override
                public Boolean call() throws Exception {
                    return JSONUtils.tryMakeAccessible(obj);
                }
            });
        }
        catch (Throwable t) {
            return false;
        }
    }

    private JSONUtils() {
    }

    static void escapeJSONString(String unsafeStr, StringBuilder buf) {
        char c;
        int i;
        if (unsafeStr == null) {
            return;
        }
        boolean needsEscaping = false;
        int n = unsafeStr.length();
        for (i = 0; i < n; ++i) {
            c = unsafeStr.charAt(i);
            if (c <= '\u00ff' && JSON_CHAR_REPLACEMENTS[c] == null) continue;
            needsEscaping = true;
            break;
        }
        if (!needsEscaping) {
            buf.append(unsafeStr);
            return;
        }
        n = unsafeStr.length();
        for (i = 0; i < n; ++i) {
            c = unsafeStr.charAt(i);
            if (c > '\u00ff') {
                buf.append("\\u");
                int nibble3 = (c & 0xF000) >> 12;
                buf.append(nibble3 <= 9 ? (char)(48 + nibble3) : (char)(65 + nibble3 - 10));
                int nibble2 = (c & 0xF00) >> 8;
                buf.append(nibble2 <= 9 ? (char)(48 + nibble2) : (char)(65 + nibble2 - 10));
                int nibble1 = (c & 0xF0) >> 4;
                buf.append(nibble1 <= 9 ? (char)(48 + nibble1) : (char)(65 + nibble1 - 10));
                int nibble0 = c & 0xF;
                buf.append(nibble0 <= 9 ? (char)(48 + nibble0) : (char)(65 + nibble0 - 10));
                continue;
            }
            String replacement = JSON_CHAR_REPLACEMENTS[c];
            if (replacement == null) {
                buf.append(c);
                continue;
            }
            buf.append(replacement);
        }
    }

    public static String escapeJSONString(String unsafeStr) {
        StringBuilder buf = new StringBuilder(unsafeStr.length() * 2);
        JSONUtils.escapeJSONString(unsafeStr, buf);
        return buf.toString();
    }

    static void indent(int depth, int indentWidth, StringBuilder buf) {
        int n;
        int maxIndent = INDENT_LEVELS.length - 1;
        for (int d = depth * indentWidth; d > 0; d -= n) {
            n = Math.min(d, maxIndent);
            buf.append(INDENT_LEVELS[n]);
        }
    }

    static Object getFieldValue(Object containingObj, Field field) throws IllegalArgumentException, IllegalAccessException {
        Class<?> fieldType = field.getType();
        if (fieldType == Integer.TYPE) {
            return field.getInt(containingObj);
        }
        if (fieldType == Long.TYPE) {
            return field.getLong(containingObj);
        }
        if (fieldType == Short.TYPE) {
            return field.getShort(containingObj);
        }
        if (fieldType == Double.TYPE) {
            return field.getDouble(containingObj);
        }
        if (fieldType == Float.TYPE) {
            return Float.valueOf(field.getFloat(containingObj));
        }
        if (fieldType == Boolean.TYPE) {
            return field.getBoolean(containingObj);
        }
        if (fieldType == Byte.TYPE) {
            return field.getByte(containingObj);
        }
        if (fieldType == Character.TYPE) {
            return Character.valueOf(field.getChar(containingObj));
        }
        return field.get(containingObj);
    }

    static boolean isBasicValueType(Class<?> cls) {
        return cls == String.class || cls == Integer.class || cls == Integer.TYPE || cls == Long.class || cls == Long.TYPE || cls == Short.class || cls == Short.TYPE || cls == Float.class || cls == Float.TYPE || cls == Double.class || cls == Double.TYPE || cls == Byte.class || cls == Byte.TYPE || cls == Character.class || cls == Character.TYPE || cls == Boolean.class || cls == Boolean.TYPE || cls.isEnum() || cls == Class.class;
    }

    static boolean isBasicValueType(Type type) {
        if (type instanceof Class) {
            return JSONUtils.isBasicValueType((Class)type);
        }
        if (type instanceof ParameterizedType) {
            return JSONUtils.isBasicValueType(((ParameterizedType)type).getRawType());
        }
        return false;
    }

    static boolean isBasicValueType(Object obj) {
        return obj == null || obj instanceof String || obj instanceof Integer || obj instanceof Boolean || obj instanceof Long || obj instanceof Float || obj instanceof Double || obj instanceof Short || obj instanceof Byte || obj instanceof Character || obj.getClass().isEnum() || obj instanceof Class;
    }

    static boolean isCollectionOrArray(Object obj) {
        Class<?> cls = obj.getClass();
        return Collection.class.isAssignableFrom(cls) || cls.isArray();
    }

    static Class<?> getRawType(Type type) {
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof ParameterizedType) {
            return (Class)((ParameterizedType)type).getRawType();
        }
        throw new IllegalArgumentException("Illegal type: " + type);
    }

    static boolean fieldIsSerializable(Field field, boolean onlySerializePublicFields, ReflectionUtils reflectionUtils) {
        int modifiers = field.getModifiers();
        if (!(onlySerializePublicFields && !Modifier.isPublic(modifiers) || Modifier.isTransient(modifiers) || Modifier.isFinal(modifiers) || (modifiers & 0x1000) != 0)) {
            return JSONUtils.makeAccessible(field, reflectionUtils);
        }
        return false;
    }

    static {
        try {
            isAccessibleMethod = AccessibleObject.class.getDeclaredMethod("isAccessible", new Class[0]);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            setAccessibleMethod = AccessibleObject.class.getDeclaredMethod("setAccessible", Boolean.TYPE);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            trySetAccessibleMethod = AccessibleObject.class.getDeclaredMethod("trySetAccessible", new Class[0]);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        JSON_CHAR_REPLACEMENTS = new String[256];
        for (int c = 0; c < 256; ++c) {
            int nibble1;
            if (c == 32) {
                c = 127;
            }
            char hexDigit1 = (nibble1 = c >> 4) <= 9 ? (char)(48 + nibble1) : (char)(65 + nibble1 - 10);
            int nibble0 = c & 0xF;
            char hexDigit0 = nibble0 <= 9 ? (char)(48 + nibble0) : (char)(65 + nibble0 - 10);
            JSONUtils.JSON_CHAR_REPLACEMENTS[c] = "\\u00" + hexDigit1 + "" + hexDigit0;
        }
        JSONUtils.JSON_CHAR_REPLACEMENTS[34] = "\\\"";
        JSONUtils.JSON_CHAR_REPLACEMENTS[92] = "\\\\";
        JSONUtils.JSON_CHAR_REPLACEMENTS[10] = "\\n";
        JSONUtils.JSON_CHAR_REPLACEMENTS[13] = "\\r";
        JSONUtils.JSON_CHAR_REPLACEMENTS[9] = "\\t";
        JSONUtils.JSON_CHAR_REPLACEMENTS[8] = "\\b";
        JSONUtils.JSON_CHAR_REPLACEMENTS[12] = "\\f";
        INDENT_LEVELS = new String[17];
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < INDENT_LEVELS.length; ++i) {
            JSONUtils.INDENT_LEVELS[i] = buf.toString();
            buf.append(' ');
        }
    }
}

