/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking.nio;

import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.networking.ChannelErrorHandler;
import com.hazelcast.internal.networking.ChannelHandler;
import com.hazelcast.internal.networking.HandlerStatus;
import com.hazelcast.internal.networking.OutboundFrame;
import com.hazelcast.internal.networking.OutboundHandler;
import com.hazelcast.internal.networking.OutboundPipeline;
import com.hazelcast.internal.networking.nio.NioChannel;
import com.hazelcast.internal.networking.nio.NioPipeline;
import com.hazelcast.internal.networking.nio.NioThread;
import com.hazelcast.internal.networking.nio.iobalancer.IOBalancer;
import com.hazelcast.internal.util.counters.SwCounter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.collection.ArrayUtils;
import com.hazelcast.util.function.Supplier;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class NioOutboundPipeline
extends NioPipeline
implements Supplier<OutboundFrame>,
OutboundPipeline {
    @Probe(name="writeQueueSize")
    public final Queue<OutboundFrame> writeQueue = new ConcurrentLinkedQueue<OutboundFrame>();
    @Probe(name="priorityWriteQueueSize")
    public final Queue<OutboundFrame> priorityWriteQueue = new ConcurrentLinkedQueue<OutboundFrame>();
    private OutboundHandler[] handlers = new OutboundHandler[0];
    private ByteBuffer sendBuffer;
    private final AtomicBoolean scheduled = new AtomicBoolean(false);
    @Probe(name="bytesWritten")
    private final SwCounter bytesWritten = SwCounter.newSwCounter();
    @Probe(name="normalFramesWritten")
    private final SwCounter normalFramesWritten = SwCounter.newSwCounter();
    @Probe(name="priorityFramesWritten")
    private final SwCounter priorityFramesWritten = SwCounter.newSwCounter();
    private volatile long lastWriteTime;
    private long bytesWrittenLastPublish;
    private long normalFramesWrittenLastPublish;
    private long priorityFramesWrittenLastPublish;
    private long processCountLastPublish;

    NioOutboundPipeline(NioChannel channel, NioThread owner, ChannelErrorHandler errorHandler, ILogger logger, IOBalancer balancer) {
        super(channel, owner, errorHandler, 4, logger, balancer);
    }

    @Override
    public long load() {
        switch (this.loadType) {
            case 0: {
                return this.processCount.get();
            }
            case 1: {
                return this.bytesWritten.get();
            }
            case 2: {
                return this.normalFramesWritten.get() + this.priorityFramesWritten.get();
            }
        }
        throw new RuntimeException();
    }

    public int totalFramesPending() {
        return this.writeQueue.size() + this.priorityWriteQueue.size();
    }

    public long lastWriteTimeMillis() {
        return this.lastWriteTime;
    }

    @Probe(name="writeQueuePendingBytes", level=ProbeLevel.DEBUG)
    public long bytesPending() {
        return this.bytesPending(this.writeQueue);
    }

    @Probe(name="priorityWriteQueuePendingBytes", level=ProbeLevel.DEBUG)
    public long priorityBytesPending() {
        return this.bytesPending(this.priorityWriteQueue);
    }

    private long bytesPending(Queue<OutboundFrame> writeQueue) {
        long bytesPending = 0L;
        for (OutboundFrame frame : writeQueue) {
            bytesPending += (long)frame.getFrameLength();
        }
        return bytesPending;
    }

    @Probe
    private long idleTimeMs() {
        return Math.max(System.currentTimeMillis() - this.lastWriteTime, 0L);
    }

    @Probe(level=ProbeLevel.DEBUG)
    private long isScheduled() {
        return this.scheduled.get() ? 1L : 0L;
    }

    public void write(OutboundFrame frame) {
        if (frame.isUrgent()) {
            this.priorityWriteQueue.offer(frame);
        } else {
            this.writeQueue.offer(frame);
        }
        this.schedule();
    }

    @Override
    public OutboundFrame get() {
        OutboundFrame frame = this.priorityWriteQueue.poll();
        if (frame == null) {
            frame = this.writeQueue.poll();
            if (frame == null) {
                return null;
            }
            this.normalFramesWritten.inc();
        } else {
            this.priorityFramesWritten.inc();
        }
        return frame;
    }

    private void schedule() {
        if (this.scheduled.get()) {
            return;
        }
        if (!this.scheduled.compareAndSet(false, true)) {
            return;
        }
        this.addTaskAndWakeup(this);
    }

    @Override
    public void process() throws Exception {
        this.processCount.inc();
        OutboundHandler[] localHandlers = this.handlers;
        HandlerStatus pipelineStatus = HandlerStatus.CLEAN;
        for (int handlerIndex = 0; handlerIndex < localHandlers.length; ++handlerIndex) {
            OutboundHandler handler = localHandlers[handlerIndex];
            HandlerStatus handlerStatus = handler.onWrite();
            if (localHandlers != this.handlers) {
                localHandlers = this.handlers;
                pipelineStatus = HandlerStatus.CLEAN;
                handlerIndex = -1;
                continue;
            }
            if (handlerStatus == HandlerStatus.CLEAN) continue;
            pipelineStatus = handlerStatus;
        }
        this.flushToSocket();
        if (this.sendBuffer.remaining() > 0) {
            pipelineStatus = HandlerStatus.DIRTY;
        }
        switch (pipelineStatus) {
            case CLEAN: {
                this.unschedule();
                break;
            }
            case DIRTY: {
                this.registerOp(4);
                break;
            }
            case BLOCKED: {
                this.unregisterOp(4);
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }

    private void unschedule() throws IOException {
        this.unregisterOp(4);
        this.scheduled.set(false);
        if (this.writeQueue.isEmpty() && this.priorityWriteQueue.isEmpty()) {
            return;
        }
        if (!this.scheduled.compareAndSet(false, true)) {
            return;
        }
        this.owner().addTask(this);
    }

    private void flushToSocket() throws IOException {
        this.lastWriteTime = System.currentTimeMillis();
        int written = this.socketChannel.write(this.sendBuffer);
        this.bytesWritten.inc(written);
    }

    void drainWriteQueues() {
        this.writeQueue.clear();
        this.priorityWriteQueue.clear();
    }

    long bytesWritten() {
        return this.bytesWritten.get();
    }

    @Override
    protected void publishMetrics() {
        if (Thread.currentThread() != this.owner) {
            return;
        }
        this.owner.bytesTransceived += this.bytesWritten.get() - this.bytesWrittenLastPublish;
        this.owner.framesTransceived += this.normalFramesWritten.get() - this.normalFramesWrittenLastPublish;
        this.owner.priorityFramesTransceived += this.priorityFramesWritten.get() - this.priorityFramesWrittenLastPublish;
        this.owner.processCount += this.processCount.get() - this.processCountLastPublish;
        this.bytesWrittenLastPublish = this.bytesWritten.get();
        this.normalFramesWrittenLastPublish = this.normalFramesWritten.get();
        this.priorityFramesWrittenLastPublish = this.priorityFramesWritten.get();
        this.processCountLastPublish = this.processCount.get();
    }

    public String toString() {
        return this.channel + ".outboundPipeline";
    }

    @Override
    protected Iterable<? extends ChannelHandler> handlers() {
        return Arrays.asList(this.handlers);
    }

    @Override
    public OutboundPipeline remove(OutboundHandler handler) {
        return this.replace(handler, new OutboundHandler[0]);
    }

    @Override
    public OutboundPipeline addLast(OutboundHandler ... addedHandlers) {
        Preconditions.checkNotNull(addedHandlers, "addedHandlers can't be null");
        for (OutboundHandler addedHandler : addedHandlers) {
            ((ChannelHandler)addedHandler.setChannel(this.channel)).handlerAdded();
        }
        this.updatePipeline(ArrayUtils.append(this.handlers, addedHandlers));
        return this;
    }

    @Override
    public OutboundPipeline replace(OutboundHandler oldHandler, OutboundHandler ... addedHandlers) {
        Preconditions.checkNotNull(oldHandler, "oldHandler can't be null");
        Preconditions.checkNotNull(addedHandlers, "newHandler can't be null");
        OutboundHandler[] newHandlers = ArrayUtils.replaceFirst(this.handlers, oldHandler, addedHandlers);
        if (newHandlers == this.handlers) {
            throw new IllegalArgumentException("handler " + oldHandler + " isn't part of the pipeline");
        }
        for (OutboundHandler addedHandler : addedHandlers) {
            ((ChannelHandler)addedHandler.setChannel(this.channel)).handlerAdded();
        }
        this.updatePipeline(newHandlers);
        return this;
    }

    private void updatePipeline(OutboundHandler[] newHandlers) {
        this.handlers = newHandlers;
        this.sendBuffer = newHandlers.length == 0 ? null : (ByteBuffer)newHandlers[newHandlers.length - 1].dst();
        ChannelHandler prev = null;
        for (OutboundHandler handler : this.handlers) {
            if (prev == null) {
                handler.src(this);
            } else {
                Object src = prev.dst();
                if (src instanceof ByteBuffer) {
                    handler.src(src);
                }
            }
            prev = handler;
        }
    }

    private String pipelineToString() {
        StringBuilder sb = new StringBuilder("out-pipeline[");
        OutboundHandler[] handlers = this.handlers;
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
    public OutboundPipeline wakeup() {
        this.addTaskAndWakeup(this);
        return this;
    }
}

