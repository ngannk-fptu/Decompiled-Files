/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking.nio;

import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.networking.ChannelErrorHandler;
import com.hazelcast.internal.networking.ChannelHandler;
import com.hazelcast.internal.networking.HandlerStatus;
import com.hazelcast.internal.networking.InboundHandler;
import com.hazelcast.internal.networking.InboundPipeline;
import com.hazelcast.internal.networking.nio.InboundHandlerWithCounters;
import com.hazelcast.internal.networking.nio.NioChannel;
import com.hazelcast.internal.networking.nio.NioPipeline;
import com.hazelcast.internal.networking.nio.NioPipelineTask;
import com.hazelcast.internal.networking.nio.NioThread;
import com.hazelcast.internal.networking.nio.iobalancer.IOBalancer;
import com.hazelcast.internal.util.counters.SwCounter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.collection.ArrayUtils;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public final class NioInboundPipeline
extends NioPipeline
implements InboundPipeline {
    private InboundHandler[] handlers = new InboundHandler[0];
    private ByteBuffer receiveBuffer;
    @Probe(name="bytesRead")
    private final SwCounter bytesRead = SwCounter.newSwCounter();
    @Probe(name="normalFramesRead")
    private final SwCounter normalFramesRead = SwCounter.newSwCounter();
    @Probe(name="priorityFramesRead")
    private final SwCounter priorityFramesRead = SwCounter.newSwCounter();
    private volatile long lastReadTime;
    private volatile long bytesReadLastPublish;
    private volatile long normalFramesReadLastPublish;
    private volatile long priorityFramesReadLastPublish;
    private volatile long processCountLastPublish;

    NioInboundPipeline(NioChannel channel, NioThread owner, ChannelErrorHandler errorHandler, ILogger logger, IOBalancer balancer) {
        super(channel, owner, errorHandler, 1, logger, balancer);
    }

    public long normalFramesRead() {
        return this.normalFramesRead.get();
    }

    public long priorityFramesRead() {
        return this.priorityFramesRead.get();
    }

    @Override
    public long load() {
        switch (this.loadType) {
            case 0: {
                return this.processCount.get();
            }
            case 1: {
                return this.bytesRead.get();
            }
            case 2: {
                return this.normalFramesRead.get() + this.priorityFramesRead.get();
            }
        }
        throw new RuntimeException();
    }

    @Probe(name="idleTimeMs")
    private long idleTimeMs() {
        return Math.max(System.currentTimeMillis() - this.lastReadTime, 0L);
    }

    public long lastReadTimeMillis() {
        return this.lastReadTime;
    }

    @Override
    void process() throws Exception {
        boolean unregisterRead;
        boolean cleanPipeline;
        this.processCount.inc();
        this.lastReadTime = System.currentTimeMillis();
        int readBytes = this.socketChannel.read(this.receiveBuffer);
        if (readBytes == -1) {
            throw new EOFException("Remote socket closed!");
        }
        this.bytesRead.inc(readBytes);
        InboundHandler[] localHandlers = this.handlers;
        do {
            cleanPipeline = true;
            unregisterRead = false;
            block6: for (int handlerIndex = 0; handlerIndex < localHandlers.length; ++handlerIndex) {
                InboundHandler handler = localHandlers[handlerIndex];
                HandlerStatus handlerStatus = handler.onRead();
                if (localHandlers != this.handlers) {
                    handlerIndex = -1;
                    localHandlers = this.handlers;
                    continue;
                }
                switch (handlerStatus) {
                    case CLEAN: {
                        continue block6;
                    }
                    case DIRTY: {
                        cleanPipeline = false;
                        continue block6;
                    }
                    case BLOCKED: {
                        cleanPipeline = true;
                        unregisterRead = true;
                        continue block6;
                    }
                    default: {
                        throw new IllegalStateException();
                    }
                }
            }
        } while (!cleanPipeline);
        if (unregisterRead) {
            this.unregisterOp(1);
        }
    }

    long bytesRead() {
        return this.bytesRead.get();
    }

    @Override
    void publishMetrics() {
        if (Thread.currentThread() != this.owner) {
            return;
        }
        this.owner.bytesTransceived += this.bytesRead.get() - this.bytesReadLastPublish;
        this.owner.framesTransceived += this.normalFramesRead.get() - this.normalFramesReadLastPublish;
        this.owner.priorityFramesTransceived += this.priorityFramesRead.get() - this.priorityFramesReadLastPublish;
        this.owner.processCount += this.processCount.get() - this.processCountLastPublish;
        this.bytesReadLastPublish = this.bytesRead.get();
        this.normalFramesReadLastPublish = this.normalFramesRead.get();
        this.priorityFramesReadLastPublish = this.priorityFramesRead.get();
        this.processCountLastPublish = this.processCount.get();
    }

    public String toString() {
        return this.channel + ".inboundPipeline";
    }

    @Override
    protected Iterable<? extends ChannelHandler> handlers() {
        return Arrays.asList(this.handlers);
    }

    @Override
    public InboundPipeline remove(InboundHandler handler) {
        return this.replace(handler, new InboundHandler[0]);
    }

    @Override
    public InboundPipeline addLast(InboundHandler ... addedHandlers) {
        Preconditions.checkNotNull(addedHandlers, "handlers can't be null");
        for (InboundHandler addedHandler : addedHandlers) {
            this.fixDependencies(addedHandler);
            ((ChannelHandler)addedHandler.setChannel(this.channel)).handlerAdded();
        }
        this.updatePipeline(ArrayUtils.append(this.handlers, addedHandlers));
        return this;
    }

    @Override
    public InboundPipeline replace(InboundHandler oldHandler, InboundHandler ... addedHandlers) {
        Preconditions.checkNotNull(oldHandler, "oldHandler can't be null");
        Preconditions.checkNotNull(addedHandlers, "addedHandlers can't be null");
        InboundHandler[] newHandlers = ArrayUtils.replaceFirst(this.handlers, oldHandler, addedHandlers);
        if (newHandlers == this.handlers) {
            throw new IllegalArgumentException("handler " + oldHandler + " isn't part of the pipeline");
        }
        for (InboundHandler addedHandler : addedHandlers) {
            this.fixDependencies(addedHandler);
            ((ChannelHandler)addedHandler.setChannel(this.channel)).handlerAdded();
        }
        this.updatePipeline(newHandlers);
        return this;
    }

    private void fixDependencies(ChannelHandler addedHandler) {
        if (addedHandler instanceof InboundHandlerWithCounters) {
            InboundHandlerWithCounters c = (InboundHandlerWithCounters)addedHandler;
            c.setNormalPacketsRead(this.normalFramesRead);
            c.setPriorityPacketsRead(this.priorityFramesRead);
        }
    }

    private void updatePipeline(InboundHandler[] handlers) {
        this.handlers = handlers;
        this.receiveBuffer = handlers.length == 0 ? null : (ByteBuffer)handlers[0].src();
        InboundHandler prev = null;
        for (InboundHandler handler : handlers) {
            Object src;
            if (prev != null && (src = handler.src()) instanceof ByteBuffer) {
                prev.dst(src);
            }
            prev = handler;
        }
    }

    private String pipelineToString() {
        StringBuilder sb = new StringBuilder("in-pipeline[");
        InboundHandler[] handlers = this.handlers;
        for (int k = 0; k < handlers.length; ++k) {
            if (k > 0) {
                sb.append("->-");
            }
            sb.append(handlers[k].getClass().getSimpleName());
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public NioInboundPipeline wakeup() {
        this.addTaskAndWakeup(new NioPipelineTask(this){

            @Override
            protected void run0() throws IOException {
                NioInboundPipeline.this.registerOp(1);
                NioInboundPipeline.this.run();
            }
        });
        return this;
    }
}

