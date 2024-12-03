/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.CoreSubscriber
 *  reactor.core.publisher.BaseSubscriber
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.FluxSink
 *  reactor.core.publisher.Mono
 *  reactor.core.publisher.SynchronousSink
 */
package org.springframework.core.io.buffer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.PooledDataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

public abstract class DataBufferUtils {
    private static final Consumer<DataBuffer> RELEASE_CONSUMER = DataBufferUtils::release;

    @Deprecated
    public static Flux<DataBuffer> read(InputStream inputStream, DataBufferFactory dataBufferFactory, int bufferSize) {
        return DataBufferUtils.readInputStream(() -> inputStream, dataBufferFactory, bufferSize);
    }

    public static Flux<DataBuffer> readInputStream(Callable<InputStream> inputStreamSupplier, DataBufferFactory dataBufferFactory, int bufferSize) {
        Assert.notNull(inputStreamSupplier, "'inputStreamSupplier' must not be null");
        return DataBufferUtils.readByteChannel(() -> Channels.newChannel((InputStream)inputStreamSupplier.call()), dataBufferFactory, bufferSize);
    }

    @Deprecated
    public static Flux<DataBuffer> read(ReadableByteChannel channel, DataBufferFactory dataBufferFactory, int bufferSize) {
        return DataBufferUtils.readByteChannel(() -> channel, dataBufferFactory, bufferSize);
    }

    public static Flux<DataBuffer> readByteChannel(Callable<ReadableByteChannel> channelSupplier, DataBufferFactory dataBufferFactory, int bufferSize) {
        Assert.notNull(channelSupplier, "'channelSupplier' must not be null");
        Assert.notNull((Object)dataBufferFactory, "'dataBufferFactory' must not be null");
        Assert.isTrue(bufferSize > 0, "'bufferSize' must be > 0");
        return Flux.using(channelSupplier, channel -> {
            ReadableByteChannelGenerator generator = new ReadableByteChannelGenerator((ReadableByteChannel)channel, dataBufferFactory, bufferSize);
            return Flux.generate((Consumer)generator);
        }, DataBufferUtils::closeChannel);
    }

    @Deprecated
    public static Flux<DataBuffer> read(AsynchronousFileChannel channel, DataBufferFactory dataBufferFactory, int bufferSize) {
        return DataBufferUtils.readAsynchronousFileChannel(() -> channel, dataBufferFactory, bufferSize);
    }

    @Deprecated
    public static Flux<DataBuffer> read(AsynchronousFileChannel channel, long position, DataBufferFactory dataBufferFactory, int bufferSize) {
        return DataBufferUtils.readAsynchronousFileChannel(() -> channel, position, dataBufferFactory, bufferSize);
    }

    public static Flux<DataBuffer> readAsynchronousFileChannel(Callable<AsynchronousFileChannel> channelSupplier, DataBufferFactory dataBufferFactory, int bufferSize) {
        return DataBufferUtils.readAsynchronousFileChannel(channelSupplier, 0L, dataBufferFactory, bufferSize);
    }

    public static Flux<DataBuffer> readAsynchronousFileChannel(Callable<AsynchronousFileChannel> channelSupplier, long position, DataBufferFactory dataBufferFactory, int bufferSize) {
        Assert.notNull(channelSupplier, "'channelSupplier' must not be null");
        Assert.notNull((Object)dataBufferFactory, "'dataBufferFactory' must not be null");
        Assert.isTrue(position >= 0L, "'position' must be >= 0");
        Assert.isTrue(bufferSize > 0, "'bufferSize' must be > 0");
        DataBuffer dataBuffer = dataBufferFactory.allocateBuffer(bufferSize);
        ByteBuffer byteBuffer = dataBuffer.asByteBuffer(0, bufferSize);
        return Flux.using(channelSupplier, channel -> Flux.create(sink -> {
            AsynchronousFileChannelReadCompletionHandler completionHandler = new AsynchronousFileChannelReadCompletionHandler((AsynchronousFileChannel)channel, (FluxSink<DataBuffer>)sink, position, dataBufferFactory, bufferSize);
            channel.read(byteBuffer, position, dataBuffer, completionHandler);
        }), DataBufferUtils::closeChannel);
    }

