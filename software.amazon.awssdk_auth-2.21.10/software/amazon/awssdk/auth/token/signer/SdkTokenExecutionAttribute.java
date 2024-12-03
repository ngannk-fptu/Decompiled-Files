/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.core.SelectedAuthScheme
 *  software.amazon.awssdk.core.interceptor.ExecutionAttribute
 *  software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute
 *  software.amazon.awssdk.http.auth.signer.BearerHttpSigner
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption
 *  software.amazon.awssdk.http.auth.spi.signer.HttpSigner
 *  software.amazon.awssdk.identity.spi.Identity
 *  software.amazon.awssdk.utils.CompletableFutureUtils
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
import software.amazon.awssdk.http.auth.spi.signer.HttpSigner;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.utils.CompletableFutureUtils;

@Deprecated
@SdkProtectedApi
public final class SdkTokenExecutionAttribute {
    @Deprecated
    public static final ExecutionAttribute<SdkToken> SDK_TOKEN = ExecutionAttribute.derivedBuilder((String)"SdkToken", SdkToken.class, (ExecutionAttribute)SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME).readMapping(SdkTokenExecutionAttribute::sdkTokenReadMapping).writeMapping(SdkTokenExecutionAttribute::sdkTokenWriteMapping).build();

    private SdkTokenExecutionAttribute() {
    }

    private static SdkToken sdkTokenReadMapping(SelectedAuthScheme<?> authScheme) {
        if (authScheme == null) {
            return null;
        }
        Identity identity = (Identity)CompletableFutureUtils.joinLikeSync((CompletableFuture)authScheme.identity());
        if (!(identity instanceof SdkToken)) {
            return null;
        }
        return (SdkToken)identity;
    }

    private static <T extends Identity> SelectedAuthScheme<?> sdkTokenWriteMapping(SelectedAuthScheme<T> authScheme, SdkToken token) {
        if (authScheme == null) {
            return new SelectedAuthScheme(CompletableFuture.completedFuture(token), (HttpSigner)BearerHttpSigner.create(), (AuthSchemeOption)AuthSchemeOption.builder().schemeId("smithy.api#httpBearerAuth").build());
        }
        return new SelectedAuthScheme(CompletableFuture.completedFuture(token), authScheme.signer(), authScheme.authSchemeOption());
    }
}

