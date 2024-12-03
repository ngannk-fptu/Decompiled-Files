/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.HttpRequest
 */
package io.micrometer.core.instrument.binder.httpcomponents.hc5;

import java.util.function.Function;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpRequest;

public class DefaultUriMapper
implements Function<HttpRequest, String> {
    public static final String URI_PATTERN_HEADER = "URI_PATTERN";

    @Override
    public String apply(HttpRequest httpRequest) {
        Header uriPattern = httpRequest.getLastHeader(URI_PATTERN_HEADER);
        if (uriPattern != null && uriPattern.getValue() != null) {
            return uriPattern.getValue();
        }
        return "UNKNOWN";
    }
}

