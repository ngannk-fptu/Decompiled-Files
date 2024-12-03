/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.uri.rules;

import com.sun.jersey.spi.uri.rules.UriMatchResultContext;
import com.sun.jersey.spi.uri.rules.UriRules;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class CombiningMatchingPatterns<R>
implements UriRules<R> {
    private final List<UriRules<R>> rs;

    public CombiningMatchingPatterns(List<UriRules<R>> rs) {
        this.rs = rs;
    }

    @Override
    public Iterator<R> match(CharSequence path, UriMatchResultContext resultContext) {
        return new XInterator(path, resultContext);
    }

    private final class XInterator
    implements Iterator<R> {
        private final CharSequence path;
        private final UriMatchResultContext resultContext;
        private Iterator<R> ruleIterator;
        private Iterator<UriRules<R>> rulesIterator;
        private R r;

        XInterator(CharSequence path, UriMatchResultContext resultContext) {
            this.path = path;
            this.resultContext = resultContext;
            this.rulesIterator = CombiningMatchingPatterns.this.rs.iterator();
            this.ruleIterator = this.rulesIterator.next().match(path, resultContext);
        }

        @Override
        public boolean hasNext() {
            if (this.r != null) {
                return true;
            }
            if (this.ruleIterator.hasNext()) {
                this.r = this.ruleIterator.next();
                return true;
            }
            while (this.rulesIterator.hasNext()) {
                this.ruleIterator = this.rulesIterator.next().match(this.path, this.resultContext);
                if (!this.ruleIterator.hasNext()) continue;
                this.r = this.ruleIterator.next();
                return true;
            }
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

