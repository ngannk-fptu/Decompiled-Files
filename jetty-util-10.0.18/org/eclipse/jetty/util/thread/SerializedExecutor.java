/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util.thread;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.slf4j.LoggerFactory;

public class SerializedExecutor
implements Executor {
    private final AtomicReference<Link> _tail = new AtomicReference();

    @Override
    public void execute(Runnable task) {
        Link link = new Link(task);
        Link lastButOne = this._tail.getAndSet(link);
        if (lastButOne == null) {
            this.run(link);
        } else {
            lastButOne._next.lazySet(link);
        }
    }

    protected void onError(Runnable task, Throwable t) {
        if (task instanceof ErrorHandlingTask) {
            ((ErrorHandlingTask)task).accept(t);
        }
        LoggerFactory.getLogger(task.getClass()).error("Error", t);
    }

    private void run(Link link) {
        while (link != null) {
            try {
                link._task.run();
            }
            catch (Throwable t) {
                this.onError(link._task, t);
            }
            finally {
                if (this._tail.compareAndSet(link, null)) {
                    link = null;
                    continue;
                }
                Link next = link._next.get();
                while (next == null) {
                    Thread.yield();
                    next = link._next.get();
                }
                link = next;
            }
        }
    }

    private class Link {
        private final Runnable _task;
        private final AtomicReference<Link> _next = new AtomicReference();

        public Link(Runnable task) {
            this._task = task;
        }
    }

    public static interface ErrorHandlingTask
    extends Runnable,
    Consumer<Throwable> {
    }
}

