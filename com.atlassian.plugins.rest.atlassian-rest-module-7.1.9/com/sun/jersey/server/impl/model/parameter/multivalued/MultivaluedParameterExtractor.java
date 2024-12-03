/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.parameter.multivalued;

import javax.ws.rs.core.MultivaluedMap;

public interface MultivaluedParameterExtractor {
    public String getName();

    public String getDefaultStringValue();

    public Object extract(MultivaluedMap<String, String> var1);
}

