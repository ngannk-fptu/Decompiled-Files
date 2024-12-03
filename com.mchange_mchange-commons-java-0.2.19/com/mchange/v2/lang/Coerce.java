/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.lang;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class Coerce {
    static final Set CAN_COERCE;

    public static boolean canCoerce(Class clazz) {
        return CAN_COERCE.contains(clazz);
    }

    public static boolean canCoerce(Object object) {
        return Coerce.canCoerce(object.getClass());
    }

    public static int toInt(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException numberFormatException) {
            return (int)Double.parseDouble(string);
        }
    }

    public static long toLong(String string) {
        try {
            return Long.parseLong(string);
        }
        catch (NumberFormatException numberFormatException) {
            return (long)Double.parseDouble(string);
        }
    }

    public static float toFloat(String string) {
        return Float.parseFloat(string);
    }

    public static double toDouble(String string) {
        return Double.parseDouble(string);
    }

    public static byte toByte(String string) {
        return (byte)Coerce.toInt(string);
    }

    public static short toShort(String string) {
        return (short)Coerce.toInt(string);
    }

    public static boolean toBoolean(String string) {
        return Boolean.valueOf(string);
    }

    public static char toChar(String string) {
        if ((string = string.trim()).length() == 1) {
            return string.charAt(0);
        }
        return (char)Coerce.toInt(string);
    }

    public static Object toObject(String string, Class clazz) {
        if (clazz == Byte.TYPE) {
            clazz = Byte.class;
        } else if (clazz == Boolean.TYPE) {
            clazz = Boolean.class;
        } else if (clazz == Character.TYPE) {
            clazz = Character.class;
        } else if (clazz == Short.TYPE) {
            clazz = Short.class;
        } else if (clazz == Integer.TYPE) {
            clazz = Integer.class;
        } else if (clazz == Long.TYPE) {
            clazz = Long.class;
        } else if (clazz == Float.TYPE) {
            clazz = Float.class;
        } else if (clazz == Double.TYPE) {
            clazz = Double.class;
        }
        if (clazz == String.class) {
            return string;
        }
        if (clazz == Byte.class) {
            return new Byte(Coerce.toByte(string));
        }
        if (clazz == Boolean.class) {
            return Boolean.valueOf(string);
        }
        if (clazz == Character.class) {
            return new Character(Coerce.toChar(string));
        }
        if (clazz == Short.class) {
            return new Short(Coerce.toShort(string));
        }
        if (clazz == Integer.class) {
            return new Integer(string);
        }
        if (clazz == Long.class) {
            return new Long(string);
        }
        if (clazz == Float.class) {
            return new Float(string);
        }
        if (clazz == Double.class) {
            return new Double(string);
        }
        throw new IllegalArgumentException("Cannot coerce to type: " + clazz.getName());
    }

    private Coerce() {
    }

    static {
        Class[] classArray = new Class[]{Byte.TYPE, Boolean.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, String.class, Byte.class, Boolean.class, Character.class, Short.class, Integer.class, Long.class, Float.class, Double.class};
        HashSet<Class> hashSet = new HashSet<Class>();
        hashSet.addAll(Arrays.asList(classArray));
        CAN_COERCE = Collections.unmodifiableSet(hashSet);
    }
}

