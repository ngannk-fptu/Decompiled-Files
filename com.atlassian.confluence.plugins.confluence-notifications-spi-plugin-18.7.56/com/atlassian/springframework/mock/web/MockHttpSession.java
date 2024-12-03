/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpSession
 *  javax.servlet.http.HttpSessionBindingEvent
 *  javax.servlet.http.HttpSessionBindingListener
 *  javax.servlet.http.HttpSessionContext
 *  org.springframework.util.Assert
 */
package com.atlassian.springframework.mock.web;

import com.atlassian.springframework.mock.web.MockServletContext;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionContext;
import org.springframework.util.Assert;

public class MockHttpSession
implements HttpSession {
    public static final String SESSION_COOKIE_NAME = "JSESSION";
    private static int nextId = 1;
    private final long creationTime = System.currentTimeMillis();
    private final ServletContext servletContext;
    private final Map<String, Object> attributes = new LinkedHashMap<String, Object>();
    private String id;
    private int maxInactiveInterval;
    private long lastAccessedTime = System.currentTimeMillis();
    private boolean invalid = false;
    private boolean isNew = true;

    public MockHttpSession() {
        this(null);
    }

    public MockHttpSession(ServletContext servletContext) {
        this(servletContext, null);
    }

    public MockHttpSession(ServletContext servletContext, String id) {
        this.servletContext = servletContext != null ? servletContext : new MockServletContext();
        this.id = id != null ? id : Integer.toString(nextId++);
    }

    public long getCreationTime() {
        this.assertIsValid();
        return this.creationTime;
    }

    public String getId() {
        return this.id;
    }

    public String changeSessionId() {
        this.id = Integer.toString(nextId++);
        return this.id;
    }

    public void access() {
        this.lastAccessedTime = System.currentTimeMillis();
        this.isNew = false;
    }

    public long getLastAccessedTime() {
        this.assertIsValid();
        return this.lastAccessedTime;
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public int getMaxInactiveInterval() {
        return this.maxInactiveInterval;
    }

    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveInterval = interval;
    }

    public HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException("getSessionContext");
    }

    public Object getAttribute(String name) {
        this.assertIsValid();
        Assert.notNull((Object)name, (String)"Attribute name must not be null");
        return this.attributes.get(name);
    }

    public Object getValue(String name) {
        return this.getAttribute(name);
    }

    public Enumeration<String> getAttributeNames() {
        this.assertIsValid();
        return Collections.enumeration(new LinkedHashSet<String>(this.attributes.keySet()));
    }

    public String[] getValueNames() {
        this.assertIsValid();
        return this.attributes.keySet().toArray(new String[this.attributes.size()]);
    }

    public void setAttribute(String name, Object value) {
        this.assertIsValid();
        Assert.notNull((Object)name, (String)"Attribute name must not be null");
        if (value != null) {
            this.attributes.put(name, value);
            if (value instanceof HttpSessionBindingListener) {
                ((HttpSessionBindingListener)value).valueBound(new HttpSessionBindingEvent((HttpSession)this, name, value));
            }
        } else {
            this.removeAttribute(name);
        }
    }

    public void putValue(String name, Object value) {
        this.setAttribute(name, value);
    }

    public void removeAttribute(String name) {
        this.assertIsValid();
        Assert.notNull((Object)name, (String)"Attribute name must not be null");
        Object value = this.attributes.remove(name);
        if (value instanceof HttpSessionBindingListener) {
            ((HttpSessionBindingListener)value).valueUnbound(new HttpSessionBindingEvent((HttpSession)this, name, value));
        }
    }

    public void removeValue(String name) {
        this.removeAttribute(name);
    }

    public void clearAttributes() {
        Iterator<Map.Entry<String, Object>> it = this.attributes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            String name = entry.getKey();
            Object value = entry.getValue();
            it.remove();
            if (!(value instanceof HttpSessionBindingListener)) continue;
            ((HttpSessionBindingListener)value).valueUnbound(new HttpSessionBindingEvent((HttpSession)this, name, value));
        }
    }

    public void invalidate() {
        this.assertIsValid();
        this.invalid = true;
        this.clearAttributes();
    }

    public boolean isInvalid() {
        return this.invalid;
    }

    private void assertIsValid() {
        if (this.isInvalid()) {
            throw new IllegalStateException("The session has already been invalidated");
        }
    }

    public boolean isNew() {
        this.assertIsValid();
        return this.isNew;
    }

    public void setNew(boolean value) {
        this.isNew = value;
    }

    public Serializable serializeState() {
        HashMap<String, Serializable> state = new HashMap<String, Serializable>();
        Iterator<Map.Entry<String, Object>> it = this.attributes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            String name = entry.getKey();
            Object value = entry.getValue();
            it.remove();
            if (value instanceof Serializable) {
                state.put(name, (Serializable)value);
                continue;
            }
            if (!(value instanceof HttpSessionBindingListener)) continue;
            ((HttpSessionBindingListener)value).valueUnbound(new HttpSessionBindingEvent((HttpSession)this, name, value));
        }
        return state;
    }

    public void deserializeState(Serializable state) {
        Assert.isTrue((boolean)(state instanceof Map), (String)"Serialized state needs to be of type [java.util.Map]");
        this.attributes.putAll((Map)((Object)state));
    }
}

