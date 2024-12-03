/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.http;

import com.amazonaws.annotation.Immutable;
import com.amazonaws.http.HttpResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Immutable
public class SdkHttpMetadata {
    private final Map<String, List<String>> allHeaders;
    private final Map<String, String> httpHeaders;
    private final int httpStatusCode;

    private SdkHttpMetadata(Map<String, String> httpHeaders, Map<String, List<String>> allHeaders, int httpStatusCode) {
        this.httpHeaders = Collections.unmodifiableMap(httpHeaders);
        this.allHeaders = this.unmodifiableHeaders(allHeaders);
        this.httpStatusCode = httpStatusCode;
    }

    public Map<String, String> getHttpHeaders() {
        return this.httpHeaders;
    }

    public Map<String, List<String>> getAllHttpHeaders() {
        return this.allHeaders;
    }

    public int getHttpStatusCode() {
        return this.httpStatusCode;
    }

    public static SdkHttpMetadata from(HttpResponse httpResponse) {
        return new SdkHttpMetadata(httpResponse.getHeaders(), httpResponse.getAllHeaders(), httpResponse.getStatusCode());
    }

    private Map<String, List<String>> unmodifiableHeaders(Map<String, List<String>> allHeaders) {
        HashMap<String, List<String>> unmodifiable = new HashMap<String, List<String>>();
        for (Map.Entry<String, List<String>> e : allHeaders.entrySet()) {
            unmodifiable.put(e.getKey(), Collections.unmodifiableList(e.getValue()));
        }
        return Collections.unmodifiableMap(unmodifiable);
    }
}

