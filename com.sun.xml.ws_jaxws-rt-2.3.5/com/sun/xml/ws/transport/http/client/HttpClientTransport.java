/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.transport.http.client;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.EndpointAddress;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.client.ClientTransportException;
import com.sun.xml.ws.resources.ClientMessages;
import com.sun.xml.ws.transport.Headers;
import com.sun.xml.ws.util.AuthUtil;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceException;

public class HttpClientTransport {
    private static final byte[] THROW_AWAY_BUFFER = new byte[8192];
    int statusCode;
    String statusMessage;
    int contentLength;
    private final Map<String, List<String>> reqHeaders;
    private Map<String, List<String>> respHeaders = null;
    private OutputStream outputStream;
    private boolean https;
    private HttpURLConnection httpConnection = null;
    private final EndpointAddress endpoint;
    private final Packet context;
    private final Integer chunkSize;

    public HttpClientTransport(@NotNull Packet packet, @NotNull Map<String, List<String>> reqHeaders) {
        this.endpoint = packet.endpointAddress;
        this.context = packet;
        this.reqHeaders = reqHeaders;
        this.chunkSize = (Integer)this.context.invocationProperties.get("com.sun.xml.ws.transport.http.client.streaming.chunk.size");
    }

    OutputStream getOutput() {
        try {
            this.createHttpConnection();
            if (this.requiresOutputStream()) {
                List<String> contentEncoding;
                this.outputStream = this.httpConnection.getOutputStream();
                if (this.chunkSize != null) {
                    this.outputStream = new WSChunkedOuputStream(this.outputStream, this.chunkSize);
                }
                if ((contentEncoding = this.reqHeaders.get("Content-Encoding")) != null && contentEncoding.get(0).contains("gzip")) {
                    this.outputStream = new GZIPOutputStream(this.outputStream);
                }
            }
            this.httpConnection.connect();
        }
        catch (Exception ex) {
            throw new ClientTransportException(ClientMessages.localizableHTTP_CLIENT_FAILED(ex), ex);
        }
        return this.outputStream;
    }

    void closeOutput() throws IOException {
        if (this.outputStream != null) {
            this.outputStream.close();
            this.outputStream = null;
        }
    }

