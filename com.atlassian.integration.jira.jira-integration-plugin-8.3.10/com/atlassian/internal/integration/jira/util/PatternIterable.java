/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 */
package com.atlassian.internal.integration.jira.util;

import com.google.common.base.Function;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternIterable<T>
implements Iterable<T> {
    private final Function<MatchResult, T> extractor;
    private final Pattern pattern;
    private final CharSequence text;

    public PatternIterable(Pattern pattern, CharSequence text, Function<MatchResult, T> extractor) {
        this.extractor = extractor;
        this.pattern = pattern;
        this.text = text;
    }

    @Override
    public Iterator<T> iterator() {
        return new PatternIterator(this.pattern.matcher(this.text));
    }

    private class PatternIterator
    implements Iterator<T> {
        private final Matcher matcher;
        private MatchResult next;

        public PatternIterator(Matcher matcher) {
            this.matcher = matcher;
            this.setNext();
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public T next() {
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            Object result = PatternIterable.this.extractor.apply((Object)this.next);
            this.setNext();
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void setNext() {
            this.next = this.matcher.find() ? this.matcher.toMatchResult() : null;
        }
    }
}

