/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.apache.tools.ant.BuildException;

public final class StringUtils {
    private static final long KILOBYTE = 1024L;
    private static final long MEGABYTE = 0x100000L;
    private static final long GIGABYTE = 0x40000000L;
    private static final long TERABYTE = 0x10000000000L;
    private static final long PETABYTE = 0x4000000000000L;
    @Deprecated
    public static final String LINE_SEP = System.lineSeparator();

    private StringUtils() {
    }

    public static Vector<String> lineSplit(String data) {
        return StringUtils.split(data, 10);
    }

    public static Vector<String> split(String data, int ch) {
        Vector<String> elems = new Vector<String>();
        int pos = -1;
        int i = 0;
        while ((pos = data.indexOf(ch, i)) != -1) {
            String elem = data.substring(i, pos);
            elems.addElement(elem);
            i = pos + 1;
        }
        elems.addElement(data.substring(i));
        return elems;
    }

    @Deprecated
    public static String replace(String data, String from, String to) {
        return data.replace(from, to);
    }

    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        pw.close();
        return sw.toString();
    }

    public static boolean endsWith(StringBuffer buffer, String suffix) {
        if (suffix.length() > buffer.length()) {
            return false;
        }
        int bufferIndex = buffer.length() - 1;
        for (int endIndex = suffix.length() - 1; endIndex >= 0; --endIndex) {
            if (buffer.charAt(bufferIndex) != suffix.charAt(endIndex)) {
                return false;
            }
            --bufferIndex;
        }
        return true;
    }

    public static String resolveBackSlash(String input) {
        StringBuilder b = new StringBuilder();
        boolean backSlashSeen = false;
        for (char c : input.toCharArray()) {
            if (!backSlashSeen) {
                if (c == '\\') {
                    backSlashSeen = true;
                    continue;
                }
                b.append(c);
                continue;
            }
            switch (c) {
                case '\\': {
                    b.append('\\');
                    break;
                }
                case 'n': {
                    b.append('\n');
                    break;
                }
                case 'r': {
                    b.append('\r');
                    break;
                }
                case 't': {
                    b.append('\t');
                    break;
                }
                case 'f': {
                    b.append('\f');
                    break;
                }
                case 's': {
                    b.append(" \t\n\r\f");
                    break;
                }
                default: {
                    b.append(c);
                }
            }
            backSlashSeen = false;
        }
        return b.toString();
    }

    public static long parseHumanSizes(String humanSize) throws Exception {
        long factor = 1L;
        char s = humanSize.charAt(0);
        switch (s) {
            case '+': {
                humanSize = humanSize.substring(1);
                break;
            }
            case '-': {
                factor = -1L;
                humanSize = humanSize.substring(1);
                break;
            }
        }
        char c = humanSize.charAt(humanSize.length() - 1);
        if (!Character.isDigit(c)) {
            int trim = 1;
            switch (c) {
                case 'K': {
                    factor *= 1024L;
                    break;
                }
                case 'M': {
                    factor *= 0x100000L;
                    break;
                }
                case 'G': {
                    factor *= 0x40000000L;
                    break;
                }
                case 'T': {
                    factor *= 0x10000000000L;
                    break;
                }
                case 'P': {
                    factor *= 0x4000000000000L;
                    break;
                }
                default: {
                    trim = 0;
                }
            }
            humanSize = humanSize.substring(0, humanSize.length() - trim);
        }
        try {
            return factor * Long.parseLong(humanSize);
        }
        catch (NumberFormatException e) {
            throw new BuildException("Failed to parse \"" + humanSize + "\"", e);
        }
    }

    public static String removeSuffix(String string, String suffix) {
        if (string.endsWith(suffix)) {
            return string.substring(0, string.length() - suffix.length());
        }
        return string;
    }

    public static String removePrefix(String string, String prefix) {
        if (string.startsWith(prefix)) {
            return string.substring(prefix.length());
        }
        return string;
    }

    public static String join(Collection<?> collection, CharSequence separator) {
        if (collection == null) {
            return "";
        }
        return collection.stream().map(String::valueOf).collect(StringUtils.joining(separator));
    }

    public static String join(Object[] array, CharSequence separator) {
        if (array == null) {
            return "";
        }
        return StringUtils.join(Arrays.asList(array), separator);
    }

    private static Collector<CharSequence, ?, String> joining(CharSequence separator) {
        return separator == null ? Collectors.joining() : Collectors.joining(separator);
    }

    public static String trimToNull(String inputString) {
        if (inputString == null) {
            return null;
        }
        String tmpString = inputString.trim();
        return tmpString.isEmpty() ? null : tmpString;
    }
}

