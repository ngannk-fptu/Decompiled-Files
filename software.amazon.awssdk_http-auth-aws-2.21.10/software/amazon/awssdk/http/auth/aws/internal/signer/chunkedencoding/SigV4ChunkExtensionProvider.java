/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.BinaryUtils
 *  software.amazon.awssdk.utils.Pair
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding;

import java.nio.charset.StandardCharsets;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.internal.signer.CredentialScope;
import software.amazon.awssdk.http.auth.aws.internal.signer.RollingSigner;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.ChunkExtensionProvider;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.SignerUtils;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.Pair;

@SdkInternalApi
public class SigV4ChunkExtensionProvider
implements ChunkExtensionProvider {
    private static final String EMPTY_HASH = BinaryUtils.toHex((byte[])SignerUtils.hash(""));
    private final RollingSigner signer;
    private final CredentialScope credentialScope;

    public SigV4ChunkExtensionProvider(RollingSigner signer, CredentialScope credentialScope) {
        this.signer = signer;
        this.credentialScope = credentialScope;
    }

    @Override
    public void reset() {
        this.signer.reset();
    }

    private String getStringToSign(String previousSignature, byte[] chunk) {
        return String.join((CharSequence)"\n", "AWS4-HMAC-SHA256-PAYLOAD", this.credentialScope.getDatetime(), this.credentialScope.scope(), previousSignature, EMPTY_HASH, BinaryUtils.toHex((byte[])SignerUtils.hash(chunk)));
    }

    @Override
    public Pair<byte[], byte[]> get(byte[] chunk) {
        String chunkSig = this.signer.sign(previousSig -> this.getStringToSign((String)previousSig, chunk));
        return Pair.of((Object)"chunk-signature".getBytes(StandardCharsets.UTF_8), (Object)chunkSig.getBytes(StandardCharsets.UTF_8));
    }
}

