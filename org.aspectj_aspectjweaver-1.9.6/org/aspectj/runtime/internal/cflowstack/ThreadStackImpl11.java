/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.internal.cflowstack;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import org.aspectj.runtime.internal.cflowstack.ThreadStack;

public class ThreadStackImpl11
implements ThreadStack {
    private Hashtable stacks = new Hashtable();
    private Thread cached_thread;
    private Stack cached_stack;
    private int change_count = 0;
    private static final int COLLECT_AT = 20000;
    private static final int MIN_COLLECT_AT = 100;

    @Override
    public synchronized Stack getThreadStack() {
        if (Thread.currentThread() != this.cached_thread) {
            this.cached_thread = Thread.currentThread();
            this.cached_stack = (Stack)this.stacks.get(this.cached_thread);
            if (this.cached_stack == null) {
                this.cached_stack = new Stack();
                this.stacks.put(this.cached_thread, this.cached_stack);
            }
            ++this.change_count;
            int size = Math.max(1, this.stacks.size());
            if (this.change_count > Math.max(100, 20000 / size)) {
                Thread t;
                Stack<Thread> dead_stacks = new Stack<Thread>();
                Enumeration<Object> e = this.stacks.keys();
                while (e.hasMoreElements()) {
                    t = (Thread)e.nextElement();
                    if (t.isAlive()) continue;
                    dead_stacks.push(t);
                }
                e = dead_stacks.elements();
                while (e.hasMoreElements()) {
                    t = (Thread)e.nextElement();
                    this.stacks.remove(t);
                }
                this.change_count = 0;
            }
        }
        return this.cached_stack;
    }

    @Override
    public void removeThreadStack() {
    }
}

