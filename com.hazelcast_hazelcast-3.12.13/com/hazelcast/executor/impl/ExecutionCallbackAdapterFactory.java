/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.executor.impl;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.Member;
import com.hazelcast.core.MultiExecutionCallback;
import com.hazelcast.logging.ILogger;
import com.hazelcast.util.MapUtil;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

class ExecutionCallbackAdapterFactory {
    private static final AtomicReferenceFieldUpdater<ExecutionCallbackAdapterFactory, Boolean> DONE = AtomicReferenceFieldUpdater.newUpdater(ExecutionCallbackAdapterFactory.class, Boolean.class, "done");
    private final MultiExecutionCallback multiExecutionCallback;
    private final ConcurrentMap<Member, ValueWrapper> responses;
    private final Collection<Member> members;
    private final ILogger logger;
    private volatile Boolean done = Boolean.FALSE;

    ExecutionCallbackAdapterFactory(ILogger logger, Collection<Member> members, MultiExecutionCallback multiExecutionCallback) {
        this.multiExecutionCallback = multiExecutionCallback;
        this.responses = MapUtil.createConcurrentHashMap(members.size());
        this.members = new HashSet<Member>(members);
        this.logger = logger;
    }

    private void onResponse(Member member, Object response) {
        this.assertNotDone();
        this.assertIsMember(member);
        this.triggerOnResponse(member, response);
        this.placeResponse(member, response);
        this.triggerOnComplete();
    }

    private void triggerOnComplete() {
        if (this.members.size() != this.responses.size() || !this.setDone()) {
            return;
        }
        Map<Member, Object> realResponses = MapUtil.createHashMap(this.members.size());
        for (Map.Entry entry : this.responses.entrySet()) {
            Member key = (Member)entry.getKey();
            Object value = ((ValueWrapper)entry.getValue()).value;
            realResponses.put(key, value);
        }
        this.multiExecutionCallback.onComplete(realResponses);
    }

    private boolean setDone() {
        return DONE.compareAndSet(this, Boolean.FALSE, Boolean.TRUE);
    }

    private void triggerOnResponse(Member member, Object response) {
        try {
            this.multiExecutionCallback.onResponse(member, response);
        }
        catch (Throwable e) {
            this.logger.warning(e.getMessage(), e);
        }
    }

    private void placeResponse(Member member, Object response) {
        ValueWrapper current = this.responses.put(member, new ValueWrapper(response));
        if (current != null) {
            this.logger.warning("Replacing current callback value[" + current.value + " with value[" + response + "].");
        }
    }

    private void assertIsMember(Member member) {
        if (!this.members.contains(member)) {
            throw new IllegalArgumentException(member + " is not known by this callback!");
        }
    }

    private void assertNotDone() {
        if (this.done.booleanValue()) {
            throw new IllegalStateException("This callback is invalid!");
        }
    }

    <V> ExecutionCallback<V> callbackFor(Member member) {
        return new InnerExecutionCallback(member);
    }

    private final class InnerExecutionCallback<V>
    implements ExecutionCallback<V> {
        private final Member member;

        private InnerExecutionCallback(Member member) {
            this.member = member;
        }

        @Override
        public void onResponse(V response) {
            ExecutionCallbackAdapterFactory.this.onResponse(this.member, response);
        }

        @Override
        public void onFailure(Throwable t) {
            ExecutionCallbackAdapterFactory.this.onResponse(this.member, t);
        }
    }

    private static final class ValueWrapper {
        final Object value;

        private ValueWrapper(Object value) {
            this.value = value;
        }
    }
}

