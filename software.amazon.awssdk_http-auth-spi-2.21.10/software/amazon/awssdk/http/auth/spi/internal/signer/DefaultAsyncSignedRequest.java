/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.ToString
 */
package software.amazon.awssdk.http.auth.spi.internal.signer;

import java.nio.ByteBuffer;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.spi.internal.signer.DefaultBaseSignedRequest;
import software.amazon.awssdk.http.auth.spi.signer.AsyncSignedRequest;
import software.amazon.awssdk.utils.ToString;

@SdkInternalApi
public final class DefaultAsyncSignedRequest
extends DefaultBaseSignedRequest<Publisher<ByteBuffer>>
implements AsyncSignedRequest {
    private DefaultAsyncSignedRequest(BuilderImpl builder) {
        super(builder);
    }

    public String toString() {
        return ToString.builder((String)"AsyncSignedRequest").add("request", (Object)this.request).build();
    }

    @SdkInternalApi
    public static final class BuilderImpl
    extends DefaultBaseSignedRequest.BuilderImpl<AsyncSignedRequest.Builder, Publisher<ByteBuffer>>
    implements AsyncSignedRequest.Builder {
        public AsyncSignedRequest build() {
            return new DefaultAsyncSignedRequest(this);
        }
    }
}

