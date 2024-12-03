/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl;

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
        return HttpProcessorBuilder.create().addAll(new ResponseDate(), new ResponseServer(!TextUtils.isBlank(serverInfo) ? serverInfo : VersionInfo.getSoftwareInfo(SOFTWARE, "org.apache.hc.core5", H2Processors.class)), new H2ResponseContent(), new H2ResponseConnControl()).addAll(new H2RequestValidateHost());
    }

    public static HttpProcessor server(String serverInfo) {
        return H2Processors.customServer(serverInfo).build();
    }

    public static HttpProcessor server() {
        return H2Processors.customServer(null).build();
    }

    public static HttpProcessorBuilder customClient(String agentInfo) {
        return HttpProcessorBuilder.create().addAll(new H2RequestContent(), new H2RequestTargetHost(), new H2RequestConnControl(), new RequestUserAgent(!TextUtils.isBlank(agentInfo) ? agentInfo : VersionInfo.getSoftwareInfo(SOFTWARE, "org.apache.hc.core5", HttpProcessors.class)), new RequestExpectContinue());
    }

    public static HttpProcessor client(String agentInfo) {
        return H2Processors.customClient(agentInfo).build();
    }

    public static HttpProcessor client() {
        return H2Processors.customClient(null).build();
    }
}

