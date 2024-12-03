/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes.pipesiterator;

import org.apache.tika.pipes.pipesiterator.TotalCountResult;

public interface TotalCounter {
    public void startTotalCount();

    public TotalCountResult getTotalCount();
}

