/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tika.exception.TikaException;

public class ExceptionUtils {
    private static final Pattern MSG_PATTERN = Pattern.compile(":[^\r\n]+");

    public static String getFilteredStackTrace(Throwable t) {
        Throwable cause = t;
        if (t.getClass().equals(TikaException.class) && t.getCause() != null) {
            cause = t.getCause();
        }
        return ExceptionUtils.getStackTrace(cause);
    }

    public static String getStackTrace(Throwable t) {
        StringWriter result = new StringWriter();
        PrintWriter writer = new PrintWriter(result);
        t.printStackTrace(writer);
        try {
            writer.flush();
            ((Writer)result).flush();
            writer.close();
            ((Writer)result).close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return ((Object)result).toString();
    }

    public static String trimMessage(String trace) {
        Matcher msgMatcher = MSG_PATTERN.matcher(trace);
        if (msgMatcher.find()) {
            return msgMatcher.replaceFirst("");
        }
        return trace;
    }
}

