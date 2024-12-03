/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.util.concurrent;

import io.atlassian.util.concurrent.NotNull;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadFactories {
    public static Builder named(String name) {
        return new Builder(name);
    }

    public static ThreadFactory namedThreadFactory(@NotNull String name) {
        return ThreadFactories.named(name).build();
    }

    public static ThreadFactory namedThreadFactory(@NotNull String name, @NotNull Type type) {
        return ThreadFactories.named(name).type(type).build();
    }

    public static ThreadFactory namedThreadFactory(@NotNull String name, @NotNull Type type, int priority) {
        return ThreadFactories.named(name).type(type).priority(priority).build();
    }

    private ThreadFactories() {
        throw new AssertionError((Object)"cannot instantiate!");
    }

    static class Default
    implements ThreadFactory {
        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix;
        final Type type;
        final int priority;
        final Thread.UncaughtExceptionHandler exceptionHandler;

        Default(String name, Type type, int priority, Thread.UncaughtExceptionHandler exceptionHandler) {
            this.namePrefix = Objects.requireNonNull(name, "name") + ":thread-";
            this.type = Objects.requireNonNull(type, "type");
            if (priority < 1) {
                throw new IllegalArgumentException("priority too low");
            }
            if (priority > 10) {
                throw new IllegalArgumentException("priority too high");
            }
            this.priority = priority;
            SecurityManager securityManager = System.getSecurityManager();
            ThreadGroup parent = securityManager != null ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.group = new ThreadGroup(parent, name);
            this.exceptionHandler = exceptionHandler;
        }

        @Override
        public Thread newThread(Runnable r) {
            String name = this.namePrefix + this.threadNumber.getAndIncrement();
            Thread t = new Thread(this.group, r, name, 0L);
            t.setDaemon(this.type.isDaemon);
            t.setPriority(this.priority);
            t.setUncaughtExceptionHandler(this.exceptionHandler);
            return t;
        }
    }

    public static enum Type {
        DAEMON(true),
        USER(false);

        final boolean isDaemon;

        private Type(boolean isDaemon) {
            this.isDaemon = isDaemon;
        }
    }

    public static final class Builder {
        String name;
        Type type = Type.USER;
        int priority = 5;
        Thread.UncaughtExceptionHandler exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

        Builder(String name) {
            this.name = name;
        }

        public Builder name(String name) {
            this.name = Objects.requireNonNull(name, "name");
            return this;
        }

        public Builder type(Type type) {
            this.type = Objects.requireNonNull(type, "type");
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder uncaughtExceptionHandler(Thread.UncaughtExceptionHandler exceptionHandler) {
            this.exceptionHandler = Objects.requireNonNull(exceptionHandler, "exceptionHandler");
            return this;
        }

        public ThreadFactory build() {
            return new Default(this.name, this.type, this.priority, this.exceptionHandler);
        }
    }
}

