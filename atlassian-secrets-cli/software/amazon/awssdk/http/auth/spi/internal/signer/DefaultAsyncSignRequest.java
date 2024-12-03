/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.spi.internal.signer;

import java.nio.ByteBuffer;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.spi.internal.signer.DefaultBaseSignRequest;
import software.amazon.awssdk.http.auth.spi.signer.AsyncSignRequest;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.utils.ToString;

@SdkInternalApi
public final class DefaultAsyncSignRequest<IdentityT extends Identity>
extends DefaultBaseSignRequest<Publisher<ByteBuffer>, IdentityT>
implements AsyncSignRequest<IdentityT> {
    private DefaultAsyncSignRequest(BuilderImpl<IdentityT> builder) {
        super(builder);
    }

    public static <IdentityT extends Identity> AsyncSignRequest.Builder<IdentityT> builder() {
        return new BuilderImpl();
    }

    public static <IdentityT extends Identity> AsyncSignRequest.Builder<IdentityT> builder(IdentityT identity) {
        return new BuilderImpl(identity, null);
    }

    public String toString() {
        return ToString.builder("AsyncSignRequest").add("request", this.request).add("identity", this.identity).add("properties", this.properties).build();
    }

    @Override
    public AsyncSignRequest.Builder<IdentityT> toBuilder() {
        return new BuilderImpl(this);
    }

    @SdkInternalApi
    public static final class BuilderImpl<IdentityT extends Identity>
    extends DefaultBaseSignRequest.BuilderImpl<AsyncSignRequest.Builder<IdentityT>, Publisher<ByteBuffer>, IdentityT>
    implements AsyncSignRequest.Builder<IdentityT> {
        private BuilderImpl() {
        }

        private BuilderImpl(IdentityT identity) {
            super(identity);
        }

        private BuilderImpl(DefaultAsyncSignRequest<IdentityT> request) {
            this.properties(request.properties);
            this.identity(request.identity);
            this.payload(request.payload);
            this.request(request.request);
        }

        @Override
        public AsyncSignRequest<IdentityT> build() {
            return new DefaultAsyncSignRequest(this);
        }

        /* synthetic */ BuilderImpl(Identity x0, 1 x1) {
            this(x0);
        }
    }
}

