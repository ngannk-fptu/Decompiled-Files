/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.async;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.FileTransformerConfiguration;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.utils.FunctionalUtils;

@SdkInternalApi
public final class FileAsyncResponseTransformer<ResponseT>
implements AsyncResponseTransformer<ResponseT, ResponseT> {
    private final Path path;
    private volatile AsynchronousFileChannel fileChannel;
    private volatile CompletableFuture<Void> cf;
    private volatile ResponseT response;
    private final long position;
    private final FileTransformerConfiguration configuration;

    public FileAsyncResponseTransformer(Path path) {
        this.path = path;
        this.configuration = FileTransformerConfiguration.defaultCreateNew();
        this.position = 0L;
    }

    public FileAsyncResponseTransformer(Path path, FileTransformerConfiguration fileConfiguration) {
        this.path = path;
        this.configuration = fileConfiguration;
        this.position = this.determineFilePositionToWrite(path);
    }

    private long determineFilePositionToWrite(Path path) {
        if (this.configuration.fileWriteOption() == FileTransformerConfiguration.FileWriteOption.CREATE_OR_APPEND_TO_EXISTING) {
            try {
                return Files.size(path);
            }
            catch (NoSuchFileException noSuchFileException) {
            }
            catch (IOException exception) {
                throw SdkClientException.create("Cannot determine the current file size " + path, exception);
            }
        }
        return 0L;
    }

    private AsynchronousFileChannel createChannel(Path path) throws IOException {
        HashSet options = new HashSet();
        switch (this.configuration.fileWriteOption()) {
            case CREATE_OR_APPEND_TO_EXISTING: {
                Collections.addAll(options, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
                break;
            }
            case CREATE_OR_REPLACE_EXISTING: {
                Collections.addAll(options, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                break;
            }
            case CREATE_NEW: {
                Collections.addAll(options, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported file write option: " + (Object)((Object)this.configuration.fileWriteOption()));
            }
        }
        ExecutorService executorService = this.configuration.executorService().orElse(null);
        return AsynchronousFileChannel.open(path, options, executorService, new FileAttribute[0]);
    }

    @Override
    public CompletableFuture<ResponseT> prepare() {
        this.cf = new CompletableFuture();
        this.cf.whenComplete((r, t) -> {
            if (t != null && this.fileChannel != null) {
                FunctionalUtils.invokeSafely(this.fileChannel::close);
            }
        });
        return this.cf.thenApply(ignored -> this.response);
    }

    @Override
    public void onResponse(ResponseT response) {
        this.response = response;
    }

    @Override
    public void onStream(SdkPublisher<ByteBuffer> publisher) {
        this.fileChannel = FunctionalUtils.invokeSafely(() -> this.createChannel(this.path));
        publisher.subscribe(new FileSubscriber(this.fileChannel, this.path, this.cf, this::exceptionOccurred, this.position));
    }

    @Override
    public void exceptionOccurred(Throwable throwable) {
        try {
            if (this.fileChannel != null) {
                FunctionalUtils.invokeSafely(this.fileChannel::close);
            }
        }
        finally {
            if (this.configuration.failureBehavior() == FileTransformerConfiguration.FailureBehavior.DELETE) {
                FunctionalUtils.invokeSafely(() -> Files.deleteIfExists(this.path));
            }
        }
        this.cf.completeExceptionally(throwable);
    }

    static class FileSubscriber
    implements Subscriber<ByteBuffer> {
        private final AtomicLong position;
        private final AsynchronousFileChannel fileChannel;
        private final Path path;
        private final CompletableFuture<Void> future;
        private final Consumer<Throwable> onErrorMethod;
        private volatile boolean writeInProgress = false;
        private volatile boolean closeOnLastWrite = false;
        private Subscription subscription;

        FileSubscriber(AsynchronousFileChannel fileChannel, Path path, CompletableFuture<Void> future, Consumer<Throwable> onErrorMethod, long startingPosition) {
            this.fileChannel = fileChannel;
            this.path = path;
            this.future = future;
            this.onErrorMethod = onErrorMethod;
            this.position = new AtomicLong(startingPosition);
        }

        @Override
        public void onSubscribe(Subscription s) {
            if (this.subscription != null) {
                s.cancel();
                return;
            }
            this.subscription = s;
            s.request(1L);
        }

        @Override
        public void onNext(ByteBuffer byteBuffer) {
            if (byteBuffer == null) {
                throw new NullPointerException("Element must not be null");
            }
            this.performWrite(byteBuffer);
        }

        private void performWrite(final ByteBuffer byteBuffer) {
            this.writeInProgress = true;
            this.fileChannel.write(byteBuffer, this.position.get(), byteBuffer, new CompletionHandler<Integer, ByteBuffer>(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    position.addAndGet(result.intValue());
                    if (byteBuffer.hasRemaining()) {
                        this.performWrite(byteBuffer);
                    } else {
                        FileSubscriber fileSubscriber = this;
                        synchronized (fileSubscriber) {
                            writeInProgress = false;
                            if (closeOnLastWrite) {
                                this.close();
                            } else {
                                subscription.request(1L);
                            }
                        }
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    subscription.cancel();
                    future.completeExceptionally(exc);
                }
            });
        }

        @Override
        public void onError(Throwable t) {
            this.onErrorMethod.accept(t);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void onComplete() {
            FileSubscriber fileSubscriber = this;
            synchronized (fileSubscriber) {
                if (this.writeInProgress) {
                    this.closeOnLastWrite = true;
                } else {
                    this.close();
                }
            }
        }

        private void close() {
            try {
                if (this.fileChannel != null) {
                    FunctionalUtils.invokeSafely(this.fileChannel::close);
                }
                this.future.complete(null);
            }
            catch (RuntimeException exception) {
                this.future.completeExceptionally(exception);
            }
        }

        public String toString() {
            return this.getClass() + ":" + this.path.toString();
        }
    }
}

