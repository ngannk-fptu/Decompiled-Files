/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.PersistenceException
 */
package org.hibernate.jpa.event.internal;

import java.util.HashMap;
import javax.persistence.PersistenceException;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.jpa.event.internal.CallbackRegistryImplementor;
import org.hibernate.jpa.event.spi.Callback;
import org.hibernate.jpa.event.spi.CallbackType;

final class CallbackRegistryImpl
implements CallbackRegistryImplementor {
    private HashMap<Class, Callback[]> preCreates = new HashMap();
    private HashMap<Class, Callback[]> postCreates = new HashMap();
    private HashMap<Class, Callback[]> preRemoves = new HashMap();
    private HashMap<Class, Callback[]> postRemoves = new HashMap();
    private HashMap<Class, Callback[]> preUpdates = new HashMap();
    private HashMap<Class, Callback[]> postUpdates = new HashMap();
    private HashMap<Class, Callback[]> postLoads = new HashMap();

    CallbackRegistryImpl() {
    }

    @Override
    public boolean hasRegisteredCallbacks(Class entityClass, CallbackType callbackType) {
        HashMap<Class, Callback[]> map = this.determineAppropriateCallbackMap(callbackType);
        return this.notEmpty(map.get(entityClass));
    }

    @Override
    public void registerCallbacks(Class entityClass, Callback[] callbacks) {
        if (callbacks == null || callbacks.length == 0) {
            return;
        }
        for (Callback callback : callbacks) {
            HashMap<Class, Callback[]> map = this.determineAppropriateCallbackMap(callback.getCallbackType());
            Callback[] entityCallbacks = map.get(entityClass);
            if (entityCallbacks == null) {
                entityCallbacks = new Callback[]{};
            }
            entityCallbacks = ArrayHelper.join(entityCallbacks, callback);
            map.put(entityClass, entityCallbacks);
        }
    }

    @Override
    public void preCreate(Object bean) {
        this.callback(this.preCreates.get(bean.getClass()), bean);
    }

    private boolean notEmpty(Callback[] callbacks) {
        return callbacks != null && callbacks.length > 0;
    }

    @Override
    public void postCreate(Object bean) {
        this.callback(this.postCreates.get(bean.getClass()), bean);
    }

    @Override
    public boolean preUpdate(Object bean) {
        return this.callback(this.preUpdates.get(bean.getClass()), bean);
    }

    @Override
    public void postUpdate(Object bean) {
        this.callback(this.postUpdates.get(bean.getClass()), bean);
    }

    @Override
    public void preRemove(Object bean) {
        this.callback(this.preRemoves.get(bean.getClass()), bean);
    }

    @Override
    public void postRemove(Object bean) {
        this.callback(this.postRemoves.get(bean.getClass()), bean);
    }

    @Override
    public boolean postLoad(Object bean) {
        return this.callback(this.postLoads.get(bean.getClass()), bean);
    }

    private boolean callback(Callback[] callbacks, Object bean) {
        if (callbacks != null && callbacks.length != 0) {
            for (Callback callback : callbacks) {
                callback.performCallback(bean);
            }
            return true;
        }
        return false;
    }

    private HashMap<Class, Callback[]> determineAppropriateCallbackMap(CallbackType callbackType) {
        if (callbackType == CallbackType.PRE_PERSIST) {
            return this.preCreates;
        }
        if (callbackType == CallbackType.POST_PERSIST) {
            return this.postCreates;
        }
        if (callbackType == CallbackType.PRE_REMOVE) {
            return this.preRemoves;
        }
        if (callbackType == CallbackType.POST_REMOVE) {
            return this.postRemoves;
        }
        if (callbackType == CallbackType.PRE_UPDATE) {
            return this.preUpdates;
        }
        if (callbackType == CallbackType.POST_UPDATE) {
            return this.postUpdates;
        }
        if (callbackType == CallbackType.POST_LOAD) {
            return this.postLoads;
        }
        throw new PersistenceException("Unrecognized JPA callback type [" + (Object)((Object)callbackType) + "]");
    }

    @Override
    public void release() {
        this.preCreates.clear();
        this.postCreates.clear();
        this.preRemoves.clear();
        this.postRemoves.clear();
        this.preUpdates.clear();
        this.postUpdates.clear();
        this.postLoads.clear();
    }

    @Override
    public boolean hasPostCreateCallbacks(Class entityClass) {
        return this.notEmpty(this.preCreates.get(entityClass));
    }

    @Override
    public boolean hasPostUpdateCallbacks(Class entityClass) {
        return this.notEmpty(this.postUpdates.get(entityClass));
    }

    @Override
    public boolean hasPostRemoveCallbacks(Class entityClass) {
        return this.notEmpty(this.postRemoves.get(entityClass));
    }

    @Override
    public boolean hasRegisteredCallbacks(Class entityClass, Class annotationClass) {
        HashMap<Class, Callback[]> map = this.determineAppropriateCallbackMap(this.toCallbackType(annotationClass));
        return map != null && map.containsKey(entityClass);
    }

    private CallbackType toCallbackType(Class annotationClass) {
        if (annotationClass == CallbackType.POST_LOAD.getCallbackAnnotation()) {
            return CallbackType.POST_LOAD;
        }
        if (annotationClass == CallbackType.PRE_PERSIST.getCallbackAnnotation()) {
            return CallbackType.PRE_PERSIST;
        }
        if (annotationClass == CallbackType.POST_PERSIST.getCallbackAnnotation()) {
            return CallbackType.POST_PERSIST;
        }
        if (annotationClass == CallbackType.PRE_UPDATE.getCallbackAnnotation()) {
            return CallbackType.PRE_UPDATE;
        }
        if (annotationClass == CallbackType.POST_UPDATE.getCallbackAnnotation()) {
            return CallbackType.POST_UPDATE;
        }
        if (annotationClass == CallbackType.PRE_REMOVE.getCallbackAnnotation()) {
            return CallbackType.PRE_REMOVE;
        }
        if (annotationClass == CallbackType.POST_REMOVE.getCallbackAnnotation()) {
            return CallbackType.POST_REMOVE;
        }
        throw new PersistenceException("Unrecognized JPA callback annotation [" + annotationClass + "]");
    }
}

