/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.httpclient.Credentials
 *  org.apache.commons.httpclient.HttpClient
 *  org.apache.commons.httpclient.NTCredentials
 *  org.apache.commons.httpclient.UsernamePasswordCredentials
 *  org.apache.commons.httpclient.auth.AuthScope
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.http.httpclient;

import com.atlassian.confluence.util.http.HttpProxyConfiguration;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated(forRemoval=true)
public class HttpClientProxyConfiguration {
    private static final Logger log = LoggerFactory.getLogger(HttpClientProxyConfiguration.class);
    private final HttpProxyConfiguration configuration;

    public static HttpClientProxyConfiguration getInstance(HttpProxyConfiguration configuration) {
        return new HttpClientProxyConfiguration(configuration);
    }

    private HttpClientProxyConfiguration(HttpProxyConfiguration configuration) {
        this.configuration = configuration;
    }

    public boolean shouldProxy(String host) {
        return this.configuration.shouldProxy(host);
    }

    public void configureClient(HttpClient client) {
        client.getHostConfiguration().setProxy(this.configuration.getHost(), this.configuration.getPort());
        ArrayList<String> authPolicies = new ArrayList<String>();
        for (ProxyAuthentication authentication : HttpClientProxyConfiguration.getProxyAuthentications(this.configuration)) {
            client.getState().setProxyCredentials(authentication.scope, authentication.credentials);
            authPolicies.add(authentication.authScheme);
        }
        if (!authPolicies.isEmpty()) {
            client.getParams().setParameter("http.auth.scheme-priority", authPolicies);
        }
    }

    private static List<ProxyAuthentication> getProxyAuthentications(HttpProxyConfiguration configuration) {
        ArrayList<ProxyAuthentication> credentials = new ArrayList<ProxyAuthentication>(3);
        for (HttpProxyConfiguration.ProxyAuthentication auth : configuration.getAuthentication()) {
            if (auth == HttpProxyConfiguration.ProxyAuthentication.NTLM && configuration.hasNtlmAuthentication()) {
                log.info("Adding  NTLM authentication credentials");
                credentials.add(HttpClientProxyConfiguration.getNtlmAuthentication(configuration));
            }
            if (auth == HttpProxyConfiguration.ProxyAuthentication.DIGEST && configuration.hasDigestAuthentication()) {
                log.info("Adding digest authentication credentials");
                credentials.add(HttpClientProxyConfiguration.getDigestAuthentication(configuration));
            }
            if (auth != HttpProxyConfiguration.ProxyAuthentication.BASIC || !configuration.hasBasicAuthentication()) continue;
            log.info("Attempting basic authentication credentials");
            credentials.add(HttpClientProxyConfiguration.getBasicAuthentication(configuration));
        }
        return credentials;
    }

    private static ProxyAuthentication getBasicAuthentication(HttpProxyConfiguration configuration) {
        AuthScope scope = new AuthScope(configuration.getHost(), configuration.getPort(), null, "basic");
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(configuration.getUsername(), configuration.getPassword());
        return new ProxyAuthentication(scope, (Credentials)credentials, "Basic");
    }

    private static ProxyAuthentication getDigestAuthentication(HttpProxyConfiguration configuration) {
        AuthScope scope = new AuthScope(configuration.getHost(), configuration.getPort(), null, "digest");
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(configuration.getUsername(), configuration.getPassword());
        return new ProxyAuthentication(scope, (Credentials)credentials, "Digest");
    }

    private static ProxyAuthentication getNtlmAuthentication(HttpProxyConfiguration configuration) {
        AuthScope scope = new AuthScope(configuration.getHost(), configuration.getPort(), configuration.getNtlmDomain(), "ntlm");
        NTCredentials credentials = new NTCredentials(configuration.getUsername(), configuration.getPassword(), configuration.getHost(), configuration.getNtlmDomain());
        return new ProxyAuthentication(scope, (Credentials)credentials, "NTLM");
    }

    private static class ProxyAuthentication {
        private final AuthScope scope;
        private final Credentials credentials;
        private final String authScheme;

        private ProxyAuthentication(AuthScope scope, Credentials credentials, String authScheme) {
            this.scope = scope;
            this.credentials = credentials;
            this.authScheme = authScheme;
        }
    }
}

