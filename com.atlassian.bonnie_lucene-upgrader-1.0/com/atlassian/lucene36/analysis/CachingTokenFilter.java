/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.TokenFilter;
import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.util.AttributeSource;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class CachingTokenFilter
extends TokenFilter {
    private List<AttributeSource.State> cache = null;
    private Iterator<AttributeSource.State> iterator = null;
    private AttributeSource.State finalState;

    public CachingTokenFilter(TokenStream input) {
        super(input);
    }

    public final boolean incrementToken() throws IOException {
        if (this.cache == null) {
            this.cache = new LinkedList<AttributeSource.State>();
            this.fillCache();
            this.iterator = this.cache.iterator();
        }
        if (!this.iterator.hasNext()) {
            return false;
        }
        this.restoreState(this.iterator.next());
        return true;
    }

    public final void end() throws IOException {
        if (this.finalState != null) {
            this.restoreState(this.finalState);
        }
    }

    public void reset() throws IOException {
        if (this.cache != null) {
            this.iterator = this.cache.iterator();
        }
    }

    private void fillCache() throws IOException {
        while (this.input.incrementToken()) {
            this.cache.add(this.captureState());
        }
        this.input.end();
        this.finalState = this.captureState();
    }
}

