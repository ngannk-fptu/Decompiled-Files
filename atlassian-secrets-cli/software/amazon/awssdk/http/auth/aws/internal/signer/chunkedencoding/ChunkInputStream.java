/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding;

import java.io.IOException;
import java.io.InputStream;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.internal.signer.io.SdkLengthAwareInputStream;

@SdkInternalApi
public final class ChunkInputStream
extends SdkLengthAwareInputStream {
    public ChunkInputStream(InputStream inputStream, long length) {
        super(inputStream, length);
    }

    @Override
    public void close() throws IOException {
        long remaining = this.remaining();
        if (remaining > 0L && this.skip(remaining) < remaining) {
            throw new IOException("Unable to drain stream for chunk. The underlying stream did not allow skipping the whole chunk.");
        }
        super.close();
    }
}

