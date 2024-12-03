/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.eclipse.jetty.util.thread.Invocable;

public interface Callback
extends Invocable {
    public static final Callback NOOP = new Callback(){

        @Override
        public Invocable.InvocationType getInvocationType() {
            return Invocable.InvocationType.NON_BLOCKING;
        }
    };

    default public void completeWith(CompletableFuture<?> completable) {
        completable.whenComplete((o, x) -> {
            if (x == null) {
                this.succeeded();
            } else {
                this.failed((Throwable)x);
            }
        });
    }

    default public void succeeded() {
    }

    default public void failed(Throwable x) {
    }

    public static Callback from(CompletableFuture<?> completable) {
        return Callback.from(completable, Invocable.InvocationType.NON_BLOCKING);
    }

    public static Callback from(final CompletableFuture<?> completable, final Invocable.InvocationType invocation) {
        if (completable instanceof Callback) {
            return (Callback)((Object)completable);
        }
        return new Callback(){

            @Override
            public void succeeded() {
                completable.complete(null);
            }

            @Override
            public void failed(Throwable x) {
                completable.completeExceptionally(x);
            }

            @Override
            public Invocable.InvocationType getInvocationType() {
                return invocation;
            }
        };
    }

    public static Callback from(Runnable success, Consumer<Throwable> failure) {
        return Callback.from(Invocable.InvocationType.BLOCKING, success, failure);
    }

    public static Callback from(final Invocable.InvocationType invocationType, final Runnable success, final Consumer<Throwable> failure) {
        return new Callback(){

            @Override
            public void succeeded() {
                success.run();
            }

            @Override
            public void failed(Throwable x) {
                failure.accept(x);
            }

            @Override
            public Invocable.InvocationType getInvocationType() {
                return invocationType;
            }
        };
    }

    public static Callback from(final Runnable completed) {
        return new Completing(){

            @Override
            public void completed() {
                completed.run();
            }
        };
    }

    public static Callback from(Invocable.InvocationType invocationType, final Runnable completed) {
        return new Completing(invocationType){

            @Override
            public void completed() {
                completed.run();
            }
        };
    }

    public static Callback from(Callback callback, final Runnable completed) {
        return new Nested(callback){

            @Override
            public void completed() {
                completed.run();
            }
        };
    }

    public static Callback from(final Runnable completed, final Callback callback) {
        return new Callback(){

            @Override
            public void succeeded() {
                try {
                    completed.run();
                    callback.succeeded();
                }
                catch (Throwable t) {
                    callback.failed(t);
                }
            }

            @Override
            public void failed(Throwable x) {
                try {
                    completed.run();
                }
                catch (Throwable t) {
                    x.addSuppressed(t);
                }
                callback.failed(x);
            }
        };
    }

    public static Callback from(final Callback callback, final Throwable cause) {
        return new Callback(){

            @Override
            public void succeeded() {
                callback.failed(cause);
            }

            @Override
            public void failed(Throwable x) {
                cause.addSuppressed(x);
                callback.failed(cause);
            }
        };
    }

    public static Callback from(final Callback callback1, final Callback callback2) {
        return new Callback(){

            @Override
            public void succeeded() {
                callback1.succeeded();
                callback2.succeeded();
            }

            @Override
            public void failed(Throwable x) {
                callback1.failed(x);
                callback2.failed(x);
            }
        };
    }

    public static Callback combine(final Callback cb1, final Callback cb2) {
        if (cb1 == null || cb1 == cb2) {
            return cb2;
        }
        if (cb2 == null) {
            return cb1;
        }
        return new Callback(){

            @Override
            public void succeeded() {
                try {
                    cb1.succeeded();
                }
                finally {
                    cb2.succeeded();
                }
            }

            @Override
            public void failed(Throwable x) {
                try {
                    cb1.failed(x);
                }
                catch (Throwable t) {
                    if (x != t) {
                        x.addSuppressed(t);
                    }
                }
                finally {
                    cb2.failed(x);
                }
            }

            @Override
            public Invocable.InvocationType getInvocationType() {
                return Invocable.combine(Invocable.getInvocationType(cb1), Invocable.getInvocationType(cb2));
            }
        };
    }

    public static class Completable
    extends CompletableFuture<Void>
    implements Callback {
        private final Invocable.InvocationType invocation;

        public static Completable from(final Callback callback) {
            return new Completable(callback.getInvocationType()){

                @Override
                public void succeeded() {
                    callback.succeeded();
                    super.succeeded();
                }

                @Override
                public void failed(Throwable x) {
                    callback.failed(x);
                    super.failed(x);
                }
            };
        }

        public Completable() {
            this(Invocable.InvocationType.NON_BLOCKING);
        }

        public Completable(Invocable.InvocationType invocation) {
            this.invocation = invocation;
        }

        @Override
        public void succeeded() {
            this.complete(null);
        }

        @Override
        public void failed(Throwable x) {
            this.completeExceptionally(x);
        }

        @Override
        public Invocable.InvocationType getInvocationType() {
            return this.invocation;
        }
    }

    public static class Nested
    extends Completing {
        private final Callback callback;

        public Nested(Callback callback) {
            this.callback = callback;
        }

        public Nested(Nested nested) {
            this.callback = nested.callback;
        }

        public Callback getCallback() {
            return this.callback;
        }

        @Override
        public void succeeded() {
            try {
                this.callback.succeeded();
            }
            finally {
                this.completed();
            }
        }

        @Override
        public void failed(Throwable x) {
            try {
                this.callback.failed(x);
            }
            finally {
                this.completed();
            }
        }

        @Override
        public Invocable.InvocationType getInvocationType() {
            return this.callback.getInvocationType();
        }
    }

    public static class Completing
    implements Callback {
        private final Invocable.InvocationType invocationType;

        public Completing() {
            this(Invocable.InvocationType.BLOCKING);
        }

        public Completing(Invocable.InvocationType invocationType) {
            this.invocationType = invocationType;
        }

        @Override
        public void succeeded() {
            this.completed();
        }

        @Override
        public void failed(Throwable x) {
            this.completed();
        }

        @Override
        public Invocable.InvocationType getInvocationType() {
            return this.invocationType;
        }

        public void completed() {
        }
    }
}

