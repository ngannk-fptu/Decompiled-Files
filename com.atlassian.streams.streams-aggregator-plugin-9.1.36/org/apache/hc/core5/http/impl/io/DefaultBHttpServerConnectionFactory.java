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
import org.apache.hc.core5.http.impl.io.DefaultBHttpServerConnection;
import org.apache.hc.core5.http.io.HttpConnectionFactory;
import org.apache.hc.core5.http.io.HttpMessageParserFactory;
import org.apache.hc.core5.http.io.HttpMessageWriterFactory;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultBHttpServerConnectionFactory
implements HttpConnectionFactory<DefaultBHttpServerConnection> {
    private final String scheme;
    private final Http1Config http1Config;
    private final CharCodingConfig charCodingConfig;
    private final ContentLengthStrategy incomingContentStrategy;
    private final ContentLengthStrategy outgoingContentStrategy;
    private final HttpMessageParserFactory<ClassicHttpRequest> requestParserFactory;
    private final HttpMessageWriterFactory<ClassicHttpResponse> responseWriterFactory;

    public DefaultBHttpServerConnectionFactory(String scheme, Http1Config http1Config, CharCodingConfig charCodingConfig, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, HttpMessageParserFactory<ClassicHttpRequest> requestParserFactory, HttpMessageWriterFactory<ClassicHttpResponse> responseWriterFactory) {
        this.scheme = scheme;
        this.http1Config = http1Config != null ? http1Config : Http1Config.DEFAULT;
        this.charCodingConfig = charCodingConfig != null ? charCodingConfig : CharCodingConfig.DEFAULT;
        this.incomingContentStrategy = incomingContentStrategy;
        this.outgoingContentStrategy = outgoingContentStrategy;
        this.requestParserFactory = requestParserFactory;
        this.responseWriterFactory = responseWriterFactory;
    }

    public DefaultBHttpServerConnectionFactory(String scheme, Http1Config http1Config, CharCodingConfig charCodingConfig, HttpMessageParserFactory<ClassicHttpRequest> requestParserFactory, HttpMessageWriterFactory<ClassicHttpResponse> responseWriterFactory) {
        this(scheme, http1Config, charCodingConfig, null, null, requestParserFactory, responseWriterFactory);
    }

    public DefaultBHttpServerConnectionFactory(String scheme, Http1Config http1Config, CharCodingConfig charCodingConfig) {
        this(scheme, http1Config, charCodingConfig, null, null, null, null);
    }

    @Override
    public DefaultBHttpServerConnection createConnection(Socket socket) throws IOException {
        DefaultBHttpServerConnection conn = new DefaultBHttpServerConnection(this.scheme, this.http1Config, CharCodingSupport.createDecoder(this.charCodingConfig), CharCodingSupport.createEncoder(this.charCodingConfig), this.incomingContentStrategy, this.outgoingContentStrategy, this.requestParserFactory, this.responseWriterFactory);
        conn.bind(socket);
        return conn;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String scheme;
        private Http1Config http1Config;
        private CharCodingConfig charCodingConfig;
        private ContentLengthStrategy incomingContentLengthStrategy;
        private ContentLengthStrategy outgoingContentLengthStrategy;
        private HttpMessageParserFactory<ClassicHttpRequest> requestParserFactory;
        private HttpMessageWriterFactory<ClassicHttpResponse> responseWriterFactory;

        private Builder() {
        }

        public Builder scheme(String scheme) {
            this.scheme = scheme;
            return this;
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

        public Builder requestParserFactory(HttpMessageParserFactory<ClassicHttpRequest> requestParserFactory) {
            this.requestParserFactory = requestParserFactory;
            return this;
        }

        public Builder responseWriterFactory(HttpMessageWriterFactory<ClassicHttpResponse> responseWriterFactory) {
            this.responseWriterFactory = responseWriterFactory;
            return this;
        }

        public DefaultBHttpServerConnectionFactory build() {
            return new DefaultBHttpServerConnectionFactory(this.scheme, this.http1Config, this.charCodingConfig, this.incomingContentLengthStrategy, this.outgoingContentLengthStrategy, this.requestParserFactory, this.responseWriterFactory);
        }
    }
}

