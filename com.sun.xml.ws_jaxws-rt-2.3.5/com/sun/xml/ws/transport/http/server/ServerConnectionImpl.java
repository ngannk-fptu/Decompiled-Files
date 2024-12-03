/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.transport.http.server;

import com.oracle.webservices.api.message.BasePropertySet;
import com.oracle.webservices.api.message.PropertySet;
import com.sun.istack.NotNull;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsExchange;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.PortAddressResolver;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.ws.resources.WsservletMessages;
import com.sun.xml.ws.transport.http.HttpAdapter;
import com.sun.xml.ws.transport.http.WSHTTPConnection;
import com.sun.xml.ws.util.ReadAllStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.ws.WebServiceException;

final class ServerConnectionImpl
extends WSHTTPConnection
implements WebServiceContextDelegate {
    private final HttpExchange httpExchange;
    private int status;
    private final HttpAdapter adapter;
    private LWHSInputStream in;
    private OutputStream out;
    private static final BasePropertySet.PropertyMap model = ServerConnectionImpl.parse(ServerConnectionImpl.class);

    public ServerConnectionImpl(@NotNull HttpAdapter adapter, @NotNull HttpExchange httpExchange) {
        this.adapter = adapter;
        this.httpExchange = httpExchange;
    }

    @Override
    @PropertySet.Property(value={"javax.xml.ws.http.request.headers", "com.sun.xml.ws.api.message.packet.inbound.transport.headers"})
    @NotNull
    public Map<String, List<String>> getRequestHeaders() {
        return this.httpExchange.getRequestHeaders();
    }

    @Override
    public String getRequestHeader(String headerName) {
        return this.httpExchange.getRequestHeaders().getFirst(headerName);
    }

    @Override
    public void setResponseHeaders(Map<String, List<String>> headers) {
        Headers r = this.httpExchange.getResponseHeaders();
        r.clear();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String name = entry.getKey();
            List<String> values = entry.getValue();
            if ("Content-Length".equalsIgnoreCase(name) || "Content-Type".equalsIgnoreCase(name)) continue;
            r.put(name, (List<String>)new ArrayList<String>(values));
        }
    }

    @Override
    public void setResponseHeader(String key, List<String> value) {
        this.httpExchange.getResponseHeaders().put(key, value);
    }

    @Override
    public Set<String> getRequestHeaderNames() {
        return this.httpExchange.getRequestHeaders().keySet();
    }

    @Override
    public List<String> getRequestHeaderValues(String headerName) {
        return this.httpExchange.getRequestHeaders().get(headerName);
    }

    @Override
    @PropertySet.Property(value={"javax.xml.ws.http.response.headers", "com.sun.xml.ws.api.message.packet.outbound.transport.headers"})
    public Map<String, List<String>> getResponseHeaders() {
        return this.httpExchange.getResponseHeaders();
    }

    @Override
    public void setContentTypeResponseHeader(@NotNull String value) {
        this.httpExchange.getResponseHeaders().set("Content-Type", value);
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    @PropertySet.Property(value={"javax.xml.ws.http.response.code"})
    public int getStatus() {
        return this.status;
    }

    @Override
    @NotNull
    public InputStream getInput() {
        if (this.in == null) {
            this.in = new LWHSInputStream(this.httpExchange.getRequestBody());
        }
        return this.in;
    }

    @Override
    @NotNull
    public OutputStream getOutput() throws IOException {
        if (this.out == null) {
            String lenHeader = this.httpExchange.getResponseHeaders().getFirst("Content-Length");
            int length = lenHeader != null ? Integer.parseInt(lenHeader) : 0;
            this.httpExchange.sendResponseHeaders(this.getStatus(), length);
            this.out = new FilterOutputStream(this.httpExchange.getResponseBody()){
                boolean closed;

                @Override
                public void close() throws IOException {
                    if (!this.closed) {
                        this.closed = true;
                        ServerConnectionImpl.this.in.readAll();
                        try {
                            super.close();
                        }
                        catch (IOException iOException) {
                            // empty catch block
                        }
                    }
                }

                @Override
                public void write(byte[] buf, int start, int len) throws IOException {
                    this.out.write(buf, start, len);
                }
            };
        }
        return this.out;
    }

    @Override
    @NotNull
    public WebServiceContextDelegate getWebServiceContextDelegate() {
        return this;
    }

    @Override
    public Principal getUserPrincipal(Packet request) {
        return this.httpExchange.getPrincipal();
    }

    @Override
    public boolean isUserInRole(Packet request, String role) {
        return false;
    }

    @Override
    @NotNull
    public String getEPRAddress(Packet request, WSEndpoint endpoint) {
        PortAddressResolver resolver = this.adapter.owner.createPortAddressResolver(this.getBaseAddress(), endpoint.getImplementationClass());
        String address = resolver.getAddressFor(endpoint.getServiceName(), endpoint.getPortName().getLocalPart());
        if (address == null) {
            throw new WebServiceException(WsservletMessages.SERVLET_NO_ADDRESS_AVAILABLE(endpoint.getPortName()));
        }
        return address;
    }

    @Override
    public String getWSDLAddress(@NotNull Packet request, @NotNull WSEndpoint endpoint) {
        String eprAddress = this.getEPRAddress(request, endpoint);
        if (this.adapter.getEndpoint().getPort() != null) {
            return eprAddress + "?wsdl";
        }
        return null;
    }

    @Override
    public boolean isSecure() {
        return this.httpExchange instanceof HttpsExchange;
    }

    @Override
    @PropertySet.Property(value={"javax.xml.ws.http.request.method"})
    @NotNull
    public String getRequestMethod() {
        return this.httpExchange.getRequestMethod();
    }

    @Override
    @PropertySet.Property(value={"javax.xml.ws.http.request.querystring"})
    public String getQueryString() {
        URI requestUri = this.httpExchange.getRequestURI();
        String query = requestUri.getQuery();
        if (query != null) {
            return query;
        }
        return null;
    }

    @Override
    @PropertySet.Property(value={"javax.xml.ws.http.request.pathinfo"})
    public String getPathInfo() {
        URI requestUri = this.httpExchange.getRequestURI();
        String reqPath = requestUri.getPath();
        String ctxtPath = this.httpExchange.getHttpContext().getPath();
        if (reqPath.length() > ctxtPath.length()) {
            return reqPath.substring(ctxtPath.length());
        }
        return null;
    }

    @PropertySet.Property(value={"com.sun.xml.ws.http.exchange"})
    public HttpExchange getExchange() {
        return this.httpExchange;
    }

    @Override
    @NotNull
    public String getBaseAddress() {
        String host;
        String protocol = this.httpExchange.getRequestHeaders().getFirst("X-Forwarded-Proto");
        if (protocol == null) {
            protocol = this.getRequestScheme();
        }
        if ((host = this.httpExchange.getRequestHeaders().getFirst("X-Forwarded-Host")) == null) {
            host = this.httpExchange.getRequestHeaders().getFirst("Host");
        }
        if (host == null) {
            host = this.getServerName() + ":" + this.getServerPort();
        }
        return protocol + "://" + host;
    }

    @Override
    public String getProtocol() {
        return this.httpExchange.getProtocol();
    }

    @Override
    public void setContentLengthResponseHeader(int value) {
        this.httpExchange.getResponseHeaders().set("Content-Length", "" + value);
    }

    @Override
    public String getRequestURI() {
        return this.httpExchange.getRequestURI().toString();
    }

    @Override
    public String getRequestScheme() {
        return this.httpExchange instanceof HttpsExchange ? "https" : "http";
    }

    @Override
    public String getServerName() {
        return this.httpExchange.getLocalAddress().getHostName();
    }

    @Override
    public int getServerPort() {
        return this.httpExchange.getLocalAddress().getPort();
    }

    @Override
    protected BasePropertySet.PropertyMap getPropertyMap() {
        return model;
    }

    private static class LWHSInputStream
    extends FilterInputStream {
        boolean closed;
        boolean readAll;

        LWHSInputStream(InputStream in) {
            super(in);
        }

        void readAll() throws IOException {
            if (!this.closed && !this.readAll) {
                ReadAllStream all = new ReadAllStream();
                all.readAll(this.in, 4000000L);
                this.in.close();
                this.in = all;
                this.readAll = true;
            }
        }

        @Override
        public void close() throws IOException {
            if (!this.closed) {
                this.readAll();
                super.close();
                this.closed = true;
            }
        }
    }
}

