/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.signer;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.signer.internal.BaseAws4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.http.SdkHttpFullRequest;

@SdkPublicApi
public final class Aws4UnsignedPayloadSigner
extends BaseAws4Signer {
    public static final String UNSIGNED_PAYLOAD = "UNSIGNED-PAYLOAD";

    private Aws4UnsignedPayloadSigner() {
    }

    public static Aws4UnsignedPayloadSigner create() {
        return new Aws4UnsignedPayloadSigner();
    }

    @Override
    public SdkHttpFullRequest sign(SdkHttpFullRequest request, ExecutionAttributes executionAttributes) {
        request = this.addContentSha256Header(request);
        return super.sign(request, executionAttributes);
    }

    @Override
    public SdkHttpFullRequest sign(SdkHttpFullRequest request, Aws4SignerParams signingParams) {
        request = this.addContentSha256Header(request);
        return super.sign(request, signingParams);
    }

    @Override
    protected String calculateContentHash(SdkHttpFullRequest.Builder mutableRequest, Aws4SignerParams signerParams, SdkChecksum contentFlexibleChecksum) {
        if ("https".equals(mutableRequest.protocol())) {
            return UNSIGNED_PAYLOAD;
        }
        return super.calculateContentHash(mutableRequest, signerParams, contentFlexibleChecksum);
    }

    private SdkHttpFullRequest addContentSha256Header(SdkHttpFullRequest request) {
        return request.toBuilder().putHeader("x-amz-content-sha256", "required").build();
    }
}

