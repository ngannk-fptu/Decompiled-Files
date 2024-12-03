/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

import com.twelvemonkeys.util.TokenIterator;

public abstract class AbstractTokenIterator
implements TokenIterator {
    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

    @Override
    public final boolean hasMoreTokens() {
        return this.hasNext();
    }

    @Override
    public final String nextToken() {
        return (String)this.next();
    }

    @Override
    public final boolean hasMoreElements() {
        return this.hasNext();
    }

    @Override
    public final String nextElement() {
        return (String)this.next();
    }
}

