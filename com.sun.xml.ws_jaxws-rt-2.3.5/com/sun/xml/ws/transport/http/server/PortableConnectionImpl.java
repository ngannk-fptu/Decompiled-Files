/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.spi.http.HttpExchange
 */
package com.sun.xml.ws.transport.http.server;

import com.oracle.webservices.api.message.BasePropertySet;
import com.oracle.webservices.api.message.PropertySet;
import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.PortAddressResolver;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.ws.resources.WsservletMessages;
import com.sun.xml.ws.transport.http.HttpAdapter;
import com.sun.xml.ws.transport.http.WSHTTPConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.spi.http.HttpExchange;

final class PortableConnectionImpl
extends WSHTTPConnection
implements WebServiceContextDelegate {
    private final HttpExchange httpExchange;
    private int status;
    private final HttpAdapter adapter;
    private boolean outputWritten;
    private static final BasePropertySet.PropertyMap model = PortableConnectionImpl.parse(PortableConnectionImpl.class);

    public PortableConnectionImpl(@NotNull HttpAdapter adapter, @NotNull HttpExchange httpExchange) {
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
        return this.httpExchange.getRequestHeader(headerName);
    }

    @Override
    public void setResponseHeaders(Map<String, List<String>> headers) {
        Map r = this.httpExchange.getResponseHeaders();
        r.clear();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String name = entry.getKey();
            List<String> values = entry.getValue();
            if (name.equalsIgnoreCase("Content-Length") || name.equalsIgnoreCase("Content-Type")) continue;
            r.put(name, new ArrayList<String>(values));
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
        return (List)this.httpExchange.getRequestHeaders().get(headerName);
    }

    @Override
    @PropertySet.Property(value={"javax.xml.ws.http.response.headers", "com.sun.xml.ws.api.message.packet.outbound.transport.headers"})
    public Map<String, List<String>> getResponseHeaders() {
        return this.httpExchange.getResponseHeaders();
    }

    @Override
    public void setContentTypeResponseHeader(@NotNull String value) {
        this.httpExchange.addResponseHeader("Content-Type", value);
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
    public InputStream getInput() throws IOException {
        return this.httpExchange.getRequestBody();
    }

    @Override
    @NotNull
    public OutputStream getOutput() throws IOException {
        assert (!this.outputWritten);
        this.outputWritten = true;
        this.httpExchange.setStatus(this.getStatus());
        return this.httpExchange.getResponseBody();
    }

    @Override
    @NotNull
    public WebServiceContextDelegate getWebServiceContextDelegate() {
        return this;
    }

    @Override
    public Principal getUserPrincipal(Packet request) {
        return this.httpExchange.getUserPrincipal();
    }

    @Override
    public boolean isUserInRole(Packet request, String role) {
        return this.httpExchange.isUserInRole(role);
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

    @PropertySet.Property(value={"javax.xml.ws.servlet.context"})
    public Object getServletContext() {
        return this.httpExchange.getAttribute("javax.xml.ws.servlet.context");
    }

    @PropertySet.Property(value={"javax.xml.ws.servlet.response"})
    public Object getServletResponse() {
        return this.httpExchange.getAttribute("javax.xml.ws.servlet.response");
    }

    @PropertySet.Property(value={"javax.xml.ws.servlet.request"})
    public Object getServletRequest() {
        return this.httpExchange.getAttribute("javax.xml.ws.servlet.request");
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
        return this.httpExchange.getScheme().equals("https");
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
        return this.httpExchange.getQueryString();
    }

    @Override
    @PropertySet.Property(value={"javax.xml.ws.http.request.pathinfo"})
    public String getPathInfo() {
        return this.httpExchange.getPathInfo();
    }

    @PropertySet.Property(value={"com.sun.xml.ws.http.exchange"})
    public HttpExchange getExchange() {
        return this.httpExchange;
    }

    @Override
    @NotNull
    public String getBaseAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.httpExchange.getScheme());
        sb.append("://");
        sb.append(this.httpExchange.getLocalAddress().getHostName());
        sb.append(":");
        sb.append(this.httpExchange.getLocalAddress().getPort());
        sb.append(this.httpExchange.getContextPath());
        return sb.toString();
    }

    @Override
    public String getProtocol() {
        return this.httpExchange.getProtocol();
    }

    @Override
    public void setContentLengthResponseHeader(int value) {
        this.httpExchange.addResponseHeader("Content-Length", "" + value);
    }

    @Override
    public String getRequestURI() {
        return this.httpExchange.getRequestURI().toString();
    }

    @Override
    public String getRequestScheme() {
        return this.httpExchange.getScheme();
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
}

