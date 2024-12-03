/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.sun.jersey.core.util.StringKeyStringValueIgnoreCaseMultivaluedMap
 *  javax.ws.rs.core.MultivaluedMap
 */
package com.atlassian.crowd.directory.query;

import com.atlassian.crowd.directory.query.MicrosoftGraphQueryParam;
import com.google.common.base.Charsets;
import com.sun.jersey.core.util.StringKeyStringValueIgnoreCaseMultivaluedMap;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import javax.ws.rs.core.MultivaluedMap;

public final class MicrosoftGraphQueryParams {
    private MicrosoftGraphQueryParams() {
    }

    public static String asEncodedQueryParam(MicrosoftGraphQueryParam microsoftGraphQueryParam) {
        try {
            return URLEncoder.encode(microsoftGraphQueryParam.asRawValue(), Charsets.UTF_8.name());
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static MultivaluedMap<String, String> asQueryParams(MicrosoftGraphQueryParam ... microsoftGraphQueryParam) {
        StringKeyStringValueIgnoreCaseMultivaluedMap map = new StringKeyStringValueIgnoreCaseMultivaluedMap();
        Arrays.stream(microsoftGraphQueryParam).forEach(param -> map.add(param.getName(), (Object)param.asRawValue()));
        return map;
    }
}

