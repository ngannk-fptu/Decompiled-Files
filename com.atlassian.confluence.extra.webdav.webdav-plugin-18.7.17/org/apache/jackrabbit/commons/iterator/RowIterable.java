/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.iterator;

import java.util.Iterator;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

@Deprecated
public class RowIterable
implements Iterable<Row> {
    private final RowIterator iterator;

    public RowIterable(RowIterator iterator) {
        this.iterator = iterator;
    }

    @Override
    public Iterator<Row> iterator() {
        return this.iterator;
    }
}

