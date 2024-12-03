/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.impl.io;

import java.io.IOException;
import java.net.Socket;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentLengthStrategy;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http.impl.CharCodingSupport;
import org.apache.hc.core5.http.impl.io.DefaultBHttpClientConnection;
import org.apache.hc.core5.http.io.HttpConnectionFactory;
import org.apache.hc.core5.http.io.HttpMessageParserFactory;
import org.apache.hc.core5.http.io.HttpMessageWriterFactory;
import org.apache.hc.core5.http.io.ResponseOutOfOrderStrategy;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultBHttpClientConnectionFactory
implements HttpConnectionFactory<DefaultBHttpClientConnection> {
    private final Http1Config http1Config;
    private final CharCodingConfig charCodingConfig;
    private final ContentLengthStrategy incomingContentStrategy;
    private final ContentLengthStrategy outgoingContentStrategy;
    private final ResponseOutOfOrderStrategy responseOutOfOrderStrategy;
    private final HttpMessageWriterFactory<ClassicHttpRequest> requestWriterFactory;
    private final HttpMessageParserFactory<ClassicHttpResponse> responseParserFactory;

    private DefaultBHttpClientConnectionFactory(Http1Config http1Config, CharCodingConfig charCodingConfig, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, ResponseOutOfOrderStrategy responseOutOfOrderStrategy, HttpMessageWriterFactory<ClassicHttpRequest> requestWriterFactory, HttpMessageParserFactory<ClassicHttpResponse> responseParserFactory) {
        this.http1Config = http1Config != null ? http1Config : Http1Config.DEFAULT;
        this.charCodingConfig = charCodingConfig != null ? charCodingConfig : CharCodingConfig.DEFAULT;
        this.incomingContentStrategy = incomingContentStrategy;
        this.outgoingContentStrategy = outgoingContentStrategy;
        this.responseOutOfOrderStrategy = responseOutOfOrderStrategy;
        this.requestWriterFactory = requestWriterFactory;
        this.responseParserFactory = responseParserFactory;
    }

    public DefaultBHttpClientConnectionFactory(Http1Config http1Config, CharCodingConfig charCodingConfig, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, HttpMessageWriterFactory<ClassicHttpRequest> requestWriterFactory, HttpMessageParserFactory<ClassicHttpResponse> responseParserFactory) {
        this(http1Config, charCodingConfig, incomingContentStrategy, outgoingContentStrategy, null, requestWriterFactory, responseParserFactory);
    }

    public DefaultBHttpClientConnectionFactory(Http1Config http1Config, CharCodingConfig charCodingConfig, HttpMessageWriterFactory<ClassicHttpRequest> requestWriterFactory, HttpMessageParserFactory<ClassicHttpResponse> responseParserFactory) {
        this(http1Config, charCodingConfig, null, null, requestWriterFactory, responseParserFactory);
    }

    public DefaultBHttpClientConnectionFactory(Http1Config http1Config, CharCodingConfig charCodingConfig) {
        this(http1Config, charCodingConfig, null, null, null, null);
    }

    public DefaultBHttpClientConnectionFactory() {
        this(null, null, null, null, null, null);
    }

    @Override
    public DefaultBHttpClientConnection createConnection(Socket socket) throws IOException {
        DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(this.http1Config, CharCodingSupport.createDecoder(this.charCodingConfig), CharCodingSupport.createEncoder(this.charCodingConfig), this.incomingContentStrategy, this.outgoingContentStrategy, this.responseOutOfOrderStrategy, this.requestWriterFactory, this.responseParserFactory);
        conn.bind(socket);
        return conn;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Http1Config http1Config;
        private CharCodingConfig charCodingConfig;
        private ContentLengthStrategy incomingContentLengthStrategy;
        private ContentLengthStrategy outgoingContentLengthStrategy;
        private ResponseOutOfOrderStrategy responseOutOfOrderStrategy;
        private HttpMessageWriterFactory<ClassicHttpRequest> requestWriterFactory;
        private HttpMessageParserFactory<ClassicHttpResponse> responseParserFactory;

        private Builder() {
        }

        public Builder http1Config(Http1Config http1Config) {
            this.http1Config = http1Config;
            return this;
        }

        public Builder charCodingConfig(CharCodingConfig charCodingConfig) {
            this.charCodingConfig = charCodingConfig;
            return this;
        }

        public Builder incomingContentLengthStrategy(ContentLengthStrategy incomingContentLengthStrategy) {
            this.incomingContentLengthStrategy = incomingContentLengthStrategy;
            return this;
        }

        public Builder outgoingContentLengthStrategy(ContentLengthStrategy outgoingContentLengthStrategy) {
            this.outgoingContentLengthStrategy = outgoingContentLengthStrategy;
            return this;
        }

        public Builder responseOutOfOrderStrategy(ResponseOutOfOrderStrategy responseOutOfOrderStrategy) {
            this.responseOutOfOrderStrategy = responseOutOfOrderStrategy;
            return this;
        }

        public Builder requestWriterFactory(HttpMessageWriterFactory<ClassicHttpRequest> requestWriterFactory) {
            this.requestWriterFactory = requestWriterFactory;
            return this;
        }

        public Builder responseParserFactory(HttpMessageParserFactory<ClassicHttpResponse> responseParserFactory) {
            this.responseParserFactory = responseParserFactory;
            return this;
        }

        public DefaultBHttpClientConnectionFactory build() {
            return new DefaultBHttpClientConnectionFactory(this.http1Config, this.charCodingConfig, this.incomingContentLengthStrategy, this.outgoingContentLengthStrategy, this.responseOutOfOrderStrategy, this.requestWriterFactory, this.responseParserFactory);
        }
    }
}

