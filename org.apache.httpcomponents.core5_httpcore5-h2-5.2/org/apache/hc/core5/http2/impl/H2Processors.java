/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.HttpRequestInterceptor
 *  org.apache.hc.core5.http.HttpResponseInterceptor
 *  org.apache.hc.core5.http.impl.HttpProcessors
 *  org.apache.hc.core5.http.protocol.HttpProcessor
 *  org.apache.hc.core5.http.protocol.HttpProcessorBuilder
 *  org.apache.hc.core5.http.protocol.RequestExpectContinue
 *  org.apache.hc.core5.http.protocol.RequestUserAgent
 *  org.apache.hc.core5.http.protocol.ResponseDate
 *  org.apache.hc.core5.http.protocol.ResponseServer
 *  org.apache.hc.core5.util.TextUtils
 *  org.apache.hc.core5.util.VersionInfo
 */
package org.apache.hc.core5.http2.impl;

import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.HttpResponseInterceptor;
import org.apache.hc.core5.http.impl.HttpProcessors;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.http.protocol.HttpProcessorBuilder;
import org.apache.hc.core5.http.protocol.RequestExpectContinue;
import org.apache.hc.core5.http.protocol.RequestUserAgent;
import org.apache.hc.core5.http.protocol.ResponseDate;
import org.apache.hc.core5.http.protocol.ResponseServer;
import org.apache.hc.core5.http2.protocol.H2RequestConnControl;
import org.apache.hc.core5.http2.protocol.H2RequestContent;
import org.apache.hc.core5.http2.protocol.H2RequestTargetHost;
import org.apache.hc.core5.http2.protocol.H2RequestValidateHost;
import org.apache.hc.core5.http2.protocol.H2ResponseConnControl;
import org.apache.hc.core5.http2.protocol.H2ResponseContent;
import org.apache.hc.core5.util.TextUtils;
import org.apache.hc.core5.util.VersionInfo;

public final class H2Processors {
    private static final String SOFTWARE = "Apache-HttpCore";

    public static HttpProcessorBuilder customServer(String serverInfo) {
        return HttpProcessorBuilder.create().addAll(new HttpResponseInterceptor[]{new ResponseDate(), new ResponseServer(!TextUtils.isBlank((CharSequence)serverInfo) ? serverInfo : VersionInfo.getSoftwareInfo((String)SOFTWARE, (String)"org.apache.hc.core5", H2Processors.class)), H2ResponseContent.INSTANCE, H2ResponseConnControl.INSTANCE}).addAll(new HttpRequestInterceptor[]{H2RequestValidateHost.INSTANCE});
    }

    public static HttpProcessor server(String serverInfo) {
        return H2Processors.customServer(serverInfo).build();
    }

    public static HttpProcessor server() {
        return H2Processors.customServer(null).build();
    }

    public static HttpProcessorBuilder customClient(String agentInfo) {
        return HttpProcessorBuilder.create().addAll(new HttpRequestInterceptor[]{H2RequestContent.INSTANCE, H2RequestTargetHost.INSTANCE, H2RequestConnControl.INSTANCE, new RequestUserAgent(!TextUtils.isBlank((CharSequence)agentInfo) ? agentInfo : VersionInfo.getSoftwareInfo((String)SOFTWARE, (String)"org.apache.hc.core5", HttpProcessors.class)), RequestExpectContinue.INSTANCE});
    }

    public static HttpProcessor client(String agentInfo) {
        return H2Processors.customClient(agentInfo).build();
    }

    public static HttpProcessor client() {
        return H2Processors.customClient(null).build();
    }
}

