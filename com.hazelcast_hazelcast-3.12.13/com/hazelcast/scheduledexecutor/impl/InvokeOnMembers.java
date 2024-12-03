/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.core.Member;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.function.Supplier;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@PrivateApi
public final class InvokeOnMembers {
    private static final int TRY_COUNT = 10;
    private static final int TRY_PAUSE_MILLIS = 300;
    private final ILogger logger;
    private final OperationService operationService;
    private final SerializationService serializationService;
    private final String serviceName;
    private final Supplier<Operation> operationFactory;
    private final Collection<Member> targets;
    private final Map<Member, Future> futures;
    private final Map<Member, Object> results;

    public InvokeOnMembers(NodeEngine nodeEngine, String serviceName, Supplier<Operation> operationFactory, Collection<Member> targets) {
        this.logger = nodeEngine.getLogger(this.getClass());
        this.operationService = nodeEngine.getOperationService();
        this.serializationService = nodeEngine.getSerializationService();
        this.serviceName = serviceName;
        this.operationFactory = operationFactory;
        this.targets = targets;
        this.futures = new HashMap<Member, Future>(targets.size());
        this.results = new HashMap<Member, Object>(targets.size());
    }

    public Map<Member, Object> invoke() throws Exception {
        this.invokeOnAllTargets();
        this.awaitCompletion();
        this.retryFailedTargets();
        return this.results;
    }

    private void invokeOnAllTargets() {
        for (Member target : this.targets) {
            InternalCompletableFuture future = this.operationService.createInvocationBuilder(this.serviceName, this.operationFactory.get(), target.getAddress()).setTryCount(10).setTryPauseMillis(300L).invoke();
            this.futures.put(target, future);
        }
    }

    private void awaitCompletion() {
        for (Map.Entry<Member, Future> responseEntry : this.futures.entrySet()) {
            try {
                Future future = responseEntry.getValue();
                this.results.put(responseEntry.getKey(), this.serializationService.toObject(future.get()));
            }
            catch (Throwable t) {
                if (this.logger.isFinestEnabled()) {
                    this.logger.finest(t);
                } else {
                    this.logger.warning(t.getMessage());
                }
                this.results.put(responseEntry.getKey(), t);
            }
        }
    }

    private void retryFailedTargets() throws InterruptedException, ExecutionException {
        Object result;
        LinkedList<Member> failedMembers = new LinkedList<Member>();
        for (Map.Entry<Member, Object> memberResult : this.results.entrySet()) {
            Member member = memberResult.getKey();
            result = memberResult.getValue();
            if (!(result instanceof Throwable)) continue;
            failedMembers.add(member);
        }
        for (Member failedMember : failedMembers) {
            Operation operation = this.operationFactory.get();
            InternalCompletableFuture future = this.operationService.createInvocationBuilder(this.serviceName, operation, failedMember.getAddress()).invoke();
            this.results.put(failedMember, future);
        }
        for (Member failedMember : failedMembers) {
            Future future = (Future)this.results.get(failedMember);
            result = future.get();
            this.results.put(failedMember, result);
        }
    }
}

