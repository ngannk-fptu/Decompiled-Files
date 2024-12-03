/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal.nrs;

import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.Objects;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.nrs.StreamedHttpResponse;

@SdkInternalApi
public class DefaultStreamedHttpResponse
extends DefaultHttpResponse
implements StreamedHttpResponse {
    private final Publisher<HttpContent> stream;

    public DefaultStreamedHttpResponse(HttpVersion version, HttpResponseStatus status, Publisher<HttpContent> stream) {
        super(version, status);
        this.stream = stream;
    }

    public DefaultStreamedHttpResponse(HttpVersion version, HttpResponseStatus status, boolean validateHeaders, Publisher<HttpContent> stream) {
        super(version, status, validateHeaders);
        this.stream = stream;
    }

    @Override
    public void subscribe(Subscriber<? super HttpContent> subscriber) {
        this.stream.subscribe(subscriber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        DefaultStreamedHttpResponse that = (DefaultStreamedHttpResponse)o;
        return Objects.equals(this.stream, that.stream);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.stream != null ? this.stream.hashCode() : 0);
        return result;
    }
}

