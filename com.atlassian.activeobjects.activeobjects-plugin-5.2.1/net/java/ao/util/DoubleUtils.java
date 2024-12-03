/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.util;

import net.java.ao.ActiveObjectsException;

public class DoubleUtils {
    public static final double MAX_VALUE = 3.40282347E38;
    public static final double MIN_VALUE = -3.40282347E38;

    public static Double checkDouble(Double d) {
        if (d.compareTo(3.40282347E38) > 0) {
            throw new ActiveObjectsException("The max value of double allowed with Active Objects is 3.40282347E38, checked double is " + d);
        }
        if (d.compareTo(-3.40282347E38) < 0) {
            throw new ActiveObjectsException("The min value of double allowed with Active Objects is -3.40282347E38, checked double is " + d);
        }
        return d;
    }
}

