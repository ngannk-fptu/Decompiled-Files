/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.iterator;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.jcr.RangeIterator;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.AccessControlPolicyIterator;
import org.apache.jackrabbit.commons.iterator.RangeIteratorAdapter;
import org.apache.jackrabbit.commons.iterator.RangeIteratorDecorator;

public class AccessControlPolicyIteratorAdapter
extends RangeIteratorDecorator
implements AccessControlPolicyIterator {
    public static final AccessControlPolicyIterator EMPTY = new AccessControlPolicyIteratorAdapter(RangeIteratorAdapter.EMPTY);

    public AccessControlPolicyIteratorAdapter(RangeIterator iterator) {
        super(iterator);
    }

    public AccessControlPolicyIteratorAdapter(Iterator iterator) {
        super(new RangeIteratorAdapter(iterator));
    }

    public AccessControlPolicyIteratorAdapter(Collection collection) {
        super(new RangeIteratorAdapter(collection));
    }

    @Override
    public AccessControlPolicy nextAccessControlPolicy() throws NoSuchElementException {
        return (AccessControlPolicy)this.next();
    }
}

