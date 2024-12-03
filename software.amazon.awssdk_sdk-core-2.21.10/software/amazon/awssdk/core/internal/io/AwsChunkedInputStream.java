/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Logger
 */
package software.amazon.awssdk.core.internal.io;

import java.io.IOException;
import java.io.InputStream;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.io.ChunkContentIterator;
import software.amazon.awssdk.core.internal.io.UnderlyingStreamBuffer;
import software.amazon.awssdk.core.io.SdkInputStream;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public abstract class AwsChunkedInputStream
extends SdkInputStream {
    public static final int DEFAULT_CHUNK_SIZE = 131072;
    protected static final int SKIP_BUFFER_SIZE = 262144;
    protected static final Logger log = Logger.loggerFor(AwsChunkedInputStream.class);
    protected InputStream is;
    protected ChunkContentIterator currentChunkIterator;
    protected UnderlyingStreamBuffer underlyingStreamBuffer;
    protected boolean isAtStart = true;
    protected boolean isTerminating = false;

    @Override
    public int read() throws IOException {
        byte[] tmp = new byte[1];
        int count = this.read(tmp, 0, 1);
        if (count > 0) {
            log.debug(() -> "One byte read from the stream.");
            int unsignedByte = tmp[0] & 0xFF;
            return unsignedByte;
        }
        return count;
    }

    @Override
    public long skip(long n) throws IOException {
        long remaining;
        int count;
        if (n <= 0L) {
            return 0L;
        }
        int toskip = (int)Math.min(262144L, n);
        byte[] temp = new byte[toskip];
        for (remaining = n; remaining > 0L && (count = this.read(temp, 0, toskip)) >= 0; remaining -= (long)count) {
        }
        return n - remaining;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    protected InputStream getWrappedInputStream() {
        return this.is;
    }
}

