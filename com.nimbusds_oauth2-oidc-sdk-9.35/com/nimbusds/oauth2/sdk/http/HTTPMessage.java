/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.common.contenttype.ContentType
 */
package com.nimbusds.oauth2.sdk.http;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.ContentTypeUtils;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

abstract class HTTPMessage {
    private final Map<String, List<String>> headers = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
    private String clientIPAddress;

    HTTPMessage() {
    }

    public ContentType getEntityContentType() {
        String value = this.getHeaderValue("Content-Type");
        if (value == null) {
            return null;
        }
        try {
            return ContentType.parse((String)value);
        }
        catch (java.text.ParseException e) {
            return null;
        }
    }

    public void setEntityContentType(ContentType ct) {
        this.setHeader("Content-Type", ct != null ? ct.toString() : null);
    }

    public void setContentType(String ct) throws ParseException {
        try {
            this.setHeader("Content-Type", ct != null ? ContentType.parse((String)ct).toString() : null);
        }
        catch (java.text.ParseException e) {
            throw new ParseException("Invalid Content-Type value: " + e.getMessage());
        }
    }

    public void ensureEntityContentType() throws ParseException {
        if (this.getEntityContentType() == null) {
            throw new ParseException("Missing HTTP Content-Type header");
        }
    }

    public void ensureEntityContentType(ContentType contentType) throws ParseException {
        ContentTypeUtils.ensureContentType(contentType, this.getEntityContentType());
    }

    public String getHeaderValue(String name) {
        return MultivaluedMapUtils.getFirstValue(this.headers, name);
    }

    public List<String> getHeaderValues(String name) {
        return this.headers.get(name);
    }

    public void setHeader(String name, String ... values) {
        if (values != null && values.length > 0) {
            this.headers.put(name, Arrays.asList(values));
        } else {
            this.headers.remove(name);
        }
    }

    public Map<String, List<String>> getHeaderMap() {
        return this.headers;
    }

    public String getClientIPAddress() {
        return this.clientIPAddress;
    }

    public void setClientIPAddress(String clientIPAddress) {
        this.clientIPAddress = clientIPAddress;
    }
}

