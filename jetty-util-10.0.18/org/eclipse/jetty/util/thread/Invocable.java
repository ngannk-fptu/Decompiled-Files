/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util.thread;

public interface Invocable {
    public static final ThreadLocal<Boolean> __nonBlocking = new ThreadLocal();

    public static Task from(InvocationType type, Runnable task) {
        return new ReadyTask(type, task);
    }

    public static boolean isNonBlockingInvocation() {
        return Boolean.TRUE.equals(__nonBlocking.get());
    }

    public static void invokeNonBlocking(Runnable task) {
        Boolean wasNonBlocking = __nonBlocking.get();
        try {
            __nonBlocking.set(Boolean.TRUE);
            task.run();
        }
        finally {
            __nonBlocking.set(wasNonBlocking);
        }
    }

    public static InvocationType combine(InvocationType it1, InvocationType it2) {
        if (it1 != null && it2 != null) {
            if (it1 == it2) {
                return it1;
            }
            if (it1 == InvocationType.EITHER) {
                return it2;
            }
            if (it2 == InvocationType.EITHER) {
                return it1;
            }
        }
        return InvocationType.BLOCKING;
    }

    public static InvocationType getInvocationType(Object o) {
        if (o instanceof Invocable) {
            return ((Invocable)o).getInvocationType();
        }
        return InvocationType.BLOCKING;
    }

    default public InvocationType getInvocationType() {
        return InvocationType.BLOCKING;
    }

    public static class ReadyTask
    implements Task {
        private final InvocationType type;
        private final Runnable task;

        public ReadyTask(InvocationType type, Runnable task) {
            this.type = type;
            this.task = task;
        }

        @Override
        public void run() {
            this.task.run();
        }

        @Override
        public InvocationType getInvocationType() {
            return this.type;
        }

        public String toString() {
            return String.format("%s@%x[%s|%s]", new Object[]{this.getClass().getSimpleName(), this.hashCode(), this.type, this.task});
        }
    }

    public static enum InvocationType {
        BLOCKING,
        NON_BLOCKING,
        EITHER;

    }

    public static interface Task
    extends Invocable,
    Runnable {
    }
}

