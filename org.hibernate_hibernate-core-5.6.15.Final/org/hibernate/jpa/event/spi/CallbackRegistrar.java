/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.event.spi;

import org.hibernate.jpa.event.spi.Callback;
import org.hibernate.jpa.event.spi.CallbackRegistry;

public interface CallbackRegistrar
extends CallbackRegistry {
    public void registerCallbacks(Class var1, Callback[] var2);
}

