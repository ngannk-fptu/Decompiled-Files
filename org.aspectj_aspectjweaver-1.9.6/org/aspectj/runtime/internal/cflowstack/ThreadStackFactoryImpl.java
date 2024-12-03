/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.internal.cflowstack;

import java.util.Stack;
import org.aspectj.runtime.internal.cflowstack.ThreadCounter;
import org.aspectj.runtime.internal.cflowstack.ThreadStack;
import org.aspectj.runtime.internal.cflowstack.ThreadStackFactory;

public class ThreadStackFactoryImpl
implements ThreadStackFactory {
    @Override
    public ThreadStack getNewThreadStack() {
        return new ThreadStackImpl();
    }

    @Override
    public ThreadCounter getNewThreadCounter() {
        return new ThreadCounterImpl();
    }

    private static class ThreadCounterImpl
    extends ThreadLocal
    implements ThreadCounter {
        private ThreadCounterImpl() {
        }

        public Object initialValue() {
            return new Counter();
        }

        public Counter getThreadCounter() {
            return (Counter)this.get();
        }

        @Override
        public void removeThreadCounter() {
            this.remove();
        }

        @Override
        public void inc() {
            ++this.getThreadCounter().value;
        }

        @Override
        public void dec() {
            --this.getThreadCounter().value;
        }

        @Override
        public boolean isNotZero() {
            return this.getThreadCounter().value != 0;
        }

        static class Counter {
            protected int value = 0;

            Counter() {
            }
        }
    }

    private static class ThreadStackImpl
    extends ThreadLocal
    implements ThreadStack {
        private ThreadStackImpl() {
        }

        public Object initialValue() {
            return new Stack();
        }

        @Override
        public Stack getThreadStack() {
            return (Stack)this.get();
        }

        @Override
        public void removeThreadStack() {
            this.remove();
        }
    }
}

