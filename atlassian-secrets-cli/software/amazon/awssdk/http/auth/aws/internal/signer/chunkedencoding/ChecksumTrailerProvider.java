/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding;

import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.internal.signer.checksums.SdkChecksum;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.TrailerProvider;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.Pair;

@SdkInternalApi
public class ChecksumTrailerProvider
implements TrailerProvider {
    private final SdkChecksum checksum;
    private final String checksumName;

    public ChecksumTrailerProvider(SdkChecksum checksum, String checksumName) {
        this.checksum = checksum;
        this.checksumName = checksumName;
    }

    @Override
    public void reset() {
        this.checksum.reset();
    }

    @Override
    public Pair<String, List<String>> get() {
        return Pair.of(this.checksumName, Collections.singletonList(BinaryUtils.toBase64(this.checksum.getChecksumBytes())));
    }
}

