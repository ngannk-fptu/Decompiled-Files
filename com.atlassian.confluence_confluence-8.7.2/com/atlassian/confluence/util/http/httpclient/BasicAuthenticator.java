/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.httpclient.Credentials
 *  org.apache.commons.httpclient.HttpClient
 *  org.apache.commons.httpclient.HttpMethod
 *  org.apache.commons.httpclient.URIException
 *  org.apache.commons.httpclient.UsernamePasswordCredentials
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.http.httpclient;

import com.atlassian.confluence.util.http.httpclient.HttpClientAuthenticator;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated(forRemoval=true)
public class BasicAuthenticator
extends HttpClientAuthenticator {
    private static final Logger log = LoggerFactory.getLogger(BasicAuthenticator.class);

    @Override
    public void preprocess(HttpClient client, HttpMethod method) {
        try {
            client.getState().setAuthenticationPreemptive(true);
            client.getState().setCredentials(null, method.getURI().getHost(), (Credentials)new UsernamePasswordCredentials(this.getProperty("username"), this.getProperty("password")));
        }
        catch (URIException e) {
            log.error("Unable to parse URI to set credentials", (Throwable)e);
        }
    }
}

