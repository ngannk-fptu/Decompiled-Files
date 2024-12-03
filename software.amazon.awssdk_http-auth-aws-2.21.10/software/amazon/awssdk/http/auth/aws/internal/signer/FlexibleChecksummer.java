/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.checksums.spi.ChecksumAlgorithm
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.http.SdkHttpRequest$Builder
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.http.auth.aws.internal.signer;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.checksums.spi.ChecksumAlgorithm;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.aws.internal.signer.Checksummer;
import software.amazon.awssdk.http.auth.aws.internal.signer.checksums.SdkChecksum;
import software.amazon.awssdk.http.auth.aws.internal.signer.io.ChecksumInputStream;
import software.amazon.awssdk.http.auth.aws.internal.signer.io.ChecksumSubscriber;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.ChecksumUtil;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.SignerUtils;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class FlexibleChecksummer
implements Checksummer {
    private final Collection<Option> options;
    private final Map<Option, SdkChecksum> optionToSdkChecksum;

    public FlexibleChecksummer(Option ... options) {
        this.options = Arrays.asList(options);
        this.optionToSdkChecksum = this.options.stream().collect(Collectors.toMap(Function.identity(), o -> ChecksumUtil.fromChecksumAlgorithm(((Option)o).algorithm)));
    }

    @Override
    public void checksum(ContentStreamProvider payload, SdkHttpRequest.Builder request) {
        InputStream payloadStream = SignerUtils.getBinaryRequestPayloadStream(payload);
        ChecksumInputStream computingStream = new ChecksumInputStream(payloadStream, this.optionToSdkChecksum.values());
        ChecksumUtil.readAll(computingStream);
        this.addChecksums(request);
    }

    @Override
    public CompletableFuture<Publisher<ByteBuffer>> checksum(Publisher<ByteBuffer> payload, SdkHttpRequest.Builder request) {
        ChecksumSubscriber checksumSubscriber = new ChecksumSubscriber(this.optionToSdkChecksum.values());
        if (payload == null) {
            this.addChecksums(request);
            return CompletableFuture.completedFuture(null);
        }
        payload.subscribe((Subscriber)checksumSubscriber);
        CompletableFuture<Publisher<ByteBuffer>> result = checksumSubscriber.completeFuture();
        result.thenRun(() -> this.addChecksums(request));
        return result;
    }

    private void addChecksums(SdkHttpRequest.Builder request) {
        this.optionToSdkChecksum.forEach((option, sdkChecksum) -> request.putHeader(((Option)option).headerName, (String)((Option)option).formatter.apply(sdkChecksum.getChecksumBytes())));
    }

    public static Option.Builder option() {
        return Option.builder();
    }

    public static class Option {
        private final ChecksumAlgorithm algorithm;
        private final String headerName;
        private final Function<byte[], String> formatter;

        Option(Builder builder) {
            this.algorithm = (ChecksumAlgorithm)Validate.paramNotNull((Object)builder.algorithm, (String)"algorithm");
            this.headerName = (String)Validate.paramNotNull((Object)builder.headerName, (String)"headerName");
            this.formatter = (Function)Validate.paramNotNull((Object)builder.formatter, (String)"formatter");
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private ChecksumAlgorithm algorithm;
            private String headerName;
            private Function<byte[], String> formatter;

            public Builder algorithm(ChecksumAlgorithm algorithm) {
                this.algorithm = algorithm;
                return this;
            }

            public Builder headerName(String headerName) {
                this.headerName = headerName;
                return this;
            }

            public Builder formatter(Function<byte[], String> formatter) {
                this.formatter = formatter;
                return this;
            }

            public Option build() {
                return new Option(this);
            }
        }
    }
}

