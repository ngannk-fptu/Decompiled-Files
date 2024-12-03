/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.checksums;

import java.nio.charset.StandardCharsets;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.internal.signer.checksums.SdkChecksum;

@SdkInternalApi
public class ConstantChecksum
implements SdkChecksum {
    private final String value;

    public ConstantChecksum(String value) {
        this.value = value;
    }

    @Override
    public void update(int b) {
    }

    @Override
    public void update(byte[] b, int off, int len) {
    }

    @Override
    public long getValue() {
        throw new UnsupportedOperationException("Use getChecksumBytes() instead.");
    }

    @Override
    public void reset() {
    }

    @Override
    public byte[] getChecksumBytes() {
        return this.value.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void mark(int readLimit) {
    }
}

