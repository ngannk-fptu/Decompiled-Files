/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 */
package org.apache.struts2.dispatcher;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.dispatcher.StringObjectEntry;

public class SessionMap
extends AbstractMap<String, Object>
implements Serializable {
    private static final long serialVersionUID = 4678843241638046854L;
    protected HttpSession session;
    protected Set<Map.Entry<String, Object>> entries;
    protected HttpServletRequest request;

    public SessionMap(HttpServletRequest request) {
        this.request = request;
        this.session = request.getSession(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void invalidate() {
        if (this.session == null) {
            return;
        }
        String string = this.session.getId().intern();
        synchronized (string) {
            this.session.invalidate();
            this.session = null;
            this.entries = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear() {
        if (this.session == null) {
            return;
        }
        String string = this.session.getId().intern();
        synchronized (string) {
            this.entries = null;
            Enumeration attributeNamesEnum = this.session.getAttributeNames();
            while (attributeNamesEnum.hasMoreElements()) {
                this.session.removeAttribute((String)attributeNamesEnum.nextElement());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        if (this.session == null) {
            return Collections.emptySet();
        }
        String string = this.session.getId().intern();
        synchronized (string) {
            if (this.entries == null) {
                this.entries = new HashSet<Map.Entry<String, Object>>();
                Enumeration enumeration = this.session.getAttributeNames();
                while (enumeration.hasMoreElements()) {
                    final String key = (String)enumeration.nextElement();
                    final Object value = this.session.getAttribute(key);
                    this.entries.add(new StringObjectEntry(key, value){

                        @Override
                        public Object setValue(Object obj) {
                            SessionMap.this.session.setAttribute(key, obj);
                            return value;
                        }
                    });
                }
            }
        }
        return this.entries;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object get(Object key) {
        if (this.session == null) {
            return null;
        }
        String string = this.session.getId().intern();
        synchronized (string) {
            return this.session.getAttribute(key != null ? key.toString() : null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object put(String key, Object value) {
        Object object = this;
        synchronized (object) {
            if (this.session == null) {
                this.session = this.request.getSession(true);
            }
        }
        object = this.session.getId().intern();
        synchronized (object) {
            Object oldValue = this.get(key);
            this.entries = null;
            this.session.setAttribute(key, value);
            return oldValue;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object remove(Object key) {
        if (this.session == null) {
            return null;
        }
        String string = this.session.getId().intern();
        synchronized (string) {
            this.entries = null;
            String keyAsString = key != null ? key.toString() : null;
            Object value = this.get(keyAsString);
            this.session.removeAttribute(keyAsString);
            return value;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsKey(Object key) {
        if (this.session == null) {
            return false;
        }
        String string = this.session.getId().intern();
        synchronized (string) {
            String keyAsString = key != null ? key.toString() : null;
            return this.session.getAttribute(keyAsString) != null;
        }
    }
}

