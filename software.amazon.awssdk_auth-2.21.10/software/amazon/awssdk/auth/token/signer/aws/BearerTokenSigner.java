/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.core.CredentialType
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.signer.Signer
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 */
package software.amazon.awssdk.auth.token.signer.aws;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.signer.params.TokenSignerParams;
import software.amazon.awssdk.auth.token.credentials.SdkToken;
import software.amazon.awssdk.auth.token.signer.SdkTokenExecutionAttribute;
import software.amazon.awssdk.core.CredentialType;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.http.SdkHttpFullRequest;

@SdkPublicApi
public final class BearerTokenSigner
implements Signer {
    private static final String BEARER_LABEL = "Bearer";

    public static BearerTokenSigner create() {
        return new BearerTokenSigner();
    }

    public CredentialType credentialType() {
        return CredentialType.TOKEN;
    }

    public SdkHttpFullRequest sign(SdkHttpFullRequest request, TokenSignerParams signerParams) {
        return this.doSign(request, signerParams);
    }

    public SdkHttpFullRequest sign(SdkHttpFullRequest request, ExecutionAttributes executionAttributes) {
        SdkToken token = (SdkToken)executionAttributes.getAttribute(SdkTokenExecutionAttribute.SDK_TOKEN);
        return this.doSign(request, TokenSignerParams.builder().token(token).build());
    }

    private SdkHttpFullRequest doSign(SdkHttpFullRequest request, TokenSignerParams signerParams) {
        return request.toBuilder().putHeader("Authorization", this.buildAuthorizationHeader(signerParams.token())).build();
    }

    private String buildAuthorizationHeader(SdkToken token) {
        return String.format("%s %s", BEARER_LABEL, token.token());
    }
}

