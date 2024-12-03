/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public final class PropertiesUtils {
    public static int getIntProperty(Properties properties, String string, int n) throws NumberFormatException {
        String string2 = properties.getProperty(string);
        return string2 != null ? Integer.parseInt(string2) : n;
    }

    public static Properties fromString(String string, String string2) throws UnsupportedEncodingException {
        try {
            Properties properties = new Properties();
            if (string != null) {
                byte[] byArray = string.getBytes(string2);
                properties.load(new ByteArrayInputStream(byArray));
            }
            return properties;
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw unsupportedEncodingException;
        }
        catch (IOException iOException) {
            throw new Error("Huh? An IOException while working with byte array streams?!", iOException);
        }
    }

    public static Properties fromString(String string) {
        try {
            return PropertiesUtils.fromString(string, "ISO-8859-1");
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new Error("Huh? An ISO-8859-1 is an unsupported encoding?!", unsupportedEncodingException);
        }
    }

    public static String toString(Properties properties, String string, String string2) throws UnsupportedEncodingException {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            properties.store(byteArrayOutputStream, string);
            byteArrayOutputStream.flush();
            return new String(byteArrayOutputStream.toByteArray(), string2);
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw unsupportedEncodingException;
        }
        catch (IOException iOException) {
            throw new Error("Huh? An IOException while working with byte array streams?!", iOException);
        }
    }

    public static String toString(Properties properties, String string) {
        try {
            return PropertiesUtils.toString(properties, string, "ISO-8859-1");
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new Error("Huh? An ISO-8859-1 is an unsupported encoding?!", unsupportedEncodingException);
        }
    }

    private PropertiesUtils() {
    }
}

