/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction.reactive;

import java.util.Deque;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.reactive.TransactionContext;

final class TransactionContextHolder {
    private final Deque<TransactionContext> transactionStack;

    TransactionContextHolder(Deque<TransactionContext> transactionStack) {
        this.transactionStack = transactionStack;
    }

    TransactionContext currentContext() {
        TransactionContext context = this.transactionStack.peek();
        if (context == null) {
            throw new NoTransactionException("No transaction in context");
        }
        return context;
    }

    TransactionContext createContext() {
        TransactionContext context = this.transactionStack.peek();
        context = context != null ? new TransactionContext(context) : new TransactionContext();
        this.transactionStack.push(context);
        return context;
    }

    boolean hasContext() {
        return !this.transactionStack.isEmpty();
    }
}

