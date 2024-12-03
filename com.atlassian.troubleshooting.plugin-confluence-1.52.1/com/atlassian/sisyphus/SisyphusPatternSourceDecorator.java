/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sisyphus;

import com.atlassian.sisyphus.SisyphusPattern;
import com.atlassian.sisyphus.SisyphusPatternSource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class SisyphusPatternSourceDecorator
implements SisyphusPatternSource {
    private final List<SisyphusPatternSource> delegateSources = new ArrayList<SisyphusPatternSource>();

    public void add(SisyphusPatternSource source) {
        this.delegateSources.add(source);
    }

    @Override
    public SisyphusPattern getPattern(String patternID) {
        for (SisyphusPatternSource src : this.delegateSources) {
            SisyphusPattern pattern = src.getPattern(patternID);
            if (pattern == null) continue;
            return pattern;
        }
        return null;
    }

    @Override
    public int size() {
        int size = 0;
        for (SisyphusPatternSource src : this.delegateSources) {
            size += src.size();
        }
        return size;
    }

    @Override
    public Iterator<SisyphusPattern> iterator() {
        return new IterableSequencer(this.delegateSources);
    }

    static class IterableSequencer
    implements Iterator<SisyphusPattern> {
        private final Iterator<? extends Iterable<SisyphusPattern>> sources;
        private Iterator<SisyphusPattern> currentIterator;

        public IterableSequencer(Iterable<? extends Iterable<SisyphusPattern>> sources) {
            this.sources = sources.iterator();
        }

        @Override
        public boolean hasNext() {
            if (this.currentIterator == null) {
                if (this.sources.hasNext()) {
                    this.currentIterator = this.sources.next().iterator();
                    return this.hasNext();
                }
                return false;
            }
            if (this.currentIterator.hasNext()) {
                return true;
            }
            if (this.sources.hasNext()) {
                this.currentIterator = this.sources.next().iterator();
                return this.hasNext();
            }
            return false;
        }

        @Override
        public SisyphusPattern next() {
            if (this.hasNext()) {
                return this.currentIterator.next();
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            if (this.currentIterator != null) {
                this.currentIterator.remove();
            }
        }
    }
}

