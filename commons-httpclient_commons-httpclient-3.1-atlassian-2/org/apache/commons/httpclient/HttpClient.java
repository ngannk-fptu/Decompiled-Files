/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient;

import java.io.IOException;
import java.security.Provider;
import java.security.Security;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodDirector;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpClient {
    private static final Log LOG = LogFactory.getLog(HttpClient.class);
    private HttpConnectionManager httpConnectionManager;
    private HttpState state = new HttpState();
    private HttpClientParams params = null;
    private HostConfiguration hostConfiguration = new HostConfiguration();

    public HttpClient() {
        this(new HttpClientParams());
    }

    public HttpClient(HttpClientParams params) {
        if (params == null) {
            throw new IllegalArgumentException("Params may not be null");
        }
        this.params = params;
        this.httpConnectionManager = null;
        Class clazz = params.getConnectionManagerClass();
        if (clazz != null) {
            try {
                this.httpConnectionManager = (HttpConnectionManager)clazz.newInstance();
            }
            catch (Exception e) {
                LOG.warn((Object)"Error instantiating connection manager class, defaulting to SimpleHttpConnectionManager", (Throwable)e);
            }
        }
        if (this.httpConnectionManager == null) {
            this.httpConnectionManager = new SimpleHttpConnectionManager();
        }
        if (this.httpConnectionManager != null) {
            this.httpConnectionManager.getParams().setDefaults(this.params);
        }
    }

    public HttpClient(HttpClientParams params, HttpConnectionManager httpConnectionManager) {
        if (httpConnectionManager == null) {
            throw new IllegalArgumentException("httpConnectionManager cannot be null");
        }
        if (params == null) {
            throw new IllegalArgumentException("Params may not be null");
        }
        this.params = params;
        this.httpConnectionManager = httpConnectionManager;
        this.httpConnectionManager.getParams().setDefaults(this.params);
    }

    public HttpClient(HttpConnectionManager httpConnectionManager) {
        this(new HttpClientParams(), httpConnectionManager);
    }

    public synchronized HttpState getState() {
        return this.state;
    }

    public synchronized void setState(HttpState state) {
        this.state = state;
    }

    public synchronized void setStrictMode(boolean strictMode) {
        if (strictMode) {
            this.params.makeStrict();
        } else {
            this.params.makeLenient();
        }
    }

    public synchronized boolean isStrictMode() {
        return false;
    }

    public synchronized void setTimeout(int newTimeoutInMilliseconds) {
        this.params.setSoTimeout(newTimeoutInMilliseconds);
    }

    public synchronized void setHttpConnectionFactoryTimeout(long timeout) {
        this.params.setConnectionManagerTimeout(timeout);
    }

    public synchronized void setConnectionTimeout(int newTimeoutInMilliseconds) {
        this.httpConnectionManager.getParams().setConnectionTimeout(newTimeoutInMilliseconds);
    }

    public int executeMethod(HttpMethod method) throws IOException, HttpException {
        LOG.trace((Object)"enter HttpClient.executeMethod(HttpMethod)");
        return this.executeMethod(null, method, null);
    }

    public int executeMethod(HostConfiguration hostConfiguration, HttpMethod method) throws IOException, HttpException {
        LOG.trace((Object)"enter HttpClient.executeMethod(HostConfiguration,HttpMethod)");
        return this.executeMethod(hostConfiguration, method, null);
    }

    public int executeMethod(HostConfiguration hostconfig, HttpMethod method, HttpState state) throws IOException, HttpException {
        LOG.trace((Object)"enter HttpClient.executeMethod(HostConfiguration,HttpMethod,HttpState)");
        if (method == null) {
            throw new IllegalArgumentException("HttpMethod parameter may not be null");
        }
        HostConfiguration defaulthostconfig = this.getHostConfiguration();
        if (hostconfig == null) {
            hostconfig = defaulthostconfig;
        }
        URI uri = method.getURI();
        if (hostconfig == defaulthostconfig || uri.isAbsoluteURI()) {
            hostconfig = (HostConfiguration)hostconfig.clone();
            if (uri.isAbsoluteURI()) {
                hostconfig.setHost(uri);
            }
        }
        HttpMethodDirector methodDirector = new HttpMethodDirector(this.getHttpConnectionManager(), hostconfig, this.params, state == null ? this.getState() : state);
        methodDirector.executeMethod(method);
        return method.getStatusCode();
    }

    public String getHost() {
        return this.hostConfiguration.getHost();
    }

    public int getPort() {
        return this.hostConfiguration.getPort();
    }

    public synchronized HostConfiguration getHostConfiguration() {
        return this.hostConfiguration;
    }

    public synchronized void setHostConfiguration(HostConfiguration hostConfiguration) {
        this.hostConfiguration = hostConfiguration;
    }

    public synchronized HttpConnectionManager getHttpConnectionManager() {
        return this.httpConnectionManager;
    }

    public synchronized void setHttpConnectionManager(HttpConnectionManager httpConnectionManager) {
        this.httpConnectionManager = httpConnectionManager;
        if (this.httpConnectionManager != null) {
            this.httpConnectionManager.getParams().setDefaults(this.params);
        }
    }

    public HttpClientParams getParams() {
        return this.params;
    }

    public void setParams(HttpClientParams params) {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        this.params = params;
    }

    static {
        if (LOG.isDebugEnabled()) {
            try {
                LOG.debug((Object)("Java version: " + System.getProperty("java.version")));
                LOG.debug((Object)("Java vendor: " + System.getProperty("java.vendor")));
                LOG.debug((Object)("Java class path: " + System.getProperty("java.class.path")));
                LOG.debug((Object)("Operating system name: " + System.getProperty("os.name")));
                LOG.debug((Object)("Operating system architecture: " + System.getProperty("os.arch")));
                LOG.debug((Object)("Operating system version: " + System.getProperty("os.version")));
                Provider[] providers = Security.getProviders();
                for (int i = 0; i < providers.length; ++i) {
                    Provider provider = providers[i];
                    LOG.debug((Object)(provider.getName() + " " + provider.getVersion() + ": " + provider.getInfo()));
                }
            }
            catch (SecurityException securityException) {
                // empty catch block
            }
        }
    }
}

