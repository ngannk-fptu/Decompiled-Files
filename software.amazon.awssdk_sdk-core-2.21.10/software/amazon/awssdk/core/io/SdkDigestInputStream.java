/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.utils.IoUtils
 */
package software.amazon.awssdk.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.internal.io.Releasable;
import software.amazon.awssdk.utils.IoUtils;

@SdkProtectedApi
public class SdkDigestInputStream
extends DigestInputStream
implements Releasable {
    private static final int SKIP_BUF_SIZE = 2048;
    private final SdkChecksum sdkChecksum;

    public SdkDigestInputStream(InputStream stream, MessageDigest digest, SdkChecksum sdkChecksum) {
        super(stream, digest);
        this.sdkChecksum = sdkChecksum;
    }

    public SdkDigestInputStream(InputStream stream, MessageDigest digest) {
        this(stream, digest, null);
    }

    @Override
    public final long skip(long n) throws IOException {
        long m;
        int len;
        if (n <= 0L) {
            return n;
        }
        byte[] b = new byte[(int)Math.min(2048L, n)];
        for (m = n; m > 0L; m -= (long)len) {
            len = this.read(b, 0, (int)Math.min(m, (long)b.length));
            if (len != -1) continue;
            return n - m;
        }
        assert (m == 0L);
        return n;
    }

    @Override
    public final void release() {
        IoUtils.closeQuietly((AutoCloseable)this, null);
        if (this.in instanceof Releasable) {
            Releasable r = (Releasable)((Object)this.in);
            r.release();
        }
    }

    @Override
    public int read() throws IOException {
        int ch = this.in.read();
        if (ch != -1) {
            this.digest.update((byte)ch);
            if (this.sdkChecksum != null) {
                this.sdkChecksum.update((byte)ch);
            }
        }
        return ch;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int result = this.in.read(b, off, len);
        if (result != -1) {
            this.digest.update(b, off, result);
            if (this.sdkChecksum != null) {
                this.sdkChecksum.update(b, off, result);
            }
        }
        return result;
    }
}

