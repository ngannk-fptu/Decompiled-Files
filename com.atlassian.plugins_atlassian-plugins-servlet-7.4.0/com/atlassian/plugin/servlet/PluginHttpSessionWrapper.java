/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.ClassLoaderStack
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpSession
 *  javax.servlet.http.HttpSessionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.servlet;

import com.atlassian.plugin.util.ClassLoaderStack;
import java.util.Enumeration;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginHttpSessionWrapper
implements HttpSession {
    private HttpSession delegate;
    private static final Logger log = LoggerFactory.getLogger(PluginHttpSessionWrapper.class);

    public PluginHttpSessionWrapper(HttpSession session) {
        this.delegate = session;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object getAttribute(String name) {
        ClassLoader classLoader = ClassLoaderStack.pop();
        try {
            if (log.isDebugEnabled()) {
                log.debug("getAttribute('{}') Popping ClassLoader: {}. New ContextClassLoader: {}", new Object[]{name, classLoader, Thread.currentThread().getContextClassLoader()});
            }
            Object object = this.delegate.getAttribute(name);
            return object;
        }
        finally {
            ClassLoaderStack.push((ClassLoader)classLoader);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAttribute(String name, Object value) {
        ClassLoader classLoader = ClassLoaderStack.pop();
        try {
            this.delegate.setAttribute(name, value);
        }
        finally {
            ClassLoaderStack.push((ClassLoader)classLoader);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object getValue(String name) {
        ClassLoader classLoader = ClassLoaderStack.pop();
        try {
            Object object = this.delegate.getValue(name);
            return object;
        }
        finally {
            ClassLoaderStack.push((ClassLoader)classLoader);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void putValue(String name, Object value) {
        ClassLoader classLoader = ClassLoaderStack.pop();
        try {
            this.delegate.putValue(name, value);
        }
        finally {
            ClassLoaderStack.push((ClassLoader)classLoader);
        }
    }

    public long getCreationTime() {
        return this.delegate.getCreationTime();
    }

    public String getId() {
        return this.delegate.getId();
    }

    public long getLastAccessedTime() {
        return this.delegate.getLastAccessedTime();
    }

    public ServletContext getServletContext() {
        return this.delegate.getServletContext();
    }

    public void setMaxInactiveInterval(int interval) {
        this.delegate.setMaxInactiveInterval(interval);
    }

    public int getMaxInactiveInterval() {
        return this.delegate.getMaxInactiveInterval();
    }

    public HttpSessionContext getSessionContext() {
        return this.delegate.getSessionContext();
    }

    public Enumeration<String> getAttributeNames() {
        return this.delegate.getAttributeNames();
    }

    public String[] getValueNames() {
        return this.delegate.getValueNames();
    }

    public void removeAttribute(String name) {
        this.delegate.removeAttribute(name);
    }

    public void removeValue(String name) {
        this.delegate.removeValue(name);
    }

    public void invalidate() {
        this.delegate.invalidate();
    }

    public boolean isNew() {
        return this.delegate.isNew();
    }
}

