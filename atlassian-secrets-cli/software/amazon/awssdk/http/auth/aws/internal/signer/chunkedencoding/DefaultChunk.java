/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.Chunk;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.ChunkInputStream;
import software.amazon.awssdk.utils.FunctionalUtils;

@SdkInternalApi
final class DefaultChunk
implements Chunk {
    private final ChunkInputStream data;

    DefaultChunk(ChunkInputStream data) {
        this.data = data;
    }

    @Override
    public boolean hasRemaining() {
        return this.data.remaining() > 0L;
    }

    @Override
    public ChunkInputStream stream() {
        return this.data;
    }

    @Override
    public void close() {
        FunctionalUtils.invokeSafely(this.data::close);
    }
}

