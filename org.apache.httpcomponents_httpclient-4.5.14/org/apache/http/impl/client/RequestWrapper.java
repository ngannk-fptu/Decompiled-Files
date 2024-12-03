/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpRequest
 *  org.apache.http.ProtocolException
 *  org.apache.http.ProtocolVersion
 *  org.apache.http.RequestLine
 *  org.apache.http.message.AbstractHttpMessage
 *  org.apache.http.message.BasicRequestLine
 *  org.apache.http.params.HttpParams
 *  org.apache.http.params.HttpProtocolParams
 *  org.apache.http.util.Args
 */
package org.apache.http.impl.client;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolException;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.Args;

@Deprecated
public class RequestWrapper
extends AbstractHttpMessage
implements HttpUriRequest {
    private final HttpRequest original;
    private URI uri;
    private String method;
    private ProtocolVersion version;
    private int execCount;

    public RequestWrapper(HttpRequest request) throws ProtocolException {
        Args.notNull((Object)request, (String)"HTTP request");
        this.original = request;
        this.setParams(request.getParams());
        this.setHeaders(request.getAllHeaders());
        if (request instanceof HttpUriRequest) {
            this.uri = ((HttpUriRequest)request).getURI();
            this.method = ((HttpUriRequest)request).getMethod();
            this.version = null;
        } else {
            RequestLine requestLine = request.getRequestLine();
            try {
                this.uri = new URI(requestLine.getUri());
            }
            catch (URISyntaxException ex) {
                throw new ProtocolException("Invalid request URI: " + requestLine.getUri(), (Throwable)ex);
            }
            this.method = requestLine.getMethod();
            this.version = request.getProtocolVersion();
        }
        this.execCount = 0;
    }

    public void resetHeaders() {
        this.headergroup.clear();
        this.setHeaders(this.original.getAllHeaders());
    }

    @Override
    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        Args.notNull((Object)method, (String)"Method name");
        this.method = method;
    }

    public ProtocolVersion getProtocolVersion() {
        if (this.version == null) {
            this.version = HttpProtocolParams.getVersion((HttpParams)this.getParams());
        }
        return this.version;
    }

    public void setProtocolVersion(ProtocolVersion version) {
        this.version = version;
    }

    @Override
    public URI getURI() {
        return this.uri;
    }

    public void setURI(URI uri) {
        this.uri = uri;
    }

    public RequestLine getRequestLine() {
        ProtocolVersion ver = this.getProtocolVersion();
        String uritext = null;
        if (this.uri != null) {
            uritext = this.uri.toASCIIString();
        }
        if (uritext == null || uritext.isEmpty()) {
            uritext = "/";
        }
        return new BasicRequestLine(this.getMethod(), uritext, ver);
    }

    @Override
    public void abort() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAborted() {
        return false;
    }

    public HttpRequest getOriginal() {
        return this.original;
    }

    public boolean isRepeatable() {
        return true;
    }

    public int getExecCount() {
        return this.execCount;
    }

    public void incrementExecCount() {
        ++this.execCount;
    }
}

