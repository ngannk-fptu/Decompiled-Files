/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl.util;

import com.sun.media.jai.codecimpl.util.JaiI18N;
import java.awt.image.DataBuffer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public final class DataBufferUtils {
    private static final String[] FLOAT_CLASS_NAMES = new String[]{"java.awt.image.DataBufferFloat", "javax.media.jai.DataBufferFloat", "com.sun.media.jai.codecimpl.util.DataBufferFloat"};
    private static final String[] DOUBLE_CLASS_NAMES = new String[]{"java.awt.image.DataBufferDouble", "javax.media.jai.DataBufferDouble", "com.sun.media.jai.codecimpl.util.DataBufferDouble"};
    private static Class floatClass = null;
    private static Class doubleClass = null;
    static /* synthetic */ Class array$$F;
    static /* synthetic */ Class array$I;
    static /* synthetic */ Class array$F;
    static /* synthetic */ Class array$$D;
    static /* synthetic */ Class array$D;

    private static final Class getDataBufferClass(int dataType) {
        String[] classNames = null;
        switch (dataType) {
            case 4: {
                classNames = FLOAT_CLASS_NAMES;
                break;
            }
            case 5: {
                classNames = DOUBLE_CLASS_NAMES;
                break;
            }
            default: {
                throw new IllegalArgumentException("dataType == " + dataType + "!");
            }
        }
        Class<?> dataBufferClass = null;
        for (int i = 0; i < classNames.length; ++i) {
            try {
                dataBufferClass = Class.forName(classNames[i]);
                if (dataBufferClass == null) continue;
                break;
            }
            catch (ClassNotFoundException e) {
                // empty catch block
            }
        }
        if (dataBufferClass == null) {
            throw new RuntimeException(JaiI18N.getString("DataBufferUtils0") + " " + (dataType == 4 ? "DataBufferFloat" : "DataBufferDouble"));
        }
        return dataBufferClass;
    }

    private static final DataBuffer constructDataBuffer(int dataType, Class[] paramTypes, Object[] paramValues) {
        Class dbClass = null;
        switch (dataType) {
            case 4: {
                dbClass = floatClass;
                break;
            }
            case 5: {
                dbClass = doubleClass;
                break;
            }
            default: {
                throw new IllegalArgumentException("dataType == " + dataType + "!");
            }
        }
        DataBuffer dataBuffer = null;
        try {
            Constructor constructor = dbClass.getConstructor(paramTypes);
            dataBuffer = (DataBuffer)constructor.newInstance(paramValues);
        }
        catch (Exception e) {
            throw new RuntimeException(JaiI18N.getString("DataBufferUtils1"));
        }
        return dataBuffer;
    }

    private static final Object invokeDataBufferMethod(DataBuffer dataBuffer, String methodName, Class[] paramTypes, Object[] paramValues) {
        if (dataBuffer == null) {
            throw new IllegalArgumentException("dataBuffer == null!");
        }
        Class<?> dbClass = dataBuffer.getClass();
        Object returnValue = null;
        try {
            Method method = dbClass.getMethod(methodName, paramTypes);
            returnValue = method.invoke((Object)dataBuffer, paramValues);
        }
        catch (Exception e) {
            throw new RuntimeException(JaiI18N.getString("DataBufferUtils2") + " \"" + methodName + "\".");
        }
        return returnValue;
    }

    public static final DataBuffer createDataBufferFloat(float[][] dataArray, int size) {
        return DataBufferUtils.constructDataBuffer(4, new Class[]{array$$F == null ? (array$$F = DataBufferUtils.class$("[[F")) : array$$F, Integer.TYPE}, new Object[]{dataArray, new Integer(size)});
    }

    public static final DataBuffer createDataBufferFloat(float[][] dataArray, int size, int[] offsets) {
        return DataBufferUtils.constructDataBuffer(4, new Class[]{array$$F == null ? (array$$F = DataBufferUtils.class$("[[F")) : array$$F, Integer.TYPE, array$I == null ? (array$I = DataBufferUtils.class$("[I")) : array$I}, new Object[]{dataArray, new Integer(size), offsets});
    }

    public static final DataBuffer createDataBufferFloat(float[] dataArray, int size) {
        return DataBufferUtils.constructDataBuffer(4, new Class[]{array$F == null ? (array$F = DataBufferUtils.class$("[F")) : array$F, Integer.TYPE}, new Object[]{dataArray, new Integer(size)});
    }

    public static final DataBuffer createDataBufferFloat(float[] dataArray, int size, int offset) {
        return DataBufferUtils.constructDataBuffer(4, new Class[]{array$F == null ? (array$F = DataBufferUtils.class$("[F")) : array$F, Integer.TYPE, Integer.TYPE}, new Object[]{dataArray, new Integer(size), new Integer(offset)});
    }

    public static final DataBuffer createDataBufferFloat(int size) {
        return DataBufferUtils.constructDataBuffer(4, new Class[]{Integer.TYPE}, new Object[]{new Integer(size)});
    }

    public static final DataBuffer createDataBufferFloat(int size, int numBanks) {
        return DataBufferUtils.constructDataBuffer(4, new Class[]{Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(size), new Integer(numBanks)});
    }

    public static final float[][] getBankDataFloat(DataBuffer dataBuffer) {
        return (float[][])DataBufferUtils.invokeDataBufferMethod(dataBuffer, "getBankData", null, null);
    }

    public static final float[] getDataFloat(DataBuffer dataBuffer) {
        return (float[])DataBufferUtils.invokeDataBufferMethod(dataBuffer, "getData", null, null);
    }

    public static final float[] getDataFloat(DataBuffer dataBuffer, int bank) {
        return (float[])DataBufferUtils.invokeDataBufferMethod(dataBuffer, "getData", new Class[]{Integer.TYPE}, new Object[]{new Integer(bank)});
    }

    public static final DataBuffer createDataBufferDouble(double[][] dataArray, int size) {
        return DataBufferUtils.constructDataBuffer(5, new Class[]{array$$D == null ? (array$$D = DataBufferUtils.class$("[[D")) : array$$D, Integer.TYPE}, new Object[]{dataArray, new Integer(size)});
    }

    public static final DataBuffer createDataBufferDouble(double[][] dataArray, int size, int[] offsets) {
        return DataBufferUtils.constructDataBuffer(5, new Class[]{array$$D == null ? (array$$D = DataBufferUtils.class$("[[D")) : array$$D, Integer.TYPE, array$I == null ? (array$I = DataBufferUtils.class$("[I")) : array$I}, new Object[]{dataArray, new Integer(size), offsets});
    }

    public static final DataBuffer createDataBufferDouble(double[] dataArray, int size) {
        return DataBufferUtils.constructDataBuffer(5, new Class[]{array$D == null ? (array$D = DataBufferUtils.class$("[D")) : array$D, Integer.TYPE}, new Object[]{dataArray, new Integer(size)});
    }

    public static final DataBuffer createDataBufferDouble(double[] dataArray, int size, int offset) {
        return DataBufferUtils.constructDataBuffer(5, new Class[]{array$D == null ? (array$D = DataBufferUtils.class$("[D")) : array$D, Integer.TYPE, Integer.TYPE}, new Object[]{dataArray, new Integer(size), new Integer(offset)});
    }

    public static final DataBuffer createDataBufferDouble(int size) {
        return DataBufferUtils.constructDataBuffer(5, new Class[]{Integer.TYPE}, new Object[]{new Integer(size)});
    }

    public static final DataBuffer createDataBufferDouble(int size, int numBanks) {
        return DataBufferUtils.constructDataBuffer(5, new Class[]{Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(size), new Integer(numBanks)});
    }

    public static final double[][] getBankDataDouble(DataBuffer dataBuffer) {
        return (double[][])DataBufferUtils.invokeDataBufferMethod(dataBuffer, "getBankData", null, null);
    }

    public static final double[] getDataDouble(DataBuffer dataBuffer) {
        return (double[])DataBufferUtils.invokeDataBufferMethod(dataBuffer, "getData", null, null);
    }

    public static final double[] getDataDouble(DataBuffer dataBuffer, int bank) {
        return (double[])DataBufferUtils.invokeDataBufferMethod(dataBuffer, "getData", new Class[]{Integer.TYPE}, new Object[]{new Integer(bank)});
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        floatClass = DataBufferUtils.getDataBufferClass(4);
        doubleClass = DataBufferUtils.getDataBufferClass(5);
    }
}

