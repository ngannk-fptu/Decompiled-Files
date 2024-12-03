/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.core.async.AsyncRequestBody
 *  software.amazon.awssdk.core.checksums.Algorithm
 *  software.amazon.awssdk.core.checksums.SdkChecksum
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.signer.AsyncSigner
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.http.SdkHttpFullRequest$Builder
 *  software.amazon.awssdk.utils.BinaryUtils
 *  software.amazon.awssdk.utils.CompletableFutureUtils
 */
package software.amazon.awssdk.auth.signer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.credentials.CredentialUtils;
import software.amazon.awssdk.auth.signer.internal.Aws4SignerRequestParams;
import software.amazon.awssdk.auth.signer.internal.BaseAws4Signer;
import software.amazon.awssdk.auth.signer.internal.ContentChecksum;
import software.amazon.awssdk.auth.signer.internal.DigestComputingSubscriber;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.checksums.Algorithm;
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
        requestBody.subscribe((Subscriber)bodyDigester);
        CompletableFuture<byte[]> digestBytes = bodyDigester.digestBytes();
        CompletionStage signedReqFuture = digestBytes.thenApply(bodyHash -> {
            String digestHex = BinaryUtils.toHex((byte[])bodyHash);
            Aws4SignerRequestParams requestParams = new Aws4SignerRequestParams(signingParams);
            SdkHttpFullRequest.Builder builder = this.doSign(request, requestParams, signingParams, new ContentChecksum(digestHex, sdkChecksum));
            return builder.build();
        });
        return CompletableFutureUtils.forwardExceptionTo((CompletableFuture)signedReqFuture, digestBytes);
    }

    private SdkChecksum createSdkChecksumFromParams(Aws4SignerParams signingParams) {
        if (signingParams.checksumParams() != null) {
            return SdkChecksum.forAlgorithm((Algorithm)signingParams.checksumParams().algorithm());
        }
        return null;
    }

    public static AsyncAws4Signer create() {
        return new AsyncAws4Signer();
    }
}

