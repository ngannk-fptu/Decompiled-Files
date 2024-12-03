/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import org.hibernate.SessionBuilder;
import org.hibernate.engine.spi.SessionOwner;

public interface SessionBuilderImplementor<T extends SessionBuilder>
extends SessionBuilder<T> {
    @Deprecated
    public T owner(SessionOwner var1);
}

