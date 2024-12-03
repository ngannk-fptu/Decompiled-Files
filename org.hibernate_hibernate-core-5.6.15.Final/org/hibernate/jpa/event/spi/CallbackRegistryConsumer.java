/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.event.spi;

import org.hibernate.jpa.event.spi.CallbackRegistry;

public interface CallbackRegistryConsumer {
    public void injectCallbackRegistry(CallbackRegistry var1);
}

