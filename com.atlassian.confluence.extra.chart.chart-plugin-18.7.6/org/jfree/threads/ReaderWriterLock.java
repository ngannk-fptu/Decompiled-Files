/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.threads;

import java.util.ArrayList;
import java.util.Iterator;

public class ReaderWriterLock {
    private ArrayList waiters = new ArrayList();

    public synchronized void lockRead() {
        ReaderWriterNode node;
        Thread me = Thread.currentThread();
        int index = this.getIndex(me);
        if (index == -1) {
            node = new ReaderWriterNode(me, 0);
            this.waiters.add(node);
        } else {
            node = (ReaderWriterNode)this.waiters.get(index);
        }
        while (this.getIndex(me) > this.firstWriter()) {
            try {
                this.wait();
            }
            catch (Exception e) {
                System.err.println("ReaderWriterLock.lockRead(): exception.");
                System.err.print(e.getMessage());
            }
        }
        ++node.nAcquires;
    }

    public synchronized void lockWrite() {
        ReaderWriterNode node;
        Thread me = Thread.currentThread();
        int index = this.getIndex(me);
        if (index == -1) {
            node = new ReaderWriterNode(me, 1);
            this.waiters.add(node);
        } else {
            node = (ReaderWriterNode)this.waiters.get(index);
            if (node.state == 0) {
                throw new IllegalArgumentException("Upgrade lock");
            }
            node.state = 1;
        }
        while (this.getIndex(me) != 0) {
            try {
                this.wait();
            }
            catch (Exception e) {
                System.err.println("ReaderWriterLock.lockWrite(): exception.");
                System.err.print(e.getMessage());
            }
        }
        ++node.nAcquires;
    }

    public synchronized void unlock() {
        Thread me = Thread.currentThread();
        int index = this.getIndex(me);
        if (index > this.firstWriter()) {
            throw new IllegalArgumentException("Lock not held");
        }
        ReaderWriterNode node = (ReaderWriterNode)this.waiters.get(index);
        --node.nAcquires;
        if (node.nAcquires == 0) {
            this.waiters.remove(index);
        }
        this.notifyAll();
    }

    private int firstWriter() {
        Iterator e = this.waiters.iterator();
        int index = 0;
        while (e.hasNext()) {
            ReaderWriterNode node = (ReaderWriterNode)e.next();
            if (node.state == 1) {
                return index;
            }
            ++index;
        }
        return Integer.MAX_VALUE;
    }

    private int getIndex(Thread t) {
        Iterator e = this.waiters.iterator();
        int index = 0;
        while (e.hasNext()) {
            ReaderWriterNode node = (ReaderWriterNode)e.next();
            if (node.t == t) {
                return index;
            }
            ++index;
        }
        return -1;
    }

    private static class ReaderWriterNode {
        protected static final int READER = 0;
        protected static final int WRITER = 1;
        protected Thread t;
        protected int state;
        protected int nAcquires;

        private ReaderWriterNode(Thread t, int state) {
            this.t = t;
            this.state = state;
            this.nAcquires = 0;
        }
    }
}

