/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.context.request;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public abstract class AbstractRequestAttributesScope
implements Scope {
    @Override
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

    @Override
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

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        attributes.registerDestructionCallback(name, callback, this.getScope());
    }

    @Override
    @Nullable
    public Object resolveContextualObject(String key) {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        return attributes.resolveReference(key);
    }

    protected abstract int getScope();
}