    public static Flux<DataBuffer> read(Resource resource, DataBufferFactory dataBufferFactory, int bufferSize) {
        return DataBufferUtils.read(resource, 0L, dataBufferFactory, bufferSize);
    }

    public static Flux<DataBuffer> read(Resource resource, long position, DataBufferFactory dataBufferFactory, int bufferSize) {
        try {
            if (resource.isFile()) {
                File file = resource.getFile();
                return DataBufferUtils.readAsynchronousFileChannel(() -> AsynchronousFileChannel.open(file.toPath(), StandardOpenOption.READ), position, dataBufferFactory, bufferSize);
            }
        }
        catch (IOException file) {
        }
        Flux<DataBuffer> result = DataBufferUtils.readByteChannel(resource::readableChannel, dataBufferFactory, bufferSize);
        return position == 0L ? result : DataBufferUtils.skipUntilByteCount(result, position);
    }

    public static Flux<DataBuffer> write(Publisher<DataBuffer> source, OutputStream outputStream) {
        Assert.notNull(source, "'source' must not be null");
        Assert.notNull((Object)outputStream, "'outputStream' must not be null");
        WritableByteChannel channel = Channels.newChannel(outputStream);
        return DataBufferUtils.write(source, channel);
    }

    public static Flux<DataBuffer> write(Publisher<DataBuffer> source, WritableByteChannel channel) {
        Assert.notNull(source, "'source' must not be null");
        Assert.notNull((Object)channel, "'channel' must not be null");
        Flux flux = Flux.from(source);
        return Flux.create(sink -> flux.subscribe(dataBuffer -> {
            try {
                ByteBuffer byteBuffer = dataBuffer.asByteBuffer();
                while (byteBuffer.hasRemaining()) {
                    channel.write(byteBuffer);
                }
                sink.next(dataBuffer);
            }
            catch (IOException ex) {
                sink.next(dataBuffer);
                sink.error((Throwable)ex);
            }
        }, arg_0 -> ((FluxSink)sink).error(arg_0), () -> ((FluxSink)sink).complete()));
    }

    public static Flux<DataBuffer> write(Publisher<DataBuffer> source, AsynchronousFileChannel channel) {
        return DataBufferUtils.write(source, channel, 0L);
    }

    public static Flux<DataBuffer> write(Publisher<DataBuffer> source, AsynchronousFileChannel channel, long position) {
        Assert.notNull(source, "'source' must not be null");
        Assert.notNull((Object)channel, "'channel' must not be null");
        Assert.isTrue(position >= 0L, "'position' must be >= 0");
        Flux flux = Flux.from(source);
        return Flux.create(sink -> flux.subscribe((CoreSubscriber)new AsynchronousFileChannelWriteCompletionHandler((FluxSink<DataBuffer>)sink, channel, position)));
    }

