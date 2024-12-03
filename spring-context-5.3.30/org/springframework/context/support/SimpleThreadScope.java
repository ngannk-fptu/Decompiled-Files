/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.ObjectFactory
 *  org.springframework.beans.factory.config.Scope
 *  org.springframework.core.NamedThreadLocal
 *  org.springframework.lang.Nullable
 */
package org.springframework.context.support;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

public class SimpleThreadScope
implements Scope {
    private static final Log logger = LogFactory.getLog(SimpleThreadScope.class);
    private final ThreadLocal<Map<String, Object>> threadScope = new NamedThreadLocal<Map<String, Object>>("SimpleThreadScope"){

        protected Map<String, Object> initialValue() {
            return new HashMap<String, Object>();
        }
    };

    public Object get(String name, ObjectFactory<?> objectFactory) {
        Map<String, Object> scope = this.threadScope.get();
        Object scopedObject = scope.get(name);
        if (scopedObject == null) {
            scopedObject = objectFactory.getObject();
            scope.put(name, scopedObject);
        }
        return scopedObject;
    }

    @Nullable
    public Object remove(String name) {
        Map<String, Object> scope = this.threadScope.get();
        return scope.remove(name);
    }

    public void registerDestructionCallback(String name, Runnable callback) {
        logger.warn((Object)"SimpleThreadScope does not support destruction callbacks. Consider using RequestScope in a web environment.");
    }

    @Nullable
    public Object resolveContextualObject(String key) {
        return null;
    }

    public String getConversationId() {
        return Thread.currentThread().getName();
    }
}

