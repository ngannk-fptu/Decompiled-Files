/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import java.io.IOException;
import java.net.Socket;
import org.apache.commons.httpclient.ConnectMethod;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodDirector;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpParams;

public class ProxyClient {
    private HttpState state = new HttpState();
    private HttpClientParams params = null;
    private HostConfiguration hostConfiguration = new HostConfiguration();

    public ProxyClient() {
        this(new HttpClientParams());
    }

    public ProxyClient(HttpClientParams params) {
        if (params == null) {
            throw new IllegalArgumentException("Params may not be null");
        }
        this.params = params;
    }

    public synchronized HttpState getState() {
        return this.state;
    }

    public synchronized void setState(HttpState state) {
        this.state = state;
    }

    public synchronized HostConfiguration getHostConfiguration() {
        return this.hostConfiguration;
    }

    public synchronized void setHostConfiguration(HostConfiguration hostConfiguration) {
        this.hostConfiguration = hostConfiguration;
    }

    public synchronized HttpClientParams getParams() {
        return this.params;
    }

    public synchronized void setParams(HttpClientParams params) {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        this.params = params;
    }

    public ConnectResponse connect() throws IOException, HttpException {
        HostConfiguration hostconf = this.getHostConfiguration();
        if (hostconf.getProxyHost() == null) {
            throw new IllegalStateException("proxy host must be configured");
        }
        if (hostconf.getHost() == null) {
            throw new IllegalStateException("destination host must be configured");
        }
        if (hostconf.getProtocol().isSecure()) {
            throw new IllegalStateException("secure protocol socket factory may not be used");
        }
        ConnectMethod method = new ConnectMethod(this.getHostConfiguration());
        method.getParams().setDefaults(this.getParams());
        DummyConnectionManager connectionManager = new DummyConnectionManager();
        connectionManager.setConnectionParams(this.getParams());
        HttpMethodDirector director = new HttpMethodDirector(connectionManager, hostconf, this.getParams(), this.getState());
        director.executeMethod(method);
        ConnectResponse response = new ConnectResponse();
        response.setConnectMethod(method);
        if (method.getStatusCode() == 200) {
            response.setSocket(connectionManager.getConnection().getSocket());
        } else {
            connectionManager.getConnection().close();
        }
        return response;
    }

    static class DummyConnectionManager
    implements HttpConnectionManager {
        private HttpConnection httpConnection;
        private HttpParams connectionParams;

        DummyConnectionManager() {
        }

        @Override
        public void closeIdleConnections(long idleTimeout) {
        }

        public HttpConnection getConnection() {
            return this.httpConnection;
        }

        public void setConnectionParams(HttpParams httpParams) {
            this.connectionParams = httpParams;
        }

        @Override
        public HttpConnection getConnectionWithTimeout(HostConfiguration hostConfiguration, long timeout) {
            this.httpConnection = new HttpConnection(hostConfiguration);
            this.httpConnection.setHttpConnectionManager(this);
            this.httpConnection.getParams().setDefaults(this.connectionParams);
            return this.httpConnection;
        }

        @Override
        public HttpConnection getConnection(HostConfiguration hostConfiguration, long timeout) throws HttpException {
            return this.getConnectionWithTimeout(hostConfiguration, timeout);
        }

        @Override
        public HttpConnection getConnection(HostConfiguration hostConfiguration) {
            return this.getConnectionWithTimeout(hostConfiguration, -1L);
        }

        @Override
        public void releaseConnection(HttpConnection conn) {
        }

        @Override
        public HttpConnectionManagerParams getParams() {
            return null;
        }

        @Override
        public void setParams(HttpConnectionManagerParams params) {
        }
    }

    public static class ConnectResponse {
        private ConnectMethod connectMethod;
        private Socket socket;

        private ConnectResponse() {
        }

        public ConnectMethod getConnectMethod() {
            return this.connectMethod;
        }

        private void setConnectMethod(ConnectMethod connectMethod) {
            this.connectMethod = connectMethod;
        }

        public Socket getSocket() {
            return this.socket;
        }

        private void setSocket(Socket socket) {
            this.socket = socket;
        }
    }
}

