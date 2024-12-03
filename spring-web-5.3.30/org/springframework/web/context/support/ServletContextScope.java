/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.ObjectFactory
 *  org.springframework.beans.factory.config.Scope
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.context.support;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ServletContextScope
implements Scope,
DisposableBean {
    private final ServletContext servletContext;
    private final Map<String, Runnable> destructionCallbacks = new LinkedHashMap<String, Runnable>();

    public ServletContextScope(ServletContext servletContext) {
        Assert.notNull((Object)servletContext, (String)"ServletContext must not be null");
        this.servletContext = servletContext;
    }

    public Object get(String name, ObjectFactory<?> objectFactory) {
        Object scopedObject = this.servletContext.getAttribute(name);
        if (scopedObject == null) {
            scopedObject = objectFactory.getObject();
            this.servletContext.setAttribute(name, scopedObject);
        }
        return scopedObject;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public Object remove(String name) {
        Object scopedObject = this.servletContext.getAttribute(name);
        if (scopedObject != null) {
            Map<String, Runnable> map = this.destructionCallbacks;
            synchronized (map) {
                this.destructionCallbacks.remove(name);
            }
            this.servletContext.removeAttribute(name);
            return scopedObject;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void registerDestructionCallback(String name, Runnable callback) {
        Map<String, Runnable> map = this.destructionCallbacks;
        synchronized (map) {
            this.destructionCallbacks.put(name, callback);
        }
    }

    @Nullable
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Nullable
    public String getConversationId() {
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void destroy() {
        Map<String, Runnable> map = this.destructionCallbacks;
        synchronized (map) {
            for (Runnable runnable : this.destructionCallbacks.values()) {
                runnable.run();
            }
            this.destructionCallbacks.clear();
        }
    }
}

