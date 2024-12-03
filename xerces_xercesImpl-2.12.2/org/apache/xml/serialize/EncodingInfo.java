/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serialize;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;
import org.apache.xerces.util.EncodingMap;

public class EncodingInfo {
    private Object[] fArgsForMethod = null;
    String ianaName;
    String javaName;
    int lastPrintable;
    Object fCharsetEncoder = null;
    Object fCharToByteConverter = null;
    boolean fHaveTriedCToB = false;
    boolean fHaveTriedCharsetEncoder = false;

    public EncodingInfo(String string, String string2, int n) {
        this.ianaName = string;
        this.javaName = EncodingMap.getIANA2JavaMapping(string);
        this.lastPrintable = n;
    }

    public String getIANAName() {
        return this.ianaName;
    }

    public Writer getWriter(OutputStream outputStream) throws UnsupportedEncodingException {
        if (this.javaName != null) {
            return new OutputStreamWriter(outputStream, this.javaName);
        }
        this.javaName = EncodingMap.getIANA2JavaMapping(this.ianaName);
        if (this.javaName == null) {
            return new OutputStreamWriter(outputStream, "UTF8");
        }
        return new OutputStreamWriter(outputStream, this.javaName);
    }

    public boolean isPrintable(char c) {
        if (c <= this.lastPrintable) {
            return true;
        }
        return this.isPrintable0(c);
    }

    private boolean isPrintable0(char c) {
        if (this.fCharsetEncoder == null && CharsetMethods.fgNIOCharsetAvailable && !this.fHaveTriedCharsetEncoder) {
            if (this.fArgsForMethod == null) {
                this.fArgsForMethod = new Object[1];
            }
            try {
                this.fArgsForMethod[0] = this.javaName;
                Object object = CharsetMethods.fgCharsetForNameMethod.invoke(null, this.fArgsForMethod);
                if (((Boolean)CharsetMethods.fgCharsetCanEncodeMethod.invoke(object, (Object[])null)).booleanValue()) {
                    this.fCharsetEncoder = CharsetMethods.fgCharsetNewEncoderMethod.invoke(object, (Object[])null);
                } else {
                    this.fHaveTriedCharsetEncoder = true;
                }
            }
            catch (Exception exception) {
                this.fHaveTriedCharsetEncoder = true;
            }
        }
        if (this.fCharsetEncoder != null) {
            try {
                this.fArgsForMethod[0] = new Character(c);
                return (Boolean)CharsetMethods.fgCharsetEncoderCanEncodeMethod.invoke(this.fCharsetEncoder, this.fArgsForMethod);
            }
            catch (Exception exception) {
                this.fCharsetEncoder = null;
                this.fHaveTriedCharsetEncoder = false;
            }
        }
        if (this.fCharToByteConverter == null) {
            if (this.fHaveTriedCToB || !CharToByteConverterMethods.fgConvertersAvailable) {
                return false;
            }
            if (this.fArgsForMethod == null) {
                this.fArgsForMethod = new Object[1];
            }
            try {
                this.fArgsForMethod[0] = this.javaName;
                this.fCharToByteConverter = CharToByteConverterMethods.fgGetConverterMethod.invoke(null, this.fArgsForMethod);
            }
            catch (Exception exception) {
                this.fHaveTriedCToB = true;
                return false;
            }
        }
        try {
            this.fArgsForMethod[0] = new Character(c);
            return (Boolean)CharToByteConverterMethods.fgCanConvertMethod.invoke(this.fCharToByteConverter, this.fArgsForMethod);
        }
        catch (Exception exception) {
            this.fCharToByteConverter = null;
            this.fHaveTriedCToB = false;
            return false;
        }
    }

    public static void testJavaEncodingName(String string) throws UnsupportedEncodingException {
        byte[] byArray = new byte[]{118, 97, 108, 105, 100};
        String string2 = new String(byArray, string);
    }

    static class CharToByteConverterMethods {
        private static Method fgGetConverterMethod = null;
        private static Method fgCanConvertMethod = null;
        private static boolean fgConvertersAvailable = false;

        private CharToByteConverterMethods() {
        }

        static {
            try {
                Class<?> clazz = Class.forName("sun.io.CharToByteConverter");
                fgGetConverterMethod = clazz.getMethod("getConverter", String.class);
                fgCanConvertMethod = clazz.getMethod("canConvert", Character.TYPE);
                fgConvertersAvailable = true;
            }
            catch (Exception exception) {
                fgGetConverterMethod = null;
                fgCanConvertMethod = null;
                fgConvertersAvailable = false;
            }
        }
    }

    static class CharsetMethods {
        private static Method fgCharsetForNameMethod = null;
        private static Method fgCharsetCanEncodeMethod = null;
        private static Method fgCharsetNewEncoderMethod = null;
        private static Method fgCharsetEncoderCanEncodeMethod = null;
        private static boolean fgNIOCharsetAvailable = false;

        private CharsetMethods() {
        }

        static {
            try {
                Class<?> clazz = Class.forName("java.nio.charset.Charset");
                Class<?> clazz2 = Class.forName("java.nio.charset.CharsetEncoder");
                fgCharsetForNameMethod = clazz.getMethod("forName", String.class);
                fgCharsetCanEncodeMethod = clazz.getMethod("canEncode", new Class[0]);
                fgCharsetNewEncoderMethod = clazz.getMethod("newEncoder", new Class[0]);
                fgCharsetEncoderCanEncodeMethod = clazz2.getMethod("canEncode", Character.TYPE);
                fgNIOCharsetAvailable = true;
            }
            catch (Exception exception) {
                fgCharsetForNameMethod = null;
                fgCharsetCanEncodeMethod = null;
                fgCharsetEncoderCanEncodeMethod = null;
                fgCharsetNewEncoderMethod = null;
                fgNIOCharsetAvailable = false;
            }
        }
    }
}

