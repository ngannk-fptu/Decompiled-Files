/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.util.Assert
 */
package org.springframework.data.mapping.callback;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.mapping.callback.DefaultEntityCallbacks;
import org.springframework.data.mapping.callback.EntityCallback;
import org.springframework.util.Assert;

public interface EntityCallbacks {
    public void addEntityCallback(EntityCallback<?> var1);

    public <T> T callback(Class<? extends EntityCallback> var1, T var2, Object ... var3);

    public static EntityCallbacks create(EntityCallback<?> ... callbacks) {
        EntityCallbacks entityCallbacks = EntityCallbacks.create();
        for (EntityCallback<?> callback : callbacks) {
            entityCallbacks.addEntityCallback(callback);
        }
        return entityCallbacks;
    }

    public static EntityCallbacks create() {
        return new DefaultEntityCallbacks();
    }

    public static EntityCallbacks create(BeanFactory beanFactory) {
        Assert.notNull((Object)beanFactory, (String)"Context must not be null!");
        return new DefaultEntityCallbacks(beanFactory);
    }
}

