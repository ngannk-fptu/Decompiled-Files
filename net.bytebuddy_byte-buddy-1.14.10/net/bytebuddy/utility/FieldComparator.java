/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.utility;

import java.lang.reflect.Field;
import java.util.Comparator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum FieldComparator implements Comparator<Field>
{
    INSTANCE;


    @Override
    public int compare(Field left, Field right) {
        if (left == right) {
            return 0;
        }
        int comparison = left.getName().compareTo(right.getName());
        if (comparison == 0) {
            return left.getType().getName().compareTo(right.getType().getName());
        }
        return comparison;
    }
}

