/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.async;

import java.nio.ByteBuffer;
import java.util.Objects;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public final class ResponsePublisher<ResponseT extends SdkResponse>
implements SdkPublisher<ByteBuffer> {
    private final ResponseT response;
    private final SdkPublisher<ByteBuffer> publisher;

    public ResponsePublisher(ResponseT response, SdkPublisher<ByteBuffer> publisher) {
        this.response = (SdkResponse)Validate.paramNotNull(response, "response");
        this.publisher = Validate.paramNotNull(publisher, "publisher");
    }

    public ResponseT response() {
        return this.response;
    }

    @Override
    public void subscribe(Subscriber<? super ByteBuffer> subscriber) {
        this.publisher.subscribe(subscriber);
    }

    public String toString() {
        return ToString.builder("ResponsePublisher").add("response", this.response).add("publisher", this.publisher).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ResponsePublisher that = (ResponsePublisher)o;
        if (!Objects.equals(this.response, that.response)) {
            return false;
        }
        return Objects.equals(this.publisher, that.publisher);
    }

    public int hashCode() {
        int result = this.response != null ? ((SdkResponse)this.response).hashCode() : 0;
        result = 31 * result + (this.publisher != null ? this.publisher.hashCode() : 0);
        return result;
    }
}

