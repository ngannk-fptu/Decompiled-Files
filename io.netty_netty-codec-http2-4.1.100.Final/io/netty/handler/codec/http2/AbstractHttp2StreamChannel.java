/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBufAllocator
 *  io.netty.channel.Channel
 *  io.netty.channel.Channel$Unsafe
 *  io.netty.channel.ChannelConfig
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelId
 *  io.netty.channel.ChannelMetadata
 *  io.netty.channel.ChannelOutboundBuffer
 *  io.netty.channel.ChannelPipeline
 *  io.netty.channel.ChannelProgressivePromise
 *  io.netty.channel.ChannelPromise
 *  io.netty.channel.DefaultChannelConfig
 *  io.netty.channel.DefaultChannelPipeline
 *  io.netty.channel.EventLoop
 *  io.netty.channel.MessageSizeEstimator
 *  io.netty.channel.MessageSizeEstimator$Handle
 *  io.netty.channel.RecvByteBufAllocator
 *  io.netty.channel.RecvByteBufAllocator$ExtendedHandle
 *  io.netty.channel.RecvByteBufAllocator$Handle
 *  io.netty.channel.VoidChannelPromise
 *  io.netty.channel.socket.ChannelInputShutdownReadComplete
 *  io.netty.channel.socket.ChannelOutputShutdownEvent
 *  io.netty.handler.ssl.SslCloseCompletionEvent
 *  io.netty.util.DefaultAttributeMap
 *  io.netty.util.ReferenceCountUtil
 *  io.netty.util.concurrent.GenericFutureListener
 *  io.netty.util.internal.ObjectUtil
 *  io.netty.util.internal.StringUtil
 *  io.netty.util.internal.logging.InternalLogger
 *  io.netty.util.internal.logging.InternalLoggerFactory
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.VoidChannelPromise;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.channel.socket.ChannelOutputShutdownEvent;
import io.netty.handler.codec.http2.DefaultHttp2ResetFrame;
import io.netty.handler.codec.http2.DefaultHttp2WindowUpdateFrame;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Frame;
import io.netty.handler.codec.http2.Http2FrameCodec;
import io.netty.handler.codec.http2.Http2FrameStream;
import io.netty.handler.codec.http2.Http2FrameStreamException;
import io.netty.handler.codec.http2.Http2FrameStreamVisitor;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.handler.codec.http2.Http2StreamChannel;
import io.netty.handler.codec.http2.Http2StreamChannelId;
import io.netty.handler.codec.http2.Http2StreamFrame;
import io.netty.handler.ssl.SslCloseCompletionEvent;
import io.netty.util.DefaultAttributeMap;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

