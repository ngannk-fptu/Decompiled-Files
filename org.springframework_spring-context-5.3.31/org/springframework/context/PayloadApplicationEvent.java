/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.ResolvableTypeProvider
 *  org.springframework.util.Assert
 */
package org.springframework.context;

import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.util.Assert;

public class PayloadApplicationEvent<T>
extends ApplicationEvent
implements ResolvableTypeProvider {
    private final T payload;

    public PayloadApplicationEvent(Object source, T payload) {
        super(source);
        Assert.notNull(payload, (String)"Payload must not be null");
        this.payload = payload;
    }

    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(this.getClass(), (ResolvableType[])new ResolvableType[]{ResolvableType.forInstance(this.getPayload())});
    }

    public T getPayload() {
        return this.payload;
    }
}

