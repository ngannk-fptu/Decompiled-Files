/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.catalina.filters;

import java.io.IOException;
import java.util.regex.Pattern;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.filters.FilterBase;

public abstract class RequestFilter
extends FilterBase {
    protected Pattern allow = null;
    protected Pattern deny = null;
    protected int denyStatus = 403;
    private static final String PLAIN_TEXT_MIME_TYPE = "text/plain";

    public String getAllow() {
        if (this.allow == null) {
            return null;
        }
        return this.allow.toString();
    }

    public void setAllow(String allow) {
        this.allow = allow == null || allow.length() == 0 ? null : Pattern.compile(allow);
    }

    public String getDeny() {
        if (this.deny == null) {
            return null;
        }
        return this.deny.toString();
    }

    public void setDeny(String deny) {
        this.deny = deny == null || deny.length() == 0 ? null : Pattern.compile(deny);
    }

    public int getDenyStatus() {
        return this.denyStatus;
    }

    public void setDenyStatus(int denyStatus) {
        this.denyStatus = denyStatus;
    }

    public abstract void doFilter(ServletRequest var1, ServletResponse var2, FilterChain var3) throws IOException, ServletException;

    @Override
    protected boolean isConfigProblemFatal() {
        return true;
    }

    protected void process(String property, ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (this.isAllowed(property)) {
            chain.doFilter(request, response);
        } else if (response instanceof HttpServletResponse) {
            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug((Object)sm.getString("requestFilter.deny", new Object[]{((HttpServletRequest)request).getRequestURI(), property}));
            }
            ((HttpServletResponse)response).sendError(this.denyStatus);
        } else {
            this.sendErrorWhenNotHttp(response);
        }
    }

    private boolean isAllowed(String property) {
        if (this.deny != null && this.deny.matcher(property).matches()) {
            return false;
        }
        if (this.allow != null && this.allow.matcher(property).matches()) {
            return true;
        }
        return this.deny != null && this.allow == null;
    }

    private void sendErrorWhenNotHttp(ServletResponse response) throws IOException {
        response.setContentType(PLAIN_TEXT_MIME_TYPE);
        response.getWriter().write(sm.getString("http.403"));
        response.getWriter().flush();
    }
}

