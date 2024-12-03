/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.metaclass;

public class DefaultMetaClassInfo {
    private static boolean origBoolean = true;
    private static boolean origBooleanRes = true;
    private static boolean origByte = true;
    private static boolean origByteRes = true;
    private static boolean origChar = true;
    private static boolean origCharRes = true;
    private static boolean origShort = true;
    private static boolean origShortRes = true;
    private static boolean origInt = true;
    private static boolean origIntRes = true;
    private static boolean origIntArray = true;
    private static boolean origIntArrayWCH = true;
    private static boolean origLong = true;
    private static boolean origLongRes = true;
    private static boolean origFloat = true;
    private static boolean origFloatRes = true;
    private static boolean origDouble = true;
    private static boolean origDoubleRes = true;
    private static boolean withoutCustomHandle = true;
    private static boolean categoryUsed = false;
    private static boolean disabledStandardMC = false;
    private static ConstantMetaClassVersioning constantMetaClassVersioning = new ConstantMetaClassVersioning();
    private static Object constantMetaClassVersioningLock = new Object();

    public static boolean isOrigBool() {
        return origBooleanRes;
    }

    public static void setOrigBool(boolean v) {
        origBoolean = v;
        origBooleanRes = withoutCustomHandle && origBoolean;
    }

    public static boolean isOrigByte() {
        return origByteRes;
    }

    public static void setOrigByte(boolean v) {
        origByte = v;
        origByteRes = withoutCustomHandle && origByte;
    }

    public static boolean isOrigChar() {
        return origCharRes;
    }

    public static void setOrigChar(boolean v) {
        origChar = v;
        origCharRes = withoutCustomHandle && origChar;
    }

    public static boolean isOrigShort() {
        return origShortRes;
    }

    public static void setOrigShort(boolean v) {
        origShort = v;
        origShortRes = withoutCustomHandle && origShort;
    }

    public static boolean isOrigInt() {
        return origIntRes;
    }

    public static void setOrigInt(boolean v) {
        origInt = v;
        origIntRes = withoutCustomHandle && origInt;
    }

    public static boolean isOrigIntArray() {
        return origIntArrayWCH;
    }

    public static void setOrigIntArray(boolean v) {
        origIntArray = v;
        origIntArrayWCH = withoutCustomHandle && origIntArray;
    }

    public static boolean isOrigLong() {
        return origLongRes;
    }

    public static void setOrigLong(boolean v) {
        origLong = v;
        origLongRes = withoutCustomHandle && origLong;
    }

    public static boolean isOrigFloat() {
        return origFloatRes;
    }

    public static void setOrigFloat(boolean v) {
        origFloat = v;
        origFloatRes = withoutCustomHandle && origFloat;
    }

    public static boolean isOrigDouble() {
        return origDoubleRes;
    }

    public static void setOrigDouble(boolean v) {
        origDouble = v;
        origDoubleRes = withoutCustomHandle && origDouble;
    }

    public static void setWithoutCustomMetaclassCreationHandle(boolean mch) {
        withoutCustomHandle = mch;
        DefaultMetaClassInfo.changeFlags(mch);
    }

    public static void setCategoryUsed(boolean b) {
        categoryUsed = b;
        disabledStandardMC = b || !withoutCustomHandle;
    }

    public static boolean disabledStandardMetaClass() {
        return disabledStandardMC;
    }

    private static void changeFlags(boolean mch) {
        if (mch) {
            disabledStandardMC = true;
            origIntArrayWCH = false;
            origBoolean = false;
            origChar = false;
            origByteRes = false;
            origLong = false;
            origIntRes = false;
            origShortRes = false;
            origDouble = false;
            origFloat = false;
        } else {
            disabledStandardMC = categoryUsed;
            origByteRes = origByte;
            origCharRes = origChar;
            origBooleanRes = origBoolean;
            origShortRes = origShort;
            origIntRes = origInt;
            origLongRes = origLong;
            origFloatRes = origFloat;
            origDoubleRes = origDouble;
            origIntArrayWCH = origIntArray;
        }
    }

    public static void setPrimitiveMeta(Class c, boolean orig) {
        if (c == Byte.class) {
            DefaultMetaClassInfo.setOrigByte(orig);
        } else if (c == Character.class) {
            DefaultMetaClassInfo.setOrigChar(orig);
        } else if (c == Short.class) {
            DefaultMetaClassInfo.setOrigShort(orig);
        } else if (c == Integer.class) {
            DefaultMetaClassInfo.setOrigInt(orig);
        } else if (c.getComponentType() == Integer.class) {
            DefaultMetaClassInfo.setOrigIntArray(orig);
        } else if (c == Long.class) {
            DefaultMetaClassInfo.setOrigLong(orig);
        } else if (c == Float.class) {
            DefaultMetaClassInfo.setOrigFloat(orig);
        } else if (c == Double.class) {
            DefaultMetaClassInfo.setOrigDouble(orig);
        }
    }

    public static ConstantMetaClassVersioning getCurrentConstantMetaClassVersioning() {
        return constantMetaClassVersioning;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ConstantMetaClassVersioning getNewConstantMetaClassVersioning() {
        Object object = constantMetaClassVersioningLock;
        synchronized (object) {
            constantMetaClassVersioning.valid = false;
            constantMetaClassVersioning = new ConstantMetaClassVersioning();
            return constantMetaClassVersioning;
        }
    }

    public static class ConstantMetaClassVersioning {
        private boolean valid = true;

        public boolean isValid() {
            return this.valid;
        }
    }
}

