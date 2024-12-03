/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpMethod
 */
package com.atlassian.crowd.embedded.admin.list;

import org.springframework.http.HttpMethod;

public class DirectoryListItemOperation {
    private final String url;
    private final String classAttribute;
    private final HttpMethod httpMethod;

    public DirectoryListItemOperation(String url, String classAttribute, HttpMethod httpMethod) {
        this.url = url;
        this.classAttribute = classAttribute;
        this.httpMethod = httpMethod;
    }

    public String getUrl() {
        return this.url;
    }

    public String getClassAttribute() {
        return this.classAttribute;
    }

    public HttpMethod getHttpMethod() {
        return this.httpMethod;
    }
}

