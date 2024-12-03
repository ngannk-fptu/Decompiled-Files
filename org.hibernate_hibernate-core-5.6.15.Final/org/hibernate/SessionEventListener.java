/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.io.Serializable;

public interface SessionEventListener
extends Serializable {
    public void transactionCompletion(boolean var1);

    public void jdbcConnectionAcquisitionStart();

    public void jdbcConnectionAcquisitionEnd();

    public void jdbcConnectionReleaseStart();

    public void jdbcConnectionReleaseEnd();

    public void jdbcPrepareStatementStart();

    public void jdbcPrepareStatementEnd();

    public void jdbcExecuteStatementStart();

    public void jdbcExecuteStatementEnd();

    public void jdbcExecuteBatchStart();

    public void jdbcExecuteBatchEnd();

    public void cachePutStart();

    public void cachePutEnd();

    public void cacheGetStart();

    public void cacheGetEnd(boolean var1);

    public void flushStart();

    public void flushEnd(int var1, int var2);

    public void partialFlushStart();

    public void partialFlushEnd(int var1, int var2);

    public void dirtyCalculationStart();

    public void dirtyCalculationEnd(boolean var1);

    public void end();
}

