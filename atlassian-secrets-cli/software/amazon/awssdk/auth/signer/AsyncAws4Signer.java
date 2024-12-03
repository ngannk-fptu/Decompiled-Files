/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.signer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.credentials.CredentialUtils;
import software.amazon.awssdk.auth.signer.internal.Aws4SignerRequestParams;
import software.amazon.awssdk.auth.signer.internal.BaseAws4Signer;
import software.amazon.awssdk.auth.signer.internal.ContentChecksum;
import software.amazon.awssdk.auth.signer.internal.DigestComputingSubscriber;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.signer.AsyncSigner;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.CompletableFutureUtils;

@SdkPublicApi
public final class AsyncAws4Signer
extends BaseAws4Signer
implements AsyncSigner {
    @Override
    public CompletableFuture<SdkHttpFullRequest> sign(SdkHttpFullRequest request, AsyncRequestBody requestBody, ExecutionAttributes executionAttributes) {
        Aws4SignerParams signingParams = this.extractSignerParams(Aws4SignerParams.builder(), executionAttributes).build();
        return this.signWithBody(request, requestBody, signingParams);
    }

    public CompletableFuture<SdkHttpFullRequest> signWithBody(SdkHttpFullRequest request, AsyncRequestBody requestBody, Aws4SignerParams signingParams) {
        if (CredentialUtils.isAnonymous(signingParams.awsCredentials())) {
            return CompletableFuture.completedFuture(request);
        }
        SdkChecksum sdkChecksum = this.createSdkChecksumFromParams(signingParams);
        DigestComputingSubscriber bodyDigester = sdkChecksum != null ? DigestComputingSubscriber.forSha256(sdkChecksum) : DigestComputingSubscriber.forSha256();
        requestBody.subscribe(bodyDigester);
        CompletableFuture<byte[]> digestBytes = bodyDigester.digestBytes();
        CompletionStage signedReqFuture = digestBytes.thenApply(bodyHash -> {
            String digestHex = BinaryUtils.toHex(bodyHash);
            Aws4SignerRequestParams requestParams = new Aws4SignerRequestParams(signingParams);
            SdkHttpFullRequest.Builder builder = this.doSign(request, requestParams, signingParams, new ContentChecksum(digestHex, sdkChecksum));
            return builder.build();
        });
        return CompletableFutureUtils.forwardExceptionTo(signedReqFuture, digestBytes);
    }

    private SdkChecksum createSdkChecksumFromParams(Aws4SignerParams signingParams) {
        if (signingParams.checksumParams() != null) {
            return SdkChecksum.forAlgorithm(signingParams.checksumParams().algorithm());
        }
        return null;
    }

    public static AsyncAws4Signer create() {
        return new AsyncAws4Signer();
    }
}

