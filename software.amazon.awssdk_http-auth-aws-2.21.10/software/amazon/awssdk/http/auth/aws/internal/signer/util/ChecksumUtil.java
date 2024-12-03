/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.checksums.DefaultChecksumAlgorithm
 *  software.amazon.awssdk.checksums.spi.ChecksumAlgorithm
 *  software.amazon.awssdk.utils.ImmutableMap
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.util;

import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.checksums.DefaultChecksumAlgorithm;
import software.amazon.awssdk.checksums.spi.ChecksumAlgorithm;
import software.amazon.awssdk.http.auth.aws.internal.signer.checksums.ConstantChecksum;
import software.amazon.awssdk.http.auth.aws.internal.signer.checksums.Crc32CChecksum;
import software.amazon.awssdk.http.auth.aws.internal.signer.checksums.Crc32Checksum;
import software.amazon.awssdk.http.auth.aws.internal.signer.checksums.Md5Checksum;
import software.amazon.awssdk.http.auth.aws.internal.signer.checksums.SdkChecksum;
import software.amazon.awssdk.http.auth.aws.internal.signer.checksums.Sha1Checksum;
import software.amazon.awssdk.http.auth.aws.internal.signer.checksums.Sha256Checksum;
import software.amazon.awssdk.utils.ImmutableMap;

@SdkInternalApi
public final class ChecksumUtil {
    private static final String CONSTANT_CHECKSUM = "CONSTANT";
    private static final Map<String, Supplier<SdkChecksum>> CHECKSUM_MAP = ImmutableMap.of((Object)DefaultChecksumAlgorithm.SHA256.algorithmId(), Sha256Checksum::new, (Object)DefaultChecksumAlgorithm.SHA1.algorithmId(), Sha1Checksum::new, (Object)DefaultChecksumAlgorithm.CRC32.algorithmId(), Crc32Checksum::new, (Object)DefaultChecksumAlgorithm.CRC32C.algorithmId(), Crc32CChecksum::new, (Object)DefaultChecksumAlgorithm.MD5.algorithmId(), Md5Checksum::new);

    private ChecksumUtil() {
    }

    public static String checksumHeaderName(ChecksumAlgorithm checksumAlgorithm) {
        return "x-amz-checksum-" + checksumAlgorithm.algorithmId().toLowerCase(Locale.US);
    }

    public static SdkChecksum fromChecksumAlgorithm(ChecksumAlgorithm checksumAlgorithm) {
        String algorithmId = checksumAlgorithm.algorithmId();
        Supplier<SdkChecksum> checksumSupplier = CHECKSUM_MAP.get(algorithmId);
        if (checksumSupplier != null) {
            return checksumSupplier.get();
        }
        if (CONSTANT_CHECKSUM.equals(algorithmId)) {
            return new ConstantChecksum(((ConstantChecksumAlgorithm)checksumAlgorithm).value);
        }
        throw new UnsupportedOperationException("Checksum not supported for " + algorithmId);
    }

    public static void readAll(InputStream inputStream) {
        try {
            byte[] buffer = new byte[4096];
            while (inputStream.read(buffer) > -1) {
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Could not finish reading stream: ", e);
        }
    }

    public static class ConstantChecksumAlgorithm
    implements ChecksumAlgorithm {
        private final String value;

        public ConstantChecksumAlgorithm(String value) {
            this.value = value;
        }

        public String algorithmId() {
            return ChecksumUtil.CONSTANT_CHECKSUM;
        }
    }
}

