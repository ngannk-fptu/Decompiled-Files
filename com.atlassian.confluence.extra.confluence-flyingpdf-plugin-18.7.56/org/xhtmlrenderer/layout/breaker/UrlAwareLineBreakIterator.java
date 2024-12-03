/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout.breaker;

import java.text.BreakIterator;
import java.text.CharacterIterator;

public class UrlAwareLineBreakIterator
extends BreakIterator {
    private static final String BREAKING_CHARS = ".,:;!?- \n\r\t/";
    private BreakIterator delegate = BreakIterator.getLineInstance();
    private String text;
    private Range currentRange;

    @Override
    public int preceding(int offset) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int last() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int previous() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int next() {
        this.checkNotAheadOfDelegate();
        Range searchRange = this.currentRange;
        if (this.isDelegateInSync()) {
            boolean reachedEnd = this.advanceDelegate();
            if (reachedEnd) {
                return -1;
            }
            if ("://".equals(this.substring(new Range(this.currentRange.getStop(), -1, 2)))) {
                searchRange = searchRange.withStart(this.currentRange.getStop() + 2);
                this.advanceDelegate();
            }
        }
        searchRange = searchRange.withStop(this.currentRange.getStop());
        int nextSlash = this.findSlashInRange(searchRange = this.trimSearchRange(searchRange));
        this.currentRange = this.currentRange.withStart(nextSlash > -1 ? nextSlash : this.delegate.current());
        return this.currentRange.getStart();
    }

    private Range trimSearchRange(Range searchRange) {
        while (searchRange.getStart() < this.currentRange.getStop() && BREAKING_CHARS.indexOf(this.text.charAt(searchRange.getStart())) > -1) {
            searchRange = searchRange.incrementStart();
        }
        while (searchRange.getStop() > searchRange.getStart() && BREAKING_CHARS.indexOf(this.text.charAt(searchRange.getStop() - 1)) > -1) {
            searchRange = searchRange.decrementStop();
        }
        return searchRange;
    }

    private int findSlashInRange(Range searchRange) {
        int nextSlash = this.text.indexOf(47, searchRange.getStart());
        return nextSlash < searchRange.getStop() ? nextSlash : -1;
    }

    private String substring(Range range) {
        return this.text.substring(Math.max(0, range.getStart()), Math.min(this.text.length(), range.getStop()));
    }

    private void checkNotAheadOfDelegate() {
        if (this.currentRange.getStart() > this.delegate.current()) {
            throw new IllegalStateException("Iterator ahead of delegate.");
        }
    }

    private boolean isDelegateInSync() {
        return this.currentRange.getStart() == this.delegate.current();
    }

    private boolean advanceDelegate() {
        int next = this.delegate.next();
        this.currentRange = this.currentRange.withStop(next);
        return next == -1;
    }

    @Override
    public int next(int n) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean isBoundary(int offset) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int following(int offset) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int first() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void setText(CharacterIterator newText) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int current() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public CharacterIterator getText() {
        return this.delegate.getText();
    }

    @Override
    public void setText(String newText) {
        this.delegate.setText(newText);
        this.text = newText;
        this.currentRange = new Range(this.delegate.current(), this.delegate.current());
    }

    private static class Range {
        int start;
        int stop;

        public Range(int start, int stop) {
            this.start = start;
            this.stop = Math.max(start, stop);
        }

        public Range(int referencePoint, int startOffset, int stopOffset) {
            this(referencePoint + startOffset, referencePoint + stopOffset);
        }

        public Range withStart(int start) {
            return new Range(start, this.stop);
        }

        public Range withStop(int stop) {
            return new Range(this.start, stop);
        }

        public Range incrementStart() {
            int newStart = this.start + 1;
            return new Range(newStart, Math.max(newStart, this.stop));
        }

        public Range decrementStop() {
            int newStop = this.stop + -1;
            return new Range(Math.min(this.start, newStop), newStop);
        }

        public int getStart() {
            return this.start;
        }

        public int getStop() {
            return this.stop;
        }

        public String toString() {
            return "[" + this.start + ", " + this.stop + ")";
        }
    }
}

