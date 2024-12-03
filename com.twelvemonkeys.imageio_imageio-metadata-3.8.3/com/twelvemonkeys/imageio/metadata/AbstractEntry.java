/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 *  com.twelvemonkeys.util.CollectionUtil
 */
package com.twelvemonkeys.imageio.metadata;

import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.lang.Validate;
import com.twelvemonkeys.util.CollectionUtil;
import java.lang.reflect.Array;
import java.util.Arrays;

public abstract class AbstractEntry
implements Entry {
    private final Object identifier;
    private final Object value;

    protected AbstractEntry(Object object, Object object2) {
        Validate.notNull((Object)object, (String)"identifier");
        this.identifier = object;
        this.value = object2;
    }

    @Override
    public final Object getIdentifier() {
        return this.identifier;
    }

    protected String getNativeIdentifier() {
        return String.valueOf(this.getIdentifier());
    }

    @Override
    public String getFieldName() {
        return null;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public String getValueAsString() {
        int n = this.valueCount();
        if (n == 0 && this.value != null && this.value.getClass().isArray() && Array.getLength(this.value) == 0) {
            return "";
        }
        if (n > 1) {
            if (n < 16) {
                return AbstractEntry.arrayToString(this.value);
            }
            String string = AbstractEntry.arrayToString(CollectionUtil.subArray((Object)this.value, (int)0, (int)4));
            String string2 = AbstractEntry.arrayToString(CollectionUtil.subArray((Object)this.value, (int)(n - 4), (int)4));
            return String.format("%s ... %s (%d)", string.substring(0, string.length() - 1), string2.substring(1), n);
        }
        if (this.value != null && this.value.getClass().isArray() && Array.getLength(this.value) == 1) {
            return String.valueOf(Array.get(this.value, 0));
        }
        return String.valueOf(this.value);
    }

    private static String arrayToString(Object object) {
        Class<?> clazz = object.getClass().getComponentType();
        if (clazz.isPrimitive()) {
            if (clazz.equals(Boolean.TYPE)) {
                return Arrays.toString((boolean[])object);
            }
            if (clazz.equals(Byte.TYPE)) {
                return Arrays.toString((byte[])object);
            }
            if (clazz.equals(Character.TYPE)) {
                return new String((char[])object);
            }
            if (clazz.equals(Double.TYPE)) {
                return Arrays.toString((double[])object);
            }
            if (clazz.equals(Float.TYPE)) {
                return Arrays.toString((float[])object);
            }
            if (clazz.equals(Integer.TYPE)) {
                return Arrays.toString((int[])object);
            }
            if (clazz.equals(Long.TYPE)) {
                return Arrays.toString((long[])object);
            }
            if (clazz.equals(Short.TYPE)) {
                return Arrays.toString((short[])object);
            }
            throw new AssertionError((Object)("Unknown type: " + clazz));
        }
        return Arrays.toString((Object[])object);
    }

    @Override
    public String getTypeName() {
        if (this.value == null) {
            return null;
        }
        return this.value.getClass().getSimpleName();
    }

    @Override
    public int valueCount() {
        if (this.value != null && this.value.getClass().isArray()) {
            return Array.getLength(this.value);
        }
        return 1;
    }

    public int hashCode() {
        return this.identifier.hashCode() + (this.value != null ? 31 * this.value.hashCode() : 0);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof AbstractEntry)) {
            return false;
        }
        AbstractEntry abstractEntry = (AbstractEntry)object;
        return this.identifier.equals(abstractEntry.identifier) && (this.value == null && abstractEntry.value == null || this.value != null && this.valueEquals(abstractEntry));
    }

    private boolean valueEquals(AbstractEntry abstractEntry) {
        return this.value.getClass().isArray() ? AbstractEntry.arrayEquals(this.value, abstractEntry.value) : this.value.equals(abstractEntry.value);
    }

    static boolean arrayEquals(Object object, Object object2) {
        if (object == object2) {
            return true;
        }
        if (object2 == null || object == null || object.getClass() != object2.getClass()) {
            return false;
        }
        Class<?> clazz = object.getClass().getComponentType();
        if (clazz.isPrimitive()) {
            if (object instanceof byte[]) {
                return Arrays.equals((byte[])object, (byte[])object2);
            }
            if (object instanceof char[]) {
                return Arrays.equals((char[])object, (char[])object2);
            }
            if (object instanceof short[]) {
                return Arrays.equals((short[])object, (short[])object2);
            }
            if (object instanceof int[]) {
                return Arrays.equals((int[])object, (int[])object2);
            }
            if (object instanceof long[]) {
                return Arrays.equals((long[])object, (long[])object2);
            }
            if (object instanceof boolean[]) {
                return Arrays.equals((boolean[])object, (boolean[])object2);
            }
            if (object instanceof float[]) {
                return Arrays.equals((float[])object, (float[])object2);
            }
            if (object instanceof double[]) {
                return Arrays.equals((double[])object, (double[])object2);
            }
            throw new AssertionError((Object)("Unsupported type:" + clazz));
        }
        return Arrays.equals((Object[])object, (Object[])object2);
    }

    public String toString() {
        String string = this.getFieldName();
        String string2 = string != null ? String.format("/%s", string) : "";
        String string3 = this.getTypeName();
        String string4 = string3 != null ? String.format(" (%s)", string3) : "";
        return String.format("%s%s: %s%s", this.getNativeIdentifier(), string2, this.getValueAsString(), string4);
    }
}

