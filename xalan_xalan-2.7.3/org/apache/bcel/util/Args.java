/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.util;

import org.apache.bcel.classfile.ClassFormatException;

public class Args {
    public static int require(int value, int required, String message) {
        if (value != required) {
            throw new ClassFormatException(String.format("%s [Value must be 0: %,d]", message, value));
        }
        return value;
    }

    public static int require0(int value, String message) {
        return Args.require(value, 0, message);
    }

    public static int requireU1(int value, String message) {
        if (value < 0 || value > 255) {
            throw new ClassFormatException(String.format("%s [Value out of range (0 - %,d) for type u1: %,d]", message, 255, value));
        }
        return value;
    }

    public static int requireU2(int value, int min, int max, String message) {
        if (max > 65535) {
            throw new IllegalArgumentException(String.format("%s programming error: max %,d > %,d", message, max, 65535));
        }
        if (min < 0) {
            throw new IllegalArgumentException(String.format("%s programming error: min %,d < 0", message, min));
        }
        if (value < min || value > max) {
            throw new ClassFormatException(String.format("%s [Value out of range (%,d - %,d) for type u2: %,d]", message, min, 65535, value));
        }
        return value;
    }

    public static int requireU2(int value, int min, String message) {
        return Args.requireU2(value, min, 65535, message);
    }

    public static int requireU2(int value, String message) {
        return Args.requireU2(value, 0, message);
    }

    public static int requireU4(int value, int min, String message) {
        if (min < 0) {
            throw new IllegalArgumentException(String.format("%s programming error: min %,d < 0", message, min));
        }
        if (value < min) {
            throw new ClassFormatException(String.format("%s [Value out of range (%,d - %,d) for type u2: %,d]", message, min, Integer.MAX_VALUE, (long)value & 0xFFFFFFFFL));
        }
        return value;
    }

    public static int requireU4(int value, String message) {
        return Args.requireU4(value, 0, message);
    }
}

