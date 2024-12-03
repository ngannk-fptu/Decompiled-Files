/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.tika.pipes.pipesiterator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.tika.config.ConfigBase;
import org.apache.tika.config.Field;
import org.apache.tika.config.Initializable;
import org.apache.tika.config.InitializableProblemHandler;
import org.apache.tika.config.Param;
import org.apache.tika.exception.TikaConfigException;
import org.apache.tika.exception.TikaTimeoutException;
import org.apache.tika.pipes.FetchEmitTuple;
import org.apache.tika.pipes.HandlerConfig;
import org.apache.tika.sax.BasicContentHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PipesIterator
extends ConfigBase
implements Callable<Integer>,
Iterable<FetchEmitTuple>,
Initializable {
    public static final long DEFAULT_MAX_WAIT_MS = 300000L;
    public static final int DEFAULT_QUEUE_SIZE = 1000;
    public static final FetchEmitTuple COMPLETED_SEMAPHORE = new FetchEmitTuple(null, null, null, null, null, null);
    private static final Logger LOGGER = LoggerFactory.getLogger(PipesIterator.class);
    private long maxWaitMs = 300000L;
    private ArrayBlockingQueue<FetchEmitTuple> queue = null;
    private int queueSize = 1000;
    private String fetcherName;
    private String emitterName;
    private FetchEmitTuple.ON_PARSE_EXCEPTION onParseException = FetchEmitTuple.ON_PARSE_EXCEPTION.EMIT;
    private BasicContentHandlerFactory.HANDLER_TYPE handlerType = BasicContentHandlerFactory.HANDLER_TYPE.TEXT;
    private HandlerConfig.PARSE_MODE parseMode = HandlerConfig.PARSE_MODE.RMETA;
    private int writeLimit = -1;
    private int maxEmbeddedResources = -1;
    private int added = 0;
    private FutureTask<Integer> futureTask;

    public static PipesIterator build(Path tikaConfigFile) throws IOException, TikaConfigException {
        try (InputStream is = Files.newInputStream(tikaConfigFile, new OpenOption[0]);){
            PipesIterator pipesIterator = PipesIterator.buildSingle("pipesIterator", PipesIterator.class, is);
            return pipesIterator;
        }
    }

    public String getFetcherName() {
        return this.fetcherName;
    }

    @Field
    public void setFetcherName(String fetcherName) {
        this.fetcherName = fetcherName;
    }

    public String getEmitterName() {
        return this.emitterName;
    }

    @Field
    public void setEmitterName(String emitterName) {
        this.emitterName = emitterName;
    }

    @Field
    public void setMaxWaitMs(long maxWaitMs) {
        this.maxWaitMs = maxWaitMs;
    }

    @Field
    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public FetchEmitTuple.ON_PARSE_EXCEPTION getOnParseException() {
        return this.onParseException;
    }

    @Field
    public void setOnParseException(String onParseException) throws TikaConfigException {
        if ("skip".equalsIgnoreCase(onParseException)) {
            this.setOnParseException(FetchEmitTuple.ON_PARSE_EXCEPTION.SKIP);
        } else if ("emit".equalsIgnoreCase(onParseException)) {
            this.setOnParseException(FetchEmitTuple.ON_PARSE_EXCEPTION.EMIT);
        } else {
            throw new TikaConfigException("must be either 'skip' or 'emit': " + onParseException);
        }
    }

    public void setOnParseException(FetchEmitTuple.ON_PARSE_EXCEPTION onParseException) {
        this.onParseException = onParseException;
    }

    @Field
    public void setHandlerType(String handlerType) {
        this.handlerType = BasicContentHandlerFactory.parseHandlerType(handlerType, BasicContentHandlerFactory.HANDLER_TYPE.TEXT);
    }

    @Field
    public void setWriteLimit(int writeLimit) {
        this.writeLimit = writeLimit;
    }

    @Field
    public void setMaxEmbeddedResources(int maxEmbeddedResources) {
        this.maxEmbeddedResources = maxEmbeddedResources;
    }

    @Field
    public void setParseMode(String parseModeString) {
        this.setParseMode(HandlerConfig.PARSE_MODE.parseMode(parseModeString));
    }

    public void setParseMode(HandlerConfig.PARSE_MODE parsePARSEMode) {
        this.parseMode = parsePARSEMode;
    }

    @Override
    public Integer call() throws Exception {
        this.enqueue();
        this.tryToAdd(COMPLETED_SEMAPHORE);
        return this.added;
    }

    protected HandlerConfig getHandlerConfig() {
        return new HandlerConfig(this.handlerType, this.parseMode, this.writeLimit, this.maxEmbeddedResources, false);
    }

    protected abstract void enqueue() throws IOException, TimeoutException, InterruptedException;

    protected void tryToAdd(FetchEmitTuple p) throws InterruptedException, TimeoutException {
        ++this.added;
        boolean offered = this.queue.offer(p, this.maxWaitMs, TimeUnit.MILLISECONDS);
        if (!offered) {
            throw new TimeoutException("timed out while offering");
        }
    }

    @Override
    public void initialize(Map<String, Param> params) throws TikaConfigException {
    }

    @Override
    public void checkInitialization(InitializableProblemHandler problemHandler) throws TikaConfigException {
    }

    @Override
    public Iterator<FetchEmitTuple> iterator() {
        if (this.futureTask != null) {
            throw new IllegalStateException("Can't call iterator more than once!");
        }
        this.futureTask = new FutureTask<Integer>(this);
        this.queue = new ArrayBlockingQueue(this.queueSize);
        new Thread(this.futureTask).start();
        return new TupleIterator();
    }

    private class TupleIterator
    implements Iterator<FetchEmitTuple> {
        FetchEmitTuple next = null;

        private TupleIterator() {
        }

        @Override
        public boolean hasNext() {
            if (this.next == null) {
                this.next = this.pollNext();
            }
            return this.next != COMPLETED_SEMAPHORE;
        }

        @Override
        public FetchEmitTuple next() {
            if (this.next == COMPLETED_SEMAPHORE) {
                throw new IllegalStateException("don't call next() after hasNext() has returned false!");
            }
            FetchEmitTuple ret = this.next;
            this.next = this.pollNext();
            return ret;
        }

        private FetchEmitTuple pollNext() throws TikaTimeoutException {
            FetchEmitTuple t = null;
            long start = System.currentTimeMillis();
            try {
                long elapsed = System.currentTimeMillis() - start;
                while (t == null && elapsed < PipesIterator.this.maxWaitMs) {
                    this.checkThreadOk();
                    t = (FetchEmitTuple)PipesIterator.this.queue.poll(100L, TimeUnit.MILLISECONDS);
                    elapsed = System.currentTimeMillis() - start;
                }
            }
            catch (InterruptedException e) {
                LOGGER.warn("interrupted");
                return COMPLETED_SEMAPHORE;
            }
            if (t == null) {
                throw new TikaTimeoutException("waited longer than " + PipesIterator.this.maxWaitMs + "ms for the next tuple");
            }
            return t;
        }

        private void checkThreadOk() throws InterruptedException {
            if (PipesIterator.this.futureTask.isDone()) {
                try {
                    PipesIterator.this.futureTask.get();
                }
                catch (ExecutionException e) {
                    throw new RuntimeException(e.getCause());
                }
            }
        }
    }
}

