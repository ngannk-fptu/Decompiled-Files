/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jasper.util;

public class FastRemovalDequeue<T> {
    private final int maxSize;
    protected Entry first;
    protected Entry last;
    private int size;

    public FastRemovalDequeue(int maxSize) {
        if (maxSize <= 1) {
            maxSize = 2;
        }
        this.maxSize = maxSize;
        this.first = null;
        this.last = null;
        this.size = 0;
    }

    public synchronized int getSize() {
        return this.size;
    }

    public synchronized Entry push(T object) {
        Entry entry = new Entry(object);
        if (this.size >= this.maxSize) {
            entry.setReplaced(this.pop());
        }
        if (this.first == null) {
            this.first = this.last = entry;
        } else {
            this.first.setPrevious(entry);
            entry.setNext(this.first);
            this.first = entry;
        }
        ++this.size;
        return entry;
    }

    public synchronized Entry unpop(T object) {
        Entry entry = new Entry(object);
        if (this.size >= this.maxSize) {
            entry.setReplaced(this.unpush());
        }
        if (this.first == null) {
            this.first = this.last = entry;
        } else {
            this.last.setNext(entry);
            entry.setPrevious(this.last);
            this.last = entry;
        }
        ++this.size;
        return entry;
    }

    public synchronized T unpush() {
        T content = null;
        if (this.first != null) {
            Entry element = this.first;
            this.first = this.first.getNext();
            content = element.getContent();
            if (this.first == null) {
                this.last = null;
            } else {
                this.first.setPrevious(null);
            }
            --this.size;
            element.invalidate();
        }
        return content;
    }

    public synchronized T pop() {
        T content = null;
        if (this.last != null) {
            Entry element = this.last;
            this.last = this.last.getPrevious();
            content = element.getContent();
            if (this.last == null) {
                this.first = null;
            } else {
                this.last.setNext(null);
            }
            --this.size;
            element.invalidate();
        }
        return content;
    }

    public synchronized void remove(Entry element) {
        if (element == null || !element.getValid()) {
            return;
        }
        Entry next = element.getNext();
        Entry prev = element.getPrevious();
        if (next != null) {
            next.setPrevious(prev);
        } else {
            this.last = prev;
        }
        if (prev != null) {
            prev.setNext(next);
        } else {
            this.first = next;
        }
        --this.size;
        element.invalidate();
    }

    public synchronized void moveFirst(Entry element) {
        if (element.getValid() && element.getPrevious() != null) {
            Entry prev = element.getPrevious();
            Entry next = element.getNext();
            prev.setNext(next);
            if (next != null) {
                next.setPrevious(prev);
            } else {
                this.last = prev;
            }
            this.first.setPrevious(element);
            element.setNext(this.first);
            element.setPrevious(null);
            this.first = element;
        }
    }

    public synchronized void moveLast(Entry element) {
        if (element.getValid() && element.getNext() != null) {
            Entry next = element.getNext();
            Entry prev = element.getPrevious();
            next.setPrevious(prev);
            if (prev != null) {
                prev.setNext(next);
            } else {
                this.first = next;
            }
            this.last.setNext(element);
            element.setPrevious(this.last);
            element.setNext(null);
            this.last = element;
        }
    }

    public class Entry {
        private boolean valid = true;
        private final T content;
        private T replaced = null;
        private Entry next = null;
        private Entry previous = null;

        private Entry(T object) {
            this.content = object;
        }

        private boolean getValid() {
            return this.valid;
        }

        private void invalidate() {
            this.valid = false;
            this.previous = null;
            this.next = null;
        }

        public final T getContent() {
            return this.content;
        }

        public final T getReplaced() {
            return this.replaced;
        }

        private void setReplaced(T replaced) {
            this.replaced = replaced;
        }

        public final void clearReplaced() {
            this.replaced = null;
        }

        private Entry getNext() {
            return this.next;
        }

        private void setNext(Entry next) {
            this.next = next;
        }

        private Entry getPrevious() {
            return this.previous;
        }

        private void setPrevious(Entry previous) {
            this.previous = previous;
        }

        public String toString() {
            return "Entry-" + this.content.toString();
        }
    }
}

