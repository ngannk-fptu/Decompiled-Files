/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.token.signer;

import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.auth.token.credentials.SdkToken;
import software.amazon.awssdk.core.SelectedAuthScheme;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.http.auth.signer.BearerHttpSigner;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.identity.spi.TokenIdentity;
import software.amazon.awssdk.utils.CompletableFutureUtils;

@Deprecated
@SdkProtectedApi
public final class SdkTokenExecutionAttribute {
    @Deprecated
    public static final ExecutionAttribute<SdkToken> SDK_TOKEN = ExecutionAttribute.derivedBuilder("SdkToken", SdkToken.class, SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME).readMapping(SdkTokenExecutionAttribute::sdkTokenReadMapping).writeMapping(SdkTokenExecutionAttribute::sdkTokenWriteMapping).build();

    private SdkTokenExecutionAttribute() {
    }

    private static SdkToken sdkTokenReadMapping(SelectedAuthScheme<?> authScheme) {
        if (authScheme == null) {
            return null;
        }
        Identity identity = (Identity)CompletableFutureUtils.joinLikeSync(authScheme.identity());
        if (!(identity instanceof SdkToken)) {
            return null;
        }
        return (SdkToken)identity;
    }

    private static <T extends Identity> SelectedAuthScheme<?> sdkTokenWriteMapping(SelectedAuthScheme<T> authScheme, SdkToken token) {
        if (authScheme == null) {
            return new SelectedAuthScheme<TokenIdentity>(CompletableFuture.completedFuture(token), BearerHttpSigner.create(), (AuthSchemeOption)AuthSchemeOption.builder().schemeId("smithy.api#httpBearerAuth").build());
        }
        return new SelectedAuthScheme<SdkToken>(CompletableFuture.completedFuture(token), authScheme.signer(), authScheme.authSchemeOption());
    }
}

