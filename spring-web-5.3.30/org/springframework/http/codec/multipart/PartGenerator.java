/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.reactivestreams.Subscription
 *  org.springframework.core.codec.DecodingException
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.core.io.buffer.DataBufferLimitException
 *  org.springframework.core.io.buffer.DataBufferUtils
 *  org.springframework.core.io.buffer.DefaultDataBufferFactory
 *  org.springframework.util.FastByteArrayOutputStream
 *  reactor.core.CoreSubscriber
 *  reactor.core.publisher.BaseSubscriber
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.FluxSink
 *  reactor.core.publisher.Mono
 *  reactor.core.scheduler.Scheduler
 *  reactor.util.context.Context
 *  reactor.util.context.ContextView
 */
package org.springframework.http.codec.multipart;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Subscription;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.DefaultParts;
import org.springframework.http.codec.multipart.MultipartParser;
import org.springframework.http.codec.multipart.MultipartUtils;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.FastByteArrayOutputStream;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

final class PartGenerator
extends BaseSubscriber<MultipartParser.Token> {
    private static final Log logger = LogFactory.getLog(PartGenerator.class);
    private final AtomicReference<State> state = new AtomicReference<InitialState>(new InitialState());
    private final AtomicInteger partCount = new AtomicInteger();
    private final AtomicBoolean requestOutstanding = new AtomicBoolean();
    private final FluxSink<Part> sink;
    private final int maxParts;
    private final boolean streaming;
    private final int maxInMemorySize;
    private final long maxDiskUsagePerPart;
    private final Mono<Path> fileStorageDirectory;
    private final Scheduler blockingOperationScheduler;

    private PartGenerator(FluxSink<Part> sink, int maxParts, int maxInMemorySize, long maxDiskUsagePerPart, boolean streaming, Mono<Path> fileStorageDirectory, Scheduler blockingOperationScheduler) {
        this.sink = sink;
        this.maxParts = maxParts;
        this.maxInMemorySize = maxInMemorySize;
        this.maxDiskUsagePerPart = maxDiskUsagePerPart;
        this.streaming = streaming;
        this.fileStorageDirectory = fileStorageDirectory;
        this.blockingOperationScheduler = blockingOperationScheduler;
    }

    public static Flux<Part> createParts(Flux<MultipartParser.Token> tokens, int maxParts, int maxInMemorySize, long maxDiskUsagePerPart, boolean streaming, Mono<Path> fileStorageDirectory, Scheduler blockingOperationScheduler) {
        return Flux.create(sink -> {
            PartGenerator generator = new PartGenerator((FluxSink<Part>)sink, maxParts, maxInMemorySize, maxDiskUsagePerPart, streaming, fileStorageDirectory, blockingOperationScheduler);
            sink.onCancel(generator::onSinkCancel);
            sink.onRequest(l -> generator.requestToken());
            tokens.subscribe((CoreSubscriber)generator);
        });
    }

    public Context currentContext() {
        return Context.of((ContextView)this.sink.contextView());
    }

    protected void hookOnSubscribe(Subscription subscription) {
        this.requestToken();
    }

    protected void hookOnNext(MultipartParser.Token token) {
        this.requestOutstanding.set(false);
        State state = this.state.get();
        if (token instanceof MultipartParser.HeadersToken) {
            state.partComplete(false);
            if (this.tooManyParts()) {
                return;
            }
            this.newPart(state, token.headers());
        } else {
            state.body(token.buffer());
        }
    }

    private void newPart(State currentState, HttpHeaders headers) {
        if (PartGenerator.isFormField(headers)) {
            this.changeStateInternal(new FormFieldState(headers));
            this.requestToken();
        } else if (!this.streaming) {
            this.changeStateInternal(new InMemoryState(headers));
            this.requestToken();
        } else {
            Flux streamingContent = Flux.create(contentSink -> {
                StreamingState newState = new StreamingState((FluxSink<DataBuffer>)contentSink);
                if (this.changeState(currentState, newState)) {
                    contentSink.onRequest(l -> this.requestToken());
                    this.requestToken();
                }
            });
            this.emitPart(DefaultParts.part(headers, (Flux<DataBuffer>)streamingContent));
        }
    }

    protected void hookOnComplete() {
        this.state.get().partComplete(true);
    }

    protected void hookOnError(Throwable throwable) {
        this.state.get().error(throwable);
        this.changeStateInternal(DisposedState.INSTANCE);
        this.sink.error(throwable);
    }

    private void onSinkCancel() {
        this.changeStateInternal(DisposedState.INSTANCE);
        this.cancel();
    }

    boolean changeState(State oldState, State newState) {
        if (this.state.compareAndSet(oldState, newState)) {
            if (logger.isTraceEnabled()) {
                logger.trace((Object)("Changed state: " + oldState + " -> " + newState));
            }
            oldState.dispose();
            return true;
        }
        logger.warn((Object)("Could not switch from " + oldState + " to " + newState + "; current state:" + this.state.get()));
        return false;
    }

    private void changeStateInternal(State newState) {
        if (this.state.get() == DisposedState.INSTANCE) {
            return;
        }
        State oldState = this.state.getAndSet(newState);
        if (logger.isTraceEnabled()) {
            logger.trace((Object)("Changed state: " + oldState + " -> " + newState));
        }
        oldState.dispose();
    }

    void emitPart(Part part) {
        if (logger.isTraceEnabled()) {
            logger.trace((Object)("Emitting: " + part));
        }
        this.sink.next((Object)part);
    }

    void emitComplete() {
        this.sink.complete();
    }

    void emitError(Throwable t) {
        this.cancel();
        this.sink.error(t);
    }

    void requestToken() {
        if (this.upstream() != null && !this.sink.isCancelled() && this.sink.requestedFromDownstream() > 0L && this.state.get().canRequest() && this.requestOutstanding.compareAndSet(false, true)) {
            this.request(1L);
        }
    }

    private boolean tooManyParts() {
        int count = this.partCount.incrementAndGet();
        if (this.maxParts > 0 && count > this.maxParts) {
            this.emitError((Throwable)new DecodingException("Too many parts (" + count + "/" + this.maxParts + " allowed)"));
            return true;
        }
        return false;
    }

    private static boolean isFormField(HttpHeaders headers) {
        MediaType contentType = headers.getContentType();
        return (contentType == null || MediaType.TEXT_PLAIN.equalsTypeAndSubtype(contentType)) && headers.getContentDisposition().getFilename() == null;
    }

    private static final class DisposedState
    implements State {
        public static final DisposedState INSTANCE = new DisposedState();

        private DisposedState() {
        }

        @Override
        public void body(DataBuffer dataBuffer) {
            DataBufferUtils.release((DataBuffer)dataBuffer);
        }

        @Override
        public void partComplete(boolean finalPart) {
        }

        public String toString() {
            return "DISPOSED";
        }
    }

    private final class WritingFileState
    implements State {
        private final HttpHeaders headers;
        private final Path file;
        private final WritableByteChannel channel;
        private final AtomicLong byteCount;
        private volatile boolean completed;
        private volatile boolean finalPart;
        private volatile boolean disposed;

        public WritingFileState(CreateFileState state, Path file, WritableByteChannel channel) {
            this.headers = state.headers;
            this.file = file;
            this.channel = channel;
            this.byteCount = new AtomicLong(state.byteCount);
        }

        public WritingFileState(IdleFileState state) {
            this.headers = state.headers;
            this.file = state.file;
            this.channel = state.channel;
            this.byteCount = state.byteCount;
        }

        @Override
        public void body(DataBuffer dataBuffer) {
            DataBufferUtils.release((DataBuffer)dataBuffer);
            PartGenerator.this.emitError(new IllegalStateException("Body token not expected"));
        }

        @Override
        public void partComplete(boolean finalPart) {
            State state = (State)PartGenerator.this.state.get();
            if (state != this) {
                state.partComplete(finalPart);
            } else {
                this.completed = true;
                this.finalPart = finalPart;
            }
        }

        public void writeBuffer(DataBuffer dataBuffer) {
            Mono.just((Object)dataBuffer).flatMap(this::writeInternal).subscribeOn(PartGenerator.this.blockingOperationScheduler).subscribe(null, PartGenerator.this::emitError, this::writeComplete);
        }

        public void writeBuffers(Iterable<DataBuffer> dataBuffers) {
            Flux.fromIterable(dataBuffers).concatMap(this::writeInternal).then().subscribeOn(PartGenerator.this.blockingOperationScheduler).subscribe(null, PartGenerator.this::emitError, this::writeComplete);
        }

        private void writeComplete() {
            IdleFileState newState = new IdleFileState(this);
            if (this.disposed) {
                newState.dispose();
            } else if (PartGenerator.this.changeState(this, newState)) {
                if (this.completed) {
                    newState.partComplete(this.finalPart);
                } else {
                    PartGenerator.this.requestToken();
                }
            } else {
                MultipartUtils.closeChannel(this.channel);
                MultipartUtils.deleteFile(this.file);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private Mono<Void> writeInternal(DataBuffer dataBuffer) {
            try {
                ByteBuffer byteBuffer = dataBuffer.asByteBuffer();
                while (byteBuffer.hasRemaining()) {
                    this.channel.write(byteBuffer);
                }
                Mono mono = Mono.empty();
                return mono;
            }
            catch (IOException ex) {
                MultipartUtils.closeChannel(this.channel);
                MultipartUtils.deleteFile(this.file);
                Mono mono = Mono.error((Throwable)ex);
                return mono;
            }
            finally {
                DataBufferUtils.release((DataBuffer)dataBuffer);
            }
        }

        @Override
        public boolean canRequest() {
            return false;
        }

        @Override
        public void dispose() {
            this.disposed = true;
        }

        public String toString() {
            return "WRITE-FILE";
        }
    }

    private final class IdleFileState
    implements State {
        private final HttpHeaders headers;
        private final Path file;
        private final WritableByteChannel channel;
        private final AtomicLong byteCount;
        private volatile boolean closeOnDispose = true;
        private volatile boolean deleteOnDispose = true;

        public IdleFileState(WritingFileState state) {
            this.headers = state.headers;
            this.file = state.file;
            this.channel = state.channel;
            this.byteCount = state.byteCount;
        }

        @Override
        public void body(DataBuffer dataBuffer) {
            long count = this.byteCount.addAndGet(dataBuffer.readableByteCount());
            if (PartGenerator.this.maxDiskUsagePerPart == -1L || count <= PartGenerator.this.maxDiskUsagePerPart) {
                this.closeOnDispose = false;
                this.deleteOnDispose = false;
                WritingFileState newState = new WritingFileState(this);
                if (PartGenerator.this.changeState(this, newState)) {
                    newState.writeBuffer(dataBuffer);
                } else {
                    MultipartUtils.closeChannel(this.channel);
                    MultipartUtils.deleteFile(this.file);
                    DataBufferUtils.release((DataBuffer)dataBuffer);
                }
            } else {
                MultipartUtils.closeChannel(this.channel);
                MultipartUtils.deleteFile(this.file);
                DataBufferUtils.release((DataBuffer)dataBuffer);
                PartGenerator.this.emitError((Throwable)new DataBufferLimitException("Part exceeded the disk usage limit of " + PartGenerator.this.maxDiskUsagePerPart + " bytes"));
            }
        }

        @Override
        public void partComplete(boolean finalPart) {
            MultipartUtils.closeChannel(this.channel);
            this.deleteOnDispose = false;
            PartGenerator.this.emitPart(DefaultParts.part(this.headers, this.file, PartGenerator.this.blockingOperationScheduler));
            if (finalPart) {
                PartGenerator.this.emitComplete();
            }
        }

        @Override
        public void dispose() {
            if (this.closeOnDispose) {
                MultipartUtils.closeChannel(this.channel);
            }
            if (this.deleteOnDispose) {
                MultipartUtils.deleteFile(this.file);
            }
        }

        public String toString() {
            return "IDLE-FILE";
        }
    }

    private final class CreateFileState
    implements State {
        private final HttpHeaders headers;
        private final Collection<DataBuffer> content;
        private final long byteCount;
        private volatile boolean completed;
        private volatile boolean finalPart;
        private volatile boolean releaseOnDispose = true;

        public CreateFileState(HttpHeaders headers, Collection<DataBuffer> content, long byteCount) {
            this.headers = headers;
            this.content = content;
            this.byteCount = byteCount;
        }

        @Override
        public void body(DataBuffer dataBuffer) {
            DataBufferUtils.release((DataBuffer)dataBuffer);
            PartGenerator.this.emitError(new IllegalStateException("Body token not expected"));
        }

        @Override
        public void partComplete(boolean finalPart) {
            this.completed = true;
            this.finalPart = finalPart;
        }

        public void createFile() {
            PartGenerator.this.fileStorageDirectory.map(this::createFileState).subscribeOn(PartGenerator.this.blockingOperationScheduler).subscribe(this::fileCreated, PartGenerator.this::emitError);
        }

        private WritingFileState createFileState(Path directory) {
            try {
                Path tempFile = Files.createTempFile(directory, null, ".multipart", new FileAttribute[0]);
                if (logger.isTraceEnabled()) {
                    logger.trace((Object)("Storing multipart data in file " + tempFile));
                }
                SeekableByteChannel channel = Files.newByteChannel(tempFile, StandardOpenOption.WRITE);
                return new WritingFileState(this, tempFile, channel);
            }
            catch (IOException ex) {
                throw new UncheckedIOException("Could not create temp file in " + directory, ex);
            }
        }

        private void fileCreated(WritingFileState newState) {
            this.releaseOnDispose = false;
            if (PartGenerator.this.changeState(this, newState)) {
                newState.writeBuffers(this.content);
                if (this.completed) {
                    newState.partComplete(this.finalPart);
                }
            } else {
                MultipartUtils.closeChannel(newState.channel);
                MultipartUtils.deleteFile(newState.file);
                this.content.forEach(DataBufferUtils::release);
            }
        }

        @Override
        public void dispose() {
            if (this.releaseOnDispose) {
                this.content.forEach(DataBufferUtils::release);
            }
        }

        public String toString() {
            return "CREATE-FILE";
        }
    }

    private final class InMemoryState
    implements State {
        private final AtomicLong byteCount = new AtomicLong();
        private final Queue<DataBuffer> content = new ConcurrentLinkedQueue<DataBuffer>();
        private final HttpHeaders headers;
        private volatile boolean releaseOnDispose = true;

        public InMemoryState(HttpHeaders headers) {
            this.headers = headers;
        }

        @Override
        public void body(DataBuffer dataBuffer) {
            long prevCount = this.byteCount.get();
            long count = this.byteCount.addAndGet(dataBuffer.readableByteCount());
            if (PartGenerator.this.maxInMemorySize == -1 || count <= (long)PartGenerator.this.maxInMemorySize) {
                this.storeBuffer(dataBuffer);
            } else if (prevCount <= (long)PartGenerator.this.maxInMemorySize) {
                this.switchToFile(dataBuffer, count);
            } else {
                DataBufferUtils.release((DataBuffer)dataBuffer);
                PartGenerator.this.emitError(new IllegalStateException("Body token not expected"));
            }
        }

        private void storeBuffer(DataBuffer dataBuffer) {
            this.content.add(dataBuffer);
            PartGenerator.this.requestToken();
        }

        private void switchToFile(DataBuffer current, long byteCount) {
            ArrayList<DataBuffer> content = new ArrayList<DataBuffer>(this.content);
            content.add(current);
            this.releaseOnDispose = false;
            CreateFileState newState = new CreateFileState(this.headers, content, byteCount);
            if (PartGenerator.this.changeState(this, newState)) {
                newState.createFile();
            } else {
                content.forEach(DataBufferUtils::release);
            }
        }

        @Override
        public void partComplete(boolean finalPart) {
            this.emitMemoryPart();
            if (finalPart) {
                PartGenerator.this.emitComplete();
            }
        }

        private void emitMemoryPart() {
            byte[] bytes = new byte[(int)this.byteCount.get()];
            int idx = 0;
            for (DataBuffer buffer : this.content) {
                int len = buffer.readableByteCount();
                buffer.read(bytes, idx, len);
                idx += len;
                DataBufferUtils.release((DataBuffer)buffer);
            }
            this.content.clear();
            Flux content = Flux.just((Object)DefaultDataBufferFactory.sharedInstance.wrap(bytes));
            PartGenerator.this.emitPart(DefaultParts.part(this.headers, (Flux<DataBuffer>)content));
        }

        @Override
        public void dispose() {
            if (this.releaseOnDispose) {
                this.content.forEach(DataBufferUtils::release);
            }
        }

        public String toString() {
            return "IN-MEMORY";
        }
    }

    private final class StreamingState
    implements State {
        private final FluxSink<DataBuffer> bodySink;

        public StreamingState(FluxSink<DataBuffer> bodySink) {
            this.bodySink = bodySink;
        }

        @Override
        public void body(DataBuffer dataBuffer) {
            if (!this.bodySink.isCancelled()) {
                this.bodySink.next((Object)dataBuffer);
                if (this.bodySink.requestedFromDownstream() > 0L) {
                    PartGenerator.this.requestToken();
                }
            } else {
                DataBufferUtils.release((DataBuffer)dataBuffer);
                PartGenerator.this.requestToken();
            }
        }

        @Override
        public void partComplete(boolean finalPart) {
            if (!this.bodySink.isCancelled()) {
                this.bodySink.complete();
            }
            if (finalPart) {
                PartGenerator.this.emitComplete();
            }
        }

        @Override
        public void error(Throwable throwable) {
            if (!this.bodySink.isCancelled()) {
                this.bodySink.error(throwable);
            }
        }

        public String toString() {
            return "STREAMING";
        }
    }

    private final class FormFieldState
    implements State {
        private final FastByteArrayOutputStream value = new FastByteArrayOutputStream();
        private final HttpHeaders headers;

        public FormFieldState(HttpHeaders headers) {
            this.headers = headers;
        }

        @Override
        public void body(DataBuffer dataBuffer) {
            int size = this.value.size() + dataBuffer.readableByteCount();
            if (PartGenerator.this.maxInMemorySize == -1 || size < PartGenerator.this.maxInMemorySize) {
                this.store(dataBuffer);
                PartGenerator.this.requestToken();
            } else {
                DataBufferUtils.release((DataBuffer)dataBuffer);
                PartGenerator.this.emitError((Throwable)new DataBufferLimitException("Form field value exceeded the memory usage limit of " + PartGenerator.this.maxInMemorySize + " bytes"));
            }
        }

        private void store(DataBuffer dataBuffer) {
            try {
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                this.value.write(bytes);
            }
            catch (IOException ex) {
                PartGenerator.this.emitError(ex);
            }
            finally {
                DataBufferUtils.release((DataBuffer)dataBuffer);
            }
        }

        @Override
        public void partComplete(boolean finalPart) {
            byte[] bytes = this.value.toByteArrayUnsafe();
            String value = new String(bytes, MultipartUtils.charset(this.headers));
            PartGenerator.this.emitPart(DefaultParts.formFieldPart(this.headers, value));
            if (finalPart) {
                PartGenerator.this.emitComplete();
            }
        }

        public String toString() {
            return "FORM-FIELD";
        }
    }

    private final class InitialState
    implements State {
        private InitialState() {
        }

        @Override
        public void body(DataBuffer dataBuffer) {
            DataBufferUtils.release((DataBuffer)dataBuffer);
            PartGenerator.this.emitError(new IllegalStateException("Body token not expected"));
        }

        @Override
        public void partComplete(boolean finalPart) {
            if (finalPart) {
                PartGenerator.this.emitComplete();
            }
        }

        public String toString() {
            return "INITIAL";
        }
    }

    private static interface State {
        public void body(DataBuffer var1);

        public void partComplete(boolean var1);

        default public void error(Throwable throwable) {
        }

        default public boolean canRequest() {
            return true;
        }

        default public void dispose() {
        }
    }
}

