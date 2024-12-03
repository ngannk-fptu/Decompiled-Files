/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 */
package com.opensymphony.module.sitemesh.filter;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class RequestDispatcherWrapper
implements RequestDispatcher {
    private RequestDispatcher rd = null;
    private boolean done = false;

    public RequestDispatcherWrapper(RequestDispatcher rd) {
        this.rd = rd;
    }

    public void forward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        if (this.done) {
            throw new IllegalStateException("Response has already been committed");
        }
        this.include(servletRequest, servletResponse);
        this.done = true;
    }

    public void include(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        if (this.done) {
            throw new IllegalStateException("Response has already been committed");
        }
        this.rd.include(servletRequest, servletResponse);
    }
}

