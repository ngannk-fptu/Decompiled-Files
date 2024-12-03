/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.transaction.backend.jta.internal.synchronization;

import java.io.Serializable;
import org.hibernate.engine.spi.SessionImplementor;

@Deprecated
public interface ManagedFlushChecker
extends Serializable {
    public boolean shouldDoManagedFlush(SessionImplementor var1);
}

