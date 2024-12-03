/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.collections.impl;

import groovyjarjarantlr.collections.List;
import groovyjarjarantlr.collections.Stack;
import groovyjarjarantlr.collections.impl.LLCell;
import groovyjarjarantlr.collections.impl.LLEnumeration;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class LList
implements List,
Stack {
    protected LLCell head = null;
    protected LLCell tail = null;
    protected int length = 0;

    public void add(Object object) {
        this.append(object);
    }

    public void append(Object object) {
        LLCell lLCell = new LLCell(object);
        if (this.length == 0) {
            this.head = this.tail = lLCell;
            this.length = 1;
        } else {
            this.tail.next = lLCell;
            this.tail = lLCell;
            ++this.length;
        }
    }

    protected Object deleteHead() throws NoSuchElementException {
        if (this.head == null) {
            throw new NoSuchElementException();
        }
        Object object = this.head.data;
        this.head = this.head.next;
        --this.length;
        return object;
    }

    public Object elementAt(int n) throws NoSuchElementException {
        int n2 = 0;
        LLCell lLCell = this.head;
        while (lLCell != null) {
            if (n == n2) {
                return lLCell.data;
            }
            ++n2;
            lLCell = lLCell.next;
        }
        throw new NoSuchElementException();
    }

    public Enumeration elements() {
        return new LLEnumeration(this);
    }

    public int height() {
        return this.length;
    }

    public boolean includes(Object object) {
        LLCell lLCell = this.head;
        while (lLCell != null) {
            if (lLCell.data.equals(object)) {
                return true;
            }
            lLCell = lLCell.next;
        }
        return false;
    }

    protected void insertHead(Object object) {
        LLCell lLCell = this.head;
        this.head = new LLCell(object);
        this.head.next = lLCell;
        ++this.length;
        if (this.tail == null) {
            this.tail = this.head;
        }
    }

    public int length() {
        return this.length;
    }

    public Object pop() throws NoSuchElementException {
        Object object = this.deleteHead();
        return object;
    }

    public void push(Object object) {
        this.insertHead(object);
    }

    public Object top() throws NoSuchElementException {
        if (this.head == null) {
            throw new NoSuchElementException();
        }
        return this.head.data;
    }
}

