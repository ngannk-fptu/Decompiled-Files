/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.event.spi;

import java.io.Serializable;
import org.hibernate.jpa.event.spi.CallbackType;

public interface CallbackRegistry
extends Serializable {
    public boolean hasRegisteredCallbacks(Class var1, CallbackType var2);

    public void preCreate(Object var1);

    public void postCreate(Object var1);

    public boolean preUpdate(Object var1);

    public void postUpdate(Object var1);

    public void preRemove(Object var1);

    public void postRemove(Object var1);

    public boolean postLoad(Object var1);

    @Deprecated
    public boolean hasPostCreateCallbacks(Class var1);

    @Deprecated
    public boolean hasPostUpdateCallbacks(Class var1);

    @Deprecated
    public boolean hasPostRemoveCallbacks(Class var1);

    @Deprecated
    public boolean hasRegisteredCallbacks(Class var1, Class var2);
}

