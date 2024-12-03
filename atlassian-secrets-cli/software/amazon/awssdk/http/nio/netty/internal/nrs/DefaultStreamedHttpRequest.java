/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal.nrs;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import java.util.Objects;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.nrs.StreamedHttpRequest;

@SdkInternalApi
public class DefaultStreamedHttpRequest
extends DefaultHttpRequest
implements StreamedHttpRequest {
    private final Publisher<HttpContent> stream;

    public DefaultStreamedHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, Publisher<HttpContent> stream) {
        super(httpVersion, method, uri);
        this.stream = stream;
    }

    public DefaultStreamedHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, boolean validateHeaders, Publisher<HttpContent> stream) {
        super(httpVersion, method, uri, validateHeaders);
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
        DefaultStreamedHttpRequest that = (DefaultStreamedHttpRequest)o;
        return Objects.equals(this.stream, that.stream);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.stream != null ? this.stream.hashCode() : 0);
        return result;
    }
}

