/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Mono
 */
package org.springframework.data.mapping.callback;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.mapping.callback.DefaultReactiveEntityCallbacks;
import org.springframework.data.mapping.callback.EntityCallback;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

public interface ReactiveEntityCallbacks {
    public void addEntityCallback(EntityCallback<?> var1);

    public <T> Mono<T> callback(Class<? extends EntityCallback> var1, T var2, Object ... var3);

    public static ReactiveEntityCallbacks create(EntityCallback<?> ... callbacks) {
        ReactiveEntityCallbacks entityCallbacks = ReactiveEntityCallbacks.create();
        for (EntityCallback<?> callback : callbacks) {
            entityCallbacks.addEntityCallback(callback);
        }
        return entityCallbacks;
    }

    public static ReactiveEntityCallbacks create() {
        return new DefaultReactiveEntityCallbacks();
    }

    public static ReactiveEntityCallbacks create(BeanFactory beanFactory) {
        Assert.notNull((Object)beanFactory, (String)"Context must not be null!");
        return new DefaultReactiveEntityCallbacks(beanFactory);
    }
}

