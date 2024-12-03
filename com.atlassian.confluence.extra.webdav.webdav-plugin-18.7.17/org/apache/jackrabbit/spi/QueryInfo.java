/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import javax.jcr.RangeIterator;

public interface QueryInfo {
    public RangeIterator getRows();

    public String[] getColumnNames();

    public String[] getSelectorNames();
}

