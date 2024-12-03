/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.multipart;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartRequest;

public interface MultipartHttpServletRequest
extends HttpServletRequest,
MultipartRequest {
    @Nullable
    public HttpMethod getRequestMethod();

    public HttpHeaders getRequestHeaders();

    @Nullable
    public HttpHeaders getMultipartHeaders(String var1);
}

