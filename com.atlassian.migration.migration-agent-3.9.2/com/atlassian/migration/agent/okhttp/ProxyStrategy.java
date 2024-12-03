/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  okhttp3.Authenticator
 *  okhttp3.Credentials
 *  okhttp3.OkHttpClient$Builder
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.okhttp;

import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.okhttp.OkHttpClientSingleton;
import com.atlassian.migration.agent.okhttp.ProxyType;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

public abstract class ProxyStrategy {
    private static final Logger log = ContextLoggerFactory.getLogger(ProxyStrategy.class);
    protected String proxyHost;
    protected Integer proxyPort;
    protected String proxyUser;
    protected String proxyPassword;
    protected String nonProxyHostsValue;
    protected Authenticator authenticator;
    protected ProxySelector proxySelector;
    protected String[] nonProxyHosts;
    protected OkHttpClientSingleton okHttpClientSingleton;

    abstract ProxyType getProxyType();

    protected Authenticator getProxyAuthenticator() {
        return this.authenticator;
    }

    protected ProxySelector getProxySelector() {
        return this.proxySelector;
    }

    boolean isProxyAuthConfigured() {
        return StringUtils.isNotBlank((String)this.proxyUser) || StringUtils.isNotBlank((String)this.proxyPassword);
    }

    protected OkHttpClient.Builder getProxyBuilder() {
        return this.okHttpClientSingleton.getBuilder().proxySelector(this.getProxySelector()).proxyAuthenticator(this.getProxyAuthenticator());
    }

    protected void setProxySelector() {
        this.proxySelector = new ProxySelector(){

            @Override
            public List<Proxy> select(URI uri) {
                ArrayList<Proxy> proxyList = new ArrayList<Proxy>();
                String host = uri.getHost();
                if (ProxyStrategy.this.shouldProxy(host)) {
                    proxyList.add(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ProxyStrategy.this.proxyHost, (int)ProxyStrategy.this.proxyPort)));
                } else {
                    proxyList.add(Proxy.NO_PROXY);
                }
                return proxyList;
            }

            @Override
            public void connectFailed(URI uri, SocketAddress socketAddress, IOException ioException) {
                throw new UnsupportedOperationException("Proxy not supported yet.", ioException);
            }
        };
    }

    protected void initFromProperty(String proxyHostProp, String proxyPortProp, String proxyUserProp, String proxyPasswordProp, String nonProxyHostsProp) {
        this.proxyHost = System.getProperty(proxyHostProp);
        this.proxyUser = System.getProperty(proxyUserProp);
        this.proxyPassword = System.getProperty(proxyPasswordProp);
        this.nonProxyHostsValue = System.getProperty(nonProxyHostsProp, "");
        int port = 80;
        try {
            port = Integer.parseInt(System.getProperty(proxyPortProp, "80"));
        }
        catch (NumberFormatException e) {
            log.warn(String.format("Property %s is not a number. Defaulting to 80.", proxyPortProp));
        }
        this.proxyPort = port;
        this.nonProxyHosts = this.nonProxyHostsValue.split("\\|");
        this.setProxyAuthenticator();
        this.setProxySelector();
    }

    public boolean isProxyConfigured() {
        return StringUtils.isNotBlank((String)this.proxyHost);
    }

    private boolean shouldProxyHostInternal(String destinationHost) {
        for (String nonProxyHost : this.nonProxyHosts) {
            if (nonProxyHost.startsWith("*") && destinationHost.endsWith(nonProxyHost.substring(1))) {
                return false;
            }
            if (!destinationHost.equals(nonProxyHost)) continue;
            return false;
        }
        return true;
    }

    public boolean shouldProxy(String destinationHost) {
        return this.isProxyConfigured() && this.shouldProxyHostInternal(destinationHost);
    }

    protected void setProxyAuthenticator() {
        this.authenticator = (route, response) -> {
            if (this.isProxyAuthConfigured()) {
                String credential = Credentials.basic((String)this.proxyUser, (String)this.proxyPassword);
                return response.request().newBuilder().header("Proxy-Authorization", credential).build();
            }
            return response.request().newBuilder().build();
        };
    }
}

