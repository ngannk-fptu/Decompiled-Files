/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.SdkAutoCloseable
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding;

import java.io.InputStream;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.ChunkInputStream;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.DefaultChunk;
import software.amazon.awssdk.utils.SdkAutoCloseable;

@SdkInternalApi
public interface Chunk
extends SdkAutoCloseable {
    public static Chunk create(InputStream data, int sizeInBytes) {
        return new DefaultChunk(new ChunkInputStream(data, sizeInBytes));
    }

    public InputStream stream();

    public boolean hasRemaining();
}

