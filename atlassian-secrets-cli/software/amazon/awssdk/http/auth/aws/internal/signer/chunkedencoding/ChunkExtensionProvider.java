/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.Resettable;
import software.amazon.awssdk.utils.Pair;

@FunctionalInterface
@SdkInternalApi
public interface ChunkExtensionProvider
extends Resettable {
    public Pair<byte[], byte[]> get(byte[] var1);
}

