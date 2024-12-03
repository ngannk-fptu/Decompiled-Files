/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.client5.http.HttpRoute
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.protocol.HttpContext
 */
package io.micrometer.core.instrument.binder.httpcomponents.hc5;

import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.httpcomponents.hc5.ApacheHttpClientObservationDocumentation;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.HttpContext;

class HttpContextUtils {
    HttpContextUtils() {
    }

    static Tags generateTagsForRoute(HttpContext context) {
        return Tags.of(HttpContextUtils.generateTagStringsForRoute(context));
    }

    static String[] generateTagStringsForRoute(HttpContext context) {
        String targetScheme = "UNKNOWN";
        String targetHost = "UNKNOWN";
        String targetPort = "UNKNOWN";
        Object routeAttribute = context.getAttribute("http.route");
        if (routeAttribute instanceof HttpRoute) {
            HttpHost host = ((HttpRoute)routeAttribute).getTargetHost();
            targetScheme = host.getSchemeName();
            targetHost = host.getHostName();
            targetPort = String.valueOf(host.getPort());
        }
        return new String[]{ApacheHttpClientObservationDocumentation.ApacheHttpClientKeyNames.TARGET_SCHEME.asString(), targetScheme, ApacheHttpClientObservationDocumentation.ApacheHttpClientKeyNames.TARGET_HOST.asString(), targetHost, ApacheHttpClientObservationDocumentation.ApacheHttpClientKeyNames.TARGET_PORT.asString(), targetPort};
    }
}

