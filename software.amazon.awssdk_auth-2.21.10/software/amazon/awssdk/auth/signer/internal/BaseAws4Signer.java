/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.checksums.SdkChecksum
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.http.SdkHttpFullRequest$Builder
 */
package software.amazon.awssdk.auth.signer.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.credentials.CredentialUtils;
import software.amazon.awssdk.auth.signer.internal.AbstractAws4Signer;
import software.amazon.awssdk.auth.signer.internal.Aws4SignerRequestParams;
import software.amazon.awssdk.auth.signer.params.Aws4PresignerParams;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.http.SdkHttpFullRequest;

@SdkInternalApi
public abstract class BaseAws4Signer
extends AbstractAws4Signer<Aws4SignerParams, Aws4PresignerParams> {
    public SdkHttpFullRequest sign(SdkHttpFullRequest request, ExecutionAttributes executionAttributes) {
        Aws4SignerParams signingParams = this.extractSignerParams(Aws4SignerParams.builder(), executionAttributes).build();
        return this.sign(request, signingParams);
    }

    public SdkHttpFullRequest sign(SdkHttpFullRequest request, Aws4SignerParams signingParams) {
        if (CredentialUtils.isAnonymous(signingParams.awsCredentials())) {
            return request;
        }
        Aws4SignerRequestParams requestParams = new Aws4SignerRequestParams(signingParams);
        return this.doSign(request, requestParams, signingParams).build();
    }

    public SdkHttpFullRequest presign(SdkHttpFullRequest requestToSign, ExecutionAttributes executionAttributes) {
        Aws4PresignerParams signingParams = this.extractPresignerParams(Aws4PresignerParams.builder(), executionAttributes).build();
        return this.presign(requestToSign, signingParams);
    }

    public SdkHttpFullRequest presign(SdkHttpFullRequest request, Aws4PresignerParams signingParams) {
        if (CredentialUtils.isAnonymous(signingParams.awsCredentials())) {
            return request;
        }
        Aws4SignerRequestParams requestParams = new Aws4SignerRequestParams(signingParams);
        return this.doPresign(request, requestParams, signingParams).build();
    }

    @Override
    protected void processRequestPayload(SdkHttpFullRequest.Builder mutableRequest, byte[] signature, byte[] signingKey, Aws4SignerRequestParams signerRequestParams, Aws4SignerParams signerParams) {
        this.processRequestPayload(mutableRequest, signature, signingKey, signerRequestParams, signerParams, (SdkChecksum)null);
    }

    @Override
    protected void processRequestPayload(SdkHttpFullRequest.Builder mutableRequest, byte[] signature, byte[] signingKey, Aws4SignerRequestParams signerRequestParams, Aws4SignerParams signerParams, SdkChecksum sdkChecksum) {
    }

    @Override
    protected String calculateContentHashPresign(SdkHttpFullRequest.Builder mutableRequest, Aws4PresignerParams signerParams) {
        return this.calculateContentHash(mutableRequest, signerParams);
    }
}

