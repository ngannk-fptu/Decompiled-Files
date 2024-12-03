/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.uri.rules;

import com.sun.jersey.server.impl.uri.rules.PatternRulePair;
import com.sun.jersey.spi.uri.rules.UriMatchResultContext;
import com.sun.jersey.spi.uri.rules.UriRules;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.MatchResult;

public final class AtomicMatchingPatterns<R>
implements UriRules<R> {
    private final Collection<PatternRulePair<R>> rules;

    public AtomicMatchingPatterns(Collection<PatternRulePair<R>> rules) {
        this.rules = rules;
    }

    @Override
    public Iterator<R> match(CharSequence path, UriMatchResultContext resultContext) {
        if (resultContext.isTracingEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("match path \"").append(path).append("\" -> ");
            boolean first = true;
            for (PatternRulePair<R> prp : this.rules) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append("\"").append(prp.p.toString()).append("\"");
                first = false;
            }
            resultContext.trace(sb.toString());
        }
        for (PatternRulePair<R> prp : this.rules) {
            MatchResult mr = prp.p.match(path);
            if (mr == null) continue;
            resultContext.setMatchResult(mr);
            return new SingleEntryIterator(prp.r);
        }
        return new EmptyIterator();
    }

    private static final class EmptyIterator<T>
    implements Iterator<T> {
        private EmptyIterator() {
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public T next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static final class SingleEntryIterator<T>
    implements Iterator<T> {
        private T t;

        SingleEntryIterator(T t) {
            this.t = t;
        }

        @Override
        public boolean hasNext() {
            return this.t != null;
        }

        @Override
        public T next() {
            if (this.hasNext()) {
                T _t = this.t;
                this.t = null;
                return _t;
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

