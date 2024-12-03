/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.http.Abortable
 *  software.amazon.awssdk.http.AbortableInputStream
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.utils.FunctionalUtils
 */
package software.amazon.awssdk.core.http;

import java.io.InputStream;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.internal.util.Crc32ChecksumValidatingInputStream;
import software.amazon.awssdk.http.Abortable;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.utils.FunctionalUtils;

@SdkProtectedApi
public final class Crc32Validation {
    private Crc32Validation() {
    }

    public static SdkHttpFullResponse validate(boolean calculateCrc32FromCompressedData, SdkHttpFullResponse httpResponse) {
        if (!httpResponse.content().isPresent()) {
            return httpResponse;
        }
        return httpResponse.toBuilder().content(Crc32Validation.process(calculateCrc32FromCompressedData, httpResponse, (AbortableInputStream)httpResponse.content().get())).build();
    }

    private static AbortableInputStream process(boolean calculateCrc32FromCompressedData, SdkHttpFullResponse httpResponse, AbortableInputStream content) {
        Optional<Long> crc32Checksum = Crc32Validation.getCrc32Checksum(httpResponse);
        if (Crc32Validation.shouldDecompress(httpResponse)) {
            if (calculateCrc32FromCompressedData && crc32Checksum.isPresent()) {
                return Crc32Validation.decompressing(Crc32Validation.crc32Validating(content, crc32Checksum.get()));
            }
            if (crc32Checksum.isPresent()) {
                return Crc32Validation.crc32Validating(Crc32Validation.decompressing(content), crc32Checksum.get());
            }
            return Crc32Validation.decompressing(content);
        }
        return crc32Checksum.map(aLong -> Crc32Validation.crc32Validating(content, aLong)).orElse(content);
    }

    private static AbortableInputStream crc32Validating(AbortableInputStream source, long expectedChecksum) {
        return AbortableInputStream.create((InputStream)new Crc32ChecksumValidatingInputStream((InputStream)source, expectedChecksum), (Abortable)source);
    }

    private static Optional<Long> getCrc32Checksum(SdkHttpFullResponse httpResponse) {
        return httpResponse.firstMatchingHeader("x-amz-crc32").map(Long::valueOf);
    }

    private static boolean shouldDecompress(SdkHttpFullResponse httpResponse) {
        return httpResponse.firstMatchingHeader("Content-Encoding").filter(e -> e.equals("gzip")).isPresent();
    }

    private static AbortableInputStream decompressing(AbortableInputStream source) {
        return AbortableInputStream.create((InputStream)((InputStream)FunctionalUtils.invokeSafely(() -> new GZIPInputStream((InputStream)source))), (Abortable)source);
    }
}

