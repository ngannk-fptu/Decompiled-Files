/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.assistedinject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class ParameterListKey {
    private final List<Type> paramList;

    public ParameterListKey(List<Type> paramList) {
        this.paramList = new ArrayList<Type>(paramList);
    }

    public ParameterListKey(Type[] types) {
        this(Arrays.asList(types));
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ParameterListKey)) {
            return false;
        }
        ParameterListKey other = (ParameterListKey)o;
        return ((Object)this.paramList).equals(other.paramList);
    }

    public int hashCode() {
        return ((Object)this.paramList).hashCode();
    }

    public String toString() {
        return this.paramList.toString();
    }
}

