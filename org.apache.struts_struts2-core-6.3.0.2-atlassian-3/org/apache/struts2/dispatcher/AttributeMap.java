/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.PageContext
 */
package org.apache.struts2.dispatcher;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.servlet.jsp.PageContext;
import org.apache.struts2.dispatcher.ApplicationMap;
import org.apache.struts2.dispatcher.RequestMap;
import org.apache.struts2.dispatcher.SessionMap;

public class AttributeMap
extends AbstractMap<String, Object> {
    protected static final String UNSUPPORTED = "method makes no sense for a simplified map";
    private final Map<String, Object> context;

    public AttributeMap(Map<String, Object> context) {
        this.context = context;
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    @Override
    public boolean containsKey(Object key) {
        return this.get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return Collections.unmodifiableSet(this.context.entrySet());
    }

    @Override
    public Object get(Object key) {
        if (key == null) {
            return null;
        }
        PageContext pc = this.getPageContext();
        if (pc == null) {
            RequestMap request = (RequestMap)this.context.get("request");
            SessionMap session = (SessionMap)this.context.get("session");
            ApplicationMap application = (ApplicationMap)this.context.get("application");
            if (request != null && request.get(key) != null) {
                return request.get(key);
            }
            if (session != null && session.get(key) != null) {
                return session.get(key);
            }
            if (application != null && application.get(key) != null) {
                return application.get(key);
            }
        } else {
            return pc.findAttribute(key.toString());
        }
        return null;
    }

    @Override
    public Set<String> keySet() {
        return Collections.unmodifiableSet(this.context.keySet());
    }

    @Override
    public Object put(String key, Object value) {
        PageContext pc = this.getPageContext();
        if (pc != null) {
            pc.setAttribute(key, value);
            return value;
        }
        return null;
    }

    @Override
    public void putAll(Map t) {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    @Override
    public Collection<Object> values() {
        return Collections.unmodifiableCollection(this.context.values());
    }

    private PageContext getPageContext() {
        return (PageContext)this.context.get("com.opensymphony.xwork2.dispatcher.PageContext");
    }

    @Override
    public String toString() {
        return "AttributeMap {request=" + this.toStringSafe(this.context.get("request")) + ", session=" + this.toStringSafe(this.context.get("session")) + ", application=" + this.toStringSafe(this.context.get("application")) + '}';
    }

    private String toStringSafe(Object obj) {
        try {
            if (obj != null) {
                return String.valueOf(obj);
            }
            return "";
        }
        catch (Exception e) {
            return "Exception thrown: " + e;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AttributeMap)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AttributeMap that = (AttributeMap)o;
        return Objects.equals(this.context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.context);
    }
}

