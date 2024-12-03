/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang.builder;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.IDKey;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class HashCodeBuilder {
    private static final ThreadLocal REGISTRY = new ThreadLocal();
    private final int iConstant;
    private int iTotal = 0;
    static /* synthetic */ Class class$org$apache$commons$lang$builder$HashCodeBuilder;

    static Set getRegistry() {
        return (Set)REGISTRY.get();
    }

    static boolean isRegistered(Object value) {
        Set registry = HashCodeBuilder.getRegistry();
        return registry != null && registry.contains(new IDKey(value));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void reflectionAppend(Object object, Class clazz, HashCodeBuilder builder, boolean useTransients, String[] excludeFields) {
        if (HashCodeBuilder.isRegistered(object)) {
            return;
        }
        try {
            HashCodeBuilder.register(object);
            AccessibleObject[] fields = clazz.getDeclaredFields();
            AccessibleObject.setAccessible(fields, true);
            for (int i = 0; i < fields.length; ++i) {
                AccessibleObject field = fields[i];
                if (ArrayUtils.contains(excludeFields, ((Field)field).getName()) || ((Field)field).getName().indexOf(36) != -1 || !useTransients && Modifier.isTransient(((Field)field).getModifiers()) || Modifier.isStatic(((Field)field).getModifiers())) continue;
                try {
                    Object fieldValue = ((Field)field).get(object);
                    builder.append(fieldValue);
                    continue;
                }
                catch (IllegalAccessException e) {
                    throw new InternalError("Unexpected IllegalAccessException");
                }
            }
        }
        finally {
            HashCodeBuilder.unregister(object);
        }
    }

    public static int reflectionHashCode(int initialNonZeroOddNumber, int multiplierNonZeroOddNumber, Object object) {
        return HashCodeBuilder.reflectionHashCode(initialNonZeroOddNumber, multiplierNonZeroOddNumber, object, false, null, null);
    }

    public static int reflectionHashCode(int initialNonZeroOddNumber, int multiplierNonZeroOddNumber, Object object, boolean testTransients) {
        return HashCodeBuilder.reflectionHashCode(initialNonZeroOddNumber, multiplierNonZeroOddNumber, object, testTransients, null, null);
    }

    public static int reflectionHashCode(int initialNonZeroOddNumber, int multiplierNonZeroOddNumber, Object object, boolean testTransients, Class reflectUpToClass) {
        return HashCodeBuilder.reflectionHashCode(initialNonZeroOddNumber, multiplierNonZeroOddNumber, object, testTransients, reflectUpToClass, null);
    }

    public static int reflectionHashCode(int initialNonZeroOddNumber, int multiplierNonZeroOddNumber, Object object, boolean testTransients, Class reflectUpToClass, String[] excludeFields) {
        Class<?> clazz;
        if (object == null) {
            throw new IllegalArgumentException("The object to build a hash code for must not be null");
        }
        HashCodeBuilder builder = new HashCodeBuilder(initialNonZeroOddNumber, multiplierNonZeroOddNumber);
        HashCodeBuilder.reflectionAppend(object, clazz, builder, testTransients, excludeFields);
        for (clazz = object.getClass(); clazz.getSuperclass() != null && clazz != reflectUpToClass; clazz = clazz.getSuperclass()) {
            HashCodeBuilder.reflectionAppend(object, clazz, builder, testTransients, excludeFields);
        }
        return builder.toHashCode();
    }

    public static int reflectionHashCode(Object object) {
        return HashCodeBuilder.reflectionHashCode(17, 37, object, false, null, null);
    }

    public static int reflectionHashCode(Object object, boolean testTransients) {
        return HashCodeBuilder.reflectionHashCode(17, 37, object, testTransients, null, null);
    }

    public static int reflectionHashCode(Object object, Collection excludeFields) {
        return HashCodeBuilder.reflectionHashCode(object, ReflectionToStringBuilder.toNoNullStringArray(excludeFields));
    }

    public static int reflectionHashCode(Object object, String[] excludeFields) {
        return HashCodeBuilder.reflectionHashCode(17, 37, object, false, null, excludeFields);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void register(Object value) {
        Class clazz = class$org$apache$commons$lang$builder$HashCodeBuilder == null ? (class$org$apache$commons$lang$builder$HashCodeBuilder = HashCodeBuilder.class$("org.apache.commons.lang.builder.HashCodeBuilder")) : class$org$apache$commons$lang$builder$HashCodeBuilder;
        synchronized (clazz) {
            if (HashCodeBuilder.getRegistry() == null) {
                REGISTRY.set(new HashSet());
            }
        }
        HashCodeBuilder.getRegistry().add(new IDKey(value));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void unregister(Object value) {
        Set registry = HashCodeBuilder.getRegistry();
        if (registry != null) {
            registry.remove(new IDKey(value));
            Class clazz = class$org$apache$commons$lang$builder$HashCodeBuilder == null ? (class$org$apache$commons$lang$builder$HashCodeBuilder = HashCodeBuilder.class$("org.apache.commons.lang.builder.HashCodeBuilder")) : class$org$apache$commons$lang$builder$HashCodeBuilder;
            synchronized (clazz) {
                registry = HashCodeBuilder.getRegistry();
                if (registry != null && registry.isEmpty()) {
                    REGISTRY.set(null);
                }
            }
        }
    }

    public HashCodeBuilder() {
        this.iConstant = 37;
        this.iTotal = 17;
    }

    public HashCodeBuilder(int initialNonZeroOddNumber, int multiplierNonZeroOddNumber) {
        if (initialNonZeroOddNumber == 0) {
            throw new IllegalArgumentException("HashCodeBuilder requires a non zero initial value");
        }
        if (initialNonZeroOddNumber % 2 == 0) {
            throw new IllegalArgumentException("HashCodeBuilder requires an odd initial value");
        }
        if (multiplierNonZeroOddNumber == 0) {
            throw new IllegalArgumentException("HashCodeBuilder requires a non zero multiplier");
        }
        if (multiplierNonZeroOddNumber % 2 == 0) {
            throw new IllegalArgumentException("HashCodeBuilder requires an odd multiplier");
        }
        this.iConstant = multiplierNonZeroOddNumber;
        this.iTotal = initialNonZeroOddNumber;
    }

    public HashCodeBuilder append(boolean value) {
        this.iTotal = this.iTotal * this.iConstant + (value ? 0 : 1);
        return this;
    }

    public HashCodeBuilder append(boolean[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (int i = 0; i < array.length; ++i) {
                this.append(array[i]);
            }
        }
        return this;
    }

    public HashCodeBuilder append(byte value) {
        this.iTotal = this.iTotal * this.iConstant + value;
        return this;
    }

    public HashCodeBuilder append(byte[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (int i = 0; i < array.length; ++i) {
                this.append(array[i]);
            }
        }
        return this;
    }

    public HashCodeBuilder append(char value) {
        this.iTotal = this.iTotal * this.iConstant + value;
        return this;
    }

    public HashCodeBuilder append(char[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (int i = 0; i < array.length; ++i) {
                this.append(array[i]);
            }
        }
        return this;
    }

    public HashCodeBuilder append(double value) {
        return this.append(Double.doubleToLongBits(value));
    }

    public HashCodeBuilder append(double[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (int i = 0; i < array.length; ++i) {
                this.append(array[i]);
            }
        }
        return this;
    }

    public HashCodeBuilder append(float value) {
        this.iTotal = this.iTotal * this.iConstant + Float.floatToIntBits(value);
        return this;
    }

    public HashCodeBuilder append(float[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (int i = 0; i < array.length; ++i) {
                this.append(array[i]);
            }
        }
        return this;
    }

    public HashCodeBuilder append(int value) {
        this.iTotal = this.iTotal * this.iConstant + value;
        return this;
    }

    public HashCodeBuilder append(int[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (int i = 0; i < array.length; ++i) {
                this.append(array[i]);
            }
        }
        return this;
    }

    public HashCodeBuilder append(long value) {
        this.iTotal = this.iTotal * this.iConstant + (int)(value ^ value >> 32);
        return this;
    }

    public HashCodeBuilder append(long[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (int i = 0; i < array.length; ++i) {
                this.append(array[i]);
            }
        }
        return this;
    }

    public HashCodeBuilder append(Object object) {
        if (object == null) {
            this.iTotal *= this.iConstant;
        } else if (object.getClass().isArray()) {
            if (object instanceof long[]) {
                this.append((long[])object);
            } else if (object instanceof int[]) {
                this.append((int[])object);
            } else if (object instanceof short[]) {
                this.append((short[])object);
            } else if (object instanceof char[]) {
                this.append((char[])object);
            } else if (object instanceof byte[]) {
                this.append((byte[])object);
            } else if (object instanceof double[]) {
                this.append((double[])object);
            } else if (object instanceof float[]) {
                this.append((float[])object);
            } else if (object instanceof boolean[]) {
                this.append((boolean[])object);
            } else {
                this.append((Object[])object);
            }
        } else {
            this.iTotal = this.iTotal * this.iConstant + object.hashCode();
        }
        return this;
    }

    public HashCodeBuilder append(Object[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (int i = 0; i < array.length; ++i) {
                this.append(array[i]);
            }
        }
        return this;
    }

    public HashCodeBuilder append(short value) {
        this.iTotal = this.iTotal * this.iConstant + value;
        return this;
    }

    public HashCodeBuilder append(short[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (int i = 0; i < array.length; ++i) {
                this.append(array[i]);
            }
        }
        return this;
    }

    public HashCodeBuilder appendSuper(int superHashCode) {
        this.iTotal = this.iTotal * this.iConstant + superHashCode;
        return this;
    }

    public int toHashCode() {
        return this.iTotal;
    }

    public int hashCode() {
        return this.toHashCode();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

