/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.data.support;

import org.springframework.data.domain.Persistable;
import org.springframework.data.support.IsNewStrategy;
import org.springframework.util.Assert;

public enum PersistableIsNewStrategy implements IsNewStrategy
{
    INSTANCE;


    @Override
    public boolean isNew(Object entity) {
        Assert.notNull((Object)entity, (String)"Entity must not be null!");
        if (!(entity instanceof Persistable)) {
            throw new IllegalArgumentException(String.format("Given object of type %s does not implement %s!", entity.getClass(), Persistable.class));
        }
        return ((Persistable)entity).isNew();
    }
}

