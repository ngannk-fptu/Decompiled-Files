/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.event.internal;

import org.hibernate.jpa.event.internal.CallbackRegistryImplementor;
import org.hibernate.jpa.event.spi.Callback;
import org.hibernate.jpa.event.spi.CallbackType;

final class EmptyCallbackRegistryImpl
implements CallbackRegistryImplementor {
    EmptyCallbackRegistryImpl() {
    }

    @Override
    public boolean hasRegisteredCallbacks(Class entityClass, CallbackType callbackType) {
        return false;
    }

    @Override
    public void preCreate(Object entity) {
    }

    @Override
    public void postCreate(Object entity) {
    }

    @Override
    public boolean preUpdate(Object entity) {
        return false;
    }

    @Override
    public void postUpdate(Object entity) {
    }

    @Override
    public void preRemove(Object entity) {
    }

    @Override
    public void postRemove(Object entity) {
    }

    @Override
    public boolean postLoad(Object entity) {
        return false;
    }

    @Override
    public boolean hasPostCreateCallbacks(Class entityClass) {
        return false;
    }

    @Override
    public boolean hasPostUpdateCallbacks(Class entityClass) {
        return false;
    }

    @Override
    public boolean hasPostRemoveCallbacks(Class entityClass) {
        return false;
    }

    @Override
    public boolean hasRegisteredCallbacks(Class entityClass, Class annotationClass) {
        return false;
    }

    @Override
    public void release() {
    }

    @Override
    public void registerCallbacks(Class entityClass, Callback[] callbacks) {
    }
}

