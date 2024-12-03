/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import org.hibernate.resource.transaction.backend.jta.internal.synchronization.AfterCompletionAction;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.ExceptionMapper;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.ManagedFlushChecker;

@Deprecated
public interface SessionOwner {
    public boolean shouldAutoCloseSession();

    public ExceptionMapper getExceptionMapper();

    public AfterCompletionAction getAfterCompletionAction();

    public ManagedFlushChecker getManagedFlushChecker();
}

