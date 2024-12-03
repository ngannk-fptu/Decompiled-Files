/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.client.urlconnection;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.CommittingOutputStream;
import com.sun.jersey.api.client.RequestWriter;
import com.sun.jersey.api.client.Statuses;
import com.sun.jersey.api.client.TerminatingClientHandler;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;
import com.sun.jersey.core.header.InBoundHeaders;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public final class URLConnectionClientHandler
extends TerminatingClientHandler {
    public static final String PROPERTY_HTTP_URL_CONNECTION_SET_METHOD_WORKAROUND = "com.sun.jersey.client.property.httpUrlConnectionSetMethodWorkaround";
    private HttpURLConnectionFactory httpURLConnectionFactory = null;

    public URLConnectionClientHandler(HttpURLConnectionFactory httpURLConnectionFactory) {
        this.httpURLConnectionFactory = httpURLConnectionFactory;
    }

    public URLConnectionClientHandler() {
        this((HttpURLConnectionFactory)null);
    }

    @Override
    public ClientResponse handle(ClientRequest ro) {
        try {
            return this._invoke(ro);
        }
        catch (Exception ex) {
            throw new ClientHandlerException(ex);
        }
    }

    private ClientResponse _invoke(final ClientRequest ro) throws IOException {
        Boolean httpUrlConnectionSetMethodWorkaround;
        HTTPSProperties httpsProperties;
        Boolean followRedirects;
        Integer connectTimeout;
        final HttpURLConnection uc = this.httpURLConnectionFactory == null ? (HttpURLConnection)ro.getURI().toURL().openConnection() : this.httpURLConnectionFactory.getHttpURLConnection(ro.getURI().toURL());
        Integer readTimeout = (Integer)ro.getProperties().get("com.sun.jersey.client.property.readTimeout");
        if (readTimeout != null) {
            uc.setReadTimeout(readTimeout);
        }
        if ((connectTimeout = (Integer)ro.getProperties().get("com.sun.jersey.client.property.connectTimeout")) != null) {
            uc.setConnectTimeout(connectTimeout);
        }
        if ((followRedirects = (Boolean)ro.getProperties().get("com.sun.jersey.client.property.followRedirects")) != null) {
            uc.setInstanceFollowRedirects(followRedirects);
        }
        if (uc instanceof HttpsURLConnection && (httpsProperties = (HTTPSProperties)ro.getProperties().get("com.sun.jersey.client.impl.urlconnection.httpsProperties")) != null) {
            httpsProperties.setConnection((HttpsURLConnection)uc);
        }
        if ((httpUrlConnectionSetMethodWorkaround = (Boolean)ro.getProperties().get(PROPERTY_HTTP_URL_CONNECTION_SET_METHOD_WORKAROUND)) != null && httpUrlConnectionSetMethodWorkaround.booleanValue()) {
            URLConnectionClientHandler.setRequestMethodUsingWorkaroundForJREBug(uc, ro.getMethod());
        } else {
            uc.setRequestMethod(ro.getMethod());
        }
        this.writeOutBoundHeaders(ro.getHeaders(), uc);
        Object entity = ro.getEntity();
        if (entity != null) {
            Logger logger2;
            uc.setDoOutput(true);
            if (ro.getMethod().equalsIgnoreCase("GET") && (logger2 = Logger.getLogger(URLConnectionClientHandler.class.getName())).isLoggable(Level.INFO)) {
                logger2.log(Level.INFO, "GET method with entity will be most likely replaced by POST, see http://java.net/jira/browse/JERSEY-1161");
            }
            this.writeRequestEntity(ro, new RequestWriter.RequestEntityWriterListener(){

                @Override
                public void onRequestEntitySize(long size) {
                    if (size != -1L && size < Integer.MAX_VALUE) {
                        uc.setFixedLengthStreamingMode((int)size);
                    } else {
                        Integer chunkedEncodingSize = (Integer)ro.getProperties().get("com.sun.jersey.client.property.chunkedEncodingSize");
                        if (chunkedEncodingSize != null) {
                            uc.setChunkedStreamingMode(chunkedEncodingSize);
                        }
                    }
                }

                @Override
                public OutputStream onGetOutputStream() throws IOException {
                    return new CommittingOutputStream(){

                        @Override
                        protected OutputStream getOutputStream() throws IOException {
                            return uc.getOutputStream();
                        }

                        @Override
                        public void commit() throws IOException {
                            URLConnectionClientHandler.this.writeOutBoundHeaders(ro.getHeaders(), uc);
                        }
                    };
                }
            });
        } else {
            this.writeOutBoundHeaders(ro.getHeaders(), uc);
        }
        int code = uc.getResponseCode();
        String reasonPhrase = uc.getResponseMessage();
        Response.StatusType status = reasonPhrase == null ? Statuses.from(code) : Statuses.from(code, reasonPhrase);
        return new URLConnectionResponse(status, this.getInputStream(uc), ro.getMethod(), this.getInBoundHeaders(uc), uc);
    }

    private static final void setRequestMethodUsingWorkaroundForJREBug(HttpURLConnection httpURLConnection, String method) {
        try {
            httpURLConnection.setRequestMethod(method);
        }
        catch (ProtocolException pe) {
            try {
                Class<?> httpURLConnectionClass = httpURLConnection.getClass();
                Field methodField = httpURLConnectionClass.getSuperclass().getDeclaredField("method");
                methodField.setAccessible(true);
                methodField.set(httpURLConnection, method);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void writeOutBoundHeaders(MultivaluedMap<String, Object> metadata, HttpURLConnection uc) {
        for (Map.Entry e : metadata.entrySet()) {
            List vs = (List)e.getValue();
            if (vs.size() == 1) {
                uc.setRequestProperty((String)e.getKey(), ClientRequest.getHeaderValue(vs.get(0)));
                continue;
            }
            StringBuilder b = new StringBuilder();
            boolean add = false;
            for (Object v : (List)e.getValue()) {
                if (add) {
                    b.append(',');
                }
                add = true;
                b.append(ClientRequest.getHeaderValue(v));
            }
            uc.setRequestProperty((String)e.getKey(), b.toString());
        }
    }

    private InBoundHeaders getInBoundHeaders(HttpURLConnection uc) {
        InBoundHeaders headers = new InBoundHeaders();
        for (Map.Entry<String, List<String>> e : uc.getHeaderFields().entrySet()) {
            if (e.getKey() == null) continue;
            headers.put(e.getKey(), e.getValue());
        }
        return headers;
    }

    private InputStream getInputStream(HttpURLConnection uc) throws IOException {
        if (uc.getResponseCode() < 400) {
            return uc.getInputStream();
        }
        InputStream ein = uc.getErrorStream();
        return ein != null ? ein : new ByteArrayInputStream(new byte[0]);
    }

    private final class URLConnectionResponse
    extends ClientResponse {
        private final String method;
        private final HttpURLConnection uc;

        URLConnectionResponse(Response.StatusType status, InputStream entity, String method, InBoundHeaders headers, HttpURLConnection uc) {
            super(status, headers, entity, URLConnectionClientHandler.this.getMessageBodyWorkers());
            this.method = method;
            this.uc = uc;
        }

        @Override
        public boolean hasEntity() {
            if (this.method.equals("HEAD") || this.getEntityInputStream() == null) {
                return false;
            }
            int l = this.uc.getContentLength();
            return l > 0 || l == -1;
        }

        @Override
        public String toString() {
            return this.uc.getRequestMethod() + " " + this.uc.getURL() + " returned a response status of " + this.getStatus() + " " + this.getClientResponseStatus();
        }
    }
}

