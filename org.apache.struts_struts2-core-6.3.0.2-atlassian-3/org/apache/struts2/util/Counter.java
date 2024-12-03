/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.util;

import java.io.Serializable;
import java.util.Iterator;

public class Counter
implements Iterator,
Serializable {
    private static final long serialVersionUID = 2796965884308060179L;
    boolean wrap = false;
    long first;
    long current = this.first = 1L;
    long interval = 1L;
    long last = -1L;

    public void setAdd(long addition) {
        this.current += addition;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getCurrent() {
        return this.current;
    }

    public void setFirst(long first) {
        this.first = first;
        this.current = first;
    }

    public long getFirst() {
        return this.first;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long getInterval() {
        return this.interval;
    }

    public void setLast(long last) {
        this.last = last;
    }

    public long getLast() {
        return this.last;
    }

    public long getNext() {
        long next = this.current;
        this.current += this.interval;
        if (this.wrap && this.current > this.last) {
            this.current -= 1L + this.last - this.first;
        }
        return next;
    }

    public long getPrevious() {
        this.current -= this.interval;
        if (this.wrap && this.current < this.first) {
            this.current += this.last - this.first + 1L;
        }
        return this.current;
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }

    public boolean isWrap() {
        return this.wrap;
    }

    @Override
    public boolean hasNext() {
        return this.last == -1L || this.wrap ? true : this.current <= this.last;
    }

    public Object next() {
        return this.getNext();
    }

    @Override
    public void remove() {
    }
}

