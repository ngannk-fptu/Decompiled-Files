/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl;

import com.hazelcast.core.Member;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.EndpointManager;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.impl.Invocation;

final class TargetInvocation
extends Invocation<Address> {
    private final Address target;

    TargetInvocation(Invocation.Context context, Operation op, Address target, Runnable doneCallback, int tryCount, long tryPauseMillis, long callTimeoutMillis, boolean deserialize, EndpointManager endpointManager) {
        super(context, op, doneCallback, tryCount, tryPauseMillis, callTimeoutMillis, deserialize, endpointManager);
        this.target = target;
    }

    TargetInvocation(Invocation.Context context, Operation op, Address target, int tryCount, long tryPauseMillis, long callTimeoutMillis, boolean deserialize) {
        this(context, op, target, null, tryCount, tryPauseMillis, callTimeoutMillis, deserialize, null);
    }

    @Override
    Address getInvocationTarget() {
        return this.target;
    }

    @Override
    Address toTargetAddress(Address target) {
        return target;
    }

    @Override
    Member toTargetMember(Address target) {
        assert (target == this.target);
        return this.context.clusterService.getMember(target);
    }

    @Override
    ExceptionAction onException(Throwable t) {
        return t instanceof MemberLeftException ? ExceptionAction.THROW_EXCEPTION : this.op.onInvocationException(t);
    }
}

