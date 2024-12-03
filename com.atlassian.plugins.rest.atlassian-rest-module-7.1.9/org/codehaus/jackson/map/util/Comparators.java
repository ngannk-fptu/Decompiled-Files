/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.util;

import java.lang.reflect.Array;

public class Comparators {
    public static Object getArrayComparator(final Object defaultValue) {
        final int length = Array.getLength(defaultValue);
        return new Object(){

            public boolean equals(Object other) {
                if (other == this) {
                    return true;
                }
                if (other == null || other.getClass() != defaultValue.getClass()) {
                    return false;
                }
                if (Array.getLength(other) != length) {
                    return false;
                }
                for (int i = 0; i < length; ++i) {
                    Object value2;
                    Object value1 = Array.get(defaultValue, i);
                    if (value1 == (value2 = Array.get(other, i)) || value1 == null || value1.equals(value2)) continue;
                    return false;
                }
                return true;
            }
        };
    }
}

