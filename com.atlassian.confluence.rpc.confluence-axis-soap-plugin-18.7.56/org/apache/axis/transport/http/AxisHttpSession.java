/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 */
package org.apache.axis.transport.http;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.axis.session.Session;

public class AxisHttpSession
implements Session {
    public static final String AXIS_SESSION_MARKER = "axis.isAxisSession";
    private HttpSession rep;
    private HttpServletRequest req;

    public AxisHttpSession(HttpServletRequest realRequest) {
        this.req = realRequest;
    }

    public AxisHttpSession(HttpSession realSession) {
        if (realSession != null) {
            this.setRep(realSession);
        }
    }

    public HttpSession getRep() {
        this.ensureSession();
        return this.rep;
    }

    private void setRep(HttpSession realSession) {
        this.rep = realSession;
        this.rep.setAttribute(AXIS_SESSION_MARKER, (Object)Boolean.TRUE);
    }

    public Object get(String key) {
        this.ensureSession();
        return this.rep.getAttribute(key);
    }

    public void set(String key, Object value) {
        this.ensureSession();
        this.rep.setAttribute(key, value);
    }

    public void remove(String key) {
        this.ensureSession();
        this.rep.removeAttribute(key);
    }

    public Enumeration getKeys() {
        this.ensureSession();
        return this.rep.getAttributeNames();
    }

    public void setTimeout(int timeout) {
        this.ensureSession();
        this.rep.setMaxInactiveInterval(timeout);
    }

    public int getTimeout() {
        this.ensureSession();
        return this.rep.getMaxInactiveInterval();
    }

    public void touch() {
    }

    public void invalidate() {
        this.rep.invalidate();
    }

    protected void ensureSession() {
        if (this.rep == null) {
            this.setRep(this.req.getSession());
        }
    }

    public Object getLockObject() {
        this.ensureSession();
        return this.rep;
    }
}

