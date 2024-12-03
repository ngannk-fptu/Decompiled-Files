/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.internal.signer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.checksums.DefaultChecksumAlgorithm;
import software.amazon.awssdk.checksums.spi.ChecksumAlgorithm;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.aws.internal.signer.FlexibleChecksummer;
import software.amazon.awssdk.http.auth.aws.internal.signer.PrecomputedSha256Checksummer;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.ChecksumUtil;
import software.amazon.awssdk.utils.BinaryUtils;

@SdkInternalApi
public interface Checksummer {
    public static Checksummer create() {
        return new FlexibleChecksummer(FlexibleChecksummer.option().headerName("x-amz-content-sha256").algorithm(DefaultChecksumAlgorithm.SHA256).formatter(BinaryUtils::toHex).build());
    }

    public static Checksummer forFlexibleChecksum(ChecksumAlgorithm checksumAlgorithm) {
        if (checksumAlgorithm != null) {
            return new FlexibleChecksummer(FlexibleChecksummer.option().headerName("x-amz-content-sha256").algorithm(DefaultChecksumAlgorithm.SHA256).formatter(BinaryUtils::toHex).build(), FlexibleChecksummer.option().headerName(ChecksumUtil.checksumHeaderName(checksumAlgorithm)).algorithm(checksumAlgorithm).formatter(BinaryUtils::toBase64).build());
        }
        throw new IllegalArgumentException("Checksum Algorithm cannot be null!");
    }

    public static Checksummer forPrecomputed256Checksum(String precomputedSha256) {
        return new PrecomputedSha256Checksummer(() -> precomputedSha256);
    }

    public static Checksummer forFlexibleChecksum(String precomputedSha256, ChecksumAlgorithm checksumAlgorithm) {
        if (checksumAlgorithm != null) {
            return new FlexibleChecksummer(FlexibleChecksummer.option().headerName("x-amz-content-sha256").algorithm(new ChecksumUtil.ConstantChecksumAlgorithm(precomputedSha256)).formatter(b -> new String((byte[])b, StandardCharsets.UTF_8)).build(), FlexibleChecksummer.option().headerName(ChecksumUtil.checksumHeaderName(checksumAlgorithm)).algorithm(checksumAlgorithm).formatter(BinaryUtils::toBase64).build());
        }
        throw new IllegalArgumentException("Checksum Algorithm cannot be null!");
    }

    public static Checksummer forNoOp() {
        return new FlexibleChecksummer(new FlexibleChecksummer.Option[0]);
    }

    public void checksum(ContentStreamProvider var1, SdkHttpRequest.Builder var2);

    public CompletableFuture<Publisher<ByteBuffer>> checksum(Publisher<ByteBuffer> var1, SdkHttpRequest.Builder var2);
}