    private static void closeChannel(@Nullable Channel channel) {
        if (channel != null && channel.isOpen()) {
            try {
                channel.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    public static Flux<DataBuffer> takeUntilByteCount(Publisher<DataBuffer> publisher, long maxByteCount) {
        Assert.notNull(publisher, "Publisher must not be null");
        Assert.isTrue(maxByteCount >= 0L, "'maxByteCount' must be a positive number");
        AtomicLong countDown = new AtomicLong(maxByteCount);
        return Flux.from(publisher).map(buffer -> {
            long count = countDown.addAndGet(-buffer.readableByteCount());
            return count >= 0L ? buffer : buffer.slice(0, buffer.readableByteCount() + (int)count);
        }).takeUntil(buffer -> countDown.get() <= 0L);
    }

    public static Flux<DataBuffer> skipUntilByteCount(Publisher<DataBuffer> publisher, long maxByteCount) {
        Assert.notNull(publisher, "Publisher must not be null");
        Assert.isTrue(maxByteCount >= 0L, "'maxByteCount' must be a positive number");
        AtomicLong byteCountDown = new AtomicLong(maxByteCount);
        return Flux.from(publisher).skipUntil(buffer -> {
            int delta = -buffer.readableByteCount();
            if (byteCountDown.addAndGet(delta) >= 0L) {
                DataBufferUtils.release(buffer);
                return false;
            }
            return true;
        }).map(buffer -> {
            long count = byteCountDown.get();
            if (count < 0L) {
                int skipCount = buffer.readableByteCount() + (int)count;
                byteCountDown.set(0L);
                return buffer.slice(skipCount, buffer.readableByteCount() - skipCount);
            }
            return buffer;
        });
    }

    public static <T extends DataBuffer> T retain(T dataBuffer) {
        if (dataBuffer instanceof PooledDataBuffer) {
            return (T)((PooledDataBuffer)dataBuffer).retain();
        }
        return dataBuffer;
    }

    public static boolean release(@Nullable DataBuffer dataBuffer) {
        return dataBuffer instanceof PooledDataBuffer && ((PooledDataBuffer)dataBuffer).release();
    }

    public static Consumer<DataBuffer> releaseConsumer() {
        return RELEASE_CONSUMER;
    }

    public static Mono<DataBuffer> join(Publisher<DataBuffer> dataBuffers) {
        Assert.notNull(dataBuffers, "'dataBuffers' must not be null");
        return Flux.from(dataBuffers).onErrorResume(DataBufferUtils::exceptionDataBuffer).collectList().filter(list -> !list.isEmpty()).flatMap(list -> {
            for (int i = 0; i < list.size(); ++i) {
                DataBuffer dataBuffer = (DataBuffer)list.get(i);
                if (!(dataBuffer instanceof ExceptionDataBuffer)) continue;
                list.subList(0, i).forEach(DataBufferUtils::release);
                return Mono.error((Throwable)((ExceptionDataBuffer)dataBuffer).throwable());
            }
            DataBufferFactory bufferFactory = ((DataBuffer)list.get(0)).factory();
            return Mono.just((Object)bufferFactory.join((List<? extends DataBuffer>)list));
        });
    }

    private static Mono<DataBuffer> exceptionDataBuffer(Throwable throwable) {
        return Mono.just((Object)new ExceptionDataBuffer(throwable));
    }

    private static final class ExceptionDataBuffer
    implements DataBuffer {
        private final Throwable throwable;

        public ExceptionDataBuffer(Throwable throwable) {
            this.throwable = throwable;
        }

        public Throwable throwable() {
            return this.throwable;
        }

        @Override
        public DataBufferFactory factory() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(IntPredicate predicate, int fromIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int lastIndexOf(IntPredicate predicate, int fromIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int readableByteCount() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int writableByteCount() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int capacity() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DataBuffer capacity(int capacity) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int readPosition() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DataBuffer readPosition(int readPosition) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int writePosition() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DataBuffer writePosition(int writePosition) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte getByte(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte read() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DataBuffer read(byte[] destination) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DataBuffer read(byte[] destination, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DataBuffer write(byte b) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DataBuffer write(byte[] source) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DataBuffer write(byte[] source, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DataBuffer write(DataBuffer ... buffers) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DataBuffer write(ByteBuffer ... buffers) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DataBuffer slice(int index, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ByteBuffer asByteBuffer() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ByteBuffer asByteBuffer(int index, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public InputStream asInputStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public InputStream asInputStream(boolean releaseOnClose) {
            throw new UnsupportedOperationException();
        }

        @Override
        public OutputStream asOutputStream() {
            throw new UnsupportedOperationException();
        }
    }

    private static class AsynchronousFileChannelWriteCompletionHandler
    extends BaseSubscriber<DataBuffer>
    implements CompletionHandler<Integer, ByteBuffer> {
        private final FluxSink<DataBuffer> sink;
        private final AsynchronousFileChannel channel;
        private final AtomicBoolean completed = new AtomicBoolean();
        private final AtomicReference<Throwable> error = new AtomicReference();
        private final AtomicLong position;
        private final AtomicReference<DataBuffer> dataBuffer = new AtomicReference();

        public AsynchronousFileChannelWriteCompletionHandler(FluxSink<DataBuffer> sink, AsynchronousFileChannel channel, long position) {
            this.sink = sink;
            this.channel = channel;
            this.position = new AtomicLong(position);
        }

        protected void hookOnSubscribe(Subscription subscription) {
            this.request(1L);
        }

        protected void hookOnNext(DataBuffer value) {
            if (!this.dataBuffer.compareAndSet(null, value)) {
                throw new IllegalStateException();
            }
            ByteBuffer byteBuffer = value.asByteBuffer();
            this.channel.write(byteBuffer, this.position.get(), byteBuffer, this);
        }

        protected void hookOnError(Throwable throwable) {
            this.error.set(throwable);
            if (this.dataBuffer.get() == null) {
                this.sink.error(throwable);
            }
        }

        protected void hookOnComplete() {
            this.completed.set(true);
            if (this.dataBuffer.get() == null) {
                this.sink.complete();
            }
        }

        @Override
        public void completed(Integer written, ByteBuffer byteBuffer) {
            long pos = this.position.addAndGet(written.intValue());
            if (byteBuffer.hasRemaining()) {
                this.channel.write(byteBuffer, pos, byteBuffer, this);
                return;
            }
            this.sinkDataBuffer();
            Throwable throwable = this.error.get();
            if (throwable != null) {
                this.sink.error(throwable);
            } else if (this.completed.get()) {
                this.sink.complete();
            } else {
                this.request(1L);
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer byteBuffer) {
            this.sinkDataBuffer();
            this.sink.error(exc);
        }

        private void sinkDataBuffer() {
            DataBuffer dataBuffer = this.dataBuffer.get();
            Assert.state(dataBuffer != null, "DataBuffer should not be null");
            this.sink.next((Object)dataBuffer);
            this.dataBuffer.set(null);
        }
    }

    private static class AsynchronousFileChannelReadCompletionHandler
    implements CompletionHandler<Integer, DataBuffer> {
        private final AsynchronousFileChannel channel;
        private final FluxSink<DataBuffer> sink;
        private final DataBufferFactory dataBufferFactory;
        private final int bufferSize;
        private final AtomicLong position;
        private final AtomicBoolean disposed = new AtomicBoolean();

        public AsynchronousFileChannelReadCompletionHandler(AsynchronousFileChannel channel, FluxSink<DataBuffer> sink, long position, DataBufferFactory dataBufferFactory, int bufferSize) {
            this.channel = channel;
            this.sink = sink;
            this.position = new AtomicLong(position);
            this.dataBufferFactory = dataBufferFactory;
            this.bufferSize = bufferSize;
        }

        @Override
        public void completed(Integer read, DataBuffer dataBuffer) {
            if (read != -1) {
                long pos = this.position.addAndGet(read.intValue());
                dataBuffer.writePosition(read);
                this.sink.next((Object)dataBuffer);
                if (!this.disposed.get()) {
                    DataBuffer newDataBuffer = this.dataBufferFactory.allocateBuffer(this.bufferSize);
                    ByteBuffer newByteBuffer = newDataBuffer.asByteBuffer(0, this.bufferSize);
                    this.channel.read(newByteBuffer, pos, newDataBuffer, this);
                }
            } else {
                DataBufferUtils.release(dataBuffer);
                this.sink.complete();
            }
        }

        @Override
        public void failed(Throwable exc, DataBuffer dataBuffer) {
            DataBufferUtils.release(dataBuffer);
            this.sink.error(exc);
        }
    }

    private static class ReadableByteChannelGenerator
    implements Consumer<SynchronousSink<DataBuffer>> {
        private final ReadableByteChannel channel;
        private final DataBufferFactory dataBufferFactory;
        private final int bufferSize;

        public ReadableByteChannelGenerator(ReadableByteChannel channel, DataBufferFactory dataBufferFactory, int bufferSize) {
            this.channel = channel;
            this.dataBufferFactory = dataBufferFactory;
            this.bufferSize = bufferSize;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void accept(SynchronousSink<DataBuffer> sink) {
            boolean release = true;
            DataBuffer dataBuffer = this.dataBufferFactory.allocateBuffer(this.bufferSize);
            try {
                ByteBuffer byteBuffer = dataBuffer.asByteBuffer(0, dataBuffer.capacity());
                int read = this.channel.read(byteBuffer);
                if (read >= 0) {
                    dataBuffer.writePosition(read);
                    release = false;
                    sink.next((Object)dataBuffer);
                } else {
                    sink.complete();
                }
            }
            catch (IOException ex) {
                sink.error((Throwable)ex);
            }
            finally {
                if (release) {
                    DataBufferUtils.release(dataBuffer);
                }
            }
        }
    }
}

