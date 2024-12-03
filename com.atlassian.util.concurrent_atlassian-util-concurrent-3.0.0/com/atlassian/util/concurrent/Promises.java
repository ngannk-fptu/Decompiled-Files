/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.Beta
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.util.concurrent.ForwardingListenableFuture$SimpleForwardingListenableFuture
 *  com.google.common.util.concurrent.FutureCallback
 *  com.google.common.util.concurrent.Futures
 *  com.google.common.util.concurrent.JdkFutureAdapters
 *  com.google.common.util.concurrent.ListenableFuture
 *  com.google.common.util.concurrent.SettableFuture
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.Effect;
import com.atlassian.util.concurrent.Effects;
import com.atlassian.util.concurrent.Promise;
import com.atlassian.util.concurrent.RuntimeInterruptedException;
import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.util.concurrent.ForwardingListenableFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.JdkFutureAdapters;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Beta
public final class Promises {
    private Promises() {
    }

    public static <A> Promise<List<A>> when(Promise<? extends A> ... promises) {
        return Promises.when(Arrays.asList(promises));
    }

    public static <A> Promise<List<A>> when(Iterable<? extends Promise<? extends A>> promises) {
        return Promises.forListenableFuture(Futures.allAsList(promises));
    }

    public static <A> Promise<A> promise(A value) {
        return new Of(Futures.immediateFuture(value));
    }

    public static <A> Promise<A> toResolvedPromise(A value) {
        return Promises.promise(value);
    }

    public static <A> Promise<A> rejected(Throwable throwable, Class<A> resultType) {
        return Promises.rejected(throwable);
    }

    public static <A> Promise<A> rejected(Throwable throwable) {
        return new Of(Futures.immediateFailedFuture((Throwable)throwable));
    }

    public static <A> Promise<A> toRejectedPromise(Throwable t, Class<A> resultType) {
        return Promises.rejected(t);
    }

    public static <A> Promise<A> toRejectedPromise(Throwable t) {
        return Promises.rejected(t);
    }

    public static <A> Promise<A> forListenableFuture(ListenableFuture<A> future) {
        return new Of<A>(future);
    }

    public static <A> Promise<A> forFuture(Future<A> future) {
        return new Of(JdkFutureAdapters.listenInPoolThread(future));
    }

    public static Effect<Throwable> reject(final SettableFuture<?> delegate) {
        return new Effect<Throwable>(){

            @Override
            public void apply(Throwable t) {
                delegate.setException(t);
            }
        };
    }

    public static <A> FutureCallback<A> futureCallback(final Effect<? super A> success, final Effect<Throwable> failure) {
        return new FutureCallback<A>(){

            public void onSuccess(A result) {
                success.apply(result);
            }

            public void onFailure(Throwable t) {
                failure.apply(t);
            }
        };
    }

    public static <A> FutureCallback<A> onSuccessDo(Effect<? super A> effect) {
        return Promises.futureCallback(effect, Effects.<Throwable>noop());
    }

    public static <A> FutureCallback<A> onFailureDo(Effect<Throwable> effect) {
        return Promises.futureCallback(Effects.noop(), effect);
    }

    private static final class Of<A>
    extends ForwardingListenableFuture.SimpleForwardingListenableFuture<A>
    implements Promise<A> {
        public Of(ListenableFuture<A> delegate) {
            super(delegate);
        }

        @Override
        public A claim() {
            try {
                return (A)this.delegate().get();
            }
            catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            }
            catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                if (cause instanceof Error) {
                    throw (Error)cause;
                }
                throw new RuntimeException(cause);
            }
        }

        @Override
        public Promise<A> done(Effect<? super A> e) {
            this.then(Promises.onSuccessDo(e));
            return this;
        }

        @Override
        public Promise<A> fail(Effect<Throwable> e) {
            this.then(Promises.onFailureDo(e));
            return this;
        }

        @Override
        public Promise<A> then(FutureCallback<? super A> callback) {
            Futures.addCallback((ListenableFuture)this.delegate(), callback);
            return this;
        }

        @Override
        public <B> Promise<B> map(Function<? super A, ? extends B> function) {
            return Promises.forListenableFuture(Futures.transform((ListenableFuture)this, function));
        }

        @Override
        public <B> Promise<B> flatMap(final Function<? super A, ? extends Promise<? extends B>> f) {
            final SettableFuture result = SettableFuture.create();
            final Effect<Throwable> failResult = Promises.reject(result);
            this.done(new Effect<A>(){

                @Override
                public void apply(A v) {
                    try {
                        Promise next = (Promise)f.apply(v);
                        next.done(new Effect<B>(){

                            @Override
                            public void apply(B t) {
                                result.set(t);
                            }
                        }).fail(failResult);
                    }
                    catch (Throwable t) {
                        result.setException(t);
                    }
                }
            }).fail(failResult);
            return new Of<A>(result);
        }

        @Override
        public Promise<A> recover(Function<Throwable, ? extends A> handleThrowable) {
            return this.fold(handleThrowable, Functions.identity());
        }

        @Override
        public <B> Promise<B> fold(final Function<Throwable, ? extends B> ft, final Function<? super A, ? extends B> fa) {
            final SettableFuture result = SettableFuture.create();
            final Effect<Throwable> error = new Effect<Throwable>(){

                @Override
                public void apply(Throwable t) {
                    try {
                        result.set(ft.apply((Object)t));
                    }
                    catch (Throwable inner) {
                        result.setException(inner);
                    }
                }
            };
            this.done(new Effect<A>(){

                @Override
                public void apply(A a) {
                    try {
                        result.set(fa.apply(a));
                    }
                    catch (Throwable t) {
                        error.apply(t);
                    }
                }
            }).fail(error);
            return new Of<A>(result);
        }
    }
}

