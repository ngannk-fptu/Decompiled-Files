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
package org.apache.sling.scripting.jsp.jasper.tagplugins.jstl;

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

public class Util {
    public static final String VALID_SCHEME_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+.-";
    public static final String DEFAULT_ENCODING = "ISO-8859-1";
    public static final int HIGHEST_SPECIAL = 62;
    public static char[][] specialCharactersRepresentation = new char[63][];

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
        int colonPos = url.indexOf(":");
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
        int index = input.toUpperCase().indexOf(name.toUpperCase());
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
        StringBuffer u = new StringBuffer(url);
        while ((sessionStart = u.toString().indexOf(";jsessionid=")) != -1) {
            int sessionEnd = u.toString().indexOf(";", sessionStart + 1);
            if (sessionEnd == -1) {
                sessionEnd = u.toString().indexOf("?", sessionStart + 1);
            }
            if (sessionEnd == -1) {
                sessionEnd = u.length();
            }
            u.delete(sessionStart, sessionEnd);
        }
        return u.toString();
    }

    public static String escapeXml(String buffer) {
        int start = 0;
        int length = buffer.length();
        char[] arrayBuffer = buffer.toCharArray();
        StringBuffer escapedBuffer = null;
        for (int i = 0; i < length; ++i) {
            char[] escaped;
            char c = arrayBuffer[i];
            if (c > '>' || (escaped = specialCharactersRepresentation[c]) == null) continue;
            if (start == 0) {
                escapedBuffer = new StringBuffer(length + 5);
            }
            if (start < i) {
                escapedBuffer.append(arrayBuffer, start, i - start);
            }
            start = i + 1;
            escapedBuffer.append(escaped);
        }
        if (start == 0) {
            return buffer;
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
            throw new JspTagException("In URL tags, when the \"context\" attribute is specified, values of both \"context\" and \"url\" must start with \"/\".");
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
        private StringWriter sw = new StringWriter();
        private ByteArrayOutputStream bos = new ByteArrayOutputStream();
        private ServletOutputStream sos = new ServletOutputStream(){

            public void write(int b) throws IOException {
                bos.write(b);
            }

            public boolean isReady() {
                return true;
            }

            public void setWriteListener(WriteListener writeListener) {
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
                throw new IllegalStateException("Unexpected internal error during &lt;import&gt: Target servlet called getWriter(), then getOutputStream()");
            }
            this.isWriterUsed = true;
            return new PrintWriter(this.sw);
        }

        public ServletOutputStream getOutputStream() {
            if (this.isWriterUsed) {
                throw new IllegalStateException("Unexpected internal error during &lt;import&gt: Target servlet called getOutputStream(), then getWriter()");
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

