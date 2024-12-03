/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.tika.pipes.async;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.apache.tika.pipes.async.AsyncConfig;
import org.apache.tika.pipes.emitter.EmitData;
import org.apache.tika.pipes.emitter.Emitter;
import org.apache.tika.pipes.emitter.EmitterManager;
import org.apache.tika.pipes.emitter.TikaEmitterException;
import org.apache.tika.utils.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncEmitter
implements Callable<Integer> {
    static final EmitData EMIT_DATA_STOP_SEMAPHORE = new EmitData(null, null);
    static final int EMITTER_FUTURE_CODE = 2;
    private static final Logger LOG = LoggerFactory.getLogger(AsyncEmitter.class);
    private final AsyncConfig asyncConfig;
    private final EmitterManager emitterManager;
    private final ArrayBlockingQueue<EmitData> emitDataQueue;
    Instant lastEmitted = Instant.now();

    public AsyncEmitter(AsyncConfig asyncConfig, ArrayBlockingQueue<EmitData> emitData, EmitterManager emitterManager) {
        this.asyncConfig = asyncConfig;
        this.emitDataQueue = emitData;
        this.emitterManager = emitterManager;
    }

    @Override
    public Integer call() throws Exception {
        EmitDataCache cache = new EmitDataCache(this.asyncConfig.getEmitMaxEstimatedBytes());
        while (true) {
            EmitData emitData;
            if ((emitData = this.emitDataQueue.poll(500L, TimeUnit.MILLISECONDS)) == EMIT_DATA_STOP_SEMAPHORE) {
                cache.emitAll();
                return 2;
            }
            if (emitData != null) {
                cache.add(emitData);
            } else {
                LOG.trace("Nothing on the async queue");
            }
            LOG.debug("cache size: ({}) bytes and extract count: {}", (Object)cache.estimatedSize, (Object)cache.size);
            long elapsed = ChronoUnit.MILLIS.between(this.lastEmitted, Instant.now());
            if (elapsed <= this.asyncConfig.getEmitWithinMillis()) continue;
            LOG.debug("{} elapsed > {}, going to emitAll", (Object)elapsed, (Object)this.asyncConfig.getEmitWithinMillis());
            cache.emitAll();
        }
    }

    private class EmitDataCache {
        private final long maxBytes;
        long estimatedSize = 0L;
        int size = 0;
        Map<String, List<EmitData>> map = new HashMap<String, List<EmitData>>();

        public EmitDataCache(long maxBytes) {
            this.maxBytes = maxBytes;
        }

        void updateEstimatedSize(long newBytes) {
            this.estimatedSize += newBytes;
        }

        void add(EmitData data) {
            ++this.size;
            long sz = data.getEstimatedSizeBytes();
            if (this.estimatedSize + sz > this.maxBytes) {
                LOG.debug("estimated size ({}) > maxBytes({}), going to emitAll", (Object)(this.estimatedSize + sz), (Object)this.maxBytes);
                this.emitAll();
            }
            List cached = this.map.computeIfAbsent(data.getEmitKey().getEmitterName(), k -> new ArrayList());
            this.updateEstimatedSize(sz);
            cached.add(data);
        }

        private void emitAll() {
            int emitted = 0;
            LOG.debug("about to emit {} files, {} estimated bytes", (Object)this.size, (Object)this.estimatedSize);
            for (Map.Entry<String, List<EmitData>> e : this.map.entrySet()) {
                Emitter emitter = AsyncEmitter.this.emitterManager.getEmitter(e.getKey());
                this.tryToEmit(emitter, e.getValue());
                emitted += e.getValue().size();
            }
            LOG.debug("emitted: {} files", (Object)emitted);
            this.estimatedSize = 0L;
            this.size = 0;
            this.map.clear();
            AsyncEmitter.this.lastEmitted = Instant.now();
        }

        private void tryToEmit(Emitter emitter, List<EmitData> cachedEmitData) {
            try {
                emitter.emit(cachedEmitData);
            }
            catch (IOException | TikaEmitterException e) {
                LOG.warn("emitter class ({}): {}", emitter.getClass(), (Object)ExceptionUtils.getStackTrace(e));
            }
        }
    }
}

