/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.ipc.http;

import io.micrometer.core.instrument.util.IOUtils;
import io.micrometer.core.ipc.http.HttpSender;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.time.Duration;
import java.util.Map;

public class HttpUrlConnectionSender
implements HttpSender {
    private static final int DEFAULT_CONNECT_TIMEOUT_MS = 1000;
    private static final int DEFAULT_READ_TIMEOUT_MS = 10000;
    private final int connectTimeoutMs;
    private final int readTimeoutMs;
    private final Proxy proxy;

    public HttpUrlConnectionSender(Duration connectTimeout, Duration readTimeout) {
        this(connectTimeout, readTimeout, null);
    }

    public HttpUrlConnectionSender(Duration connectTimeout, Duration readTimeout, Proxy proxy) {
        this.connectTimeoutMs = (int)connectTimeout.toMillis();
        this.readTimeoutMs = (int)readTimeout.toMillis();
        this.proxy = proxy;
    }

    public HttpUrlConnectionSender() {
        this.connectTimeoutMs = 1000;
        this.readTimeoutMs = 10000;
        this.proxy = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public HttpSender.Response send(HttpSender.Request request) throws IOException {
        HttpURLConnection con = null;
        try {
            con = this.proxy != null ? (HttpURLConnection)request.getUrl().openConnection(this.proxy) : (HttpURLConnection)request.getUrl().openConnection();
            con.setConnectTimeout(this.connectTimeoutMs);
            con.setReadTimeout(this.readTimeoutMs);
            HttpSender.Method method = request.getMethod();
            con.setRequestMethod(method.name());
            for (Map.Entry<String, String> header : request.getRequestHeaders().entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }
            if (method != HttpSender.Method.GET) {
                con.setDoOutput(true);
                try (OutputStream os = con.getOutputStream();){
                    os.write(request.getEntity());
                    os.flush();
                }
            }
            int status = con.getResponseCode();
            String body = null;
            try {
                if (con.getErrorStream() != null) {
                    body = IOUtils.toString(con.getErrorStream());
                } else if (con.getInputStream() != null) {
                    body = IOUtils.toString(con.getInputStream());
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            HttpSender.Response response = new HttpSender.Response(status, body);
            return response;
        }
        finally {
            try {
                if (con != null) {
                    con.disconnect();
                }
            }
            catch (Exception exception) {}
        }
    }
}

