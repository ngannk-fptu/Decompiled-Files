/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.Header
 *  org.apache.http.HttpRequest
 */
package io.micrometer.core.instrument.binder.httpcomponents;

import java.util.function.Function;
import org.apache.http.Header;
import org.apache.http.HttpRequest;

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

