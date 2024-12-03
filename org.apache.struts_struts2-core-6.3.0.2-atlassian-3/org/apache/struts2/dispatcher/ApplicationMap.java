/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.apache.struts2.dispatcher;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import org.apache.struts2.dispatcher.StringObjectEntry;

public class ApplicationMap
extends AbstractMap<String, Object>
implements Serializable {
    private static final long serialVersionUID = 9136809763083228202L;
    private final ServletContext context;
    private Set<Map.Entry<String, Object>> entries;

    public ApplicationMap(ServletContext ctx) {
        this.context = ctx;
    }

    @Override
    public void clear() {
        this.entries = null;
        Enumeration e = this.context.getAttributeNames();
        while (e.hasMoreElements()) {
            this.context.removeAttribute((String)e.nextElement());
        }
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        if (this.entries == null) {
            Object value;
            String key;
            this.entries = new HashSet<Map.Entry<String, Object>>();
            Enumeration enumeration = this.context.getAttributeNames();
            while (enumeration.hasMoreElements()) {
                key = (String)enumeration.nextElement();
                value = this.context.getAttribute(key);
                this.entries.add(new StringObjectEntry(key, value){

                    @Override
                    public Object setValue(Object obj) {
                        ApplicationMap.this.context.setAttribute(key, obj);
                        return value;
                    }
                });
            }
            enumeration = this.context.getInitParameterNames();
            while (enumeration.hasMoreElements()) {
                key = (String)enumeration.nextElement();
                value = this.context.getInitParameter(key);
                this.entries.add(new StringObjectEntry(key, value){

                    @Override
                    public Object setValue(Object obj) {
                        ApplicationMap.this.context.setAttribute(key, obj);
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
        Object value = this.context.getAttribute(key.toString());
        return value == null ? this.context.getInitParameter(key.toString()) : value;
    }

    @Override
    public Object put(String key, Object value) {
        Object oldValue = this.get(key);
        this.entries = null;
        this.context.setAttribute(key, value);
        return oldValue;
    }

    public Object remove(String key) {
        this.entries = null;
        Object value = this.get(key);
        this.context.removeAttribute(key);
        return value;
    }
}

