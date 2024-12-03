/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.node.stax;

import com.atlassian.dbexporter.node.stax.XmlFactoryException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

final class StaxUtils {
    private static final String WOODSTOX_INPUT_FACTORY = "com.ctc.wstx.stax.WstxInputFactory";
    private static final String DEFAULT_INPUT_FACTORY = "com.sun.xml.internal.stream.XMLInputFactoryImpl";
    private static final String WOODSTOX_OUTPUT_FACTORY = "com.ctc.wstx.stax.WstxOutputFactory";
    private static final String DEFAULT_OUTPUT_FACTORY = "com.sun.xml.internal.stream.XMLOutputFactoryImpl";
    private static final char BACKSLASH = '\\';
    private static final Map<Character, String> CHAR_TO_UNICODE;

    StaxUtils() {
    }

    public static String unicodeEncode(String string) {
        if (string == null) {
            return null;
        }
        StringBuilder copy = new StringBuilder();
        copy.setLength(0);
        boolean copied = false;
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            String s = CHAR_TO_UNICODE.get(Character.valueOf(c));
            if (s != null && !copied) {
                copy.append(string.substring(0, i));
                copied = true;
            }
            if (!copied) continue;
            if (s == null) {
                copy.append(c);
                continue;
            }
            copy.append(s);
        }
        return copied ? copy.toString() : string;
    }

    public static String unicodeDecode(String string) {
        if (string == null) {
            return null;
        }
        StringBuilder copy = new StringBuilder();
        copy.setLength(0);
        boolean copied = false;
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c == '\\') {
                if (!copied) {
                    copy.append(string.substring(0, i));
                    copied = true;
                }
                if (string.charAt(++i) == '\\') {
                    copy.append('\\');
                    continue;
                }
                String value = string.substring(++i, i + 4);
                copy.append((char)Integer.parseInt(value, 16));
                i += 3;
                continue;
            }
            if (!copied) continue;
            copy.append(c);
        }
        return copied ? copy.toString() : string;
    }

    public static DateFormat newDateFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf;
    }

    static XMLInputFactory newXmlInputFactory() {
        return StaxUtils.newInstance(XMLInputFactory.class, WOODSTOX_INPUT_FACTORY, DEFAULT_INPUT_FACTORY);
    }

    static XMLOutputFactory newXmlOutputFactory() {
        return StaxUtils.newInstance(XMLOutputFactory.class, WOODSTOX_OUTPUT_FACTORY, DEFAULT_OUTPUT_FACTORY);
    }

    private static <T> T newInstance(Class<T> type, String ... classNames) {
        ArrayList<XmlFactoryException> exceptions = new ArrayList<XmlFactoryException>();
        for (String className : classNames) {
            try {
                return StaxUtils.newInstance(type, className);
            }
            catch (XmlFactoryException e) {
                exceptions.add(e);
            }
        }
        throw new XmlFactoryException("Could not instantiate any of " + Arrays.toString(classNames), exceptions.toArray(new Throwable[exceptions.size()]));
    }

    private static <T> T newInstance(Class<T> type, String className) {
        try {
            return type.cast(StaxUtils.class.getClassLoader().loadClass(className).newInstance());
        }
        catch (InstantiationException e) {
            throw new XmlFactoryException("Could not instantiate " + className, new Throwable[]{e});
        }
        catch (IllegalAccessException e) {
            throw new XmlFactoryException("Could not instantiate " + className, new Throwable[]{e});
        }
        catch (ClassNotFoundException e) {
            throw new XmlFactoryException("Could not find class " + className, new Throwable[]{e});
        }
    }

    static {
        String escapeString = "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\u000b\f\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f\ufffe\uffff";
        CHAR_TO_UNICODE = new HashMap<Character, String>();
        for (int i = 0; i < "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\u000b\f\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f\ufffe\uffff".length(); ++i) {
            char c = "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\u000b\f\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f\ufffe\uffff".charAt(i);
            CHAR_TO_UNICODE.put(Character.valueOf(c), String.format("\\u%04X", c));
        }
        CHAR_TO_UNICODE.put(Character.valueOf('\\'), "\\\\");
    }
}

