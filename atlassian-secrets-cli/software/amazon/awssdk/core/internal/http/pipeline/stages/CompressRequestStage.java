/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.CompressionConfiguration;
import software.amazon.awssdk.core.RequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.core.internal.async.CompressionAsyncRequestBody;
import software.amazon.awssdk.core.internal.compression.Compressor;
import software.amazon.awssdk.core.internal.compression.CompressorType;
import software.amazon.awssdk.core.internal.http.HttpClientDependencies;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.MutableRequestToRequestPipeline;
import software.amazon.awssdk.core.internal.sync.CompressionContentStreamProvider;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.utils.IoUtils;

@SdkInternalApi
public class CompressRequestStage
implements MutableRequestToRequestPipeline {
    private static final int DEFAULT_MIN_COMPRESSION_SIZE = 10240;
    private static final int MIN_COMPRESSION_SIZE_LIMIT = 0xA00000;
    private final CompressionConfiguration compressionConfig;

    public CompressRequestStage(HttpClientDependencies dependencies) {
        this.compressionConfig = dependencies.clientConfiguration().option(SdkClientOption.COMPRESSION_CONFIGURATION);
    }

    @Override
    public SdkHttpFullRequest.Builder execute(SdkHttpFullRequest.Builder input, RequestExecutionContext context) throws Exception {
        if (!this.shouldCompress(input, context)) {
            return input;
        }
        Compressor compressor = this.resolveCompressorType(context.executionAttributes());
        if (!this.isStreaming(context)) {
            this.compressEntirePayload(input, compressor);
            this.updateContentEncodingHeader(input, compressor);
            this.updateContentLengthHeader(input);
            return input;
        }
        if (!this.isTransferEncodingChunked(input)) {
            return input;
        }
        if (context.requestProvider() == null) {
            input.contentStreamProvider(new CompressionContentStreamProvider(input.contentStreamProvider(), compressor));
        } else {
            context.requestProvider((AsyncRequestBody)CompressionAsyncRequestBody.builder().asyncRequestBody(context.requestProvider()).compressor(compressor).build());
        }
        this.updateContentEncodingHeader(input, compressor);
        return input;
    }

    private boolean shouldCompress(SdkHttpFullRequest.Builder input, RequestExecutionContext context) {
        if (context.executionAttributes().getAttribute(SdkInternalExecutionAttribute.REQUEST_COMPRESSION) == null) {
            return false;
        }
        if (this.resolveCompressorType(context.executionAttributes()) == null) {
            return false;
        }
        if (!this.resolveRequestCompressionEnabled(context)) {
            return false;
        }
        if (this.isStreaming(context)) {
            return true;
        }
        if (input.contentStreamProvider() == null) {
            return false;
        }
        return this.isRequestSizeWithinThreshold(input, context);
    }

    private boolean isStreaming(RequestExecutionContext context) {
        return context.executionAttributes().getAttribute(SdkInternalExecutionAttribute.REQUEST_COMPRESSION).isStreaming();
    }

    private void compressEntirePayload(SdkHttpFullRequest.Builder input, Compressor compressor) {
        ContentStreamProvider wrappedProvider = input.contentStreamProvider();
        ContentStreamProvider compressedStreamProvider = () -> compressor.compress(wrappedProvider.newStream());
        input.contentStreamProvider(compressedStreamProvider);
    }

    private void updateContentEncodingHeader(SdkHttpFullRequest.Builder input, Compressor compressor) {
        if (input.firstMatchingHeader("Content-encoding").isPresent()) {
            input.appendHeader("Content-encoding", compressor.compressorType());
        } else {
            input.putHeader("Content-encoding", compressor.compressorType());
        }
    }

    private void updateContentLengthHeader(SdkHttpFullRequest.Builder input) {
        InputStream inputStream = input.contentStreamProvider().newStream();
        try {
            byte[] bytes = IoUtils.toByteArray(inputStream);
            String length = String.valueOf(bytes.length);
            input.putHeader("Content-Length", length);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private boolean isTransferEncodingChunked(SdkHttpFullRequest.Builder input) {
        return input.firstMatchingHeader("Transfer-Encoding").map(headerValue -> headerValue.equals("chunked")).orElse(false);
    }

    private Compressor resolveCompressorType(ExecutionAttributes executionAttributes) {
        List<String> encodings = executionAttributes.getAttribute(SdkInternalExecutionAttribute.REQUEST_COMPRESSION).getEncodings();
        for (String encoding : encodings) {
            if (!CompressorType.isSupported(encoding = encoding.toLowerCase(Locale.ROOT))) continue;
            return CompressorType.of(encoding).newCompressor();
        }
        return null;
    }

    private boolean resolveRequestCompressionEnabled(RequestExecutionContext context) {
        Optional<Boolean> requestCompressionEnabledRequestLevel = context.originalRequest().overrideConfiguration().flatMap(RequestOverrideConfiguration::compressionConfiguration).map(CompressionConfiguration::requestCompressionEnabled);
        if (requestCompressionEnabledRequestLevel.isPresent()) {
            return requestCompressionEnabledRequestLevel.get();
        }
        Boolean isEnabled = this.compressionConfig.requestCompressionEnabled();
        if (isEnabled != null) {
            return isEnabled;
        }
        return true;
    }

    private boolean isRequestSizeWithinThreshold(SdkHttpFullRequest.Builder input, RequestExecutionContext context) {
        int minimumCompressionThreshold = this.resolveMinCompressionSize(context);
        this.validateMinCompressionSizeInput(minimumCompressionThreshold);
        int requestSize = SdkBytes.fromInputStream(input.contentStreamProvider().newStream()).asByteArray().length;
        return requestSize >= minimumCompressionThreshold;
    }

    private int resolveMinCompressionSize(RequestExecutionContext context) {
        Optional<Integer> minimumCompressionSizeRequestLevel = context.originalRequest().overrideConfiguration().flatMap(RequestOverrideConfiguration::compressionConfiguration).map(CompressionConfiguration::minimumCompressionThresholdInBytes);
        if (minimumCompressionSizeRequestLevel.isPresent()) {
            return minimumCompressionSizeRequestLevel.get();
        }
        Integer threshold = this.compressionConfig.minimumCompressionThresholdInBytes();
        if (threshold != null) {
            return threshold;
        }
        return 10240;
    }

    private void validateMinCompressionSizeInput(int minCompressionSize) {
        if (minCompressionSize < 0 || minCompressionSize > 0xA00000) {
            throw SdkClientException.create("The minimum compression size must be non-negative with a maximum value of 10485760.", new IllegalArgumentException());
        }
    }
}

