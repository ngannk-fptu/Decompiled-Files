/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.iterator;

import java.util.Collection;
import java.util.Iterator;
import javax.jcr.RangeIterator;
import javax.jcr.version.Version;
import javax.jcr.version.VersionIterator;
import org.apache.jackrabbit.commons.iterator.RangeIteratorAdapter;
import org.apache.jackrabbit.commons.iterator.RangeIteratorDecorator;

public class VersionIteratorAdapter
extends RangeIteratorDecorator
implements VersionIterator {
    public static final VersionIterator EMPTY = new VersionIteratorAdapter(RangeIteratorAdapter.EMPTY);

    public VersionIteratorAdapter(RangeIterator iterator) {
        super(iterator);
    }

    public VersionIteratorAdapter(Iterator iterator) {
        super(new RangeIteratorAdapter(iterator));
    }

    public VersionIteratorAdapter(Collection collection) {
        super(new RangeIteratorAdapter(collection));
    }

    @Override
    public Version nextVersion() {
        return (Version)this.next();
    }
}

