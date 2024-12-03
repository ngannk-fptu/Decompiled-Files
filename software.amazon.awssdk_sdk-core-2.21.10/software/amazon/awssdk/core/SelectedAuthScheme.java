/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption
 *  software.amazon.awssdk.http.auth.spi.signer.HttpSigner
 *  software.amazon.awssdk.identity.spi.Identity
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.core;

import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption;
import software.amazon.awssdk.http.auth.spi.signer.HttpSigner;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public final class SelectedAuthScheme<T extends Identity> {
    private final CompletableFuture<? extends T> identity;
    private final HttpSigner<T> signer;
    private final AuthSchemeOption authSchemeOption;

    public SelectedAuthScheme(CompletableFuture<? extends T> identity, HttpSigner<T> signer, AuthSchemeOption authSchemeOption) {
        this.identity = (CompletableFuture)Validate.paramNotNull(identity, (String)"identity");
        this.signer = (HttpSigner)Validate.paramNotNull(signer, (String)"signer");
        this.authSchemeOption = (AuthSchemeOption)Validate.paramNotNull((Object)authSchemeOption, (String)"authSchemeOption");
    }

    public CompletableFuture<? extends T> identity() {
        return this.identity;
    }

    public HttpSigner<T> signer() {
        return this.signer;
    }

    public AuthSchemeOption authSchemeOption() {
        return this.authSchemeOption;
    }
}

