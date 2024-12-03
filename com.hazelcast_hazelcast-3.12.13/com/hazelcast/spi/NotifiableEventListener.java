/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.spi.EventRegistration;

public interface NotifiableEventListener<S> {
    public void onRegister(S var1, String var2, String var3, EventRegistration var4);

    public void onDeregister(S var1, String var2, String var3, EventRegistration var4);
}

