/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.service.spi;

import org.hibernate.Incubating;

@FunctionalInterface
@Incubating
public interface EventActionWithParameter<T, U, X> {
    public void applyEventToListener(T var1, U var2, X var3);
}

