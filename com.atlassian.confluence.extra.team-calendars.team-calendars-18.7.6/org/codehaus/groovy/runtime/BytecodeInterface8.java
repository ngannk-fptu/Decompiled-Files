/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport;
import org.codehaus.groovy.runtime.metaclass.DefaultMetaClassInfo;

public class BytecodeInterface8 {
    public static boolean disabledStandardMetaClass() {
        return DefaultMetaClassInfo.disabledStandardMetaClass();
    }

    public static boolean isOrigInt() {
        return DefaultMetaClassInfo.isOrigInt();
    }

    public static boolean isOrigIntArray() {
        return DefaultMetaClassInfo.isOrigIntArray();
    }

    public static int intArrayGet(int[] a, int i) {
        try {
            return a[i];
        }
        catch (Throwable t) {
            return a[DefaultGroovyMethodsSupport.normaliseIndex(i, a.length)];
        }
    }

    public static void intArraySet(int[] a, int i, int v) {
        try {
            a[i] = v;
        }
        catch (Throwable t) {
            a[DefaultGroovyMethodsSupport.normaliseIndex((int)i, (int)a.length)] = v;
        }
    }

    public static boolean isOrigB() {
        return DefaultMetaClassInfo.isOrigByte();
    }

    public static boolean isOrigBArray() {
        return false;
    }

    public static byte bArrayGet(byte[] a, int i) {
        try {
            return a[i];
        }
        catch (Throwable t) {
            return a[DefaultGroovyMethodsSupport.normaliseIndex(i, a.length)];
        }
    }

    public static void bArraySet(byte[] a, int i, byte v) {
        try {
            a[i] = v;
        }
        catch (Throwable t) {
            a[DefaultGroovyMethodsSupport.normaliseIndex((int)i, (int)a.length)] = v;
        }
    }

    public static boolean isOrigS() {
        return DefaultMetaClassInfo.isOrigShort();
    }

    public static boolean isOrigSArray() {
        return false;
    }

    public static short sArrayGet(short[] a, int i) {
        try {
            return a[i];
        }
        catch (Throwable t) {
            return a[DefaultGroovyMethodsSupport.normaliseIndex(i, a.length)];
        }
    }

    public static void sArraySet(short[] a, int i, short v) {
        try {
            a[i] = v;
        }
        catch (Throwable t) {
            a[DefaultGroovyMethodsSupport.normaliseIndex((int)i, (int)a.length)] = v;
        }
    }

    public static boolean isOrigC() {
        return DefaultMetaClassInfo.isOrigChar();
    }

    public static boolean isOrigCArray() {
        return false;
    }

    public static char cArrayGet(char[] a, int i) {
        try {
            return a[i];
        }
        catch (Throwable t) {
            return a[DefaultGroovyMethodsSupport.normaliseIndex(i, a.length)];
        }
    }

    public static void cArraySet(char[] a, int i, char v) {
        try {
            a[i] = v;
        }
        catch (Throwable t) {
            a[DefaultGroovyMethodsSupport.normaliseIndex((int)i, (int)a.length)] = v;
        }
    }

    public static boolean isOrigL() {
        return DefaultMetaClassInfo.isOrigLong();
    }

    public static boolean isOrigLArray() {
        return false;
    }

    public static long lArrayGet(long[] a, int i) {
        try {
            return a[i];
        }
        catch (Throwable t) {
            return a[DefaultGroovyMethodsSupport.normaliseIndex(i, a.length)];
        }
    }

    public static void lArraySet(long[] a, int i, long v) {
        try {
            a[i] = v;
        }
        catch (Throwable t) {
            a[DefaultGroovyMethodsSupport.normaliseIndex((int)i, (int)a.length)] = v;
        }
    }

    public static boolean isOrigZ() {
        return DefaultMetaClassInfo.isOrigBool();
    }

    public static boolean isOrigZArray() {
        return false;
    }

    public static boolean zArrayGet(boolean[] a, int i) {
        try {
            return a[i];
        }
        catch (Throwable t) {
            return a[DefaultGroovyMethodsSupport.normaliseIndex(i, a.length)];
        }
    }

    public static void zArraySet(boolean[] a, int i, boolean v) {
        try {
            a[i] = v;
        }
        catch (Throwable t) {
            a[DefaultGroovyMethodsSupport.normaliseIndex((int)i, (int)a.length)] = v;
        }
    }

    public static boolean isOrigF() {
        return DefaultMetaClassInfo.isOrigFloat();
    }

    public static boolean isOrigFArray() {
        return false;
    }

    public static float fArrayGet(float[] a, int i) {
        try {
            return a[i];
        }
        catch (Throwable t) {
            return a[DefaultGroovyMethodsSupport.normaliseIndex(i, a.length)];
        }
    }

    public static void fArraySet(float[] a, int i, float v) {
        try {
            a[i] = v;
        }
        catch (Throwable t) {
            a[DefaultGroovyMethodsSupport.normaliseIndex((int)i, (int)a.length)] = v;
        }
    }

    public static boolean isOrigD() {
        return DefaultMetaClassInfo.isOrigDouble();
    }

    public static boolean isOrigDArray() {
        return false;
    }

    public static double dArrayGet(double[] a, int i) {
        try {
            return a[i];
        }
        catch (Throwable t) {
            return a[DefaultGroovyMethodsSupport.normaliseIndex(i, a.length)];
        }
    }

    public static void dArraySet(double[] a, int i, double v) {
        try {
            a[i] = v;
        }
        catch (Throwable t) {
            a[DefaultGroovyMethodsSupport.normaliseIndex((int)i, (int)a.length)] = v;
        }
    }

    public static Object objectArrayGet(Object[] a, int i) {
        try {
            return a[i];
        }
        catch (Throwable t) {
            return a[DefaultGroovyMethodsSupport.normaliseIndex(i, a.length)];
        }
    }

    public static void objectArraySet(Object[] a, int i, Object v) {
        try {
            a[i] = v;
        }
        catch (Throwable t) {
            a[DefaultGroovyMethodsSupport.normaliseIndex((int)i, (int)a.length)] = v;
        }
    }
}

