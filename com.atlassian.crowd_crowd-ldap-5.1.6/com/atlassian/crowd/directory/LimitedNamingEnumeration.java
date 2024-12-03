/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.directory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.NoSuchElementException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public class LimitedNamingEnumeration<T>
implements NamingEnumeration<T> {
    private final NamingEnumeration<T> ne;
    private final int limit;
    private int count = 0;

    public LimitedNamingEnumeration(NamingEnumeration<T> ne, int limit) {
        this.ne = (NamingEnumeration)Preconditions.checkNotNull(ne);
        this.limit = limit;
    }

    @VisibleForTesting
    public int getLimit() {
        return this.limit;
    }

    @Override
    public void close() throws NamingException {
        this.ne.close();
    }

    @Override
    public boolean hasMore() throws NamingException {
        return this.count < this.limit && this.ne.hasMore();
    }

    @Override
    public boolean hasMoreElements() {
        return this.count < this.limit && this.ne.hasMoreElements();
    }

    @Override
    public T next() throws NamingException {
        if (this.count < this.limit) {
            ++this.count;
            return this.ne.next();
        }
        throw new NoSuchElementException(this.count + " items already yielded");
    }

    @Override
    public T nextElement() {
        if (this.count < this.limit) {
            ++this.count;
            return (T)this.ne.nextElement();
        }
        throw new NoSuchElementException(this.count + " items already yielded");
    }
}

