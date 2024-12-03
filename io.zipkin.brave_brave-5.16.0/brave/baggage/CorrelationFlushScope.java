/*
 * Decompiled with CFR 0.152.
 */
package brave.baggage;

import brave.baggage.BaggageField;
import brave.baggage.CorrelationScopeDecorator;
import brave.baggage.CorrelationUpdateScope;
import brave.internal.CorrelationContext;
import brave.propagation.CurrentTraceContext;
import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.concurrent.atomic.AtomicBoolean;

final class CorrelationFlushScope
extends AtomicBoolean
implements CurrentTraceContext.Scope {
    final CorrelationUpdateScope updateScope;
    static final ThreadLocal<ArrayDeque<Object>> updateScopeStack = new ThreadLocal();

    CorrelationFlushScope(CorrelationUpdateScope updateScope) {
        this.updateScope = updateScope;
        CorrelationFlushScope.pushCurrentUpdateScope(updateScope);
    }

    @Override
    public void close() {
        if (!this.compareAndSet(false, true)) {
            return;
        }
        CorrelationFlushScope.popCurrentUpdateScope(this.updateScope);
        this.updateScope.close();
    }

    static void flush(BaggageField field, String value) {
        LinkedHashSet<CorrelationContext> syncedContexts = new LinkedHashSet<CorrelationContext>();
        for (Object o : CorrelationFlushScope.updateScopeStack()) {
            CorrelationUpdateScope next = (CorrelationUpdateScope)o;
            String name = next.name(field);
            if (name == null) continue;
            if (!syncedContexts.contains(next.context)) {
                if (!CorrelationScopeDecorator.equal(next.context.getValue(name), value)) {
                    next.context.update(name, value);
                }
                syncedContexts.add(next.context);
            }
            next.handleUpdate(field, value);
        }
    }

    static ArrayDeque<Object> updateScopeStack() {
        ArrayDeque<Object> stack = updateScopeStack.get();
        if (stack == null) {
            stack = new ArrayDeque();
            updateScopeStack.set(stack);
        }
        return stack;
    }

    static void pushCurrentUpdateScope(CorrelationUpdateScope updateScope) {
        CorrelationFlushScope.updateScopeStack().push(updateScope);
    }

    static void popCurrentUpdateScope(CorrelationUpdateScope expected) {
        Object popped = CorrelationFlushScope.updateScopeStack().pop();
        assert (CorrelationScopeDecorator.equal(popped, expected)) : "Misalignment: popped updateScope " + popped + " !=  expected " + expected;
    }
}

