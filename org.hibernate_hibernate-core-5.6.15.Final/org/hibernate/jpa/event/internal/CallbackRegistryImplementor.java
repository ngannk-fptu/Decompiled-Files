/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.event.internal;

import org.hibernate.jpa.event.spi.CallbackBuilder;
import org.hibernate.jpa.event.spi.CallbackRegistrar;

public interface CallbackRegistryImplementor
extends CallbackRegistrar,
CallbackBuilder.CallbackRegistrar {
    public void release();
}

