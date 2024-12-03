/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpHost
 *  org.apache.http.conn.routing.HttpRoute
 *  org.apache.http.protocol.HttpContext
 */
package io.micrometer.core.instrument.binder.httpcomponents;

import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.httpcomponents.ApacheHttpClientObservationDocumentation;
import org.apache.http.HttpHost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.protocol.HttpContext;

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

