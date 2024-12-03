/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.Resource
 *  org.springframework.http.HttpHeaders
 */
package org.springframework.web.servlet.resource;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

public interface HttpResource
extends Resource {
    public HttpHeaders getResponseHeaders();
}

