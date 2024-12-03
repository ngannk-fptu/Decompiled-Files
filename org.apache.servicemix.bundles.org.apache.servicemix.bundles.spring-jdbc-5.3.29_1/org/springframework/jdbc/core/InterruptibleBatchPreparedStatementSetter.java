/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.core;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

public interface InterruptibleBatchPreparedStatementSetter
extends BatchPreparedStatementSetter {
    public boolean isBatchExhausted(int var1);
}

