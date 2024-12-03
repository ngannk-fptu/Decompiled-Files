/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.result.plain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.struts2.result.plain.DateHttpHeader;
import org.apache.struts2.result.plain.HttpHeader;
import org.apache.struts2.result.plain.IntHttpHeader;
import org.apache.struts2.result.plain.StringHttpHeader;

class HttpHeaders {
    private final List<HttpHeader<String>> stringHeaders = new ArrayList<HttpHeader<String>>();
    private final List<HttpHeader<Long>> dateHeaders = new ArrayList<HttpHeader<Long>>();
    private final List<HttpHeader<Integer>> intHeaders = new ArrayList<HttpHeader<Integer>>();

    HttpHeaders() {
    }

    public HttpHeaders add(String name, String value) {
        this.stringHeaders.add(new StringHttpHeader(name, value));
        return this;
    }

    public HttpHeaders add(String name, Long value) {
        this.dateHeaders.add(new DateHttpHeader(name, value));
        return this;
    }

    public HttpHeaders add(String name, Integer value) {
        this.intHeaders.add(new IntHttpHeader(name, value));
        return this;
    }

    public List<HttpHeader<String>> getStringHeaders() {
        return Collections.unmodifiableList(this.stringHeaders);
    }

    public List<HttpHeader<Long>> getDateHeaders() {
        return Collections.unmodifiableList(this.dateHeaders);
    }

    public List<HttpHeader<Integer>> getIntHeaders() {
        return Collections.unmodifiableList(this.intHeaders);
    }
}

