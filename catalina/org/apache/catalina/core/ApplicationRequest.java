/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletRequestWrapper
 */
package org.apache.catalina.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;

class ApplicationRequest
extends ServletRequestWrapper {
    @Deprecated
    protected static final String[] specials = new String[]{"javax.servlet.include.request_uri", "javax.servlet.include.context_path", "javax.servlet.include.servlet_path", "javax.servlet.include.path_info", "javax.servlet.include.query_string", "javax.servlet.include.mapping", "javax.servlet.forward.request_uri", "javax.servlet.forward.context_path", "javax.servlet.forward.servlet_path", "javax.servlet.forward.path_info", "javax.servlet.forward.query_string", "javax.servlet.forward.mapping"};
    private static final Set<String> specialsSet = new HashSet<String>(Arrays.asList(specials));
    protected final HashMap<String, Object> attributes = new HashMap();

    ApplicationRequest(ServletRequest request) {
        super(request);
        this.setRequest(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object getAttribute(String name) {
        HashMap<String, Object> hashMap = this.attributes;
        synchronized (hashMap) {
            return this.attributes.get(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Enumeration<String> getAttributeNames() {
        HashMap<String, Object> hashMap = this.attributes;
        synchronized (hashMap) {
            return Collections.enumeration(this.attributes.keySet());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeAttribute(String name) {
        HashMap<String, Object> hashMap = this.attributes;
        synchronized (hashMap) {
            this.attributes.remove(name);
            if (!this.isSpecial(name)) {
                this.getRequest().removeAttribute(name);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAttribute(String name, Object value) {
        HashMap<String, Object> hashMap = this.attributes;
        synchronized (hashMap) {
            this.attributes.put(name, value);
            if (!this.isSpecial(name)) {
                this.getRequest().setAttribute(name, value);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setRequest(ServletRequest request) {
        super.setRequest(request);
        HashMap<String, Object> hashMap = this.attributes;
        synchronized (hashMap) {
            this.attributes.clear();
            Enumeration names = request.getAttributeNames();
            while (names.hasMoreElements()) {
                String name = (String)names.nextElement();
                Object value = request.getAttribute(name);
                this.attributes.put(name, value);
            }
        }
    }

    @Deprecated
    protected boolean isSpecial(String name) {
        return specialsSet.contains(name);
    }
}

