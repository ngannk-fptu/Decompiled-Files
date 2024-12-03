/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.uri.rules;

import com.sun.jersey.server.impl.uri.rules.PatternRulePair;
import com.sun.jersey.spi.uri.rules.UriMatchResultContext;
import com.sun.jersey.spi.uri.rules.UriRules;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.MatchResult;

public final class SequentialMatchingPatterns<R>
implements UriRules<R> {
    private final List<PatternRulePair<R>> rules;

    public SequentialMatchingPatterns(List<PatternRulePair<R>> rules) {
        this.rules = rules;
    }

    @Override
    public Iterator<R> match(CharSequence path, UriMatchResultContext resultContext) {
        return new XInterator(path, resultContext);
    }

    private final class XInterator
    implements Iterator<R> {
        private final CharSequence path;
        private final UriMatchResultContext resultContext;
        private final Iterator<PatternRulePair<R>> i;
        private R r;

        XInterator(CharSequence path, UriMatchResultContext resultContext) {
            this.path = path;
            this.resultContext = resultContext;
            this.i = SequentialMatchingPatterns.this.rules.iterator();
        }

        @Override
        public boolean hasNext() {
            if (this.r != null) {
                return true;
            }
            while (this.i.hasNext()) {
                PatternRulePair prp = this.i.next();
                MatchResult mr = prp.p.match(this.path);
                if (mr == null) continue;
                this.resultContext.setMatchResult(mr);
                this.r = prp.r;
                return true;
            }
            this.r = null;
            return false;
        }

        @Override
        public R next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            Object _r = this.r;
            this.r = null;
            return _r;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

