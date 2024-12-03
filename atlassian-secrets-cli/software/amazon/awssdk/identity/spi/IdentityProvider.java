/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.identity.spi;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.identity.spi.ResolveIdentityRequest;

@SdkPublicApi
@ThreadSafe
public interface IdentityProvider<IdentityT extends Identity> {
    public Class<IdentityT> identityType();

    public CompletableFuture<? extends IdentityT> resolveIdentity(ResolveIdentityRequest var1);

    default public CompletableFuture<? extends IdentityT> resolveIdentity(Consumer<ResolveIdentityRequest.Builder> consumer) {
        return this.resolveIdentity((ResolveIdentityRequest)ResolveIdentityRequest.builder().applyMutation(consumer).build());
    }

    default public CompletableFuture<? extends IdentityT> resolveIdentity() {
        return this.resolveIdentity((ResolveIdentityRequest)ResolveIdentityRequest.builder().build());
    }
}

