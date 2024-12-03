/*
 * Decompiled with CFR 0.152.
 */
package brave.propagation;

import brave.Tracer;
import brave.internal.Nullable;
import brave.internal.collect.WeakConcurrentMap;
import brave.propagation.CurrentTraceContext;
import brave.propagation.ThreadLocalSpan;
import brave.propagation.TraceContext;
import java.io.Closeable;
import java.lang.ref.Reference;
import java.util.Arrays;
import java.util.Map;

public final class StrictScopeDecorator
implements CurrentTraceContext.ScopeDecorator,
Closeable {
    final PendingScopes pendingScopes = new PendingScopes();

    public static StrictScopeDecorator create() {
        return new StrictScopeDecorator();
    }

    @Override
    public CurrentTraceContext.Scope decorateScope(@Nullable TraceContext context, CurrentTraceContext.Scope scope) {
        String className;
        int i;
        if (scope == CurrentTraceContext.Scope.NOOP) {
            return scope;
        }
        CallerStackTrace caller = new CallerStackTrace(context);
        StackTraceElement[] stackTrace = caller.getStackTrace();
        for (i = 1; i < stackTrace.length && ((className = stackTrace[i].getClassName()).equals(Tracer.class.getName()) || className.endsWith("CurrentTraceContext") || className.equals(ThreadLocalSpan.class.getName())); ++i) {
        }
        int from = i;
        stackTrace = Arrays.copyOfRange(stackTrace, from, stackTrace.length);
        caller.setStackTrace(stackTrace);
        StrictScope strictScope = new StrictScope(scope, caller);
        this.pendingScopes.putIfProbablyAbsent(strictScope, caller);
        return strictScope;
    }

    @Override
    public void close() {
        this.pendingScopes.expungeStaleEntries();
        for (Map.Entry entry : this.pendingScopes) {
            CallerStackTrace caller = (CallerStackTrace)entry.getValue();
            if (caller.closed) continue;
            StrictScopeDecorator.throwCallerError(caller);
        }
    }

    static void throwCallerError(CallerStackTrace caller) {
        AssertionError toThrow = new AssertionError((Object)("Thread [" + caller.threadName + "] opened a scope of " + caller.context + " here:"));
        ((Throwable)((Object)toThrow)).setStackTrace(caller.getStackTrace());
        throw toThrow;
    }

    StrictScopeDecorator() {
    }

    static class PendingScopes
    extends WeakConcurrentMap<CurrentTraceContext.Scope, CallerStackTrace> {
        PendingScopes() {
        }

        @Override
        protected void expungeStaleEntries() {
            Reference reference;
            while ((reference = this.poll()) != null) {
                CallerStackTrace caller = (CallerStackTrace)this.removeStaleEntry(reference);
                if (caller == null || caller.closed) continue;
                StrictScopeDecorator.throwCallerError(caller);
            }
        }
    }

    static class CallerStackTrace
    extends Throwable {
        final String threadName = Thread.currentThread().getName();
        final long threadId = Thread.currentThread().getId();
        final TraceContext context;
        volatile boolean closed;

        CallerStackTrace(@Nullable TraceContext context) {
            super("Thread [" + Thread.currentThread().getName() + "] opened scope for " + context + " here:");
            this.context = context;
        }
    }

    final class StrictScope
    implements CurrentTraceContext.Scope {
        final CurrentTraceContext.Scope delegate;
        final CallerStackTrace caller;

        StrictScope(CurrentTraceContext.Scope delegate, CallerStackTrace caller) {
            this.delegate = delegate;
            this.caller = caller;
        }

        @Override
        public void close() {
            this.caller.closed = true;
            StrictScopeDecorator.this.pendingScopes.remove(this);
            if (Thread.currentThread().getId() != this.caller.threadId) {
                throw new IllegalStateException(String.format("Thread [%s] opened scope, but thread [%s] closed it", this.caller.threadName, Thread.currentThread().getName()), this.caller);
            }
            this.delegate.close();
        }

        public String toString() {
            return this.caller.getMessage();
        }
    }
}

