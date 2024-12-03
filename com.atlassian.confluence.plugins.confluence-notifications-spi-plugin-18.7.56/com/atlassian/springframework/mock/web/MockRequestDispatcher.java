/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.util.Assert
 */
package com.atlassian.springframework.mock.web;

import com.atlassian.springframework.mock.web.MockHttpServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

public class MockRequestDispatcher
implements RequestDispatcher {
    private final Log logger = LogFactory.getLog(this.getClass());
    private final String resource;

    public MockRequestDispatcher(String resource) {
        Assert.notNull((Object)resource, (String)"resource must not be null");
        this.resource = resource;
    }

    public void forward(ServletRequest request, ServletResponse response) {
        Assert.notNull((Object)request, (String)"Request must not be null");
        Assert.notNull((Object)response, (String)"Response must not be null");
        if (response.isCommitted()) {
            throw new IllegalStateException("Cannot perform forward - response is already committed");
        }
        this.getMockHttpServletResponse(response).setForwardedUrl(this.resource);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("MockRequestDispatcher: forwarding to [" + this.resource + "]"));
        }
    }

    public void include(ServletRequest request, ServletResponse response) {
        Assert.notNull((Object)request, (String)"Request must not be null");
        Assert.notNull((Object)response, (String)"Response must not be null");
        this.getMockHttpServletResponse(response).addIncludedUrl(this.resource);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("MockRequestDispatcher: including [" + this.resource + "]"));
        }
    }

    protected MockHttpServletResponse getMockHttpServletResponse(ServletResponse response) {
        if (response instanceof MockHttpServletResponse) {
            return (MockHttpServletResponse)response;
        }
        if (response instanceof HttpServletResponseWrapper) {
            return this.getMockHttpServletResponse(((HttpServletResponseWrapper)response).getResponse());
        }
        throw new IllegalArgumentException("MockRequestDispatcher requires MockHttpServletResponse");
    }
}

