/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.signer;

import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SelectedAuthScheme;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4FamilyHttpSigner;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption;
import software.amazon.awssdk.http.auth.spi.signer.AsyncSignRequest;
import software.amazon.awssdk.http.auth.spi.signer.AsyncSignedRequest;
import software.amazon.awssdk.http.auth.spi.signer.HttpSigner;
import software.amazon.awssdk.http.auth.spi.signer.SignRequest;
import software.amazon.awssdk.http.auth.spi.signer.SignedRequest;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.utils.CompletableFutureUtils;

@Deprecated
@SdkProtectedApi
public final class S3SignerExecutionAttribute
extends SdkExecutionAttribute {
    @Deprecated
    public static final ExecutionAttribute<Boolean> ENABLE_CHUNKED_ENCODING = ExecutionAttribute.derivedBuilder("ChunkedEncoding", Boolean.class, SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME).readMapping(S3SignerExecutionAttribute::enableChunkedEncodingReadMapping).writeMapping(S3SignerExecutionAttribute::enableChunkedEncodingWriteMapping).build();
    @Deprecated
    public static final ExecutionAttribute<Boolean> ENABLE_PAYLOAD_SIGNING = ExecutionAttribute.derivedBuilder("PayloadSigning", Boolean.class, SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME).readMapping(S3SignerExecutionAttribute::enablePayloadSigningReadMapping).writeMapping(S3SignerExecutionAttribute::enablePayloadSigningWriteMapping).build();

    private S3SignerExecutionAttribute() {
    }

    private static Boolean enableChunkedEncodingReadMapping(SelectedAuthScheme<?> authScheme) {
        if (authScheme == null) {
            return null;
        }
        AuthSchemeOption authOption = authScheme.authSchemeOption();
        return authOption.signerProperty(AwsV4FamilyHttpSigner.CHUNK_ENCODING_ENABLED);
    }

    private static <T extends Identity> SelectedAuthScheme<?> enableChunkedEncodingWriteMapping(SelectedAuthScheme<T> authScheme, Boolean enableChunkedEncoding) {
        if (authScheme == null) {
            return new SelectedAuthScheme<UnsetIdentity>(CompletableFuture.completedFuture(new UnsetIdentity()), new UnsetHttpSigner(), (AuthSchemeOption)AuthSchemeOption.builder().schemeId("unset").putSignerProperty(AwsV4FamilyHttpSigner.CHUNK_ENCODING_ENABLED, enableChunkedEncoding).build());
        }
        return new SelectedAuthScheme<T>(authScheme.identity(), authScheme.signer(), (AuthSchemeOption)authScheme.authSchemeOption().copy(o -> o.putSignerProperty(AwsV4FamilyHttpSigner.CHUNK_ENCODING_ENABLED, enableChunkedEncoding)));
    }

    private static Boolean enablePayloadSigningReadMapping(SelectedAuthScheme<?> authScheme) {
        if (authScheme == null) {
            return null;
        }
        return authScheme.authSchemeOption().signerProperty(AwsV4FamilyHttpSigner.PAYLOAD_SIGNING_ENABLED);
    }

    private static <T extends Identity> SelectedAuthScheme<?> enablePayloadSigningWriteMapping(SelectedAuthScheme<T> authScheme, Boolean payloadSigningEnabled) {
        if (authScheme == null) {
            return new SelectedAuthScheme<UnsetIdentity>(CompletableFuture.completedFuture(new UnsetIdentity()), new UnsetHttpSigner(), (AuthSchemeOption)AuthSchemeOption.builder().schemeId("unset").putSignerProperty(AwsV4FamilyHttpSigner.PAYLOAD_SIGNING_ENABLED, payloadSigningEnabled).build());
        }
        return new SelectedAuthScheme<T>(authScheme.identity(), authScheme.signer(), (AuthSchemeOption)authScheme.authSchemeOption().copy(o -> o.putSignerProperty(AwsV4FamilyHttpSigner.PAYLOAD_SIGNING_ENABLED, payloadSigningEnabled)));
    }

    private static class UnsetHttpSigner
    implements HttpSigner<UnsetIdentity> {
        private UnsetHttpSigner() {
        }

        @Override
        public SignedRequest sign(SignRequest<? extends UnsetIdentity> request) {
            throw new IllegalStateException("A signer was not configured.");
        }

        @Override
        public CompletableFuture<AsyncSignedRequest> signAsync(AsyncSignRequest<? extends UnsetIdentity> request) {
            return CompletableFutureUtils.failedFuture(new IllegalStateException("A signer was not configured."));
        }
    }

    private static class UnsetIdentity
    implements Identity {
        private UnsetIdentity() {
        }
    }
}

