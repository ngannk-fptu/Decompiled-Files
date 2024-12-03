/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 *  reactor.util.context.Context
 */
package org.springframework.transaction.reactive;

import java.util.ArrayDeque;
import java.util.function.Function;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.reactive.TransactionContext;
import org.springframework.transaction.reactive.TransactionContextHolder;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public abstract class TransactionContextManager {
    private TransactionContextManager() {
    }

    public static Mono<TransactionContext> currentContext() {
        return Mono.deferContextual(ctx -> {
            TransactionContextHolder holder;
            if (ctx.hasKey(TransactionContext.class)) {
                return Mono.just((Object)ctx.get(TransactionContext.class));
            }
            if (ctx.hasKey(TransactionContextHolder.class) && (holder = (TransactionContextHolder)ctx.get(TransactionContextHolder.class)).hasContext()) {
                return Mono.just((Object)holder.currentContext());
            }
            return Mono.error((Throwable)((Object)new NoTransactionInContextException()));
        });
    }

    public static Function<Context, Context> createTransactionContext() {
        return context -> context.put(TransactionContext.class, (Object)new TransactionContext());
    }

    public static Function<Context, Context> getOrCreateContext() {
        return context -> {
            TransactionContextHolder holder = (TransactionContextHolder)context.get(TransactionContextHolder.class);
            if (holder.hasContext()) {
                return context.put(TransactionContext.class, (Object)holder.currentContext());
            }
            return context.put(TransactionContext.class, (Object)holder.createContext());
        };
    }

    public static Function<Context, Context> getOrCreateContextHolder() {
        return context -> {
            if (!context.hasKey(TransactionContextHolder.class)) {
                return context.put(TransactionContextHolder.class, (Object)new TransactionContextHolder(new ArrayDeque<TransactionContext>()));
            }
            return context;
        };
    }

    private static class NoTransactionInContextException
    extends NoTransactionException {
        public NoTransactionInContextException() {
            super("No transaction in context");
        }

        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }
}

