/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.lang.reflect.Method;

class Base64Helper {
    private static Method decodeMethod;
    private static Method encodeMethod;
    private static Object decoderObj;
    private static Object encoderObj;

    Base64Helper() {
    }

    static String printBase64Binary(byte[] binaryArray) {
        try {
            return (String)encodeMethod.invoke(encoderObj, new Object[]{binaryArray});
        }
        catch (Throwable e) {
            throw new UnsupportedOperationException("Failed to use platform's base64 encode method");
        }
    }

    static byte[] parseBase64Binary(String base64input) {
        try {
            return (byte[])decodeMethod.invoke(decoderObj, base64input);
        }
        catch (Throwable e) {
            throw new UnsupportedOperationException("Failed to use platform's base64 decode method");
        }
    }

    static {
        try {
            Class<?> javaUtilBase64Class = Class.forName("java.util.Base64");
            Method getDecoderMethod = javaUtilBase64Class.getMethod("getDecoder", new Class[0]);
            decoderObj = getDecoderMethod.invoke(null, new Object[0]);
            decodeMethod = decoderObj.getClass().getMethod("decode", String.class);
            Method getEncoderMethod = javaUtilBase64Class.getMethod("getEncoder", new Class[0]);
            encoderObj = getEncoderMethod.invoke(null, new Object[0]);
            encodeMethod = encoderObj.getClass().getMethod("encodeToString", byte[].class);
        }
        catch (Throwable e) {
            decodeMethod = null;
            encodeMethod = null;
        }
        if (encodeMethod == null) {
            decoderObj = null;
            encoderObj = null;
            try {
                Class<?> javaxXmlBindDatatypeConverterClass = Class.forName("javax.xml.bind.DatatypeConverter");
                decodeMethod = javaxXmlBindDatatypeConverterClass.getMethod("parseBase64Binary", String.class);
                encodeMethod = javaxXmlBindDatatypeConverterClass.getMethod("printBase64Binary", byte[].class);
            }
            catch (Throwable e) {
                decodeMethod = null;
                encodeMethod = null;
            }
        }
    }
}