    @Nullable
    InputStream getInput() {
        InputStream in;
        try {
            String contentEncoding;
            in = this.readResponse();
            if (in != null && (contentEncoding = this.httpConnection.getContentEncoding()) != null && contentEncoding.contains("gzip")) {
                in = new GZIPInputStream(in);
            }
        }
        catch (IOException e) {
            throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(this.statusCode, this.statusMessage), e);
        }
        return in;
    }

    public Map<String, List<String>> getHeaders() {
        if (this.respHeaders != null) {
            return this.respHeaders;
        }
        this.respHeaders = new Headers();
        this.respHeaders.putAll(this.httpConnection.getHeaderFields());
        return this.respHeaders;
    }

    @Nullable
    protected InputStream readResponse() {
        InputStream is;
        try {
            is = this.httpConnection.getInputStream();
        }
        catch (IOException ioe) {
            is = this.httpConnection.getErrorStream();
        }
        if (is == null) {
            return is;
        }
        final InputStream temp = is;
        return new FilterInputStream(temp){
            boolean closed;

            @Override
            public void close() throws IOException {
                if (!this.closed) {
                    this.closed = true;
                    while (temp.read(THROW_AWAY_BUFFER) != -1) {
                    }
                    super.close();
                }
            }
        };
    }

    protected void readResponseCodeAndMessage() {
        try {
            this.statusCode = this.httpConnection.getResponseCode();
            this.statusMessage = this.httpConnection.getResponseMessage();
            this.contentLength = this.httpConnection.getContentLength();
        }
        catch (IOException ioe) {
            throw new WebServiceException((Throwable)ioe);
        }
    }

    protected HttpURLConnection openConnection(Packet packet) {
        return null;
    }

    protected boolean checkHTTPS(HttpURLConnection connection) {
        if (connection instanceof HttpsURLConnection) {
            SSLSocketFactory sslSocketFactory;
            HostnameVerifier verifier;
            String verificationProperty = (String)this.context.invocationProperties.get("com.sun.xml.ws.client.http.HostnameVerificationProperty");
            if (verificationProperty != null && verificationProperty.equalsIgnoreCase("true")) {
                ((HttpsURLConnection)connection).setHostnameVerifier(new HttpClientVerifier());
            }
            if ((verifier = (HostnameVerifier)this.context.invocationProperties.get("com.sun.xml.ws.transport.https.client.hostname.verifier")) != null) {
                ((HttpsURLConnection)connection).setHostnameVerifier(verifier);
            }
            if ((sslSocketFactory = (SSLSocketFactory)this.context.invocationProperties.get("com.sun.xml.ws.transport.https.client.SSLSocketFactory")) != null) {
                ((HttpsURLConnection)connection).setSSLSocketFactory(sslSocketFactory);
            }
            return true;
        }
        return false;
    }

    private void createHttpConnection() throws IOException {
        Authenticator auth;
        Integer chunkSize;
        Integer connectTimeout;
        String scheme;
        this.httpConnection = this.openConnection(this.context);
        if (this.httpConnection == null) {
            this.httpConnection = (HttpURLConnection)this.endpoint.openConnection();
        }
        if ((scheme = this.endpoint.getURI().getScheme()).equals("https")) {
            this.https = true;
        }
        if (this.checkHTTPS(this.httpConnection)) {
            this.https = true;
        }
        this.httpConnection.setAllowUserInteraction(true);
        this.httpConnection.setDoOutput(true);
        this.httpConnection.setDoInput(true);
        String requestMethod = (String)this.context.invocationProperties.get("javax.xml.ws.http.request.method");
        String method = requestMethod != null ? requestMethod : "POST";
        this.httpConnection.setRequestMethod(method);
        Integer reqTimeout = (Integer)this.context.invocationProperties.get("com.sun.xml.ws.request.timeout");
        if (reqTimeout != null) {
            this.httpConnection.setReadTimeout(reqTimeout);
        }
        if ((connectTimeout = (Integer)this.context.invocationProperties.get("com.sun.xml.ws.connect.timeout")) != null) {
            this.httpConnection.setConnectTimeout(connectTimeout);
        }
        if ((chunkSize = (Integer)this.context.invocationProperties.get("com.sun.xml.ws.transport.http.client.streaming.chunk.size")) != null) {
            this.httpConnection.setChunkedStreamingMode(chunkSize);
        }
        if ((auth = (Authenticator)this.context.invocationProperties.get("com.sun.xml.ws.request.authenticator")) != null) {
            AuthUtil.setAuthenticator(auth, this.httpConnection);
        }
        for (Map.Entry<String, List<String>> entry : this.reqHeaders.entrySet()) {
            if ("Content-Length".equals(entry.getKey())) continue;
            for (String value : entry.getValue()) {
                this.httpConnection.addRequestProperty(entry.getKey(), value);
            }
        }
    }

    boolean isSecure() {
        return this.https;
    }

    protected void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    private boolean requiresOutputStream() {
        return !this.httpConnection.getRequestMethod().equalsIgnoreCase("GET") && !this.httpConnection.getRequestMethod().equalsIgnoreCase("HEAD") && !this.httpConnection.getRequestMethod().equalsIgnoreCase("DELETE");
    }

    @Nullable
    String getContentType() {
        return this.httpConnection.getContentType();
    }

    public int getContentLength() {
        return this.httpConnection.getContentLength();
    }

    static {
        try {
            JAXBContext.newInstance((Class[])new Class[0]).createUnmarshaller();
        }
        catch (JAXBException jAXBException) {
            // empty catch block
        }
    }

    private static final class WSChunkedOuputStream
    extends FilterOutputStream {
        final int chunkSize;

        WSChunkedOuputStream(OutputStream actual, int chunkSize) {
            super(actual);
            this.chunkSize = chunkSize;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            while (len > 0) {
                int sent = len > this.chunkSize ? this.chunkSize : len;
                this.out.write(b, off, sent);
                len -= sent;
                off += sent;
            }
        }
    }

    private static class LocalhostHttpClientVerifier
    implements HostnameVerifier {
        private LocalhostHttpClientVerifier() {
        }

        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return "localhost".equalsIgnoreCase(s) || "127.0.0.1".equals(s);
        }
    }

    private static class HttpClientVerifier
    implements HostnameVerifier {
        private HttpClientVerifier() {
        }

        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }
}

