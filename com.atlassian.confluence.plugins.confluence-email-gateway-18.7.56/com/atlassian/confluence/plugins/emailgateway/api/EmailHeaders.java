/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.annotations.PublicApi;
import com.google.common.collect.Maps;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@PublicApi
public class EmailHeaders
implements Serializable {
    private Map<String, List<String>> headers;

    private EmailHeaders() {
    }

    public EmailHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public Set<String> getHeaderNames() {
        return this.headers.keySet();
    }

    public List<String> getHeaderValues(String headerName) {
        if (this.headers.containsKey(headerName)) {
            return Collections.unmodifiableList(this.headers.get(headerName));
        }
        return Collections.emptyList();
    }

    public Map<String, List<String>> getAllHeaders() {
        LinkedHashMap mapCopy = Maps.newLinkedHashMap();
        for (String headerName : this.headers.keySet()) {
            mapCopy.put(headerName, this.getHeaderValues(headerName));
        }
        return Collections.unmodifiableMap(mapCopy);
    }
}

