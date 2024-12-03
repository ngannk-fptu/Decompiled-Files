/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axiom.om.util;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.util.LogOutputStream;
import org.apache.commons.logging.Log;

public class CommonUtils {
    private CommonUtils() {
    }

    public static final String replace(String name, String oldT, String newT) {
        if (name == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer(name.length() * 2);
        int len = oldT.length();
        try {
            int start = 0;
            int i = name.indexOf(oldT, start);
            while (i >= 0) {
                sb.append(name.substring(start, i));
                sb.append(newT);
                start = i + len;
                i = name.indexOf(oldT, start);
            }
            if (start < name.length()) {
                sb.append(name.substring(start));
            }
        }
        catch (NullPointerException e) {
            // empty catch block
        }
        return new String(sb);
    }

    public static String callStackToString() {
        return CommonUtils.stackToString(new RuntimeException());
    }

    public static String stackToString(Throwable e) {
        StringWriter sw = new StringWriter();
        BufferedWriter bw = new BufferedWriter(sw);
        PrintWriter pw = new PrintWriter(bw);
        e.printStackTrace(pw);
        pw.close();
        String text = sw.getBuffer().toString();
        text = text.substring(text.indexOf("at"));
        text = CommonUtils.replace(text, "at ", "DEBUG_FRAME = ");
        return text;
    }

    public static long logDebug(OMElement om, Log log) {
        return CommonUtils.logDebug(om, log, Integer.MAX_VALUE);
    }

    public static long logDebug(OMElement om, Log log, int limit) {
        OMOutputFormat format = new OMOutputFormat();
        format.setDoOptimize(true);
        format.setIgnoreXMLDeclaration(true);
        return CommonUtils.logDebug(om, log, limit, format);
    }

    public static long logDebug(OMElement om, Log log, int limit, OMOutputFormat format) {
        LogOutputStream logStream = new LogOutputStream(log, limit);
        try {
            om.serialize(logStream, format);
            logStream.flush();
            logStream.close();
        }
        catch (Throwable t) {
            log.debug((Object)t);
            log.error((Object)t);
        }
        return logStream.getLength();
    }

    public static boolean isTextualPart(String contentType) {
        String ct = contentType.trim();
        return ct.startsWith("text/") || ct.startsWith("application/soap") || ct.startsWith("application/xml") || ct.indexOf("charset") != -1;
    }
}

