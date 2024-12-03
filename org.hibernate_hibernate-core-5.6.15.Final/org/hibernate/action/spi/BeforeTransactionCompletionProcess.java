/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.action.spi;

import org.hibernate.engine.spi.SessionImplementor;

public interface BeforeTransactionCompletionProcess {
    public void doBeforeTransactionCompletion(SessionImplementor var1);
}

