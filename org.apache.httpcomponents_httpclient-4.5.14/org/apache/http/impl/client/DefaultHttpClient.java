/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpRequestInterceptor
 *  org.apache.http.HttpResponseInterceptor
 *  org.apache.http.HttpVersion
 *  org.apache.http.ProtocolVersion
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.params.HttpConnectionParams
 *  org.apache.http.params.HttpParams
 *  org.apache.http.params.HttpProtocolParams
 *  org.apache.http.params.SyncBasicHttpParams
 *  org.apache.http.protocol.BasicHttpProcessor
 *  org.apache.http.protocol.HTTP
 *  org.apache.http.protocol.RequestContent
 *  org.apache.http.protocol.RequestExpectContinue
 *  org.apache.http.protocol.RequestTargetHost
 *  org.apache.http.protocol.RequestUserAgent
 *  org.apache.http.util.VersionInfo
 */
package org.apache.http.impl.client;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.client.protocol.RequestAuthCache;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.client.protocol.RequestDefaultHeaders;
import org.apache.http.client.protocol.RequestProxyAuthentication;
import org.apache.http.client.protocol.RequestTargetAuthentication;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.VersionInfo;

@Deprecated
@Contract(threading=ThreadingBehavior.SAFE_CONDITIONAL)
public class DefaultHttpClient
extends AbstractHttpClient {
    public DefaultHttpClient(ClientConnectionManager conman, HttpParams params) {
        super(conman, params);
    }

    public DefaultHttpClient(ClientConnectionManager conman) {
        super(conman, null);
    }

    public DefaultHttpClient(HttpParams params) {
        super(null, params);
    }

    public DefaultHttpClient() {
        super(null, null);
    }

    @Override
    protected HttpParams createHttpParams() {
        SyncBasicHttpParams params = new SyncBasicHttpParams();
        DefaultHttpClient.setDefaultHttpParams((HttpParams)params);
        return params;
    }

    public static void setDefaultHttpParams(HttpParams params) {
        HttpProtocolParams.setVersion((HttpParams)params, (ProtocolVersion)HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset((HttpParams)params, (String)HTTP.DEF_CONTENT_CHARSET.name());
        HttpConnectionParams.setTcpNoDelay((HttpParams)params, (boolean)true);
        HttpConnectionParams.setSocketBufferSize((HttpParams)params, (int)8192);
        HttpProtocolParams.setUserAgent((HttpParams)params, (String)VersionInfo.getUserAgent((String)"Apache-HttpClient", (String)"org.apache.http.client", DefaultHttpClient.class));
    }

    @Override
    protected BasicHttpProcessor createHttpProcessor() {
        BasicHttpProcessor httpproc = new BasicHttpProcessor();
        httpproc.addInterceptor((HttpRequestInterceptor)new RequestDefaultHeaders());
        httpproc.addInterceptor((HttpRequestInterceptor)new RequestContent());
        httpproc.addInterceptor((HttpRequestInterceptor)new RequestTargetHost());
        httpproc.addInterceptor((HttpRequestInterceptor)new RequestClientConnControl());
        httpproc.addInterceptor((HttpRequestInterceptor)new RequestUserAgent());
        httpproc.addInterceptor((HttpRequestInterceptor)new RequestExpectContinue());
        httpproc.addInterceptor((HttpRequestInterceptor)new RequestAddCookies());
        httpproc.addInterceptor((HttpResponseInterceptor)new ResponseProcessCookies());
        httpproc.addInterceptor((HttpRequestInterceptor)new RequestAuthCache());
        httpproc.addInterceptor((HttpRequestInterceptor)new RequestTargetAuthentication());
        httpproc.addInterceptor((HttpRequestInterceptor)new RequestProxyAuthentication());
        return httpproc;
    }
}

