/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.BinaryUtils
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.core.async;

import java.io.File;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.FileRequestBodyConfiguration;
import software.amazon.awssdk.core.async.AsyncRequestBodySplitConfiguration;
import software.amazon.awssdk.core.async.BlockingInputStreamAsyncRequestBody;
import software.amazon.awssdk.core.async.BlockingOutputStreamAsyncRequestBody;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.core.internal.async.ByteBuffersAsyncRequestBody;
import software.amazon.awssdk.core.internal.async.FileAsyncRequestBody;
import software.amazon.awssdk.core.internal.async.InputStreamWithExecutorAsyncRequestBody;
import software.amazon.awssdk.core.internal.async.SplittingPublisher;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public interface AsyncRequestBody
extends SdkPublisher<ByteBuffer> {
    public Optional<Long> contentLength();

    default public String contentType() {
        return "application/octet-stream";
    }

    public static AsyncRequestBody fromPublisher(final Publisher<ByteBuffer> publisher) {
        return new AsyncRequestBody(){

            @Override
            public Optional<Long> contentLength() {
                return Optional.empty();
            }

            public void subscribe(Subscriber<? super ByteBuffer> s) {
                publisher.subscribe(s);
            }
        };
    }

    public static AsyncRequestBody fromFile(Path path) {
        return (AsyncRequestBody)FileAsyncRequestBody.builder().path(path).build();
    }

    public static AsyncRequestBody fromFile(File file) {
        return (AsyncRequestBody)FileAsyncRequestBody.builder().path(file.toPath()).build();
    }

    public static AsyncRequestBody fromFile(FileRequestBodyConfiguration configuration) {
        Validate.notNull((Object)configuration, (String)"configuration", (Object[])new Object[0]);
        return (AsyncRequestBody)FileAsyncRequestBody.builder().path(configuration.path()).position(configuration.position()).chunkSizeInBytes(configuration.chunkSizeInBytes()).numBytesToRead(configuration.numBytesToRead()).build();
    }

    public static AsyncRequestBody fromFile(Consumer<FileRequestBodyConfiguration.Builder> configuration) {
        Validate.notNull(configuration, (String)"configuration", (Object[])new Object[0]);
        return AsyncRequestBody.fromFile((FileRequestBodyConfiguration)((FileRequestBodyConfiguration.Builder)FileRequestBodyConfiguration.builder().applyMutation(configuration)).build());
    }

    public static AsyncRequestBody fromString(String string, Charset cs) {
        return ByteBuffersAsyncRequestBody.from("text/plain; charset=" + cs.name(), string.getBytes(cs));
    }

    public static AsyncRequestBody fromString(String string) {
        return AsyncRequestBody.fromString(string, StandardCharsets.UTF_8);
    }

    public static AsyncRequestBody fromBytes(byte[] bytes) {
        byte[] clonedBytes = (byte[])bytes.clone();
        return ByteBuffersAsyncRequestBody.from(clonedBytes);
    }

    public static AsyncRequestBody fromBytesUnsafe(byte[] bytes) {
        return ByteBuffersAsyncRequestBody.from(bytes);
    }

    public static AsyncRequestBody fromByteBuffer(ByteBuffer byteBuffer) {
        ByteBuffer immutableCopy = BinaryUtils.immutableCopyOf((ByteBuffer)byteBuffer);
        immutableCopy.rewind();
        return ByteBuffersAsyncRequestBody.of(Long.valueOf(immutableCopy.remaining()), immutableCopy);
    }

    public static AsyncRequestBody fromRemainingByteBuffer(ByteBuffer byteBuffer) {
        ByteBuffer immutableCopy = BinaryUtils.immutableCopyOfRemaining((ByteBuffer)byteBuffer);
        return ByteBuffersAsyncRequestBody.of(Long.valueOf(immutableCopy.remaining()), immutableCopy);
    }

    public static AsyncRequestBody fromByteBufferUnsafe(ByteBuffer byteBuffer) {
        ByteBuffer readOnlyBuffer = byteBuffer.asReadOnlyBuffer();
        readOnlyBuffer.rewind();
        return ByteBuffersAsyncRequestBody.of(Long.valueOf(readOnlyBuffer.remaining()), readOnlyBuffer);
    }

    public static AsyncRequestBody fromRemainingByteBufferUnsafe(ByteBuffer byteBuffer) {
        ByteBuffer readOnlyBuffer = byteBuffer.asReadOnlyBuffer();
        return ByteBuffersAsyncRequestBody.of(Long.valueOf(readOnlyBuffer.remaining()), readOnlyBuffer);
    }

    public static AsyncRequestBody fromByteBuffers(ByteBuffer ... byteBuffers) {
        ByteBuffer[] immutableCopy = (ByteBuffer[])Arrays.stream(byteBuffers).map(BinaryUtils::immutableCopyOf).peek(Buffer::rewind).toArray(ByteBuffer[]::new);
        return ByteBuffersAsyncRequestBody.of(immutableCopy);
    }

    public static AsyncRequestBody fromRemainingByteBuffers(ByteBuffer ... byteBuffers) {
        ByteBuffer[] immutableCopy = (ByteBuffer[])Arrays.stream(byteBuffers).map(BinaryUtils::immutableCopyOfRemaining).peek(Buffer::rewind).toArray(ByteBuffer[]::new);
        return ByteBuffersAsyncRequestBody.of(immutableCopy);
    }

    public static AsyncRequestBody fromByteBuffersUnsafe(ByteBuffer ... byteBuffers) {
        ByteBuffer[] readOnlyBuffers = (ByteBuffer[])Arrays.stream(byteBuffers).map(ByteBuffer::asReadOnlyBuffer).peek(Buffer::rewind).toArray(ByteBuffer[]::new);
        return ByteBuffersAsyncRequestBody.of(readOnlyBuffers);
    }

    public static AsyncRequestBody fromRemainingByteBuffersUnsafe(ByteBuffer ... byteBuffers) {
        ByteBuffer[] readOnlyBuffers = (ByteBuffer[])Arrays.stream(byteBuffers).map(ByteBuffer::asReadOnlyBuffer).toArray(ByteBuffer[]::new);
        return ByteBuffersAsyncRequestBody.of(readOnlyBuffers);
    }

    public static AsyncRequestBody fromInputStream(InputStream inputStream, Long contentLength, ExecutorService executor) {
        return new InputStreamWithExecutorAsyncRequestBody(inputStream, contentLength, executor);
    }

    public static BlockingInputStreamAsyncRequestBody forBlockingInputStream(Long contentLength) {
        return new BlockingInputStreamAsyncRequestBody(contentLength);
    }

    public static BlockingOutputStreamAsyncRequestBody forBlockingOutputStream(Long contentLength) {
        return new BlockingOutputStreamAsyncRequestBody(contentLength);
    }

    public static AsyncRequestBody empty() {
        return AsyncRequestBody.fromBytes(new byte[0]);
    }

    default public SdkPublisher<AsyncRequestBody> split(AsyncRequestBodySplitConfiguration splitConfiguration) {
        Validate.notNull((Object)splitConfiguration, (String)"splitConfiguration", (Object[])new Object[0]);
        return new SplittingPublisher(this, splitConfiguration);
    }

    default public SdkPublisher<AsyncRequestBody> split(Consumer<AsyncRequestBodySplitConfiguration.Builder> splitConfiguration) {
        Validate.notNull(splitConfiguration, (String)"splitConfiguration", (Object[])new Object[0]);
        return this.split((AsyncRequestBodySplitConfiguration)((AsyncRequestBodySplitConfiguration.Builder)AsyncRequestBodySplitConfiguration.builder().applyMutation(splitConfiguration)).build());
    }
}

