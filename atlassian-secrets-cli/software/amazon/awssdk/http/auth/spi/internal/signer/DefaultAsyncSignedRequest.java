/*
 * Decompiled with CFR 0.152.
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

    public static BuilderImpl builder() {
        return new BuilderImpl();
    }

    public String toString() {
        return ToString.builder("AsyncSignedRequest").add("request", this.request).build();
    }

    @Override
    public AsyncSignedRequest.Builder toBuilder() {
        return (AsyncSignedRequest.Builder)((AsyncSignedRequest.Builder)AsyncSignedRequest.builder().request(this.request)).payload(this.payload);
    }

    @SdkInternalApi
    public static final class BuilderImpl
    extends DefaultBaseSignedRequest.BuilderImpl<AsyncSignedRequest.Builder, Publisher<ByteBuffer>>
    implements AsyncSignedRequest.Builder {
        private BuilderImpl() {
        }

        @Override
        public AsyncSignedRequest build() {
            return new DefaultAsyncSignedRequest(this);
        }
    }
}

