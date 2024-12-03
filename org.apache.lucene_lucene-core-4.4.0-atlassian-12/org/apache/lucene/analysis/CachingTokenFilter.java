/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.AttributeSource;

public final class CachingTokenFilter
extends TokenFilter {
    private List<AttributeSource.State> cache = null;
    private Iterator<AttributeSource.State> iterator = null;
    private AttributeSource.State finalState;

    public CachingTokenFilter(TokenStream input) {
        super(input);
    }

    @Override
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

    @Override
    public final void end() {
        if (this.finalState != null) {
            this.restoreState(this.finalState);
        }
    }

    @Override
    public void reset() {
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

