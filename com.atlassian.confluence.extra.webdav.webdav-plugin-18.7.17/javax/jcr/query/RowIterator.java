/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.query;

import javax.jcr.RangeIterator;
import javax.jcr.query.Row;

public interface RowIterator
extends RangeIterator {
    public Row nextRow();
}

