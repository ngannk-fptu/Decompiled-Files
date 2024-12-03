/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  org.slf4j.Logger
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.FunctionalUtils
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.NumericUtils
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.SdkBuilder
 */
package software.amazon.awssdk.core.internal.async;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncRequestBodySplitConfiguration;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.core.internal.async.FileAsyncRequestBodySplitHelper;
import software.amazon.awssdk.core.internal.util.Mimetype;
import software.amazon.awssdk.core.internal.util.NoopSubscription;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.NumericUtils;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.SdkBuilder;

@SdkInternalApi
public final class FileAsyncRequestBody
implements AsyncRequestBody {
    private static final software.amazon.awssdk.utils.Logger log = software.amazon.awssdk.utils.Logger.loggerFor(FileAsyncRequestBody.class);
    private static final int DEFAULT_CHUNK_SIZE = 16384;
    private final Path path;
    private final long fileLength;
    private final int chunkSizeInBytes;
    private final long position;
    private final long numBytesToRead;

    private FileAsyncRequestBody(DefaultBuilder builder) {
        this.path = builder.path;
        this.chunkSizeInBytes = builder.chunkSizeInBytes == null ? 16384 : builder.chunkSizeInBytes;
        this.fileLength = (Long)FunctionalUtils.invokeSafely(() -> Files.size(this.path));
        this.position = builder.position == null ? 0L : Validate.isNotNegative((long)builder.position, (String)"position");
        this.numBytesToRead = builder.numBytesToRead == null ? this.fileLength - this.position : Validate.isNotNegative((long)builder.numBytesToRead, (String)"numBytesToRead");
    }

    @Override
    public SdkPublisher<AsyncRequestBody> split(AsyncRequestBodySplitConfiguration splitConfiguration) {
        Validate.notNull((Object)splitConfiguration, (String)"splitConfiguration", (Object[])new Object[0]);
        return new FileAsyncRequestBodySplitHelper(this, splitConfiguration).split();
    }

    public Path path() {
        return this.path;
    }

    public long fileLength() {
        return this.fileLength;
    }

    public int chunkSizeInBytes() {
        return this.chunkSizeInBytes;
    }

    public long position() {
        return this.position;
    }

    public long numBytesToRead() {
        return this.numBytesToRead;
    }

    @Override
    public Optional<Long> contentLength() {
        return Optional.of(this.numBytesToRead);
    }

    @Override
    public String contentType() {
        return Mimetype.getInstance().getMimetype(this.path);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void subscribe(Subscriber<? super ByteBuffer> s) {
        AsynchronousFileChannel channel = null;
        try {
            FileSubscription subscription;
            channel = FileAsyncRequestBody.openInputChannel(this.path);
            FileSubscription fileSubscription = subscription = new FileSubscription(channel, s);
            synchronized (fileSubscription) {
                s.onSubscribe((Subscription)subscription);
            }
        }
        catch (IOException | RuntimeException e) {
            if (channel != null) {
                FunctionalUtils.runAndLogError((Logger)log.logger(), (String)"Unable to close file channel", channel::close);
            }
            s.onSubscribe((Subscription)new NoopSubscription(s));
            s.onError((Throwable)e);
        }
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    private static AsynchronousFileChannel openInputChannel(Path path) throws IOException {
        return AsynchronousFileChannel.open(path, StandardOpenOption.READ);
    }

    private final class FileSubscription
    implements Subscription {
        private final AsynchronousFileChannel inputChannel;
        private final Subscriber<? super ByteBuffer> subscriber;
        private final AtomicLong currentPosition;
        private final AtomicLong remainingBytes;
        private final long sizeAtStart;
        private final FileTime modifiedTimeAtStart;
        private long outstandingDemand = 0L;
        private boolean readInProgress = false;
        private volatile boolean done = false;
        private final Object lock = new Object();

        private FileSubscription(AsynchronousFileChannel inputChannel, Subscriber<? super ByteBuffer> subscriber) throws IOException {
            this.inputChannel = inputChannel;
            this.subscriber = subscriber;
            this.sizeAtStart = inputChannel.size();
            this.modifiedTimeAtStart = Files.getLastModifiedTime(FileAsyncRequestBody.this.path, new LinkOption[0]);
            this.remainingBytes = new AtomicLong(FileAsyncRequestBody.this.numBytesToRead);
            this.currentPosition = new AtomicLong(FileAsyncRequestBody.this.position);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void request(long n) {
            if (this.done) {
                return;
            }
            if (n < 1L) {
                IllegalArgumentException ex = new IllegalArgumentException(this.subscriber + " violated the Reactive Streams rule 3.9 by requesting a non-positive number of elements.");
                this.signalOnError(ex);
            } else {
                try {
                    Object ex = this.lock;
                    synchronized (ex) {
                        this.outstandingDemand = Long.MAX_VALUE - this.outstandingDemand < n ? Long.MAX_VALUE : (this.outstandingDemand += n);
                        if (!this.readInProgress) {
                            this.readInProgress = true;
                            this.readData();
                        }
                    }
                }
                catch (Exception e) {
                    this.signalOnError(e);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void cancel() {
            FileSubscription fileSubscription = this;
            synchronized (fileSubscription) {
                if (!this.done) {
                    this.done = true;
                    this.closeFile();
                }
            }
        }

        private void readData() {
            if (!this.inputChannel.isOpen() || this.done) {
                return;
            }
            ByteBuffer buffer = ByteBuffer.allocate(Math.min(FileAsyncRequestBody.this.chunkSizeInBytes, NumericUtils.saturatedCast((long)this.remainingBytes.get())));
            this.inputChannel.read(buffer, this.currentPosition.get(), buffer, new CompletionHandler<Integer, ByteBuffer>(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    block9: {
                        try {
                            if (result > 0) {
                                attachment.flip();
                                int readBytes = attachment.remaining();
                                FileSubscription.this.currentPosition.addAndGet(readBytes);
                                FileSubscription.this.remainingBytes.addAndGet(-readBytes);
                                FileSubscription.this.signalOnNext(attachment);
                                if (FileSubscription.this.remainingBytes.get() == 0L) {
                                    FileSubscription.this.closeFile();
                                    FileSubscription.this.signalOnComplete();
                                }
                                Object object = FileSubscription.this.lock;
                                synchronized (object) {
                                    if (--FileSubscription.this.outstandingDemand > 0L) {
                                        FileSubscription.this.readData();
                                    } else {
                                        FileSubscription.this.readInProgress = false;
                                    }
                                    break block9;
                                }
                            }
                            FileSubscription.this.closeFile();
                            FileSubscription.this.signalOnComplete();
                        }
                        catch (Throwable throwable) {
                            FileSubscription.this.closeFile();
                            FileSubscription.this.signalOnError(throwable);
                        }
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    FileSubscription.this.signalOnError(exc);
                    FileSubscription.this.closeFile();
                }
            });
        }

        private void closeFile() {
            try {
                this.inputChannel.close();
            }
            catch (IOException e) {
                log.warn(() -> "Failed to close the file", (Throwable)e);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void signalOnNext(ByteBuffer attachment) {
            FileSubscription fileSubscription = this;
            synchronized (fileSubscription) {
                if (!this.done) {
                    this.subscriber.onNext((Object)attachment);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void signalOnComplete() {
            try {
                long sizeAtEnd = Files.size(FileAsyncRequestBody.this.path);
                if (this.sizeAtStart != sizeAtEnd) {
                    this.signalOnError(new IOException("File size changed after reading started. Initial size: " + this.sizeAtStart + ". Current size: " + sizeAtEnd));
                    return;
                }
                if (this.remainingBytes.get() > 0L) {
                    this.signalOnError(new IOException("Fewer bytes were read than were expected, was the file modified after reading started?"));
                    return;
                }
                FileTime modifiedTimeAtEnd = Files.getLastModifiedTime(FileAsyncRequestBody.this.path, new LinkOption[0]);
                if (this.modifiedTimeAtStart.compareTo(modifiedTimeAtEnd) != 0) {
                    this.signalOnError(new IOException("File last-modified time changed after reading started. Initial modification time: " + this.modifiedTimeAtStart + ". Current modification time: " + modifiedTimeAtEnd));
                    return;
                }
            }
            catch (NoSuchFileException e) {
                this.signalOnError(new IOException("Unable to check file status after read. Was the file deleted or were its permissions changed?", e));
                return;
            }
            catch (IOException e) {
                this.signalOnError(new IOException("Unable to check file status after read.", e));
                return;
            }
            FileSubscription fileSubscription = this;
            synchronized (fileSubscription) {
                if (!this.done) {
                    this.done = true;
                    this.subscriber.onComplete();
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void signalOnError(Throwable t) {
            FileSubscription fileSubscription = this;
            synchronized (fileSubscription) {
                if (!this.done) {
                    this.done = true;
                    this.subscriber.onError(t);
                }
            }
        }
    }

    private static final class DefaultBuilder
    implements Builder {
        private Long position;
        private Path path;
        private Integer chunkSizeInBytes;
        private Long numBytesToRead;

        private DefaultBuilder() {
        }

        @Override
        public Builder path(Path path) {
            this.path = path;
            return this;
        }

        public void setPath(Path path) {
            this.path(path);
        }

        @Override
        public Builder chunkSizeInBytes(Integer chunkSizeInBytes) {
            this.chunkSizeInBytes = chunkSizeInBytes;
            return this;
        }

        @Override
        public Builder position(Long position) {
            this.position = position;
            return this;
        }

        @Override
        public Builder numBytesToRead(Long numBytesToRead) {
            this.numBytesToRead = numBytesToRead;
            return this;
        }

        public void setChunkSizeInBytes(Integer chunkSizeInBytes) {
            this.chunkSizeInBytes(chunkSizeInBytes);
        }

        public FileAsyncRequestBody build() {
            return new FileAsyncRequestBody(this);
        }
    }

    public static interface Builder
    extends SdkBuilder<Builder, FileAsyncRequestBody> {
        public Builder path(Path var1);

        public Builder chunkSizeInBytes(Integer var1);

        public Builder position(Long var1);

        public Builder numBytesToRead(Long var1);
    }
}

