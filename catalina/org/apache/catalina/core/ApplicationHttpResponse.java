/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 */
package org.apache.catalina.core;

import java.io.IOException;
import java.util.Locale;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

class ApplicationHttpResponse
extends HttpServletResponseWrapper {
    protected boolean included = false;

    ApplicationHttpResponse(HttpServletResponse response, boolean included) {
        super(response);
        this.setIncluded(included);
    }

    public void reset() {
        if (!this.included || this.getResponse().isCommitted()) {
            this.getResponse().reset();
        }
    }

    public void setContentLength(int len) {
        if (!this.included) {
            this.getResponse().setContentLength(len);
        }
    }

    public void setContentLengthLong(long len) {
        if (!this.included) {
            this.getResponse().setContentLengthLong(len);
        }
    }

    public void setContentType(String type) {
        if (!this.included) {
            this.getResponse().setContentType(type);
        }
    }

    public void setLocale(Locale loc) {
        if (!this.included) {
            this.getResponse().setLocale(loc);
        }
    }

    public void setBufferSize(int size) {
        if (!this.included) {
            this.getResponse().setBufferSize(size);
        }
    }

    public void addCookie(Cookie cookie) {
        if (!this.included) {
            ((HttpServletResponse)this.getResponse()).addCookie(cookie);
        }
    }

    public void addDateHeader(String name, long value) {
        if (!this.included) {
            ((HttpServletResponse)this.getResponse()).addDateHeader(name, value);
        }
    }

    public void addHeader(String name, String value) {
        if (!this.included) {
            ((HttpServletResponse)this.getResponse()).addHeader(name, value);
        }
    }

    public void addIntHeader(String name, int value) {
        if (!this.included) {
            ((HttpServletResponse)this.getResponse()).addIntHeader(name, value);
        }
    }

    public void sendError(int sc) throws IOException {
        if (!this.included) {
            ((HttpServletResponse)this.getResponse()).sendError(sc);
        }
    }

    public void sendError(int sc, String msg) throws IOException {
        if (!this.included) {
            ((HttpServletResponse)this.getResponse()).sendError(sc, msg);
        }
    }

    public void sendRedirect(String location) throws IOException {
        if (!this.included) {
            ((HttpServletResponse)this.getResponse()).sendRedirect(location);
        }
    }

    public void setDateHeader(String name, long value) {
        if (!this.included) {
            ((HttpServletResponse)this.getResponse()).setDateHeader(name, value);
        }
    }

    public void setHeader(String name, String value) {
        if (!this.included) {
            ((HttpServletResponse)this.getResponse()).setHeader(name, value);
        }
    }

    public void setIntHeader(String name, int value) {
        if (!this.included) {
            ((HttpServletResponse)this.getResponse()).setIntHeader(name, value);
        }
    }

    public void setStatus(int sc) {
        if (!this.included) {
            ((HttpServletResponse)this.getResponse()).setStatus(sc);
        }
    }

    @Deprecated
    public void setStatus(int sc, String msg) {
        if (!this.included) {
            ((HttpServletResponse)this.getResponse()).setStatus(sc, msg);
        }
    }

    void setIncluded(boolean included) {
        this.included = included;
    }

    void setResponse(HttpServletResponse response) {
        super.setResponse((ServletResponse)response);
    }
}

