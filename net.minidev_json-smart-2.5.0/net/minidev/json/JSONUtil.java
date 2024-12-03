/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.asm.FieldFilter
 */
package net.minidev.json;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minidev.asm.FieldFilter;
import net.minidev.json.annotate.JsonIgnore;

public class JSONUtil {
    public static final JsonSmartFieldFilter JSON_SMART_FIELD_FILTER = new JsonSmartFieldFilter();

    public static Object convertToStrict(Object obj, Class<?> dest) {
        String asString;
        if (obj == null) {
            return null;
        }
        if (dest.isAssignableFrom(obj.getClass())) {
            return obj;
        }
        if (dest.isPrimitive()) {
            if (dest == Integer.TYPE) {
                if (obj instanceof Number) {
                    return ((Number)obj).intValue();
                }
                return Integer.valueOf(obj.toString());
            }
            if (dest == Short.TYPE) {
                if (obj instanceof Number) {
                    return ((Number)obj).shortValue();
                }
                return Short.valueOf(obj.toString());
            }
            if (dest == Long.TYPE) {
                if (obj instanceof Number) {
                    return ((Number)obj).longValue();
                }
                return Long.valueOf(obj.toString());
            }
            if (dest == Byte.TYPE) {
                if (obj instanceof Number) {
                    return ((Number)obj).byteValue();
                }
                return Byte.valueOf(obj.toString());
            }
            if (dest == Float.TYPE) {
                if (obj instanceof Number) {
                    return Float.valueOf(((Number)obj).floatValue());
                }
                return Float.valueOf(obj.toString());
            }
            if (dest == Double.TYPE) {
                if (obj instanceof Number) {
                    return ((Number)obj).doubleValue();
                }
                return Double.valueOf(obj.toString());
            }
            if (dest == Character.TYPE) {
                String asString2 = dest.toString();
                if (asString2.length() > 0) {
                    return Character.valueOf(asString2.charAt(0));
                }
            } else if (dest == Boolean.TYPE) {
                return (Boolean)obj;
            }
            throw new RuntimeException("Primitive: Can not convert " + obj.getClass().getName() + " to " + dest.getName());
        }
        if (dest.isEnum()) {
            return Enum.valueOf(dest, obj.toString());
        }
        if (dest == Integer.class) {
            if (obj instanceof Number) {
                return ((Number)obj).intValue();
            }
            return Integer.valueOf(obj.toString());
        }
        if (dest == Long.class) {
            if (obj instanceof Number) {
                return ((Number)obj).longValue();
            }
            return Long.valueOf(obj.toString());
        }
        if (dest == Short.class) {
            if (obj instanceof Number) {
                return ((Number)obj).shortValue();
            }
            return Short.valueOf(obj.toString());
        }
        if (dest == Byte.class) {
            if (obj instanceof Number) {
                return ((Number)obj).byteValue();
            }
            return Byte.valueOf(obj.toString());
        }
        if (dest == Float.class) {
            if (obj instanceof Number) {
                return Float.valueOf(((Number)obj).floatValue());
            }
            return Float.valueOf(obj.toString());
        }
        if (dest == Double.class) {
            if (obj instanceof Number) {
                return ((Number)obj).doubleValue();
            }
            return Double.valueOf(obj.toString());
        }
        if (dest == Character.class && (asString = dest.toString()).length() > 0) {
            return Character.valueOf(asString.charAt(0));
        }
        throw new RuntimeException("Object: Can not Convert " + obj.getClass().getName() + " to " + dest.getName());
    }

