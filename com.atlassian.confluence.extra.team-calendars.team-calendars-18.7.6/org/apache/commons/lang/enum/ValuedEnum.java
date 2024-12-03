/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang.enum;

import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.enum.Enum;

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
        return this.iValue - ((ValuedEnum)other).iValue;
    }

    public String toString() {
        if (this.iToString == null) {
            String shortName = ClassUtils.getShortClassName(this.getEnumClass());
            this.iToString = shortName + "[" + this.getName() + "=" + this.getValue() + "]";
        }
        return this.iToString;
    }
}

