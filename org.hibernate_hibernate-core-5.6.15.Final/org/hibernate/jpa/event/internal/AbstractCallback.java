/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.event.internal;

import org.hibernate.jpa.event.spi.Callback;
import org.hibernate.jpa.event.spi.CallbackType;

abstract class AbstractCallback
implements Callback {
    private final CallbackType callbackType;

    AbstractCallback(CallbackType callbackType) {
        this.callbackType = callbackType;
    }

    @Override
    public CallbackType getCallbackType() {
        return this.callbackType;
    }
}

