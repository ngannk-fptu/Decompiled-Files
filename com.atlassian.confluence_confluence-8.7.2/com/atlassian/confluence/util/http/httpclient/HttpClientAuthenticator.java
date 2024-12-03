/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.httpclient.HttpClient
 *  org.apache.commons.httpclient.HttpMethod
 *  org.apache.commons.httpclient.methods.GetMethod
 */
package com.atlassian.confluence.util.http.httpclient;

import com.atlassian.confluence.util.http.Authenticator;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

@Deprecated(forRemoval=true)
public abstract class HttpClientAuthenticator
extends Authenticator {
    public HttpMethod makeMethod(HttpClient client, String url) {
        return new GetMethod(url);
    }

    public void preprocess(HttpClient client, HttpMethod method) {
    }
}

