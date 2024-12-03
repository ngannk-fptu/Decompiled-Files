/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.httpinvoker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.remoting.httpinvoker.AbstractHttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;
import org.springframework.remoting.support.RemoteInvocationResult;

@Deprecated
public class SimpleHttpInvokerRequestExecutor
extends AbstractHttpInvokerRequestExecutor {
    private int connectTimeout = -1;
    private int readTimeout = -1;

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    @Override
    protected RemoteInvocationResult doExecuteRequest(HttpInvokerClientConfiguration config, ByteArrayOutputStream baos) throws IOException, ClassNotFoundException {
        HttpURLConnection con = this.openConnection(config);
        this.prepareConnection(con, baos.size());
        this.writeRequestBody(config, con, baos);
        this.validateResponse(config, con);
        InputStream responseBody = this.readResponseBody(config, con);
        return this.readRemoteInvocationResult(responseBody, config.getCodebaseUrl());
    }

    protected HttpURLConnection openConnection(HttpInvokerClientConfiguration config) throws IOException {
        URLConnection con = new URL(config.getServiceUrl()).openConnection();
        if (!(con instanceof HttpURLConnection)) {
            throw new IOException("Service URL [" + config.getServiceUrl() + "] does not resolve to an HTTP connection");
        }
        return (HttpURLConnection)con;
    }

    protected void prepareConnection(HttpURLConnection connection, int contentLength) throws IOException {
        Locale locale;
        if (this.connectTimeout >= 0) {
            connection.setConnectTimeout(this.connectTimeout);
        }
        if (this.readTimeout >= 0) {
            connection.setReadTimeout(this.readTimeout);
        }
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", this.getContentType());
        connection.setRequestProperty("Content-Length", Integer.toString(contentLength));
        LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
        if (localeContext != null && (locale = localeContext.getLocale()) != null) {
            connection.setRequestProperty("Accept-Language", locale.toLanguageTag());
        }
        if (this.isAcceptGzipEncoding()) {
            connection.setRequestProperty("Accept-Encoding", "gzip");
        }
    }

    protected void writeRequestBody(HttpInvokerClientConfiguration config, HttpURLConnection con, ByteArrayOutputStream baos) throws IOException {
        baos.writeTo(con.getOutputStream());
    }

    protected void validateResponse(HttpInvokerClientConfiguration config, HttpURLConnection con) throws IOException {
        if (con.getResponseCode() >= 300) {
            throw new IOException("Did not receive successful HTTP response: status code = " + con.getResponseCode() + ", status message = [" + con.getResponseMessage() + "]");
        }
    }

    protected InputStream readResponseBody(HttpInvokerClientConfiguration config, HttpURLConnection con) throws IOException {
        if (this.isGzipResponse(con)) {
            return new GZIPInputStream(con.getInputStream());
        }
        return con.getInputStream();
    }

    protected boolean isGzipResponse(HttpURLConnection con) {
        String encodingHeader = con.getHeaderField("Content-Encoding");
        return encodingHeader != null && encodingHeader.toLowerCase().contains("gzip");
    }
}

