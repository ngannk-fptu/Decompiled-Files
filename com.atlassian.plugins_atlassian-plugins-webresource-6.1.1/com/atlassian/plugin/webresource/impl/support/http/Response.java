/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.util.LastModifiedHandler
 *  javax.annotation.Nonnull
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.io.output.CountingOutputStream
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.webresource.impl.support.http;

import com.atlassian.plugin.servlet.util.LastModifiedHandler;
import com.atlassian.plugin.webresource.impl.support.http.Request;
import java.io.IOException;
import java.io.OutputStream;
import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Response {
    private static final Logger log = LoggerFactory.getLogger(Response.class);
    private final Request request;
    private final HttpServletResponse originalResponse;
    private final CountingOutputStream decoratedOutputStream;

    public Response(Request request, HttpServletResponse response) {
        this.request = request;
        this.originalResponse = response;
        try {
            this.decoratedOutputStream = new CountingOutputStream((OutputStream)response.getOutputStream());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addHeader(String name, String value) {
        this.originalResponse.addHeader(name, value);
    }

    @Deprecated
    public boolean checkRequestHelper(@Nonnull LastModifiedHandler lastModifiedHandler) {
        return lastModifiedHandler.checkRequest(this.request.getOriginalRequest(), this.originalResponse);
    }

    public void sendRedirect(String location, String contentType) {
        this.setContentTypeIfNotBlank(contentType);
        try {
            this.request.getOriginalRequest().getRequestDispatcher(location).forward((ServletRequest)this.request.getOriginalRequest(), (ServletResponse)this.originalResponse);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (ServletException e2) {
            throw new RuntimeException(e2);
        }
    }

    public void sendError(int code) {
        log.debug("Sending error code {} for response to request with URL {}", (Object)code, (Object)this.request.getPath());
        try {
            this.originalResponse.sendError(code);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setContentType(String contentType) {
        this.originalResponse.setContentType(contentType);
    }

    public OutputStream getOutputStream() {
        return this.decoratedOutputStream;
    }

    public void setContentTypeIfNotBlank(String contentType) {
        if (StringUtils.isNotBlank((CharSequence)contentType)) {
            this.setContentType(contentType);
        }
    }

    public void setStatus(int status) {
        this.originalResponse.setStatus(status);
    }

    public int getStatus() {
        return this.originalResponse.getStatus();
    }

    public long numBytesWritten() {
        return this.decoratedOutputStream.getByteCount();
    }
}

