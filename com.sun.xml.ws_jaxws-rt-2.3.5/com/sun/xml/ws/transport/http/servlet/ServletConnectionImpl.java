/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.servlet.ServletContext
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.transport.http.servlet;

import com.oracle.webservices.api.message.BasePropertySet;
import com.oracle.webservices.api.message.PropertySet;
import com.sun.istack.NotNull;
import com.sun.xml.ws.api.ha.HaInfo;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.PortAddressResolver;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.ws.resources.WsservletMessages;
import com.sun.xml.ws.transport.Headers;
import com.sun.xml.ws.transport.http.WSHTTPConnection;
import com.sun.xml.ws.transport.http.servlet.ServletAdapter;
import com.sun.xml.ws.util.ReadAllStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceException;

public class ServletConnectionImpl
extends WSHTTPConnection
implements WebServiceContextDelegate {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ServletContext context;
    private int status;
    private Headers requestHeaders;
    private final ServletAdapter adapter;
    private Headers responseHeaders;
    private HaInfo haInfo;
    private ServerInputStream in;
    private OutputStream out;
    private static final BasePropertySet.PropertyMap model = ServletConnectionImpl.parse(ServletConnectionImpl.class);

    public ServletConnectionImpl(@NotNull ServletAdapter adapter, ServletContext context, HttpServletRequest request, HttpServletResponse response) {
        this.adapter = adapter;
        this.context = context;
        this.request = request;
        this.response = response;
    }

    @Override
    @PropertySet.Property(value={"javax.xml.ws.http.request.headers", "com.sun.xml.ws.api.message.packet.inbound.transport.headers"})
    @NotNull
    public Map<String, List<String>> getRequestHeaders() {
        if (this.requestHeaders == null) {
            this.requestHeaders = new Headers();
            Enumeration enums = this.request.getHeaderNames();
            while (enums.hasMoreElements()) {
                String headerName = (String)enums.nextElement();
                Enumeration e = this.request.getHeaders(headerName);
                if (e == null) continue;
                ArrayList<String> values = (ArrayList<String>)this.requestHeaders.get(headerName);
                while (e.hasMoreElements()) {
                    String headerValue = (String)e.nextElement();
                    if (values == null) {
                        values = new ArrayList<String>();
                        this.requestHeaders.put(headerName, values);
                    }
                    values.add(headerValue);
                }
            }
        }
        return this.requestHeaders;
    }

    @Override
    public Set<String> getRequestHeaderNames() {
        return this.getRequestHeaders().keySet();
    }

    @Override
    public List<String> getRequestHeaderValues(@NotNull String headerName) {
        if (this.requestHeaders != null) {
            return (List)this.requestHeaders.get(headerName);
        }
        return null;
    }

    @Override
    public void setResponseHeaders(Map<String, List<String>> headers) {
        if (headers == null) {
            this.responseHeaders = null;
        } else {
            if (this.responseHeaders == null) {
                this.responseHeaders = new Headers();
            } else {
                this.responseHeaders.clear();
            }
            this.responseHeaders.putAll((Map<? extends String, ? extends List<String>>)headers);
        }
    }

    @Override
    public void setResponseHeader(String key, String value) {
        this.setResponseHeader(key, Collections.singletonList(value));
    }

    @Override
    public void setResponseHeader(String key, List<String> value) {
        if (this.responseHeaders == null) {
            this.responseHeaders = new Headers();
        }
        this.responseHeaders.put(key, value);
    }

    @Override
    @PropertySet.Property(value={"javax.xml.ws.http.response.headers", "com.sun.xml.ws.api.message.packet.outbound.transport.headers"})
    public Map<String, List<String>> getResponseHeaders() {
        return this.responseHeaders;
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
    public void setContentTypeResponseHeader(@NotNull String value) {
        this.response.setContentType(value);
    }

    @Override
    @NotNull
    public InputStream getInput() throws IOException {
        if (this.in == null) {
            this.in = new ServerInputStream((InputStream)this.request.getInputStream());
        }
        return this.in;
    }

    @Override
    @NotNull
    public OutputStream getOutput() throws IOException {
        this.response.setStatus(this.status);
        if (this.responseHeaders != null) {
            for (Map.Entry entry : this.responseHeaders.entrySet()) {
                String name = (String)entry.getKey();
                if (name == null || name.equalsIgnoreCase("Content-Type") || name.equalsIgnoreCase("Content-Length")) continue;
                for (String value : (List)entry.getValue()) {
                    this.response.addHeader(name, value);
                }
            }
        }
        if (this.out == null) {
            this.out = new FilterOutputStream((OutputStream)this.response.getOutputStream()){
                boolean closed;

                @Override
                public void close() throws IOException {
                    if (!this.closed) {
                        this.closed = true;
                        if (ServletConnectionImpl.this.in != null) {
                            ServletConnectionImpl.this.in.readAll();
                        }
                        try {
                            if (ServletConnectionImpl.this.status == 202) {
                                this.out.close();
                            } else {
                                super.close();
                            }
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
    public Principal getUserPrincipal(Packet p) {
        return this.request.getUserPrincipal();
    }

    @Override
    public boolean isUserInRole(Packet p, String role) {
        return this.request.isUserInRole(role);
    }

    @Override
    @NotNull
    public String getEPRAddress(Packet p, WSEndpoint endpoint) {
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
    @PropertySet.Property(value={"javax.xml.ws.http.request.method"})
    @NotNull
    public String getRequestMethod() {
        return this.request.getMethod();
    }

    @Override
    public boolean isSecure() {
        return this.request.isSecure();
    }

    @Override
    public Principal getUserPrincipal() {
        return this.request.getUserPrincipal();
    }

    @Override
    public boolean isUserInRole(String role) {
        return this.request.isUserInRole(role);
    }

    @Override
    public String getRequestHeader(String headerName) {
        return this.request.getHeader(headerName);
    }

    @Override
    @PropertySet.Property(value={"javax.xml.ws.http.request.querystring"})
    public String getQueryString() {
        return this.request.getQueryString();
    }

    @Override
    @PropertySet.Property(value={"javax.xml.ws.http.request.pathinfo"})
    @NotNull
    public String getPathInfo() {
        return this.request.getPathInfo();
    }

    @Override
    @NotNull
    public String getRequestURI() {
        return this.request.getRequestURI();
    }

    @Override
    @NotNull
    public String getRequestScheme() {
        return this.request.getScheme();
    }

    @Override
    @NotNull
    public String getServerName() {
        return this.request.getServerName();
    }

    @Override
    @NotNull
    public int getServerPort() {
        return this.request.getServerPort();
    }

    @Override
    @NotNull
    public String getContextPath() {
        return this.request.getContextPath();
    }

    @Override
    @NotNull
    public String getBaseAddress() {
        return ServletConnectionImpl.getBaseAddress(this.request);
    }

    @NotNull
    static String getBaseAddress(HttpServletRequest request) {
        String host;
        StringBuilder buf = new StringBuilder();
        String protocol = request.getHeader("X-Forwarded-Proto");
        if (protocol == null) {
            protocol = request.getScheme();
        }
        if ((host = request.getHeader("X-Forwarded-Host")) == null) {
            host = request.getHeader("Host");
        }
        if (host == null) {
            host = request.getServerName() + ":" + request.getServerPort();
        }
        buf.append(protocol);
        buf.append("://");
        buf.append(host);
        buf.append(request.getContextPath());
        return buf.toString();
    }

    @Override
    public Object getRequestAttribute(String key) {
        return this.request.getAttribute(key);
    }

    @PropertySet.Property(value={"javax.xml.ws.servlet.context"})
    public ServletContext getContext() {
        return this.context;
    }

    @PropertySet.Property(value={"javax.xml.ws.servlet.response"})
    public HttpServletResponse getResponse() {
        return this.response;
    }

    @PropertySet.Property(value={"javax.xml.ws.servlet.request"})
    public HttpServletRequest getRequest() {
        return this.request;
    }

    @PropertySet.Property(value={"com.sun.xml.ws.transport.http.servlet.requestURL"})
    public String getRequestURL() {
        return this.request.getRequestURL().toString();
    }

    @Override
    public String getProtocol() {
        return this.request.getProtocol();
    }

    @Override
    public void setContentLengthResponseHeader(int value) {
        this.response.setContentLength(value);
    }

    @Override
    public String getCookie(String name) {
        Cookie[] cookies = this.request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (!cookie.getName().equals(name)) continue;
                return cookie.getValue();
            }
        }
        return null;
    }

    @Override
    public void setCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        this.response.addCookie(cookie);
    }

    @PropertySet.Property(value={"com.sun.xml.ws.api.message.packet.hainfo"})
    public HaInfo getHaInfo() {
        if (this.haInfo == null) {
            String replicaInstance = null;
            String key = null;
            String jrouteId = null;
            Cookie[] cookies = this.request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (replicaInstance == null && cookie.getName().equals("JREPLICA")) {
                        replicaInstance = cookie.getValue();
                        continue;
                    }
                    if (key == null && cookie.getName().equals("METRO_KEY")) {
                        key = cookie.getValue();
                        continue;
                    }
                    if (jrouteId != null || !cookie.getName().equals("JROUTE")) continue;
                    jrouteId = cookie.getValue();
                }
                if (replicaInstance != null && key != null) {
                    String proxyJroute = this.request.getHeader("proxy-jroute");
                    boolean failOver = jrouteId != null && proxyJroute != null && !jrouteId.equals(proxyJroute);
                    this.haInfo = new HaInfo(key, replicaInstance, failOver);
                }
            }
        }
        return this.haInfo;
    }

    public void setHaInfo(HaInfo replicaInfo) {
        this.haInfo = replicaInfo;
    }

    @Override
    protected BasePropertySet.PropertyMap getPropertyMap() {
        return model;
    }

    private static class ServerInputStream
    extends FilterInputStream {
        boolean closed;
        boolean readAll;

        ServerInputStream(InputStream in) {
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

