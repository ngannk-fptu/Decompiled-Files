/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 */
package com.atlassian.core.filters.legacy;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public final class NoContentLocationHeaderResponseWrapper
extends HttpServletResponseWrapper {
    public NoContentLocationHeaderResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    public void setHeader(String name, String value) {
        if (this.isContentLocationHeader(name)) {
            return;
        }
        super.setHeader(name, value);
    }

    public void addHeader(String name, String value) {
        if (this.isContentLocationHeader(name)) {
            return;
        }
        super.addHeader(name, value);
    }

    private boolean isContentLocationHeader(String headerName) {
        return headerName != null && "content-location".equalsIgnoreCase(headerName);
    }
}

