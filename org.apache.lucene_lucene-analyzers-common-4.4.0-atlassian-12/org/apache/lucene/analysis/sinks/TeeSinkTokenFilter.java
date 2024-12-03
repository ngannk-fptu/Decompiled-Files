/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.util.AttributeImpl
 *  org.apache.lucene.util.AttributeSource
 *  org.apache.lucene.util.AttributeSource$State
 */
package org.apache.lucene.analysis.sinks;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeSource;

public final class TeeSinkTokenFilter
extends TokenFilter {
    private final List<WeakReference<SinkTokenStream>> sinks = new LinkedList<WeakReference<SinkTokenStream>>();
    private static final SinkFilter ACCEPT_ALL_FILTER = new SinkFilter(){

        @Override
        public boolean accept(AttributeSource source) {
            return true;
        }
    };

    public TeeSinkTokenFilter(TokenStream input) {
        super(input);
    }

    public SinkTokenStream newSinkTokenStream() {
        return this.newSinkTokenStream(ACCEPT_ALL_FILTER);
    }

    public SinkTokenStream newSinkTokenStream(SinkFilter filter) {
        SinkTokenStream sink = new SinkTokenStream(this.cloneAttributes(), filter);
        this.sinks.add(new WeakReference<SinkTokenStream>(sink));
        return sink;
    }

    public void addSinkTokenStream(SinkTokenStream sink) {
        if (!this.getAttributeFactory().equals(sink.getAttributeFactory())) {
            throw new IllegalArgumentException("The supplied sink is not compatible to this tee");
        }
        Iterator it = this.cloneAttributes().getAttributeImplsIterator();
        while (it.hasNext()) {
            sink.addAttributeImpl((AttributeImpl)it.next());
        }
        this.sinks.add(new WeakReference<SinkTokenStream>(sink));
    }

    public void consumeAllTokens() throws IOException {
        while (this.incrementToken()) {
        }
    }

    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            AttributeSource.State state = null;
            for (WeakReference<SinkTokenStream> ref : this.sinks) {
                SinkTokenStream sink = (SinkTokenStream)((Object)ref.get());
                if (sink == null || !sink.accept((AttributeSource)this)) continue;
                if (state == null) {
                    state = this.captureState();
                }
                sink.addState(state);
            }
            return true;
        }
        return false;
    }

    public final void end() throws IOException {
        super.end();
        AttributeSource.State finalState = this.captureState();
        for (WeakReference<SinkTokenStream> ref : this.sinks) {
            SinkTokenStream sink = (SinkTokenStream)((Object)ref.get());
            if (sink == null) continue;
            sink.setFinalState(finalState);
        }
    }

    public static final class SinkTokenStream
    extends TokenStream {
        private final List<AttributeSource.State> cachedStates = new LinkedList<AttributeSource.State>();
        private AttributeSource.State finalState;
        private Iterator<AttributeSource.State> it = null;
        private SinkFilter filter;

        private SinkTokenStream(AttributeSource source, SinkFilter filter) {
            super(source);
            this.filter = filter;
        }

        private boolean accept(AttributeSource source) {
            return this.filter.accept(source);
        }

        private void addState(AttributeSource.State state) {
            if (this.it != null) {
                throw new IllegalStateException("The tee must be consumed before sinks are consumed.");
            }
            this.cachedStates.add(state);
        }

        private void setFinalState(AttributeSource.State finalState) {
            this.finalState = finalState;
        }

        public final boolean incrementToken() {
            if (this.it == null) {
                this.it = this.cachedStates.iterator();
            }
            if (!this.it.hasNext()) {
                return false;
            }
            AttributeSource.State state = this.it.next();
            this.restoreState(state);
            return true;
        }

        public final void end() {
            if (this.finalState != null) {
                this.restoreState(this.finalState);
            }
        }

        public final void reset() {
            this.it = this.cachedStates.iterator();
        }
    }

    public static abstract class SinkFilter {
        public abstract boolean accept(AttributeSource var1);

        public void reset() throws IOException {
        }
    }
}

