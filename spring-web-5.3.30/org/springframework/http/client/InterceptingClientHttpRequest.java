/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.StreamUtils
 */
package org.springframework.http.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.http.client.AbstractBufferingClientHttpRequest;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

class InterceptingClientHttpRequest
extends AbstractBufferingClientHttpRequest {
    private final ClientHttpRequestFactory requestFactory;
    private final List<ClientHttpRequestInterceptor> interceptors;
    private HttpMethod method;
    private URI uri;

    protected InterceptingClientHttpRequest(ClientHttpRequestFactory requestFactory, List<ClientHttpRequestInterceptor> interceptors, URI uri, HttpMethod method) {
        this.requestFactory = requestFactory;
        this.interceptors = interceptors;
        this.method = method;
        this.uri = uri;
    }

    @Override
    public HttpMethod getMethod() {
        return this.method;
    }

    @Override
    public String getMethodValue() {
        return this.method.name();
    }

    @Override
    public URI getURI() {
        return this.uri;
    }

    @Override
    protected final ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
        InterceptingRequestExecution requestExecution = new InterceptingRequestExecution();
        return requestExecution.execute(this, bufferedOutput);
    }

    private class InterceptingRequestExecution
    implements ClientHttpRequestExecution {
        private final Iterator<ClientHttpRequestInterceptor> iterator;

        public InterceptingRequestExecution() {
            this.iterator = InterceptingClientHttpRequest.this.interceptors.iterator();
        }

        @Override
        public ClientHttpResponse execute(HttpRequest request, byte[] body) throws IOException {
            if (this.iterator.hasNext()) {
                ClientHttpRequestInterceptor nextInterceptor = this.iterator.next();
                return nextInterceptor.intercept(request, body, this);
            }
            HttpMethod method = request.getMethod();
            Assert.state((method != null ? 1 : 0) != 0, (String)"No standard HTTP method");
            ClientHttpRequest delegate = InterceptingClientHttpRequest.this.requestFactory.createRequest(request.getURI(), method);
            request.getHeaders().forEach((key, value) -> delegate.getHeaders().addAll((String)key, (List<? extends String>)value));
            if (body.length > 0) {
                if (delegate instanceof StreamingHttpOutputMessage) {
                    StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage)((Object)delegate);
                    streamingOutputMessage.setBody(outputStream -> StreamUtils.copy((byte[])body, (OutputStream)outputStream));
                } else {
                    StreamUtils.copy((byte[])body, (OutputStream)delegate.getBody());
                }
            }
            return delegate.execute();
        }
    }
}

