/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.checksums;

import java.security.MessageDigest;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.internal.signer.checksums.SdkChecksum;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.DigestAlgorithm;

@SdkInternalApi
public class Sha1Checksum
implements SdkChecksum {
    private MessageDigest digest = this.getDigest();
    private MessageDigest digestLastMarked;

    @Override
    public void update(int b) {
        this.digest.update((byte)b);
    }

    @Override
    public void update(byte[] b, int off, int len) {
        this.digest.update(b, off, len);
    }

    @Override
    public long getValue() {
        throw new UnsupportedOperationException("Use getChecksumBytes() instead.");
    }

    @Override
    public void reset() {
        this.digest = this.digestLastMarked == null ? this.getDigest() : this.cloneFrom(this.digestLastMarked);
    }

    private MessageDigest getDigest() {
        return DigestAlgorithm.SHA1.getDigest();
    }

    @Override
    public byte[] getChecksumBytes() {
        return this.digest.digest();
    }

    @Override
    public void mark(int readLimit) {
        this.digestLastMarked = this.cloneFrom(this.digest);
    }

    private MessageDigest cloneFrom(MessageDigest from) {
        try {
            return (MessageDigest)from.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("unexpected", e);
        }
    }
}

