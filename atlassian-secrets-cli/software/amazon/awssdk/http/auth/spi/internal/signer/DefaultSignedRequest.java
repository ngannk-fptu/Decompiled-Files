/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.spi.internal.signer;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.auth.spi.internal.signer.DefaultBaseSignedRequest;
import software.amazon.awssdk.http.auth.spi.signer.SignedRequest;
import software.amazon.awssdk.utils.ToString;

@SdkInternalApi
public final class DefaultSignedRequest
extends DefaultBaseSignedRequest<ContentStreamProvider>
implements SignedRequest {
    private DefaultSignedRequest(BuilderImpl builder) {
        super(builder);
    }

    public static BuilderImpl builder() {
        return new BuilderImpl();
    }

    public String toString() {
        return ToString.builder("SyncSignedRequest").add("request", this.request).build();
    }

    @Override
    public SignedRequest.Builder toBuilder() {
        return (SignedRequest.Builder)((SignedRequest.Builder)SignedRequest.builder().request(this.request)).payload(this.payload);
    }

    @SdkInternalApi
    public static final class BuilderImpl
    extends DefaultBaseSignedRequest.BuilderImpl<SignedRequest.Builder, ContentStreamProvider>
    implements SignedRequest.Builder {
        private BuilderImpl() {
        }

        @Override
        public SignedRequest build() {
            return new DefaultSignedRequest(this);
        }
    }
}

