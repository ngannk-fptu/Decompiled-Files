/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.apache.struts2.dispatcher;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.dispatcher.StringObjectEntry;

public class RequestMap
extends AbstractMap<String, Object>
implements Serializable {
    private static final long serialVersionUID = -7675640869293787926L;
    private final HttpServletRequest request;
    private Set<Map.Entry<String, Object>> entries;

    public RequestMap(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void clear() {
        this.entries = null;
        Enumeration keys = this.request.getAttributeNames();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            this.request.removeAttribute(key);
        }
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        if (this.entries == null) {
            this.entries = new HashSet<Map.Entry<String, Object>>();
            Enumeration enumeration = this.request.getAttributeNames();
            while (enumeration.hasMoreElements()) {
                final String key = (String)enumeration.nextElement();
                final Object value = this.request.getAttribute(key);
                this.entries.add(new StringObjectEntry(key, value){

                    @Override
                    public Object setValue(Object obj) {
                        RequestMap.this.request.setAttribute(key, obj);
                        return value;
                    }
                });
            }
        }
        return this.entries;
    }

    @Override
    public Object get(Object key) {
        if (key == null) {
            return null;
        }
        return this.request.getAttribute(key.toString());
    }

    @Override
    public Object put(String key, Object value) {
        Object oldValue = this.get(key);
        this.entries = null;
        this.request.setAttribute(key, value);
        return oldValue;
    }

    public Object remove(String key) {
        this.entries = null;
        Object value = this.get(key);
        this.request.removeAttribute(key);
        return value;
    }
}

