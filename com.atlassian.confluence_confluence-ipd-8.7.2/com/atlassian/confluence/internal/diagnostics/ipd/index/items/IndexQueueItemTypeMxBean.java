/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.diagnostics.ipd.index.items;

import javax.management.MXBean;

@MXBean
public interface IndexQueueItemTypeMxBean {
    public Long get_value();

    public Long get_total();
}

