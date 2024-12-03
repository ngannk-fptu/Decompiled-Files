/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.instance.LifecycleServiceImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.nio.Disposable;
import com.hazelcast.spi.CoreService;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.SplitBrainHandlerService;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ExceptionUtil;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Future;

class ClusterMergeTask
implements Runnable {
    private static final String MERGE_TASKS_EXECUTOR = "hz:cluster-merge";
    private final Node node;
    private final LifecycleServiceImpl lifecycleService;

    ClusterMergeTask(Node node) {
        this.node = node;
        this.lifecycleService = node.hazelcastInstance.getLifecycleService();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        this.lifecycleService.fireLifecycleEvent(LifecycleEvent.LifecycleState.MERGING);
        boolean joined = false;
        try {
            this.resetState();
            Collection<Runnable> coreTasks = this.collectMergeTasks(true);
            Collection<Runnable> nonCoreTasks = this.collectMergeTasks(false);
            this.resetServices();
            this.rejoin();
            joined = this.isJoined();
            if (joined) {
                try {
                    this.executeMergeTasks(coreTasks);
                    this.executeMergeTasks(nonCoreTasks);
                }
                catch (Throwable throwable) {
                    this.disposeTasks(coreTasks, nonCoreTasks);
                    throw throwable;
                }
                this.disposeTasks(coreTasks, nonCoreTasks);
            }
            this.lifecycleService.fireLifecycleEvent(joined ? LifecycleEvent.LifecycleState.MERGED : LifecycleEvent.LifecycleState.MERGE_FAILED);
        }
        catch (Throwable throwable) {
            this.lifecycleService.fireLifecycleEvent(joined ? LifecycleEvent.LifecycleState.MERGED : LifecycleEvent.LifecycleState.MERGE_FAILED);
            throw throwable;
        }
    }

    private void disposeTasks(Collection<Runnable> ... tasks) {
        for (Collection<Runnable> task : tasks) {
            for (Runnable runnable : task) {
                if (!(runnable instanceof Disposable)) continue;
                ((Disposable)((Object)runnable)).dispose();
            }
        }
    }

    private boolean isJoined() {
        return this.node.isRunning() && this.node.getClusterService().isJoined();
    }

    private void resetState() {
        this.node.reset();
        this.node.getClusterService().reset();
        this.node.getNodeExtension().getInternalHotRestartService().resetService(true);
        this.node.networkingService.stop();
        this.node.nodeEngine.reset();
    }

    private Collection<Runnable> collectMergeTasks(boolean coreServices) {
        Collection<SplitBrainHandlerService> services = this.node.nodeEngine.getServices(SplitBrainHandlerService.class);
        LinkedList<Runnable> tasks = new LinkedList<Runnable>();
        for (SplitBrainHandlerService service : services) {
            Runnable runnable;
            if (coreServices != this.isCoreService(service) || (runnable = service.prepareMergeRunnable()) == null) continue;
            tasks.add(runnable);
        }
        return tasks;
    }

    private boolean isCoreService(SplitBrainHandlerService service) {
        return service instanceof CoreService;
    }

    private void resetServices() {
        Collection<ManagedService> managedServices = this.node.nodeEngine.getServices(ManagedService.class);
        for (ManagedService service : managedServices) {
            if (service instanceof ClusterService) continue;
            service.reset();
        }
    }

    private void rejoin() {
        this.node.networkingService.start();
        this.node.join();
    }

    private void executeMergeTasks(Collection<Runnable> tasks) {
        LinkedList futures = new LinkedList();
        for (Runnable runnable : tasks) {
            Future<?> f = this.node.nodeEngine.getExecutionService().submit(MERGE_TASKS_EXECUTOR, runnable);
            futures.add(f);
        }
        for (Future future : futures) {
            try {
                this.waitOnFuture(future);
            }
            catch (HazelcastInstanceNotActiveException e) {
                EmptyStatement.ignore(e);
            }
            catch (Exception e) {
                this.node.getLogger(this.getClass()).severe("While merging...", e);
            }
        }
    }

    private <V> V waitOnFuture(Future<V> future) {
        try {
            return future.get();
        }
        catch (Throwable t) {
            if (!this.node.isRunning()) {
                future.cancel(true);
                throw new HazelcastInstanceNotActiveException();
            }
            throw ExceptionUtil.rethrow(t);
        }
    }
}

