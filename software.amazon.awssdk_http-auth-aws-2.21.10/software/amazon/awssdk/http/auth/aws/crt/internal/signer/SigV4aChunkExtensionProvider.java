/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Pair
 */
package software.amazon.awssdk.http.auth.aws.crt.internal.signer;

import java.nio.charset.StandardCharsets;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.crt.internal.signer.RollingSigner;
import software.amazon.awssdk.http.auth.aws.internal.signer.CredentialScope;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.ChunkExtensionProvider;
import software.amazon.awssdk.utils.Pair;

@SdkInternalApi
public class SigV4aChunkExtensionProvider
implements ChunkExtensionProvider {
    private final RollingSigner signer;
    private final CredentialScope credentialScope;

    public SigV4aChunkExtensionProvider(RollingSigner signer, CredentialScope credentialScope) {
        this.signer = signer;
        this.credentialScope = credentialScope;
    }

    @Override
    public void reset() {
        this.signer.reset();
    }

    @Override
    public Pair<byte[], byte[]> get(byte[] chunk) {
        byte[] chunkSig = this.signer.sign(chunk);
        return Pair.of((Object)"chunk-signature".getBytes(StandardCharsets.UTF_8), (Object)chunkSig);
    }
}

