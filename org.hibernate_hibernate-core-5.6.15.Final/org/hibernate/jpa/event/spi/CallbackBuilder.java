/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.event.spi;

import org.hibernate.mapping.Property;

@Deprecated
public interface CallbackBuilder {
    public void buildCallbacksForEntity(Class var1, CallbackRegistrar var2);

    public void buildCallbacksForEmbeddable(Property var1, Class var2, CallbackRegistrar var3);

    public void release();

    @Deprecated
    public static interface CallbackRegistrar
    extends org.hibernate.jpa.event.spi.CallbackRegistrar {
    }
}

