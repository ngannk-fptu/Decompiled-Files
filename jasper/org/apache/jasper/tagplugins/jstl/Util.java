/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.WriteListener
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.JspTagException
 *  javax.servlet.jsp.PageContext
 */
package org.apache.jasper.tagplugins.jstl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import org.apache.jasper.Constants;
import org.apache.jasper.compiler.Localizer;

public class Util {
    private static final String VALID_SCHEME_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+.-";
    public static final String DEFAULT_ENCODING = "ISO-8859-1";
    private static final int HIGHEST_SPECIAL = 62;
    private static final char[][] specialCharactersRepresentation = new char[63][];

    public static int getScope(String scope) {
        int ret = 1;
        if ("request".equalsIgnoreCase(scope)) {
            ret = 2;
        } else if ("session".equalsIgnoreCase(scope)) {
            ret = 3;
        } else if ("application".equalsIgnoreCase(scope)) {
            ret = 4;
        }
        return ret;
    }

    public static boolean isAbsoluteUrl(String url) {
        if (url == null) {
            return false;
        }
        int colonPos = url.indexOf(58);
        if (colonPos == -1) {
            return false;
        }
        for (int i = 0; i < colonPos; ++i) {
            if (VALID_SCHEME_CHAR.indexOf(url.charAt(i)) != -1) continue;
            return false;
        }
        return true;
    }

    public static String getContentTypeAttribute(String input, String name) {
        int end;
        int begin;
        int index = input.toUpperCase(Locale.ENGLISH).indexOf(name.toUpperCase(Locale.ENGLISH));
        if (index == -1) {
            return null;
        }
        index += name.length();
        if ((index = input.indexOf(61, index)) == -1) {
            return null;
        }
        if ((input = input.substring(++index).trim()).charAt(0) == '\"') {
            begin = 1;
            end = input.indexOf(34, begin);
            if (end == -1) {
                return null;
            }
        } else {
            begin = 0;
            end = input.indexOf(59);
            if (end == -1) {
                end = input.indexOf(32);
            }
            if (end == -1) {
                end = input.length();
            }
        }
        return input.substring(begin, end).trim();
    }

    public static String stripSession(String url) {
        int sessionStart;
        StringBuilder u = new StringBuilder(url);
        while ((sessionStart = u.toString().indexOf(";" + Constants.SESSION_PARAMETER_NAME + "=")) != -1) {
            int sessionEnd = u.toString().indexOf(59, sessionStart + 1);
            if (sessionEnd == -1) {
                sessionEnd = u.toString().indexOf(63, sessionStart + 1);
            }
            if (sessionEnd == -1) {
                sessionEnd = u.length();
            }
            u.delete(sessionStart, sessionEnd);
        }
        return u.toString();
    }

    public static String escapeXml(String buffer) {
        String result = Util.escapeXml(buffer.toCharArray(), buffer.length());
        if (result == null) {
            return buffer;
        }
        return result;
    }

    public static String escapeXml(char[] arrayBuffer, int length) {
        int start = 0;
        StringBuilder escapedBuffer = null;
        for (int i = 0; i < length; ++i) {
            char[] escaped;
            char c = arrayBuffer[i];
            if (c > '>' || (escaped = specialCharactersRepresentation[c]) == null) continue;
            if (start == 0) {
                escapedBuffer = new StringBuilder(length + 5);
            }
            if (start < i) {
                escapedBuffer.append(arrayBuffer, start, i - start);
            }
            start = i + 1;
            escapedBuffer.append(escaped);
        }
        if (start == 0) {
            return null;
        }
        if (start < length) {
            escapedBuffer.append(arrayBuffer, start, length - start);
        }
        return escapedBuffer.toString();
    }

    public static String resolveUrl(String url, String context, PageContext pageContext) throws JspException {
        if (Util.isAbsoluteUrl(url)) {
            return url;
        }
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        if (context == null) {
            if (url.startsWith("/")) {
                return request.getContextPath() + url;
            }
            return url;
        }
        if (!context.startsWith("/") || !url.startsWith("/")) {
            throw new JspTagException(Localizer.getMessage("jstl.urlMustStartWithSlash"));
        }
        if (context.equals("/")) {
            return url;
        }
        return context + url;
    }

    static {
        Util.specialCharactersRepresentation[38] = "&amp;".toCharArray();
        Util.specialCharactersRepresentation[60] = "&lt;".toCharArray();
        Util.specialCharactersRepresentation[62] = "&gt;".toCharArray();
        Util.specialCharactersRepresentation[34] = "&#034;".toCharArray();
        Util.specialCharactersRepresentation[39] = "&#039;".toCharArray();
    }

    public static class ImportResponseWrapper
    extends HttpServletResponseWrapper {
        private final StringWriter sw = new StringWriter();
        private final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        private final ServletOutputStream sos = new ServletOutputStream(){

            public void write(int b) throws IOException {
                bos.write(b);
            }

            public boolean isReady() {
                return false;
            }

            public void setWriteListener(WriteListener listener) {
                throw new UnsupportedOperationException();
            }
        };
        private boolean isWriterUsed;
        private boolean isStreamUsed;
        private int status = 200;
        private String charEncoding;

        public ImportResponseWrapper(HttpServletResponse arg0) {
            super(arg0);
        }

        public PrintWriter getWriter() {
            if (this.isStreamUsed) {
                throw new IllegalStateException(Localizer.getMessage("jstl.writerAfterOS"));
            }
            this.isWriterUsed = true;
            return new PrintWriter(this.sw);
        }

        public ServletOutputStream getOutputStream() {
            if (this.isWriterUsed) {
                throw new IllegalStateException(Localizer.getMessage("jstl.OSAfterWriter"));
            }
            this.isStreamUsed = true;
            return this.sos;
        }

        public void setContentType(String x) {
        }

        public void setLocale(Locale x) {
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return this.status;
        }

        public String getCharEncoding() {
            return this.charEncoding;
        }

        public void setCharEncoding(String ce) {
            this.charEncoding = ce;
        }

        public String getString() throws UnsupportedEncodingException {
            if (this.isWriterUsed) {
                return this.sw.toString();
            }
            if (this.isStreamUsed) {
                if (this.charEncoding != null && !this.charEncoding.equals("")) {
                    return this.bos.toString(this.charEncoding);
                }
                return this.bos.toString(Util.DEFAULT_ENCODING);
            }
            return "";
        }
    }
}

