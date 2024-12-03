/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 */
package com.opensymphony.module.sitemesh.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class DebugResponseWrapper
extends HttpServletResponseWrapper {
    private static int lastCount = 0;
    private int count = 0;

    public DebugResponseWrapper(HttpServletResponse response) {
        super(response);
        if (this.enabled()) {
            this.count = ++lastCount;
            this.debug("<CONSTRUCT>", null, null);
        }
    }

    public void addCookie(Cookie cookie) {
        if (this.enabled()) {
            this.debug("addCookie", cookie.getName(), cookie.toString());
        }
        super.addCookie(cookie);
    }

    public void addDateHeader(String name, long date) {
        if (this.enabled()) {
            this.debug("addDateHeader", name, String.valueOf(date));
        }
        super.addDateHeader(name, date);
    }

    public void addHeader(String name, String value) {
        if (this.enabled()) {
            this.debug("addHeader", name, value);
        }
        super.addHeader(name, value);
    }

    public void addIntHeader(String name, int value) {
        if (this.enabled()) {
            this.debug("addIntHeader", name, String.valueOf(value));
        }
        super.addIntHeader(name, value);
    }

    public boolean containsHeader(String name) {
        return super.containsHeader(name);
    }

    public String encodeRedirectUrl(String url) {
        return super.encodeRedirectUrl(url);
    }

    public String encodeRedirectURL(String url) {
        return super.encodeRedirectURL(url);
    }

    public void sendError(int sc) throws IOException {
        if (this.enabled()) {
            this.debug("sendError", String.valueOf(sc), null);
        }
        super.sendError(sc);
    }

    public void sendError(int sc, String msg) throws IOException {
        if (this.enabled()) {
            this.debug("sendError", String.valueOf(sc), msg);
        }
        super.sendError(sc, msg);
    }

    public void sendRedirect(String location) throws IOException {
        if (this.enabled()) {
            this.debug("sendRedirect", location, null);
        }
        super.sendRedirect(location);
    }

    public void setDateHeader(String name, long date) {
        if (this.enabled()) {
            this.debug("setDateHeader", name, String.valueOf(date));
        }
        super.setDateHeader(name, date);
    }

    public void setHeader(String name, String value) {
        if (this.enabled()) {
            this.debug("setHeader", name, value);
        }
        super.setHeader(name, value);
    }

    public void setIntHeader(String name, int value) {
        if (this.enabled()) {
            this.debug("setIntHeader", name, String.valueOf(value));
        }
        super.setIntHeader(name, value);
    }

    public void setStatus(int sc) {
        if (this.enabled()) {
            this.debug("setStatus", String.valueOf(sc), null);
        }
        super.setStatus(sc);
    }

    public void setStatus(int sc, String msg) {
        if (this.enabled()) {
            this.debug("setStatus", String.valueOf(sc), msg);
        }
        super.setStatus(sc, msg);
    }

    public void flushBuffer() throws IOException {
        if (this.enabled()) {
            this.debug("flushBuffer", null, null);
        }
        super.flushBuffer();
    }

    public int getBufferSize() {
        return super.getBufferSize();
    }

    public String getCharacterEncoding() {
        return super.getCharacterEncoding();
    }

    public Locale getLocale() {
        return super.getLocale();
    }

    public ServletOutputStream getOutputStream() throws IOException {
        if (this.enabled()) {
            this.debug("getOutputStream", null, null);
        }
        return super.getOutputStream();
    }

    public PrintWriter getWriter() throws IOException {
        if (this.enabled()) {
            this.debug("getWriter", null, null);
        }
        return super.getWriter();
    }

    public boolean isCommitted() {
        return super.isCommitted();
    }

    public void reset() {
        if (this.enabled()) {
            this.debug("reset", null, null);
        }
        super.reset();
    }

    public void setBufferSize(int size) {
        if (this.enabled()) {
            this.debug("setBufferSize", String.valueOf(size), null);
        }
        super.setBufferSize(size);
    }

    public void setContentLength(int len) {
        if (this.enabled()) {
            this.debug("setContentLength", String.valueOf(len), null);
        }
        super.setContentLength(len);
    }

    public void setContentType(String type) {
        if (this.enabled()) {
            this.debug("setContentType", type, null);
        }
        super.setContentType(type);
    }

    public void setLocale(Locale locale) {
        if (this.enabled()) {
            this.debug("setBufferSize", locale.getDisplayName(), null);
        }
        super.setLocale(locale);
    }

    private boolean enabled() {
        return true;
    }

    private void debug(String methodName, String arg1, String arg2) {
        StringBuffer s = new StringBuffer();
        s.append("[debug ");
        s.append(this.count);
        s.append("] ");
        s.append(methodName);
        s.append("()");
        if (arg1 != null) {
            s.append(" : '");
            s.append(arg1);
            s.append("'");
        }
        if (arg2 != null) {
            s.append(" = '");
            s.append(arg2);
            s.append("'");
        }
        System.out.println(s);
    }
}

