/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.concurrency;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import nonapi.io.github.classgraph.concurrency.InterruptionChecker;
import nonapi.io.github.classgraph.utils.LogNode;

public class WorkQueue<T>
implements AutoCloseable {
    private final WorkUnitProcessor<T> workUnitProcessor;
    private final BlockingQueue<WorkUnitWrapper<T>> workUnits = new LinkedBlockingQueue<WorkUnitWrapper<T>>();
    private final int numWorkers;
    private final AtomicInteger numIncompleteWorkUnits = new AtomicInteger();
    private final ConcurrentLinkedQueue<Future<?>> workerFutures = new ConcurrentLinkedQueue();
    private final InterruptionChecker interruptionChecker;
    private final LogNode log;

    public static <U> void runWorkQueue(Collection<U> elements, ExecutorService executorService, InterruptionChecker interruptionChecker, int numParallelTasks, LogNode log, WorkUnitProcessor<U> workUnitProcessor) throws InterruptedException, ExecutionException {
        if (elements.isEmpty()) {
            return;
        }
        try (WorkQueue<U> workQueue = new WorkQueue<U>(elements, workUnitProcessor, numParallelTasks, interruptionChecker, log);){
            super.startWorkers(executorService, numParallelTasks - 1);
            super.runWorkLoop();
        }
    }

    private WorkQueue(Collection<T> initialWorkUnits, WorkUnitProcessor<T> workUnitProcessor, int numWorkers, InterruptionChecker interruptionChecker, LogNode log) {
        this.workUnitProcessor = workUnitProcessor;
        this.numWorkers = numWorkers;
        this.interruptionChecker = interruptionChecker;
        this.log = log;
        this.addWorkUnits(initialWorkUnits);
    }

    private void startWorkers(ExecutorService executorService, int numTasks) {
        for (int i = 0; i < numTasks; ++i) {
            this.workerFutures.add(executorService.submit(new Callable<Void>(){

                @Override
                public Void call() throws Exception {
                    WorkQueue.this.runWorkLoop();
                    return null;
                }
            }));
        }
    }

    private void sendPoisonPills() {
        for (int i = 0; i < this.numWorkers; ++i) {
            this.workUnits.add(new WorkUnitWrapper<Object>(null));
        }
    }

    private void runWorkLoop() throws InterruptedException, ExecutionException {
        while (true) {
            try {
                this.interruptionChecker.check();
                WorkUnitWrapper<T> workUnitWrapper = this.workUnits.take();
                if (workUnitWrapper.workUnit == null) break;
                this.workUnitProcessor.processWorkUnit(workUnitWrapper.workUnit, this, this.log);
            }
            catch (Error | InterruptedException e) {
                this.workUnits.clear();
                this.numIncompleteWorkUnits.set(0);
                this.sendPoisonPills();
                throw e;
            }
            catch (RuntimeException e) {
                this.workUnits.clear();
                this.numIncompleteWorkUnits.set(0);
                this.sendPoisonPills();
                throw new ExecutionException("Worker thread threw unchecked exception", e);
            }
            if (this.numIncompleteWorkUnits.decrementAndGet() != 0) continue;
            this.sendPoisonPills();
        }
    }

    public void addWorkUnit(T workUnit) {
        if (workUnit == null) {
            throw new NullPointerException("workUnit cannot be null");
        }
        this.numIncompleteWorkUnits.incrementAndGet();
        this.workUnits.add(new WorkUnitWrapper<T>(workUnit));
    }

    public void addWorkUnits(Collection<T> workUnits) {
        for (T workUnit : workUnits) {
            this.addWorkUnit(workUnit);
        }
    }

    @Override
    public void close() throws ExecutionException {
        Future<?> future;
        while ((future = this.workerFutures.poll()) != null) {
            try {
                future.get();
            }
            catch (CancellationException e) {
                if (this.log == null) continue;
                this.log.log("~", "Worker thread was cancelled");
            }
            catch (InterruptedException e) {
                if (this.log != null) {
                    this.log.log("~", "Worker thread was interrupted");
                }
                this.interruptionChecker.interrupt();
            }
            catch (ExecutionException e) {
                this.interruptionChecker.setExecutionException(e);
                this.interruptionChecker.interrupt();
            }
        }
    }

    public static interface WorkUnitProcessor<T> {
        public void processWorkUnit(T var1, WorkQueue<T> var2, LogNode var3) throws InterruptedException;
    }

    private static class WorkUnitWrapper<T> {
        final T workUnit;

        public WorkUnitWrapper(T workUnit) {
            this.workUnit = workUnit;
        }
    }
}