abstract class AbstractHttp2StreamChannel
extends DefaultAttributeMap
implements Http2StreamChannel {
    static final Http2FrameStreamVisitor WRITABLE_VISITOR = new Http2FrameStreamVisitor(){

        @Override
        public boolean visit(Http2FrameStream stream) {
            AbstractHttp2StreamChannel childChannel = (AbstractHttp2StreamChannel)((Http2FrameCodec.DefaultHttp2FrameStream)stream).attachment;
            childChannel.trySetWritable();
            return true;
        }
    };
    static final Http2FrameStreamVisitor CHANNEL_INPUT_SHUTDOWN_READ_COMPLETE_VISITOR = new UserEventStreamVisitor(ChannelInputShutdownReadComplete.INSTANCE);
    static final Http2FrameStreamVisitor CHANNEL_OUTPUT_SHUTDOWN_EVENT_VISITOR = new UserEventStreamVisitor(ChannelOutputShutdownEvent.INSTANCE);
    static final Http2FrameStreamVisitor SSL_CLOSE_COMPLETION_EVENT_VISITOR = new UserEventStreamVisitor(SslCloseCompletionEvent.SUCCESS);
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractHttp2StreamChannel.class);
    private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
    private static final int MIN_HTTP2_FRAME_SIZE = 9;
    private static final AtomicLongFieldUpdater<AbstractHttp2StreamChannel> TOTAL_PENDING_SIZE_UPDATER = AtomicLongFieldUpdater.newUpdater(AbstractHttp2StreamChannel.class, "totalPendingSize");
    private static final AtomicIntegerFieldUpdater<AbstractHttp2StreamChannel> UNWRITABLE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AbstractHttp2StreamChannel.class, "unwritable");
    private final ChannelFutureListener windowUpdateFrameWriteListener = new ChannelFutureListener(){

        public void operationComplete(ChannelFuture future) {
            AbstractHttp2StreamChannel.windowUpdateFrameWriteComplete(future, AbstractHttp2StreamChannel.this);
        }
    };
    private final Http2StreamChannelConfig config = new Http2StreamChannelConfig(this);
    private final Http2ChannelUnsafe unsafe = new Http2ChannelUnsafe();
    private final ChannelId channelId;
    private final ChannelPipeline pipeline;
    private final Http2FrameCodec.DefaultHttp2FrameStream stream;
    private final ChannelPromise closePromise;
    private volatile boolean registered;
    private volatile long totalPendingSize;
    private volatile int unwritable;
    private Runnable fireChannelWritabilityChangedTask;
    private boolean outboundClosed;
    private int flowControlledBytes;
    private ReadStatus readStatus = ReadStatus.IDLE;
    private Queue<Object> inboundBuffer;
    private boolean firstFrameWritten;
    private boolean readCompletePending;

    private static void windowUpdateFrameWriteComplete(ChannelFuture future, Channel streamChannel) {
        Throwable cause = future.cause();
        if (cause != null) {
            Throwable unwrappedCause;
            if (cause instanceof Http2FrameStreamException && (unwrappedCause = cause.getCause()) != null) {
                cause = unwrappedCause;
            }
            streamChannel.pipeline().fireExceptionCaught(cause);
            streamChannel.unsafe().close(streamChannel.unsafe().voidPromise());
        }
    }

    AbstractHttp2StreamChannel(Http2FrameCodec.DefaultHttp2FrameStream stream, int id, ChannelHandler inboundHandler) {
        this.stream = stream;
        stream.attachment = this;
        this.pipeline = new DefaultChannelPipeline(this){

            protected void incrementPendingOutboundBytes(long size) {
                AbstractHttp2StreamChannel.this.incrementPendingOutboundBytes(size, true);
            }

            protected void decrementPendingOutboundBytes(long size) {
                AbstractHttp2StreamChannel.this.decrementPendingOutboundBytes(size, true);
            }
        };
        this.closePromise = this.pipeline.newPromise();
        this.channelId = new Http2StreamChannelId(this.parent().id(), id);
        if (inboundHandler != null) {
            this.pipeline.addLast(new ChannelHandler[]{inboundHandler});
        }
    }

    private void incrementPendingOutboundBytes(long size, boolean invokeLater) {
        if (size == 0L) {
            return;
        }
        long newWriteBufferSize = TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, size);
        if (newWriteBufferSize > (long)this.config().getWriteBufferHighWaterMark()) {
            this.setUnwritable(invokeLater);
        }
    }

    private void decrementPendingOutboundBytes(long size, boolean invokeLater) {
        if (size == 0L) {
            return;
        }
        long newWriteBufferSize = TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, -size);
        if (newWriteBufferSize < (long)this.config().getWriteBufferLowWaterMark() && this.parent().isWritable()) {
            this.setWritable(invokeLater);
        }
    }

    final void trySetWritable() {
        if (this.totalPendingSize < (long)this.config().getWriteBufferLowWaterMark()) {
            this.setWritable(false);
        }
    }

    private void setWritable(boolean invokeLater) {
        block1: {
            int newValue;
            int oldValue;
            while (!UNWRITABLE_UPDATER.compareAndSet(this, oldValue = this.unwritable, newValue = oldValue & 0xFFFFFFFE)) {
            }
            if (oldValue == 0 || newValue != 0) break block1;
            this.fireChannelWritabilityChanged(invokeLater);
        }
    }

    private void setUnwritable(boolean invokeLater) {
        block1: {
            int newValue;
            int oldValue;
            while (!UNWRITABLE_UPDATER.compareAndSet(this, oldValue = this.unwritable, newValue = oldValue | 1)) {
            }
            if (oldValue != 0) break block1;
            this.fireChannelWritabilityChanged(invokeLater);
        }
    }

    private void fireChannelWritabilityChanged(boolean invokeLater) {
        final ChannelPipeline pipeline = this.pipeline();
        if (invokeLater) {
            Runnable task = this.fireChannelWritabilityChangedTask;
            if (task == null) {
                this.fireChannelWritabilityChangedTask = task = new Runnable(){

                    @Override
                    public void run() {
                        pipeline.fireChannelWritabilityChanged();
                    }
                };
            }
            this.eventLoop().execute(task);
        } else {
            pipeline.fireChannelWritabilityChanged();
        }
    }

    @Override
    public Http2FrameStream stream() {
        return this.stream;
    }

    void closeOutbound() {
        this.outboundClosed = true;
    }

    void streamClosed() {
        this.unsafe.readEOS();
        this.unsafe.doBeginRead();
    }

    public ChannelMetadata metadata() {
        return METADATA;
    }

    public ChannelConfig config() {
        return this.config;
    }

    public boolean isOpen() {
        return !this.closePromise.isDone();
    }

    public boolean isActive() {
        return this.isOpen();
    }

    public boolean isWritable() {
        return this.unwritable == 0;
    }

    public ChannelId id() {
        return this.channelId;
    }

    public EventLoop eventLoop() {
        return this.parent().eventLoop();
    }

    public Channel parent() {
        return this.parentContext().channel();
    }

    public boolean isRegistered() {
        return this.registered;
    }

    public SocketAddress localAddress() {
        return this.parent().localAddress();
    }

    public SocketAddress remoteAddress() {
        return this.parent().remoteAddress();
    }

    public ChannelFuture closeFuture() {
        return this.closePromise;
    }

    public long bytesBeforeUnwritable() {
        long bytes = (long)this.config().getWriteBufferHighWaterMark() - this.totalPendingSize + 1L;
        return bytes > 0L && this.isWritable() ? bytes : 0L;
    }

    public long bytesBeforeWritable() {
        long bytes = this.totalPendingSize - (long)this.config().getWriteBufferLowWaterMark() + 1L;
        return bytes <= 0L || this.isWritable() ? 0L : bytes;
    }

    public Channel.Unsafe unsafe() {
        return this.unsafe;
    }

    public ChannelPipeline pipeline() {
        return this.pipeline;
    }

    public ByteBufAllocator alloc() {
        return this.config().getAllocator();
    }

    public Channel read() {
        this.pipeline().read();
        return this;
    }

    public Channel flush() {
        this.pipeline().flush();
        return this;
    }

    public ChannelFuture bind(SocketAddress localAddress) {
        return this.pipeline().bind(localAddress);
    }

    public ChannelFuture connect(SocketAddress remoteAddress) {
        return this.pipeline().connect(remoteAddress);
    }

    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        return this.pipeline().connect(remoteAddress, localAddress);
    }

    public ChannelFuture disconnect() {
        return this.pipeline().disconnect();
    }

    public ChannelFuture close() {
        return this.pipeline().close();
    }

    public ChannelFuture deregister() {
        return this.pipeline().deregister();
    }

    public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
        return this.pipeline().bind(localAddress, promise);
    }

    public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return this.pipeline().connect(remoteAddress, promise);
    }

    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        return this.pipeline().connect(remoteAddress, localAddress, promise);
    }

    public ChannelFuture disconnect(ChannelPromise promise) {
        return this.pipeline().disconnect(promise);
    }

    public ChannelFuture close(ChannelPromise promise) {
        return this.pipeline().close(promise);
    }

    public ChannelFuture deregister(ChannelPromise promise) {
        return this.pipeline().deregister(promise);
    }

    public ChannelFuture write(Object msg) {
        return this.pipeline().write(msg);
    }

    public ChannelFuture write(Object msg, ChannelPromise promise) {
        return this.pipeline().write(msg, promise);
    }

    public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        return this.pipeline().writeAndFlush(msg, promise);
    }

    public ChannelFuture writeAndFlush(Object msg) {
        return this.pipeline().writeAndFlush(msg);
    }

    public ChannelPromise newPromise() {
        return this.pipeline().newPromise();
    }

    public ChannelProgressivePromise newProgressivePromise() {
        return this.pipeline().newProgressivePromise();
    }

    public ChannelFuture newSucceededFuture() {
        return this.pipeline().newSucceededFuture();
    }

    public ChannelFuture newFailedFuture(Throwable cause) {
        return this.pipeline().newFailedFuture(cause);
    }

    public ChannelPromise voidPromise() {
        return this.pipeline().voidPromise();
    }

    public int hashCode() {
        return this.id().hashCode();
    }

    public boolean equals(Object o) {
        return this == o;
    }

    public int compareTo(Channel o) {
        if (this == o) {
            return 0;
        }
        return this.id().compareTo((Object)o.id());
    }

    public String toString() {
        return this.parent().toString() + "(H2 - " + this.stream + ')';
    }

    void fireChildRead(Http2Frame frame) {
        assert (this.eventLoop().inEventLoop());
        if (!this.isActive()) {
            ReferenceCountUtil.release((Object)frame);
        } else if (this.readStatus != ReadStatus.IDLE) {
            assert (this.inboundBuffer == null || this.inboundBuffer.isEmpty());
            RecvByteBufAllocator.Handle allocHandle = this.unsafe.recvBufAllocHandle();
            this.unsafe.doRead0(frame, allocHandle);
            if (allocHandle.continueReading()) {
                this.maybeAddChannelToReadCompletePendingQueue();
            } else {
                this.unsafe.notifyReadComplete(allocHandle, true);
            }
        } else {
            if (this.inboundBuffer == null) {
                this.inboundBuffer = new ArrayDeque<Object>(4);
            }
            this.inboundBuffer.add(frame);
        }
    }

    void fireChildReadComplete() {
        assert (this.eventLoop().inEventLoop());
        assert (this.readStatus != ReadStatus.IDLE || !this.readCompletePending);
        this.unsafe.notifyReadComplete(this.unsafe.recvBufAllocHandle(), false);
    }

    final void closeWithError(Http2Error error) {
        assert (this.eventLoop().inEventLoop());
        this.unsafe.close(this.unsafe.voidPromise(), error);
    }

    private void maybeAddChannelToReadCompletePendingQueue() {
        if (!this.readCompletePending) {
            this.readCompletePending = true;
            this.addChannelToReadCompletePendingQueue();
        }
    }

    protected void flush0(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    protected ChannelFuture write0(ChannelHandlerContext ctx, Object msg) {
        ChannelPromise promise = ctx.newPromise();
        ctx.write(msg, promise);
        return promise;
    }

    protected abstract boolean isParentReadInProgress();

    protected abstract void addChannelToReadCompletePendingQueue();

    protected abstract ChannelHandlerContext parentContext();

    private static final class Http2StreamChannelConfig
    extends DefaultChannelConfig {
        Http2StreamChannelConfig(Channel channel) {
            super(channel);
        }

        public MessageSizeEstimator getMessageSizeEstimator() {
            return FlowControlledFrameSizeEstimator.INSTANCE;
        }

        public ChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
            throw new UnsupportedOperationException();
        }

        public ChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
            if (!(allocator.newHandle() instanceof RecvByteBufAllocator.ExtendedHandle)) {
                throw new IllegalArgumentException("allocator.newHandle() must return an object of type: " + RecvByteBufAllocator.ExtendedHandle.class);
            }
            super.setRecvByteBufAllocator(allocator);
            return this;
        }
    }

    private final class Http2ChannelUnsafe
    implements Channel.Unsafe {
        private final VoidChannelPromise unsafeVoidPromise;
        private RecvByteBufAllocator.Handle recvHandle;
        private boolean writeDoneAndNoFlush;
        private boolean closeInitiated;
        private boolean readEOS;

        private Http2ChannelUnsafe() {
            this.unsafeVoidPromise = new VoidChannelPromise((Channel)AbstractHttp2StreamChannel.this, false);
        }

        public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
            if (!promise.setUncancellable()) {
                return;
            }
            promise.setFailure((Throwable)new UnsupportedOperationException());
        }

        public RecvByteBufAllocator.Handle recvBufAllocHandle() {
            if (this.recvHandle == null) {
                this.recvHandle = AbstractHttp2StreamChannel.this.config().getRecvByteBufAllocator().newHandle();
                this.recvHandle.reset(AbstractHttp2StreamChannel.this.config());
            }
            return this.recvHandle;
        }

        public SocketAddress localAddress() {
            return AbstractHttp2StreamChannel.this.parent().unsafe().localAddress();
        }

        public SocketAddress remoteAddress() {
            return AbstractHttp2StreamChannel.this.parent().unsafe().remoteAddress();
        }

        public void register(EventLoop eventLoop, ChannelPromise promise) {
            if (!promise.setUncancellable()) {
                return;
            }
            if (AbstractHttp2StreamChannel.this.registered) {
                promise.setFailure((Throwable)new UnsupportedOperationException("Re-register is not supported"));
                return;
            }
            AbstractHttp2StreamChannel.this.registered = true;
            promise.setSuccess();
            AbstractHttp2StreamChannel.this.pipeline().fireChannelRegistered();
            if (AbstractHttp2StreamChannel.this.isActive()) {
                AbstractHttp2StreamChannel.this.pipeline().fireChannelActive();
            }
        }

        public void bind(SocketAddress localAddress, ChannelPromise promise) {
            if (!promise.setUncancellable()) {
                return;
            }
            promise.setFailure((Throwable)new UnsupportedOperationException());
        }

        public void disconnect(ChannelPromise promise) {
            this.close(promise);
        }

        public void close(ChannelPromise promise) {
            this.close(promise, Http2Error.CANCEL);
        }

        void close(final ChannelPromise promise, Http2Error error) {
            if (!promise.setUncancellable()) {
                return;
            }
            if (this.closeInitiated) {
                if (AbstractHttp2StreamChannel.this.closePromise.isDone()) {
                    promise.setSuccess();
                } else if (!(promise instanceof VoidChannelPromise)) {
                    AbstractHttp2StreamChannel.this.closePromise.addListener((GenericFutureListener)new ChannelFutureListener(){

                        public void operationComplete(ChannelFuture future) {
                            promise.setSuccess();
                        }
                    });
                }
                return;
            }
            this.closeInitiated = true;
            AbstractHttp2StreamChannel.this.readCompletePending = false;
            boolean wasActive = AbstractHttp2StreamChannel.this.isActive();
            if (AbstractHttp2StreamChannel.this.parent().isActive() && !this.readEOS && Http2CodecUtil.isStreamIdValid(AbstractHttp2StreamChannel.this.stream.id())) {
                DefaultHttp2ResetFrame resetFrame = new DefaultHttp2ResetFrame(error).stream(AbstractHttp2StreamChannel.this.stream());
                this.write(resetFrame, AbstractHttp2StreamChannel.this.unsafe().voidPromise());
                this.flush();
            }
            if (AbstractHttp2StreamChannel.this.inboundBuffer != null) {
                Object msg;
                while ((msg = AbstractHttp2StreamChannel.this.inboundBuffer.poll()) != null) {
                    ReferenceCountUtil.release(msg);
                }
                AbstractHttp2StreamChannel.this.inboundBuffer = null;
            }
            AbstractHttp2StreamChannel.this.outboundClosed = true;
            AbstractHttp2StreamChannel.this.closePromise.setSuccess();
            promise.setSuccess();
            this.fireChannelInactiveAndDeregister(this.voidPromise(), wasActive);
        }

        public void closeForcibly() {
            this.close(AbstractHttp2StreamChannel.this.unsafe().voidPromise());
        }

        public void deregister(ChannelPromise promise) {
            this.fireChannelInactiveAndDeregister(promise, false);
        }

        private void fireChannelInactiveAndDeregister(final ChannelPromise promise, final boolean fireChannelInactive) {
            if (!promise.setUncancellable()) {
                return;
            }
            if (!AbstractHttp2StreamChannel.this.registered) {
                promise.setSuccess();
                return;
            }
            this.invokeLater(new Runnable(){

                @Override
                public void run() {
                    if (fireChannelInactive) {
                        AbstractHttp2StreamChannel.this.pipeline.fireChannelInactive();
                    }
                    if (AbstractHttp2StreamChannel.this.registered) {
                        AbstractHttp2StreamChannel.this.registered = false;
                        AbstractHttp2StreamChannel.this.pipeline.fireChannelUnregistered();
                    }
                    Http2ChannelUnsafe.this.safeSetSuccess(promise);
                }
            });
        }

        private void safeSetSuccess(ChannelPromise promise) {
            if (!(promise instanceof VoidChannelPromise) && !promise.trySuccess()) {
                logger.warn("Failed to mark a promise as success because it is done already: {}", (Object)promise);
            }
        }

        private void invokeLater(Runnable task) {
            try {
                AbstractHttp2StreamChannel.this.eventLoop().execute(task);
            }
            catch (RejectedExecutionException e) {
                logger.warn("Can't invoke task later as EventLoop rejected it", (Throwable)e);
            }
        }

        public void beginRead() {
            if (!AbstractHttp2StreamChannel.this.isActive()) {
                return;
            }
            this.updateLocalWindowIfNeeded();
            switch (AbstractHttp2StreamChannel.this.readStatus) {
                case IDLE: {
                    AbstractHttp2StreamChannel.this.readStatus = ReadStatus.IN_PROGRESS;
                    this.doBeginRead();
                    break;
                }
                case IN_PROGRESS: {
                    AbstractHttp2StreamChannel.this.readStatus = ReadStatus.REQUESTED;
                    break;
                }
            }
        }

        private Object pollQueuedMessage() {
            return AbstractHttp2StreamChannel.this.inboundBuffer == null ? null : AbstractHttp2StreamChannel.this.inboundBuffer.poll();
        }

        void doBeginRead() {
            while (AbstractHttp2StreamChannel.this.readStatus != ReadStatus.IDLE) {
                Object message = this.pollQueuedMessage();
                if (message == null) {
                    if (this.readEOS) {
                        AbstractHttp2StreamChannel.this.unsafe.closeForcibly();
                    }
                    this.flush();
                    break;
                }
                RecvByteBufAllocator.Handle allocHandle = this.recvBufAllocHandle();
                allocHandle.reset(AbstractHttp2StreamChannel.this.config());
                boolean continueReading = false;
                do {
                    this.doRead0((Http2Frame)message, allocHandle);
                } while ((this.readEOS || (continueReading = allocHandle.continueReading())) && (message = this.pollQueuedMessage()) != null);
                if (continueReading && AbstractHttp2StreamChannel.this.isParentReadInProgress() && !this.readEOS) {
                    AbstractHttp2StreamChannel.this.maybeAddChannelToReadCompletePendingQueue();
                    continue;
                }
                this.notifyReadComplete(allocHandle, true);
            }
        }

        void readEOS() {
            this.readEOS = true;
        }

        private void updateLocalWindowIfNeeded() {
            if (AbstractHttp2StreamChannel.this.flowControlledBytes != 0) {
                int bytes = AbstractHttp2StreamChannel.this.flowControlledBytes;
                AbstractHttp2StreamChannel.this.flowControlledBytes = 0;
                ChannelFuture future = AbstractHttp2StreamChannel.this.write0(AbstractHttp2StreamChannel.this.parentContext(), new DefaultHttp2WindowUpdateFrame(bytes).stream(AbstractHttp2StreamChannel.this.stream));
                this.writeDoneAndNoFlush = true;
                if (future.isDone()) {
                    AbstractHttp2StreamChannel.windowUpdateFrameWriteComplete(future, AbstractHttp2StreamChannel.this);
                } else {
                    future.addListener((GenericFutureListener)AbstractHttp2StreamChannel.this.windowUpdateFrameWriteListener);
                }
            }
        }

        void notifyReadComplete(RecvByteBufAllocator.Handle allocHandle, boolean forceReadComplete) {
            if (!AbstractHttp2StreamChannel.this.readCompletePending && !forceReadComplete) {
                return;
            }
            AbstractHttp2StreamChannel.this.readCompletePending = false;
            if (AbstractHttp2StreamChannel.this.readStatus == ReadStatus.REQUESTED) {
                AbstractHttp2StreamChannel.this.readStatus = ReadStatus.IN_PROGRESS;
            } else {
                AbstractHttp2StreamChannel.this.readStatus = ReadStatus.IDLE;
            }
            allocHandle.readComplete();
            AbstractHttp2StreamChannel.this.pipeline().fireChannelReadComplete();
            this.flush();
            if (this.readEOS) {
                AbstractHttp2StreamChannel.this.unsafe.closeForcibly();
            }
        }

        void doRead0(Http2Frame frame, RecvByteBufAllocator.Handle allocHandle) {
            int bytes;
            if (frame instanceof Http2DataFrame) {
                bytes = ((Http2DataFrame)frame).initialFlowControlledBytes();
                AbstractHttp2StreamChannel.this.flowControlledBytes = AbstractHttp2StreamChannel.this.flowControlledBytes + bytes;
            } else {
                bytes = 9;
            }
            allocHandle.attemptedBytesRead(bytes);
            allocHandle.lastBytesRead(bytes);
            allocHandle.incMessagesRead(1);
            AbstractHttp2StreamChannel.this.pipeline().fireChannelRead((Object)frame);
        }

        public void write(Object msg, ChannelPromise promise) {
            if (!promise.setUncancellable()) {
                ReferenceCountUtil.release((Object)msg);
                return;
            }
            if (!AbstractHttp2StreamChannel.this.isActive() || AbstractHttp2StreamChannel.this.outboundClosed && (msg instanceof Http2HeadersFrame || msg instanceof Http2DataFrame)) {
                ReferenceCountUtil.release((Object)msg);
                promise.setFailure((Throwable)new ClosedChannelException());
                return;
            }
            try {
                if (msg instanceof Http2StreamFrame) {
                    Http2StreamFrame frame = this.validateStreamFrame((Http2StreamFrame)msg).stream(AbstractHttp2StreamChannel.this.stream());
                    this.writeHttp2StreamFrame(frame, promise);
                } else {
                    String msgStr = msg.toString();
                    ReferenceCountUtil.release((Object)msg);
                    promise.setFailure((Throwable)new IllegalArgumentException("Message must be an " + StringUtil.simpleClassName(Http2StreamFrame.class) + ": " + msgStr));
                }
            }
            catch (Throwable t) {
                promise.tryFailure(t);
            }
        }

        private void writeHttp2StreamFrame(Http2StreamFrame frame, final ChannelPromise promise) {
            if (!(AbstractHttp2StreamChannel.this.firstFrameWritten || Http2CodecUtil.isStreamIdValid(AbstractHttp2StreamChannel.this.stream().id()) || frame instanceof Http2HeadersFrame)) {
                ReferenceCountUtil.release((Object)frame);
                promise.setFailure((Throwable)new IllegalArgumentException("The first frame must be a headers frame. Was: " + frame.name()));
                return;
            }
            final boolean firstWrite = AbstractHttp2StreamChannel.this.firstFrameWritten ? false : (AbstractHttp2StreamChannel.this.firstFrameWritten = true);
            ChannelFuture f = AbstractHttp2StreamChannel.this.write0(AbstractHttp2StreamChannel.this.parentContext(), frame);
            if (f.isDone()) {
                if (firstWrite) {
                    this.firstWriteComplete(f, promise);
                } else {
                    this.writeComplete(f, promise);
                }
            } else {
                final long bytes = FlowControlledFrameSizeEstimator.HANDLE_INSTANCE.size((Object)frame);
                AbstractHttp2StreamChannel.this.incrementPendingOutboundBytes(bytes, false);
                f.addListener((GenericFutureListener)new ChannelFutureListener(){

                    public void operationComplete(ChannelFuture future) {
                        if (firstWrite) {
                            Http2ChannelUnsafe.this.firstWriteComplete(future, promise);
                        } else {
                            Http2ChannelUnsafe.this.writeComplete(future, promise);
                        }
                        AbstractHttp2StreamChannel.this.decrementPendingOutboundBytes(bytes, false);
                    }
                });
                this.writeDoneAndNoFlush = true;
            }
        }

        private void firstWriteComplete(ChannelFuture future, ChannelPromise promise) {
            Throwable cause = future.cause();
            if (cause == null) {
                promise.setSuccess();
            } else {
                this.closeForcibly();
                promise.setFailure(this.wrapStreamClosedError(cause));
            }
        }

        private void writeComplete(ChannelFuture future, ChannelPromise promise) {
            Throwable cause = future.cause();
            if (cause == null) {
                promise.setSuccess();
            } else {
                Throwable error = this.wrapStreamClosedError(cause);
                if (error instanceof IOException) {
                    if (AbstractHttp2StreamChannel.this.config.isAutoClose()) {
                        this.closeForcibly();
                    } else {
                        AbstractHttp2StreamChannel.this.outboundClosed = true;
                    }
                }
                promise.setFailure(error);
            }
        }

        private Throwable wrapStreamClosedError(Throwable cause) {
            if (cause instanceof Http2Exception && ((Http2Exception)cause).error() == Http2Error.STREAM_CLOSED) {
                return new ClosedChannelException().initCause(cause);
            }
            return cause;
        }

        private Http2StreamFrame validateStreamFrame(Http2StreamFrame frame) {
            if (frame.stream() != null && frame.stream() != AbstractHttp2StreamChannel.this.stream) {
                String msgString = frame.toString();
                ReferenceCountUtil.release((Object)frame);
                throw new IllegalArgumentException("Stream " + frame.stream() + " must not be set on the frame: " + msgString);
            }
            return frame;
        }

        public void flush() {
            if (!this.writeDoneAndNoFlush || AbstractHttp2StreamChannel.this.isParentReadInProgress()) {
                return;
            }
            this.writeDoneAndNoFlush = false;
            AbstractHttp2StreamChannel.this.flush0(AbstractHttp2StreamChannel.this.parentContext());
        }

        public ChannelPromise voidPromise() {
            return this.unsafeVoidPromise;
        }

        public ChannelOutboundBuffer outboundBuffer() {
            return null;
        }
    }

    private static enum ReadStatus {
        IDLE,
        IN_PROGRESS,
        REQUESTED;

    }

    private static final class FlowControlledFrameSizeEstimator
    implements MessageSizeEstimator {
        static final FlowControlledFrameSizeEstimator INSTANCE = new FlowControlledFrameSizeEstimator();
        private static final MessageSizeEstimator.Handle HANDLE_INSTANCE = new MessageSizeEstimator.Handle(){

            public int size(Object msg) {
                return msg instanceof Http2DataFrame ? (int)Math.min(Integer.MAX_VALUE, (long)((Http2DataFrame)msg).initialFlowControlledBytes() + 9L) : 9;
            }
        };

        private FlowControlledFrameSizeEstimator() {
        }

        public MessageSizeEstimator.Handle newHandle() {
            return HANDLE_INSTANCE;
        }
    }

    private static final class UserEventStreamVisitor
    implements Http2FrameStreamVisitor {
        private final Object event;

        UserEventStreamVisitor(Object event) {
            this.event = ObjectUtil.checkNotNull((Object)event, (String)"event");
        }

        @Override
        public boolean visit(Http2FrameStream stream) {
            AbstractHttp2StreamChannel childChannel = (AbstractHttp2StreamChannel)((Http2FrameCodec.DefaultHttp2FrameStream)stream).attachment;
            childChannel.pipeline().fireUserEventTriggered(this.event);
            return true;
        }
    }
}

