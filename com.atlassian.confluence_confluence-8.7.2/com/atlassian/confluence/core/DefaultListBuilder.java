/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.core.ListBuilderCallback;
import java.util.Iterator;
import java.util.List;

public class DefaultListBuilder<T>
implements ListBuilder<T> {
    private final ListBuilderCallback<T> callback;

    public static <T> DefaultListBuilder<T> newInstance(ListBuilderCallback<T> callback) {
        return new DefaultListBuilder<T>(callback);
    }

    private DefaultListBuilder(ListBuilderCallback<T> callback) {
        this.callback = callback;
    }

    @Override
    public List<T> getRange(int startIndex, int endIndex) {
        if (endIndex < startIndex) {
            throw new IllegalArgumentException("End of range is before beginning. (" + startIndex + ", " + endIndex + ")");
        }
        return this.getPage(startIndex, 1 + (endIndex - startIndex));
    }

    @Override
    public List<T> getPage(int offset, int maxResults) {
        if (offset < 0) {
            throw new IllegalArgumentException("Start of page can not be a negative number: " + offset);
        }
        if (maxResults < 0) {
            throw new IllegalArgumentException("Max results not set, or set to negative number: " + maxResults);
        }
        return this.callback.getElements(offset, maxResults);
    }

    @Override
    public Iterator<List<T>> iterator() {
        return new DefaultListBuilderIterator();
    }

    @Override
    public int getAvailableSize() {
        return this.callback.getAvailableSize();
    }

    private class DefaultListBuilderIterator
    implements Iterator<List<T>> {
        private static final int PAGE_SIZE = 500;
        private final int size;
        private int i;

        private DefaultListBuilderIterator() {
            this.size = DefaultListBuilder.this.getAvailableSize();
            this.i = 0;
        }

        @Override
        public boolean hasNext() {
            return this.i < this.size;
        }

        @Override
        public List<T> next() {
            List page = DefaultListBuilder.this.getPage(this.i, 500);
            this.i += 500;
            return page;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove items from DefaultListBuilder");
        }
    }
}

