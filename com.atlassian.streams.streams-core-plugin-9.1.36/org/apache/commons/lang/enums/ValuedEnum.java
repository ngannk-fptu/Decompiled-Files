/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang.enums;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.enums.Enum;

public abstract class ValuedEnum
extends Enum {
    private static final long serialVersionUID = -7129650521543789085L;
    private final int iValue;

    protected ValuedEnum(String name, int value) {
        super(name);
        this.iValue = value;
    }

    protected static Enum getEnum(Class enumClass, int value) {
        if (enumClass == null) {
            throw new IllegalArgumentException("The Enum Class must not be null");
        }
        List list = Enum.getEnumList(enumClass);
        Iterator it = list.iterator();
        while (it.hasNext()) {
            ValuedEnum enumeration = (ValuedEnum)it.next();
            if (enumeration.getValue() != value) continue;
            return enumeration;
        }
        return null;
    }

    public final int getValue() {
        return this.iValue;
    }

    public int compareTo(Object other) {
        if (other == this) {
            return 0;
        }
        if (other.getClass() != this.getClass()) {
            if (other.getClass().getName().equals(this.getClass().getName())) {
                return this.iValue - this.getValueInOtherClassLoader(other);
            }
            throw new ClassCastException("Different enum class '" + ClassUtils.getShortClassName(other.getClass()) + "'");
        }
        return this.iValue - ((ValuedEnum)other).iValue;
    }

    private int getValueInOtherClassLoader(Object other) {
        try {
            Method mth = other.getClass().getMethod("getValue", null);
            Integer value = (Integer)mth.invoke(other, null);
            return value;
        }
        catch (NoSuchMethodException e) {
        }
        catch (IllegalAccessException e) {
        }
        catch (InvocationTargetException invocationTargetException) {
            // empty catch block
        }
        throw new IllegalStateException("This should not happen");
    }

    public String toString() {
        if (this.iToString == null) {
            String shortName = ClassUtils.getShortClassName(this.getEnumClass());
            this.iToString = shortName + "[" + this.getName() + "=" + this.getValue() + "]";
        }
        return this.iToString;
    }
}

