/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.internal;

import java.awt.color.ICC_Profile;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Debug {
    private static final Logger LOGGER = Logger.getLogger(Debug.class.getName());
    private static final String NEWLINE = "\r\n";
    private static long counter;

    public static void debug(String message) {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(message);
        }
    }

    public static void debug() {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(NEWLINE);
        }
    }

    private static String getDebug(String message, int[] v) {
        StringBuilder result = new StringBuilder();
        if (v == null) {
            result.append(message + " (" + null + ")" + NEWLINE);
        } else {
            result.append(message + " (" + v.length + ")" + NEWLINE);
            for (int element : v) {
                result.append("\t" + element + NEWLINE);
            }
            result.append(NEWLINE);
        }
        return result.toString();
    }

    private static String getDebug(String message, byte[] v) {
        int max = 250;
        return Debug.getDebug(message, v, 250);
    }

    private static String getDebug(String message, byte[] v, int max) {
        StringBuilder result = new StringBuilder();
        if (v == null) {
            result.append(message + " (" + null + ")" + NEWLINE);
        } else {
            result.append(message + " (" + v.length + ")" + NEWLINE);
            for (int i = 0; i < max && i < v.length; ++i) {
                int b = 0xFF & v[i];
                char c = b == 0 || b == 10 || b == 11 || b == 13 ? (char)' ' : (char)((char)b);
                result.append("\t" + i + ": " + b + " (" + c + ", 0x" + Integer.toHexString(b) + ")" + NEWLINE);
            }
            if (v.length > max) {
                result.append("\t...\r\n");
            }
            result.append(NEWLINE);
        }
        return result.toString();
    }

    private static String getDebug(String message, char[] v) {
        StringBuilder result = new StringBuilder();
        if (v == null) {
            result.append(message + " (" + null + ")" + NEWLINE);
        } else {
            result.append(message + " (" + v.length + ")" + NEWLINE);
            for (char element : v) {
                result.append("\t" + element + " (" + (0xFF & element) + ")" + NEWLINE);
            }
            result.append(NEWLINE);
        }
        return result.toString();
    }

    private static void debug(String message, Map<?, ?> map) {
        Debug.debug(Debug.getDebug(message, map));
    }

    private static String getDebug(String message, Map<?, ?> map) {
        StringBuilder result = new StringBuilder();
        if (map == null) {
            return message + " map: " + null;
        }
        ArrayList keys = new ArrayList(map.keySet());
        result.append(message + " map: " + keys.size() + NEWLINE);
        for (int i = 0; i < keys.size(); ++i) {
            Object key = keys.get(i);
            Object value = map.get(key);
            result.append("\t" + i + ": '" + key + "' -> '" + value + "'" + NEWLINE);
        }
        result.append(NEWLINE);
        return result.toString();
    }

    private static String byteQuadToString(int bytequad) {
        byte b1 = (byte)(bytequad >> 24 & 0xFF);
        byte b2 = (byte)(bytequad >> 16 & 0xFF);
        byte b3 = (byte)(bytequad >> 8 & 0xFF);
        byte b4 = (byte)(bytequad >> 0 & 0xFF);
        char c1 = (char)b1;
        char c2 = (char)b2;
        char c3 = (char)b3;
        char c4 = (char)b4;
        StringBuilder buffer = new StringBuilder(31);
        buffer.append(new String(new char[]{c1, c2, c3, c4}));
        buffer.append(" bytequad: ");
        buffer.append(bytequad);
        buffer.append(" b1: ");
        buffer.append(b1);
        buffer.append(" b2: ");
        buffer.append(b2);
        buffer.append(" b3: ");
        buffer.append(b3);
        buffer.append(" b4: ");
        buffer.append(b4);
        return buffer.toString();
    }

    public static void debug(String message, Object value) {
        if (value == null) {
            Debug.debug(message, "null");
        } else if (value instanceof char[]) {
            Debug.debug(message, (char[])value);
        } else if (value instanceof byte[]) {
            Debug.debug(message, (byte[])value);
        } else if (value instanceof int[]) {
            Debug.debug(message, (int[])value);
        } else if (value instanceof String) {
            Debug.debug(message, (String)value);
        } else if (value instanceof List) {
            Debug.debug(message, (List)value);
        } else if (value instanceof Map) {
            Debug.debug(message, (Map)value);
        } else if (value instanceof ICC_Profile) {
            Debug.debug(message, (ICC_Profile)value);
        } else if (value instanceof File) {
            Debug.debug(message, (File)value);
        } else if (value instanceof Date) {
            Debug.debug(message, (Date)value);
        } else if (value instanceof Calendar) {
            Debug.debug(message, (Calendar)value);
        } else {
            Debug.debug(message, value.toString());
        }
    }

    private static void debug(String message, byte[] v) {
        Debug.debug(Debug.getDebug(message, v));
    }

    private static void debug(String message, char[] v) {
        Debug.debug(Debug.getDebug(message, v));
    }

    private static void debug(String message, Calendar value) {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);
        Debug.debug(message, value == null ? "null" : df.format(value.getTime()));
    }

    private static void debug(String message, Date value) {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);
        Debug.debug(message, value == null ? "null" : df.format(value));
    }

    private static void debug(String message, File file) {
        Debug.debug(message + ": " + (file == null ? "null" : file.getPath()));
    }

    private static void debug(String message, ICC_Profile value) {
        Debug.debug("ICC_Profile " + message + ": " + (value == null ? "null" : value.toString()));
        if (value != null) {
            Debug.debug("\t getProfileClass: " + Debug.byteQuadToString(value.getProfileClass()));
            Debug.debug("\t getPCSType: " + Debug.byteQuadToString(value.getPCSType()));
            Debug.debug("\t getColorSpaceType() : " + Debug.byteQuadToString(value.getColorSpaceType()));
        }
    }

    private static void debug(String message, int[] v) {
        Debug.debug(Debug.getDebug(message, v));
    }

    private static void debug(String message, List<?> v) {
        String suffix = " [" + counter++ + "]";
        Debug.debug(message + " (" + v.size() + ")" + suffix);
        for (Object aV : v) {
            Debug.debug("\t" + aV.toString() + suffix);
        }
        Debug.debug();
    }

    private static void debug(String message, String value) {
        Debug.debug(message + " " + value);
    }

    public static void debug(Throwable e) {
        Debug.debug(Debug.getDebug(e));
    }

    public static void debug(Throwable e, int value) {
        Debug.debug(Debug.getDebug(e, value));
    }

    private static String getDebug(Throwable e) {
        return Debug.getDebug(e, -1);
    }

    private static String getDebug(Throwable e, int max) {
        StringBuilder result = new StringBuilder(35);
        SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss:SSS", Locale.ENGLISH);
        String datetime = timestamp.format(new Date()).toLowerCase();
        result.append(NEWLINE);
        result.append("Throwable: " + (e == null ? "" : "(" + e.getClass().getName() + ")") + ":" + datetime + NEWLINE);
        result.append("Throwable: " + (e == null ? "null" : e.getLocalizedMessage()) + NEWLINE);
        result.append(NEWLINE);
        result.append(Debug.getStackTrace(e, max));
        result.append("Caught here:\r\n");
        result.append(Debug.getStackTrace(new Exception(), max, 1));
        result.append(NEWLINE);
        return result.toString();
    }

    private static String getStackTrace(Throwable e, int limit) {
        return Debug.getStackTrace(e, limit, 0);
    }

    private static String getStackTrace(Throwable e, int limit, int skip) {
        StringBuilder result = new StringBuilder();
        if (e != null) {
            StackTraceElement[] stes = e.getStackTrace();
            if (stes != null) {
                for (int i = skip; i < stes.length && (limit < 0 || i < limit); ++i) {
                    StackTraceElement ste = stes[i];
                    result.append("\tat " + ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")" + NEWLINE);
                }
                if (limit >= 0 && stes.length > limit) {
                    result.append("\t...\r\n");
                }
            }
            result.append(NEWLINE);
        }
        return result.toString();
    }

    private Debug() {
    }
}

