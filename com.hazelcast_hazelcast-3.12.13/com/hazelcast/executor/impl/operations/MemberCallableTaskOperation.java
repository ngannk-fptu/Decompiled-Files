/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.executor.impl.operations;

import com.hazelcast.core.MemberLeftException;
import com.hazelcast.executor.impl.operations.AbstractCallableTaskOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.spi.impl.MutatingOperation;

public final class MemberCallableTaskOperation
extends AbstractCallableTaskOperation
implements IdentifiedDataSerializable,
MutatingOperation {
    public MemberCallableTaskOperation() {
    }

    public MemberCallableTaskOperation(String name, String uuid, Data callableData) {
        super(name, uuid, callableData);
    }

    @Override
    public ExceptionAction onInvocationException(Throwable throwable) {
        if (throwable instanceof MemberLeftException || throwable instanceof TargetNotMemberException) {
            return ExceptionAction.THROW_EXCEPTION;
        }
        return super.onInvocationException(throwable);
    }

    @Override
    public int getId() {
        return 1;
    }
}

