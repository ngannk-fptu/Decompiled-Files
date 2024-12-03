/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction.support;

import java.io.Flushable;

public interface SmartTransactionObject
extends Flushable {
    public boolean isRollbackOnly();

    @Override
    public void flush();
}

