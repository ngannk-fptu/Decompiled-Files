/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.ObjectFactory
 *  org.springframework.beans.factory.config.Scope
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.context.request;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public abstract class AbstractRequestAttributesScope
implements Scope {
    public Object get(String name, ObjectFactory<?> objectFactory) {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        Object scopedObject = attributes.getAttribute(name, this.getScope());
        if (scopedObject == null) {
            scopedObject = objectFactory.getObject();
            attributes.setAttribute(name, scopedObject, this.getScope());
            Object retrievedObject = attributes.getAttribute(name, this.getScope());
            if (retrievedObject != null) {
                scopedObject = retrievedObject;
            }
        }
        return scopedObject;
    }

    @Nullable
    public Object remove(String name) {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        Object scopedObject = attributes.getAttribute(name, this.getScope());
        if (scopedObject != null) {
            attributes.removeAttribute(name, this.getScope());
            return scopedObject;
        }
        return null;
    }

    public void registerDestructionCallback(String name, Runnable callback) {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        attributes.registerDestructionCallback(name, callback, this.getScope());
    }

    @Nullable
    public Object resolveContextualObject(String key) {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        return attributes.resolveReference(key);
    }

    protected abstract int getScope();
}