    public static Object convertToX(Object obj, Class<?> dest) {
        String asString;
        if (obj == null) {
            return null;
        }
        if (dest.isAssignableFrom(obj.getClass())) {
            return obj;
        }
        if (dest.isPrimitive()) {
            if (obj instanceof Number) {
                return obj;
            }
            if (dest == Integer.TYPE) {
                return Integer.valueOf(obj.toString());
            }
            if (dest == Short.TYPE) {
                return Short.valueOf(obj.toString());
            }
            if (dest == Long.TYPE) {
                return Long.valueOf(obj.toString());
            }
            if (dest == Byte.TYPE) {
                return Byte.valueOf(obj.toString());
            }
            if (dest == Float.TYPE) {
                return Float.valueOf(obj.toString());
            }
            if (dest == Double.TYPE) {
                return Double.valueOf(obj.toString());
            }
            if (dest == Character.TYPE) {
                String asString2 = dest.toString();
                if (asString2.length() > 0) {
                    return Character.valueOf(asString2.charAt(0));
                }
            } else if (dest == Boolean.TYPE) {
                return (Boolean)obj;
            }
            throw new RuntimeException("Primitive: Can not convert " + obj.getClass().getName() + " to " + dest.getName());
        }
        if (dest.isEnum()) {
            return Enum.valueOf(dest, obj.toString());
        }
        if (dest == Integer.class) {
            if (obj instanceof Number) {
                return ((Number)obj).intValue();
            }
            return Integer.valueOf(obj.toString());
        }
        if (dest == Long.class) {
            if (obj instanceof Number) {
                return ((Number)obj).longValue();
            }
            return Long.valueOf(obj.toString());
        }
        if (dest == Short.class) {
            if (obj instanceof Number) {
                return ((Number)obj).shortValue();
            }
            return Short.valueOf(obj.toString());
        }
        if (dest == Byte.class) {
            if (obj instanceof Number) {
                return ((Number)obj).byteValue();
            }
            return Byte.valueOf(obj.toString());
        }
        if (dest == Float.class) {
            if (obj instanceof Number) {
                return Float.valueOf(((Number)obj).floatValue());
            }
            return Float.valueOf(obj.toString());
        }
        if (dest == Double.class) {
            if (obj instanceof Number) {
                return ((Number)obj).doubleValue();
            }
            return Double.valueOf(obj.toString());
        }
        if (dest == Character.class && (asString = dest.toString()).length() > 0) {
            return Character.valueOf(asString.charAt(0));
        }
        throw new RuntimeException("Object: Can not Convert " + obj.getClass().getName() + " to " + dest.getName());
    }

    public static String getSetterName(String key) {
        int len = key.length();
        char[] b = new char[len + 3];
        b[0] = 115;
        b[1] = 101;
        b[2] = 116;
        char c = key.charAt(0);
        if (c >= 'a' && c <= 'z') {
            c = (char)(c - 32);
        }
        b[3] = c;
        for (int i = 1; i < len; ++i) {
            b[i + 3] = key.charAt(i);
        }
        return new String(b);
    }

    public static String getGetterName(String key) {
        int len = key.length();
        char[] b = new char[len + 3];
        b[0] = 103;
        b[1] = 101;
        b[2] = 116;
        char c = key.charAt(0);
        if (c >= 'a' && c <= 'z') {
            c = (char)(c - 32);
        }
        b[3] = c;
        for (int i = 1; i < len; ++i) {
            b[i + 3] = key.charAt(i);
        }
        return new String(b);
    }

    public static String getIsName(String key) {
        int len = key.length();
        char[] b = new char[len + 2];
        b[0] = 105;
        b[1] = 115;
        char c = key.charAt(0);
        if (c >= 'a' && c <= 'z') {
            c = (char)(c - 32);
        }
        b[2] = c;
        for (int i = 1; i < len; ++i) {
            b[i + 2] = key.charAt(i);
        }
        return new String(b);
    }

    public static class JsonSmartFieldFilter
    implements FieldFilter {
        public boolean canUse(Field field) {
            JsonIgnore ignore = field.getAnnotation(JsonIgnore.class);
            return ignore == null || !ignore.value();
        }

        public boolean canUse(Field field, Method method) {
            JsonIgnore ignore = method.getAnnotation(JsonIgnore.class);
            return ignore == null || !ignore.value();
        }

        public boolean canRead(Field field) {
            return true;
        }

        public boolean canWrite(Field field) {
            return true;
        }
    }
}

