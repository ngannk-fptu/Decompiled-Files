/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.flyingpdf.html;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BookmarksBuilder {
    private final List<BookmarkEntry> entries = new ArrayList<BookmarkEntry>();
    private final Stack<BookmarkEntry> currentEntryStack = new Stack();

    public void beginEntry(String title) {
        BookmarkEntry currentEntry = new BookmarkEntry(title);
        if (this.currentEntryStack.isEmpty()) {
            this.entries.add(currentEntry);
        } else {
            this.currentEntryStack.peek().addChildEntry(currentEntry);
        }
        this.currentEntryStack.push(currentEntry);
    }

    public void endEntry() {
        if (this.currentEntryStack.isEmpty()) {
            throw new IllegalStateException("Too many endEntry calls made.");
        }
        this.currentEntryStack.pop();
    }

    public List<BookmarkEntry> getEntries() {
        return this.entries;
    }

    public static class BookmarkEntry {
        private final String title;
        private final List<BookmarkEntry> childEntries;

        public BookmarkEntry(String title) {
            this.title = title;
            this.childEntries = new ArrayList<BookmarkEntry>();
        }

        public void addChildEntry(BookmarkEntry child) {
            this.childEntries.add(child);
        }

        public String getTitle() {
            return this.title;
        }

        public List<BookmarkEntry> getChildEntries() {
            return this.childEntries;
        }

        public boolean hasChildEntries() {
            return !this.childEntries.isEmpty();
        }
    }
}

