/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.impl.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentLengthStrategy;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.LengthRequiredException;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.UnsupportedHttpVersionException;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http.impl.DefaultContentLengthStrategy;
import org.apache.hc.core5.http.impl.io.BHttpConnectionBase;
import org.apache.hc.core5.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.hc.core5.http.impl.io.DefaultHttpResponseParserFactory;
import org.apache.hc.core5.http.impl.io.NoResponseOutOfOrderStrategy;
import org.apache.hc.core5.http.impl.io.ResponseOutOfOrderException;
import org.apache.hc.core5.http.impl.io.SocketHolder;
import org.apache.hc.core5.http.io.HttpClientConnection;
import org.apache.hc.core5.http.io.HttpMessageParser;
import org.apache.hc.core5.http.io.HttpMessageParserFactory;
import org.apache.hc.core5.http.io.HttpMessageWriter;
import org.apache.hc.core5.http.io.HttpMessageWriterFactory;
import org.apache.hc.core5.http.io.ResponseOutOfOrderStrategy;
import org.apache.hc.core5.http.message.BasicTokenIterator;
import org.apache.hc.core5.util.Args;

public class DefaultBHttpClientConnection
extends BHttpConnectionBase
implements HttpClientConnection {
    private final HttpMessageParser<ClassicHttpResponse> responseParser;
    private final HttpMessageWriter<ClassicHttpRequest> requestWriter;
    private final ContentLengthStrategy incomingContentStrategy;
    private final ContentLengthStrategy outgoingContentStrategy;
    private final ResponseOutOfOrderStrategy responseOutOfOrderStrategy;
    private volatile boolean consistent;

    public DefaultBHttpClientConnection(Http1Config http1Config, CharsetDecoder charDecoder, CharsetEncoder charEncoder, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, ResponseOutOfOrderStrategy responseOutOfOrderStrategy, HttpMessageWriterFactory<ClassicHttpRequest> requestWriterFactory, HttpMessageParserFactory<ClassicHttpResponse> responseParserFactory) {
        super(http1Config, charDecoder, charEncoder);
        this.requestWriter = (requestWriterFactory != null ? requestWriterFactory : DefaultHttpRequestWriterFactory.INSTANCE).create();
        this.responseParser = (responseParserFactory != null ? responseParserFactory : DefaultHttpResponseParserFactory.INSTANCE).create(http1Config);
        this.incomingContentStrategy = incomingContentStrategy != null ? incomingContentStrategy : DefaultContentLengthStrategy.INSTANCE;
        this.outgoingContentStrategy = outgoingContentStrategy != null ? outgoingContentStrategy : DefaultContentLengthStrategy.INSTANCE;
        this.responseOutOfOrderStrategy = responseOutOfOrderStrategy != null ? responseOutOfOrderStrategy : NoResponseOutOfOrderStrategy.INSTANCE;
        this.consistent = true;
    }

    public DefaultBHttpClientConnection(Http1Config http1Config, CharsetDecoder charDecoder, CharsetEncoder charEncoder, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, HttpMessageWriterFactory<ClassicHttpRequest> requestWriterFactory, HttpMessageParserFactory<ClassicHttpResponse> responseParserFactory) {
        this(http1Config, charDecoder, charEncoder, incomingContentStrategy, outgoingContentStrategy, null, requestWriterFactory, responseParserFactory);
    }

    public DefaultBHttpClientConnection(Http1Config http1Config, CharsetDecoder charDecoder, CharsetEncoder charEncoder) {
        this(http1Config, charDecoder, charEncoder, null, null, null, null);
    }

    public DefaultBHttpClientConnection(Http1Config http1Config) {
        this(http1Config, null, null);
    }

    protected void onResponseReceived(ClassicHttpResponse response) {
    }

    protected void onRequestSubmitted(ClassicHttpRequest request) {
    }

    @Override
    public void bind(Socket socket) throws IOException {
        super.bind(socket);
    }

    @Override
    public void sendRequestHeader(ClassicHttpRequest request) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        SocketHolder socketHolder = this.ensureOpen();
        this.requestWriter.write(request, this.outbuffer, socketHolder.getOutputStream());
        this.onRequestSubmitted(request);
        this.incrementRequestCount();
    }

    @Override
    public void sendRequestEntity(final ClassicHttpRequest request) throws HttpException, IOException {
        block16: {
            Args.notNull(request, "HTTP request");
            final SocketHolder socketHolder = this.ensureOpen();
            HttpEntity entity = request.getEntity();
            if (entity == null) {
                return;
            }
            long len = this.outgoingContentStrategy.determineLength(request);
            if (len == -9223372036854775807L) {
                throw new LengthRequiredException();
            }
            try (OutputStream outStream = this.createContentOutputStream(len, this.outbuffer, new OutputStream(){
                final OutputStream socketOutputStream;
                final InputStream socketInputStream;
                long totalBytes;
                {
                    this.socketOutputStream = socketHolder.getOutputStream();
                    this.socketInputStream = socketHolder.getInputStream();
                }

                void checkForEarlyResponse(long totalBytesSent, int nextWriteSize) throws IOException {
                    if (DefaultBHttpClientConnection.this.responseOutOfOrderStrategy.isEarlyResponseDetected(request, DefaultBHttpClientConnection.this, this.socketInputStream, totalBytesSent, nextWriteSize)) {
                        throw new ResponseOutOfOrderException();
                    }
                }

                @Override
                public void write(byte[] b) throws IOException {
                    this.checkForEarlyResponse(this.totalBytes, b.length);
                    this.totalBytes += (long)b.length;
                    this.socketOutputStream.write(b);
                }

                @Override
                public void write(byte[] b, int off, int len) throws IOException {
                    this.checkForEarlyResponse(this.totalBytes, len);
                    this.totalBytes += (long)len;
                    this.socketOutputStream.write(b, off, len);
                }

                @Override
                public void write(int b) throws IOException {
                    this.checkForEarlyResponse(this.totalBytes, 1);
                    ++this.totalBytes;
                    this.socketOutputStream.write(b);
                }

                @Override
                public void flush() throws IOException {
                    this.socketOutputStream.flush();
                }

                @Override
                public void close() throws IOException {
                    this.socketOutputStream.close();
                }
            }, entity.getTrailers());){
                entity.writeTo(outStream);
            }
            catch (ResponseOutOfOrderException ex) {
                if (len <= 0L) break block16;
                this.consistent = false;
            }
        }
    }

    @Override
    public boolean isConsistent() {
        return this.consistent;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void terminateRequest(ClassicHttpRequest request) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        SocketHolder socketHolder = this.ensureOpen();
        HttpEntity entity = request.getEntity();
        if (entity == null) {
            return;
        }
        BasicTokenIterator ti = new BasicTokenIterator(request.headerIterator("Connection"));
        while (ti.hasNext()) {
            String token = (String)ti.next();
            if (!"close".equalsIgnoreCase(token)) continue;
            this.consistent = false;
            return;
        }
        long len = this.outgoingContentStrategy.determineLength(request);
        if (len == -1L) {
            OutputStream outStream = this.createContentOutputStream(len, this.outbuffer, socketHolder.getOutputStream(), entity.getTrailers());
            Throwable throwable = null;
            if (outStream != null) {
                if (throwable != null) {
                    try {
                        outStream.close();
                    }
                    catch (Throwable x2) {
                        throwable.addSuppressed(x2);
                    }
                } else {
                    outStream.close();
                }
            }
        } else if (len >= 0L && len <= 1024L) {
            try (OutputStream outStream = this.createContentOutputStream(len, this.outbuffer, socketHolder.getOutputStream(), null);){
                entity.writeTo(outStream);
            }
        } else {
            this.consistent = false;
        }
    }

    @Override
    public ClassicHttpResponse receiveResponseHeader() throws HttpException, IOException {
        SocketHolder socketHolder = this.ensureOpen();
        ClassicHttpResponse response = this.responseParser.parse(this.inBuffer, socketHolder.getInputStream());
        ProtocolVersion transportVersion = response.getVersion();
        if (transportVersion != null && transportVersion.greaterEquals(HttpVersion.HTTP_2)) {
            throw new UnsupportedHttpVersionException(transportVersion);
        }
        this.version = transportVersion;
        this.onResponseReceived(response);
        int status = response.getCode();
        if (status < 100) {
            throw new ProtocolException("Invalid response: " + status);
        }
        if (response.getCode() >= 200) {
            this.incrementResponseCount();
        }
        return response;
    }

    @Override
    public void receiveResponseEntity(ClassicHttpResponse response) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        SocketHolder socketHolder = this.ensureOpen();
        long len = this.incomingContentStrategy.determineLength(response);
        response.setEntity(this.createIncomingEntity(response, this.inBuffer, socketHolder.getInputStream(), len));
    }
}

