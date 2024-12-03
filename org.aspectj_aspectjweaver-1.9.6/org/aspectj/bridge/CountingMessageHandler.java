/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.bridge;

import java.util.Enumeration;
import java.util.Hashtable;
import org.aspectj.bridge.AbortException;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.util.LangUtil;

public class CountingMessageHandler
implements IMessageHandler {
    public final IMessageHandler delegate;
    public final CountingMessageHandler proxy;
    private final Hashtable<IMessage.Kind, IntHolder> counters;

    public static CountingMessageHandler makeCountingMessageHandler(IMessageHandler handler) {
        if (handler instanceof CountingMessageHandler) {
            return (CountingMessageHandler)handler;
        }
        return new CountingMessageHandler(handler);
    }

    public CountingMessageHandler(IMessageHandler delegate) {
        LangUtil.throwIaxIfNull(delegate, "delegate");
        this.delegate = delegate;
        this.counters = new Hashtable();
        this.proxy = delegate instanceof CountingMessageHandler ? (CountingMessageHandler)delegate : null;
    }

    @Override
    public boolean handleMessage(IMessage message) throws AbortException {
        IMessage.Kind kind;
        if (null != this.proxy) {
            return this.proxy.handleMessage(message);
        }
        if (null != message && !this.isIgnoring(kind = message.getKind())) {
            this.increment(kind);
        }
        return this.delegate.handleMessage(message);
    }

    @Override
    public boolean isIgnoring(IMessage.Kind kind) {
        return this.delegate.isIgnoring(kind);
    }

    @Override
    public void dontIgnore(IMessage.Kind kind) {
        this.delegate.dontIgnore(kind);
    }

    @Override
    public void ignore(IMessage.Kind kind) {
        this.delegate.ignore(kind);
    }

    public String toString() {
        return this.delegate.toString();
    }

    public int numMessages(IMessage.Kind kind, boolean orGreater) {
        if (null != this.proxy) {
            return this.proxy.numMessages(kind, orGreater);
        }
        int result = 0;
        if (null == kind) {
            Enumeration<IntHolder> enu = this.counters.elements();
            while (enu.hasMoreElements()) {
                result += enu.nextElement().count;
            }
        } else if (!orGreater) {
            result = this.numMessages(kind);
        } else {
            for (IMessage.Kind k : IMessage.KINDS) {
                if (!kind.isSameOrLessThan(k)) continue;
                result += this.numMessages(k);
            }
        }
        return result;
    }

    public boolean hasErrors() {
        return 0 < this.numMessages(IMessage.ERROR, true);
    }

    private int numMessages(IMessage.Kind kind) {
        if (null != this.proxy) {
            return this.proxy.numMessages(kind);
        }
        IntHolder counter = this.counters.get(kind);
        return null == counter ? 0 : counter.count;
    }

    private void increment(IMessage.Kind kind) {
        if (null != this.proxy) {
            throw new IllegalStateException("not called when proxying");
        }
        IntHolder counter = this.counters.get(kind);
        if (null == counter) {
            counter = new IntHolder();
            this.counters.put(kind, counter);
        }
        ++counter.count;
    }

    public void reset() {
        if (this.proxy != null) {
            this.proxy.reset();
        }
        this.counters.clear();
    }

    private static class IntHolder {
        int count;

        private IntHolder() {
        }
    }
}

