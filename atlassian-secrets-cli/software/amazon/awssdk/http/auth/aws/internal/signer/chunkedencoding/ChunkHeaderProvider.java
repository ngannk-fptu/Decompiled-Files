/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.Resettable;

@FunctionalInterface
@SdkInternalApi
public interface ChunkHeaderProvider
extends Resettable {
    public byte[] get(byte[] var1);
}

