/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.typedarrays;

import org.mozilla.javascript.ScriptRuntime;

public class Conversions {
    public static int toInt8(Object arg) {
        return (byte)ScriptRuntime.toInt32(arg);
    }

    public static int toUint8(Object arg) {
        return ScriptRuntime.toInt32(arg) & 0xFF;
    }

    public static int toUint8Clamp(Object arg) {
        double d = ScriptRuntime.toNumber(arg);
        if (d <= 0.0) {
            return 0;
        }
        if (d >= 255.0) {
            return 255;
        }
        double f = Math.floor(d);
        if (f + 0.5 < d) {
            return (int)(f + 1.0);
        }
        if (d < f + 0.5) {
            return (int)f;
        }
        if ((int)f % 2 != 0) {
            return (int)f + 1;
        }
        return (int)f;
    }

    public static int toInt16(Object arg) {
        return (short)ScriptRuntime.toInt32(arg);
    }

    public static int toUint16(Object arg) {
        return ScriptRuntime.toInt32(arg) & 0xFFFF;
    }

    public static int toInt32(Object arg) {
        return ScriptRuntime.toInt32(arg);
    }

    public static long toUint32(Object arg) {
        return ScriptRuntime.toUint32(arg);
    }
}

