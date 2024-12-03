/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import org.xhtmlrenderer.util.XRLog;

public class GeneralUtil {
    public static final DecimalFormat PADDED_HASH_FORMAT = new DecimalFormat("0000000000");

    public static InputStream openStreamFromClasspath(Object obj, String resource) {
        InputStream readStream = null;
        try {
            URL stream;
            ClassLoader loader = obj.getClass().getClassLoader();
            readStream = loader == null ? ClassLoader.getSystemResourceAsStream(resource) : loader.getResourceAsStream(resource);
            if (readStream == null && (stream = resource.getClass().getResource(resource)) != null) {
                readStream = stream.openStream();
            }
        }
        catch (Exception ex) {
            XRLog.exception("Could not open stream from CLASSPATH: " + resource, ex);
        }
        return readStream;
    }

    public static URL getURLFromClasspath(Object obj, String resource) {
        URL url = null;
        try {
            ClassLoader loader = obj.getClass().getClassLoader();
            url = loader == null ? ClassLoader.getSystemResource(resource) : loader.getResource(resource);
            if (url == null) {
                url = resource.getClass().getResource(resource);
            }
        }
        catch (Exception ex) {
            XRLog.exception("Could not get URL from CLASSPATH: " + resource, ex);
        }
        return url;
    }

    public static void dumpShortException(Exception ex) {
        String s = ex.getMessage();
        if (s == null || s.trim().equals("null")) {
            s = "{no ex. message}";
        }
        System.out.println(s + ", " + ex.getClass());
        StackTraceElement[] stes = ex.getStackTrace();
        for (int i = 0; i < stes.length && i < 5; ++i) {
            StackTraceElement ste = stes[i];
            System.out.println("  " + ste.getClassName() + "." + ste.getMethodName() + "(ln " + ste.getLineNumber() + ")");
        }
    }

    public static String trackBack(int cnt) {
        Exception ex = new Exception();
        StringBuffer sb = new StringBuffer();
        ArrayList<String> list = new ArrayList<String>(cnt);
        StackTraceElement[] stes = ex.getStackTrace();
        if (cnt >= stes.length) {
            cnt = stes.length - 1;
        }
        for (int i = cnt; i >= 1; --i) {
            StackTraceElement ste = stes[i];
            sb.append(GeneralUtil.classNameOnly(ste.getClassName()));
            sb.append(".");
            sb.append(ste.getMethodName());
            sb.append("(ln ").append(ste.getLineNumber()).append(")");
            list.add(sb.toString());
            sb = new StringBuffer();
        }
        Iterator iter = list.iterator();
        StringBuffer padding = new StringBuffer("");
        StringBuffer trackback = new StringBuffer();
        while (iter.hasNext()) {
            String s = (String)iter.next();
            trackback.append(padding).append(s).append("\n");
            padding.append("   ");
        }
        return trackback.toString();
    }

    public static String classNameOnly(Object o) {
        String s = "[null object ref]";
        if (o != null) {
            s = GeneralUtil.classNameOnly(o.getClass().getName());
        }
        return s;
    }

    public static String classNameOnly(String cname) {
        String s = "[null object ref]";
        if (cname != null) {
            s = cname.substring(cname.lastIndexOf(46) + 1);
        }
        return s;
    }

    public static String paddedHashCode(Object o) {
        String s = "0000000000";
        if (o != null) {
            s = PADDED_HASH_FORMAT.format(o.hashCode());
        }
        return s;
    }

    public static boolean isMacOSX() {
        try {
            if (System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
                return true;
            }
        }
        catch (SecurityException e) {
            System.err.println(e.getLocalizedMessage());
        }
        return false;
    }

    public static StringBuffer htmlEscapeSpace(String uri) {
        StringBuffer sbURI = new StringBuffer((int)((double)uri.length() * 1.5));
        for (int i = 0; i < uri.length(); ++i) {
            char ch = uri.charAt(i);
            if (ch == ' ') {
                sbURI.append("%20");
                continue;
            }
            if (ch == '\\') {
                sbURI.append('/');
                continue;
            }
            sbURI.append(ch);
        }
        return sbURI;
    }

    public static String inputStreamToString(InputStream is) throws IOException {
        int n;
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        StringWriter sw = new StringWriter();
        char[] c = new char[1024];
        while ((n = br.read(c, 0, c.length)) >= 0) {
            sw.write(c, 0, n);
        }
        isr.close();
        return sw.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void writeStringToFile(String content, String encoding, String fileName) throws IOException {
        File f = new File(fileName);
        FileOutputStream fos = new FileOutputStream(f);
        try {
            OutputStreamWriter osw = new OutputStreamWriter((OutputStream)fos, encoding);
            BufferedWriter bw = new BufferedWriter(osw);
            PrintWriter pw = new PrintWriter(bw);
            try {
                pw.print(content);
                pw.flush();
                bw.flush();
            }
            finally {
                try {
                    pw.close();
                }
                catch (Exception exception) {}
            }
        }
        catch (IOException e) {
            throw e;
        }
        finally {
            try {
                fos.close();
            }
            catch (Exception exception) {}
        }
        System.out.println("Wrote file: " + f.getAbsolutePath());
    }

    public static int parseIntRelaxed(String s) {
        if (s == null || s.length() == 0 || s.trim().length() == 0) {
            return 0;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                buffer.append(c);
                continue;
            }
            if (buffer.length() > 0) break;
        }
        if (buffer.length() == 0) {
            return 0;
        }
        try {
            return Integer.parseInt(buffer.toString());
        }
        catch (NumberFormatException exception) {
            return Integer.MAX_VALUE;
        }
    }

    public static String escapeHTML(String s) {
        if (s == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        int n = s.length();
        block7: for (int i = 0; i < n; ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '<': {
                    sb.append("&lt;");
                    continue block7;
                }
                case '>': {
                    sb.append("&gt;");
                    continue block7;
                }
                case '&': {
                    sb.append("&amp;");
                    continue block7;
                }
                case '\"': {
                    sb.append("&quot;");
                    continue block7;
                }
                case ' ': {
                    sb.append("&nbsp;");
                    continue block7;
                }
                default: {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }
}

