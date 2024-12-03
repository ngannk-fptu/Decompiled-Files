/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.utils.ToString
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

    public String toString() {
        return ToString.builder((String)"SyncSignedRequest").add("request", (Object)this.request).build();
    }

    @SdkInternalApi
    public static final class BuilderImpl
    extends DefaultBaseSignedRequest.BuilderImpl<SignedRequest.Builder, ContentStreamProvider>
    implements SignedRequest.Builder {
        public SignedRequest build() {
            return new DefaultSignedRequest(this);
        }
    }
}

