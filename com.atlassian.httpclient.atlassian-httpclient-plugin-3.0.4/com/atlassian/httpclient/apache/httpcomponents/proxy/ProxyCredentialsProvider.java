/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  io.atlassian.fugue.Option
 */
package com.atlassian.httpclient.apache.httpcomponents.proxy;

import com.atlassian.httpclient.apache.httpcomponents.proxy.ProxyConfig;
import com.atlassian.httpclient.apache.httpcomponents.proxy.ProxyConfigFactory;
import com.atlassian.httpclient.api.factory.HttpClientOptions;
import com.google.common.collect.Iterables;
import io.atlassian.fugue.Option;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.SystemDefaultCredentialsProvider;

public class ProxyCredentialsProvider
implements CredentialsProvider {
    private final SystemDefaultCredentialsProvider delegate;

    private ProxyCredentialsProvider(SystemDefaultCredentialsProvider delegate) {
        this.delegate = delegate;
    }

    public static Option<ProxyCredentialsProvider> build(HttpClientOptions options) {
        Iterable authenticationInfos = Iterables.filter(ProxyConfigFactory.getProxyAuthentication(options), authenticationInfo -> authenticationInfo.getCredentials().isDefined());
        return Iterables.isEmpty((Iterable)authenticationInfos) ? Option.none() : Option.some((Object)ProxyCredentialsProvider.createCredentialProvider(authenticationInfos));
    }

    private static ProxyCredentialsProvider createCredentialProvider(Iterable<ProxyConfig.AuthenticationInfo> authenticationInfos) {
        SystemDefaultCredentialsProvider credentialsProvider = new SystemDefaultCredentialsProvider();
        for (ProxyConfig.AuthenticationInfo authenticationInfo : authenticationInfos) {
            authenticationInfo.getCredentials().foreach(credentials -> credentialsProvider.setCredentials(authenticationInfo.getAuthScope(), (Credentials)credentials));
        }
        return new ProxyCredentialsProvider(credentialsProvider);
    }

    @Override
    public void setCredentials(AuthScope authscope, Credentials credentials) {
        this.delegate.setCredentials(authscope, credentials);
    }

    @Override
    public Credentials getCredentials(AuthScope authscope) {
        return this.delegate.getCredentials(authscope);
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }
}

