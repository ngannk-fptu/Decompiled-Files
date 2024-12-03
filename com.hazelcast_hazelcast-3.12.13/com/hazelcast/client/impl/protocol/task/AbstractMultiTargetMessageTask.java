/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.AbstractMessageTask;
import com.hazelcast.core.Member;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.SimpleExecutionCallback;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.function.Supplier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

public abstract class AbstractMultiTargetMessageTask<P>
extends AbstractMessageTask<P> {
    protected AbstractMultiTargetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() throws Throwable {
        Supplier<Operation> operationSupplier = this.createOperationSupplier();
        Collection<Member> targets = this.getTargets();
        this.returnResponseIfNoTargetLeft(targets, Collections.EMPTY_MAP);
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        MultiTargetCallback callback = new MultiTargetCallback(targets);
        for (Member target : targets) {
            Operation op = operationSupplier.get();
            InvocationBuilder builder = operationService.createInvocationBuilder(this.getServiceName(), op, target.getAddress()).setResultDeserialized(false).setExecutionCallback(new SingleTargetCallback(target, callback));
            builder.invoke();
        }
    }

    private void returnResponseIfNoTargetLeft(Collection<Member> targets, Map<Member, Object> results) throws Throwable {
        if (targets.isEmpty()) {
            this.sendResponse(this.reduce(results));
        }
    }

    protected abstract Supplier<Operation> createOperationSupplier();

    protected abstract Object reduce(Map<Member, Object> var1) throws Throwable;

    public abstract Collection<Member> getTargets();

    private final class SingleTargetCallback
    extends SimpleExecutionCallback<Object> {
        final Member target;
        final MultiTargetCallback parent;

        private SingleTargetCallback(Member target, MultiTargetCallback parent) {
            this.target = target;
            this.parent = parent;
        }

        @Override
        public void notify(Object object) {
            this.parent.notify(this.target, object);
        }
    }

    private final class MultiTargetCallback {
        final Collection<Member> targets;
        final Map<Member, Object> results;

        private MultiTargetCallback(Collection<Member> targets) {
            this.targets = new HashSet<Member>(targets);
            this.results = MapUtil.createHashMap(targets.size());
        }

        public synchronized void notify(Member target, Object result) {
            if (!this.targets.remove(target)) {
                if (this.results.containsKey(target)) {
                    throw new IllegalArgumentException("Duplicate response from -> " + target);
                }
                throw new IllegalArgumentException("Unknown target! -> " + target);
            }
            this.results.put(target, result);
            try {
                AbstractMultiTargetMessageTask.this.returnResponseIfNoTargetLeft(this.targets, this.results);
            }
            catch (Throwable throwable) {
                AbstractMultiTargetMessageTask.this.handleProcessingFailure(throwable);
            }
        }
    }
}

