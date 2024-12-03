/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.tika.pipes.async;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.tika.exception.TikaException;
import org.apache.tika.pipes.FetchEmitTuple;
import org.apache.tika.pipes.PipesClient;
import org.apache.tika.pipes.PipesException;
import org.apache.tika.pipes.PipesResult;
import org.apache.tika.pipes.async.AsyncConfig;
import org.apache.tika.pipes.async.AsyncEmitter;
import org.apache.tika.pipes.async.OfferLargerThanQueueSize;
import org.apache.tika.pipes.emitter.EmitData;
import org.apache.tika.pipes.emitter.EmitterManager;
import org.apache.tika.pipes.pipesiterator.PipesIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncProcessor
implements Closeable {
    static final int PARSER_FUTURE_CODE = 1;
    static final int WATCHER_FUTURE_CODE = 3;
    private static final Logger LOG = LoggerFactory.getLogger(AsyncProcessor.class);
    private final ArrayBlockingQueue<FetchEmitTuple> fetchEmitTuples;
    private final ArrayBlockingQueue<EmitData> emitData;
    private final ExecutorCompletionService<Integer> executorCompletionService;
    private final ExecutorService executorService;
    private final AsyncConfig asyncConfig;
    private final AtomicLong totalProcessed = new AtomicLong(0L);
    private static long MAX_OFFER_WAIT_MS = 120000L;
    private volatile int numParserThreadsFinished = 0;
    private volatile int numEmitterThreadsFinished = 0;
    private boolean addedEmitterSemaphores = false;
    boolean isShuttingDown = false;

    public AsyncProcessor(Path tikaConfigPath) throws TikaException, IOException {
        this.asyncConfig = AsyncConfig.load(tikaConfigPath);
        this.fetchEmitTuples = new ArrayBlockingQueue(this.asyncConfig.getQueueSize());
        this.emitData = new ArrayBlockingQueue(100);
        this.executorService = Executors.newFixedThreadPool(this.asyncConfig.getNumClients() + this.asyncConfig.getNumEmitters() + 1);
        this.executorCompletionService = new ExecutorCompletionService(this.executorService);
        if (!tikaConfigPath.toAbsolutePath().equals(this.asyncConfig.getTikaConfig().toAbsolutePath())) {
            LOG.warn("TikaConfig for AsyncProcessor ({}) is different from TikaConfig for workers ({}). If this is intended, please ignore this warning.", (Object)tikaConfigPath.toAbsolutePath(), (Object)this.asyncConfig.getTikaConfig().toAbsolutePath());
        }
        this.executorCompletionService.submit(() -> {
            try {
                while (true) {
                    Thread.sleep(500L);
                    this.checkActive();
                }
            }
            catch (InterruptedException e) {
                return 3;
            }
        });
        for (int i = 0; i < this.asyncConfig.getNumClients(); ++i) {
            this.executorCompletionService.submit(new FetchEmitWorker(this.asyncConfig, this.fetchEmitTuples, this.emitData));
        }
        EmitterManager emitterManager = EmitterManager.load(this.asyncConfig.getTikaConfig());
        for (int i = 0; i < this.asyncConfig.getNumEmitters(); ++i) {
            this.executorCompletionService.submit(new AsyncEmitter(this.asyncConfig, this.emitData, emitterManager));
        }
    }

    public synchronized boolean offer(List<FetchEmitTuple> newFetchEmitTuples, long offerMs) throws PipesException, InterruptedException {
        if (this.isShuttingDown) {
            throw new IllegalStateException("Can't call offer after calling close() or shutdownNow()");
        }
        if (newFetchEmitTuples.size() > this.asyncConfig.getQueueSize()) {
            throw new OfferLargerThanQueueSize(newFetchEmitTuples.size(), this.asyncConfig.getQueueSize());
        }
        long start = System.currentTimeMillis();
        long elapsed = System.currentTimeMillis() - start;
        while (elapsed < offerMs) {
            if (this.fetchEmitTuples.remainingCapacity() > newFetchEmitTuples.size()) {
                try {
                    this.fetchEmitTuples.addAll(newFetchEmitTuples);
                    return true;
                }
                catch (IllegalStateException e) {
                    LOG.debug("couldn't add full list", (Throwable)e);
                }
            }
            Thread.sleep(100L);
            elapsed = System.currentTimeMillis() - start;
        }
        return false;
    }

    public int getCapacity() {
        return this.fetchEmitTuples.remainingCapacity();
    }

    public synchronized boolean offer(FetchEmitTuple t, long offerMs) throws PipesException, InterruptedException {
        if (this.fetchEmitTuples == null) {
            throw new IllegalStateException("queue hasn't been initialized yet.");
        }
        if (this.isShuttingDown) {
            throw new IllegalStateException("Can't call offer after calling close() or shutdownNow()");
        }
        this.checkActive();
        return this.fetchEmitTuples.offer(t, offerMs, TimeUnit.MILLISECONDS);
    }

    public void finished() throws InterruptedException {
        for (int i = 0; i < this.asyncConfig.getNumClients(); ++i) {
            boolean offered = this.fetchEmitTuples.offer(PipesIterator.COMPLETED_SEMAPHORE, MAX_OFFER_WAIT_MS, TimeUnit.MILLISECONDS);
            if (offered) continue;
            throw new RuntimeException("Couldn't offer completed semaphore within " + MAX_OFFER_WAIT_MS + " ms");
        }
    }

    public synchronized boolean checkActive() throws InterruptedException {
        Future<Integer> future = this.executorCompletionService.poll();
        if (future != null) {
            try {
                Integer i = future.get();
                switch (i) {
                    case 1: {
                        ++this.numParserThreadsFinished;
                        LOG.debug("fetchEmitWorker finished, total {}", (Object)this.numParserThreadsFinished);
                        break;
                    }
                    case 2: {
                        ++this.numEmitterThreadsFinished;
                        LOG.debug("emitter thread finished, total {}", (Object)this.numEmitterThreadsFinished);
                        break;
                    }
                    case 3: {
                        LOG.debug("watcher thread finished");
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Don't recognize this future code: " + i);
                    }
                }
            }
            catch (ExecutionException e) {
                LOG.error("execution exception", (Throwable)e);
                throw new RuntimeException(e);
            }
        }
        if (this.numParserThreadsFinished == this.asyncConfig.getNumClients() && !this.addedEmitterSemaphores) {
            for (int i = 0; i < this.asyncConfig.getNumEmitters(); ++i) {
                try {
                    boolean offered = this.emitData.offer(AsyncEmitter.EMIT_DATA_STOP_SEMAPHORE, MAX_OFFER_WAIT_MS, TimeUnit.MILLISECONDS);
                    if (offered) continue;
                    throw new RuntimeException("Couldn't offer emit data stop semaphore within " + MAX_OFFER_WAIT_MS + " ms");
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            this.addedEmitterSemaphores = true;
        }
        return this.numParserThreadsFinished != this.asyncConfig.getNumClients() || this.numEmitterThreadsFinished != this.asyncConfig.getNumEmitters();
    }

    @Override
    public void close() throws IOException {
        this.executorService.shutdownNow();
        this.asyncConfig.getPipesReporter().close();
    }

    public long getTotalProcessed() {
        return this.totalProcessed.get();
    }

    private class FetchEmitWorker
    implements Callable<Integer> {
        private final AsyncConfig asyncConfig;
        private final ArrayBlockingQueue<FetchEmitTuple> fetchEmitTuples;
        private final ArrayBlockingQueue<EmitData> emitDataQueue;

        private FetchEmitWorker(AsyncConfig asyncConfig, ArrayBlockingQueue<FetchEmitTuple> fetchEmitTuples, ArrayBlockingQueue<EmitData> emitDataQueue) {
            this.asyncConfig = asyncConfig;
            this.fetchEmitTuples = fetchEmitTuples;
            this.emitDataQueue = emitDataQueue;
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        public Integer call() throws Exception {
            try (PipesClient pipesClient = new PipesClient(this.asyncConfig);){
                while (true) {
                    boolean offered;
                    FetchEmitTuple t;
                    if ((t = this.fetchEmitTuples.poll(1L, TimeUnit.SECONDS)) == null) {
                        if (!LOG.isTraceEnabled()) continue;
                        LOG.trace("null fetch emit tuple");
                        continue;
                    }
                    if (t == PipesIterator.COMPLETED_SEMAPHORE) {
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("hit completed semaphore");
                        }
                        Integer n = 1;
                        return n;
                    }
                    PipesResult result = null;
                    long start = System.currentTimeMillis();
                    try {
                        result = pipesClient.process(t);
                    }
                    catch (IOException e) {
                        result = PipesResult.UNSPECIFIED_CRASH;
                    }
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("timer -- pipes client process: {} ms", (Object)(System.currentTimeMillis() - start));
                    }
                    long offerStart = System.currentTimeMillis();
                    if (!(result.getStatus() != PipesResult.STATUS.PARSE_SUCCESS && result.getStatus() != PipesResult.STATUS.PARSE_SUCCESS_WITH_EXCEPTION || (offered = this.emitDataQueue.offer(result.getEmitData(), MAX_OFFER_WAIT_MS, TimeUnit.MILLISECONDS)))) {
                        throw new RuntimeException("Couldn't offer emit data to queue within " + MAX_OFFER_WAIT_MS + " ms");
                    }
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("timer -- offered: {} ms", (Object)(System.currentTimeMillis() - offerStart));
                    }
                    long elapsed = System.currentTimeMillis() - start;
                    this.asyncConfig.getPipesReporter().report(t, result, elapsed);
                    AsyncProcessor.this.totalProcessed.incrementAndGet();
                    continue;
                    break;
                }
            }
        }
    }
}

