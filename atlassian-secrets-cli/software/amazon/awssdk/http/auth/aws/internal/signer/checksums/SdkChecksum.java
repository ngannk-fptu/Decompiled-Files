/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.checksums;

import java.nio.ByteBuffer;
import java.util.zip.Checksum;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public interface SdkChecksum
extends Checksum {
    public byte[] getChecksumBytes();

    public void mark(int var1);

    @Override
    default public void update(byte[] b) {
        this.update(b, 0, b.length);
    }

    @Override
    default public void update(ByteBuffer buffer) {
        int pos = buffer.position();
        int limit = buffer.limit();
        int rem = limit - pos;
        if (rem <= 0) {
            return;
        }
        if (buffer.hasArray()) {
            this.update(buffer.array(), pos + buffer.arrayOffset(), rem);
        } else {
            byte[] b = new byte[Math.min(buffer.remaining(), 4096)];
            while (buffer.hasRemaining()) {
                int length = Math.min(buffer.remaining(), b.length);
                buffer.get(b, 0, length);
                this.update(b, 0, length);
            }
        }
        buffer.position(limit);
    }
}

