/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.diagnostics.ipd.index.items;

import com.atlassian.confluence.internal.diagnostics.ipd.index.items.IndexQueueItemTypeMxBean;
import java.util.concurrent.atomic.AtomicLong;

public class IndexQueueItemType
implements IndexQueueItemTypeMxBean {
    private final AtomicLong value = new AtomicLong(0L);
    private final AtomicLong total = new AtomicLong(0L);

    @Override
    public Long get_value() {
        return this.value.get();
    }

    @Override
    public Long get_total() {
        return this.total.get();
    }

    public void setValue(long value) {
        this.value.set(value);
    }

    public void setTotal(long total) {
        this.total.set(total);
    }
}

