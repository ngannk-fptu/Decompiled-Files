/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.context.request;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.AbstractRequestAttributesScope;
import org.springframework.web.context.request.RequestContextHolder;

public class SessionScope
extends AbstractRequestAttributesScope {
    @Override
    protected int getScope() {
        return 1;
    }

    @Override
    public String getConversationId() {
        return RequestContextHolder.currentRequestAttributes().getSessionId();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        Object mutex;
        Object object = mutex = RequestContextHolder.currentRequestAttributes().getSessionMutex();
        synchronized (object) {
            return super.get(name, objectFactory);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    public Object remove(String name) {
        Object mutex;
        Object object = mutex = RequestContextHolder.currentRequestAttributes().getSessionMutex();
        synchronized (object) {
            return super.remove(name);
        }
    }
}

