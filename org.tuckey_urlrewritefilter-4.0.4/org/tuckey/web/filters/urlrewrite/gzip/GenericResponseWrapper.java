/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 */
package org.tuckey.web.filters.urlrewrite.gzip;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.tuckey.web.filters.urlrewrite.gzip.FilterServletOutputStream;
import org.tuckey.web.filters.urlrewrite.utils.Log;

public class GenericResponseWrapper
extends HttpServletResponseWrapper
implements Serializable {
    private static final Log LOG = Log.getLog(GenericResponseWrapper.class);
    private int statusCode = 200;
    private String contentType;
    private final Map<String, List<Serializable>> headersMap = new TreeMap<String, List<Serializable>>(String.CASE_INSENSITIVE_ORDER);
    private final List cookies = new ArrayList();
    private final ServletOutputStream outstr;
    private PrintWriter writer;
    private boolean disableFlushBuffer = true;

    public GenericResponseWrapper(HttpServletResponse response, OutputStream outstr) {
        super(response);
        this.outstr = new FilterServletOutputStream(outstr);
    }

    public ServletOutputStream getOutputStream() {
        return this.outstr;
    }

    public void setStatus(int code) {
        this.statusCode = code;
        super.setStatus(code);
    }

    public void sendError(int i, String string) throws IOException {
        this.statusCode = i;
        super.sendError(i, string);
    }

    public void sendError(int i) throws IOException {
        this.statusCode = i;
        super.sendError(i);
    }

    public void sendRedirect(String string) throws IOException {
        this.statusCode = 302;
        super.sendRedirect(string);
    }

    public void setStatus(int code, String msg) {
        this.statusCode = code;
        LOG.warn("Discarding message because this method is deprecated.");
        super.setStatus(code);
    }

    public int getStatus() {
        return this.statusCode;
    }

    public void setContentType(String type) {
        this.contentType = type;
        super.setContentType(type);
    }

    public String getContentType() {
        return this.contentType;
    }

    public PrintWriter getWriter() throws IOException {
        if (this.writer == null) {
            this.writer = new PrintWriter(new OutputStreamWriter((OutputStream)this.outstr, this.getCharacterEncoding()), true);
        }
        return this.writer;
    }

    public void addHeader(String name, String value) {
        List<Serializable> values = this.headersMap.get(name);
        if (values == null) {
            values = new LinkedList<Serializable>();
            this.headersMap.put(name, values);
        }
        values.add((Serializable)((Object)value));
        super.addHeader(name, value);
    }

    public void setHeader(String name, String value) {
        LinkedList<String> values = new LinkedList<String>();
        values.add(value);
        this.headersMap.put(name, values);
        super.setHeader(name, value);
    }

    public void addDateHeader(String name, long date) {
        List<Serializable> values = this.headersMap.get(name);
        if (values == null) {
            values = new LinkedList<Serializable>();
            this.headersMap.put(name, values);
        }
        values.add(Long.valueOf(date));
        super.addDateHeader(name, date);
    }

    public void setDateHeader(String name, long date) {
        LinkedList<Long> values = new LinkedList<Long>();
        values.add(date);
        this.headersMap.put(name, values);
        super.setDateHeader(name, date);
    }

    public void addIntHeader(String name, int value) {
        List<Serializable> values = this.headersMap.get(name);
        if (values == null) {
            values = new LinkedList<Serializable>();
            this.headersMap.put(name, values);
        }
        values.add(Integer.valueOf(value));
        super.addIntHeader(name, value);
    }

    public void setIntHeader(String name, int value) {
        LinkedList<Integer> values = new LinkedList<Integer>();
        values.add(value);
        this.headersMap.put(name, values);
        super.setIntHeader(name, value);
    }

    public void addCookie(Cookie cookie) {
        this.cookies.add(cookie);
        super.addCookie(cookie);
    }

    public void flushBuffer() throws IOException {
        this.flush();
        if (!this.disableFlushBuffer) {
            super.flushBuffer();
        }
    }

    public void reset() {
        super.reset();
        this.cookies.clear();
        this.headersMap.clear();
        this.statusCode = 200;
        this.contentType = null;
    }

    public void flush() throws IOException {
        if (this.writer != null) {
            this.writer.flush();
        }
        this.outstr.flush();
    }

    public void setDisableFlushBuffer() {
        this.disableFlushBuffer = true;
    }
}

