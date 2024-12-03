/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.async;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.FileTransformerConfiguration;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.async.ResponsePublisher;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.core.internal.async.ByteArrayAsyncResponseTransformer;
import software.amazon.awssdk.core.internal.async.FileAsyncResponseTransformer;
import software.amazon.awssdk.core.internal.async.InputStreamResponseTransformer;
import software.amazon.awssdk.core.internal.async.PublisherAsyncResponseTransformer;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public interface AsyncResponseTransformer<ResponseT, ResultT> {
    public CompletableFuture<ResultT> prepare();

    public void onResponse(ResponseT var1);

    public void onStream(SdkPublisher<ByteBuffer> var1);

    public void exceptionOccurred(Throwable var1);

    public static <ResponseT> AsyncResponseTransformer<ResponseT, ResponseT> toFile(Path path) {
        return new FileAsyncResponseTransformer(path);
    }

    public static <ResponseT> AsyncResponseTransformer<ResponseT, ResponseT> toFile(Path path, FileTransformerConfiguration config) {
        return new FileAsyncResponseTransformer(path, config);
    }

    public static <ResponseT> AsyncResponseTransformer<ResponseT, ResponseT> toFile(Path path, Consumer<FileTransformerConfiguration.Builder> config) {
        Validate.paramNotNull(config, "config");
        return new FileAsyncResponseTransformer(path, (FileTransformerConfiguration)FileTransformerConfiguration.builder().applyMutation(config).build());
    }

    public static <ResponseT> AsyncResponseTransformer<ResponseT, ResponseT> toFile(File file) {
        return AsyncResponseTransformer.toFile(file.toPath());
    }

    public static <ResponseT> AsyncResponseTransformer<ResponseT, ResponseT> toFile(File file, FileTransformerConfiguration config) {
        return new FileAsyncResponseTransformer(file.toPath(), config);
    }

    public static <ResponseT> AsyncResponseTransformer<ResponseT, ResponseT> toFile(File file, Consumer<FileTransformerConfiguration.Builder> config) {
        Validate.paramNotNull(config, "config");
        return new FileAsyncResponseTransformer(file.toPath(), (FileTransformerConfiguration)FileTransformerConfiguration.builder().applyMutation(config).build());
    }

    public static <ResponseT> AsyncResponseTransformer<ResponseT, ResponseBytes<ResponseT>> toBytes() {
        return new ByteArrayAsyncResponseTransformer();
    }

    public static <ResponseT extends SdkResponse> AsyncResponseTransformer<ResponseT, ResponsePublisher<ResponseT>> toPublisher() {
        return new PublisherAsyncResponseTransformer();
    }

    public static <ResponseT extends SdkResponse> AsyncResponseTransformer<ResponseT, ResponseInputStream<ResponseT>> toBlockingInputStream() {
        return new InputStreamResponseTransformer();
    }
}

