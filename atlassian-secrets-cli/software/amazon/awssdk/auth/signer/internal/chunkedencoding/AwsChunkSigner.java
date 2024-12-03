/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.signer.internal.chunkedencoding;

import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public interface AwsChunkSigner {
    public String signChunk(byte[] var1, String var2);

    public String signChecksumChunk(byte[] var1, String var2, String var3);
}

