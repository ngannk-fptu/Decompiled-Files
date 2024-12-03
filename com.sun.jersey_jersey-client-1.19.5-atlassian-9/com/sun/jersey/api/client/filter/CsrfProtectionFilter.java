/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client.filter;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CsrfProtectionFilter
extends ClientFilter {
    private static final Set<String> METHODS_TO_IGNORE;
    private static final String HEADER_NAME = "X-Requested-By";
    private final String requestedBy;

    public CsrfProtectionFilter() {
        this("");
    }

    public CsrfProtectionFilter(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    @Override
    public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
        if (!METHODS_TO_IGNORE.contains(cr.getMethod()) && !cr.getHeaders().containsKey((Object)HEADER_NAME)) {
            cr.getHeaders().add((Object)HEADER_NAME, (Object)this.requestedBy);
        }
        return this.getNext().handle(cr);
    }

    static {
        HashSet<String> mti = new HashSet<String>();
        mti.add("GET");
        mti.add("OPTIONS");
        mti.add("HEAD");
        METHODS_TO_IGNORE = Collections.unmodifiableSet(mti);
    }
}

