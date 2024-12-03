/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.identity.spi.Identity
 *  software.amazon.awssdk.utils.ToString
 */
package software.amazon.awssdk.http.auth.spi.internal.signer;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.auth.spi.internal.signer.DefaultBaseSignRequest;
import software.amazon.awssdk.http.auth.spi.signer.SignRequest;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.utils.ToString;

@SdkInternalApi
public final class DefaultSignRequest<IdentityT extends Identity>
extends DefaultBaseSignRequest<ContentStreamProvider, IdentityT>
implements SignRequest<IdentityT> {
    private DefaultSignRequest(BuilderImpl<IdentityT> builder) {
        super(builder);
    }

    public static <IdentityT extends Identity> SignRequest.Builder<IdentityT> builder() {
        return new BuilderImpl();
    }

    public static <IdentityT extends Identity> SignRequest.Builder<IdentityT> builder(IdentityT identity) {
        return new BuilderImpl(identity, null);
    }

    public String toString() {
        return ToString.builder((String)"SignRequest").add("request", (Object)this.request).add("identity", (Object)this.identity).add("properties", (Object)this.properties).build();
    }

    public SignRequest.Builder<IdentityT> toBuilder() {
        return new BuilderImpl(this);
    }

    @SdkInternalApi
    public static final class BuilderImpl<IdentityT extends Identity>
    extends DefaultBaseSignRequest.BuilderImpl<SignRequest.Builder<IdentityT>, ContentStreamProvider, IdentityT>
    implements SignRequest.Builder<IdentityT> {
        private BuilderImpl() {
        }

        private BuilderImpl(IdentityT identity) {
            super(identity);
        }

        private BuilderImpl(DefaultSignRequest<IdentityT> request) {
            this.properties(request.properties);
            this.identity(request.identity);
            this.payload(request.payload);
            this.request(request.request);
        }

        public SignRequest<IdentityT> build() {
            return new DefaultSignRequest(this);
        }

        /* synthetic */ BuilderImpl(Identity x0, 1 x1) {
            this(x0);
        }
    }
}

