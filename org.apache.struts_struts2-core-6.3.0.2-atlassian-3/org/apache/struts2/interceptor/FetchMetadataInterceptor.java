/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.TextParseUtil;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.interceptor.ResourceIsolationPolicy;
import org.apache.struts2.interceptor.StrutsResourceIsolationPolicy;

public class FetchMetadataInterceptor
extends AbstractInterceptor {
    private static final Logger LOG = LogManager.getLogger(FetchMetadataInterceptor.class);
    private static final String VARY_HEADER_VALUE = String.format("%s,%s,%s,%s", "Sec-Fetch-Dest", "Sec-Fetch-Mode", "Sec-Fetch-Site", "Sec-Fetch-User");
    private static final String SC_FORBIDDEN = String.valueOf(403);
    private final Set<String> exemptedPaths = new HashSet<String>();
    private final ResourceIsolationPolicy resourceIsolationPolicy = new StrutsResourceIsolationPolicy();

    @Inject(required=false)
    public void setExemptedPaths(String paths) {
        this.exemptedPaths.addAll(TextParseUtil.commaDelimitedStringToSet(paths));
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ActionContext context = invocation.getInvocationContext();
        HttpServletRequest request = context.getServletRequest();
        this.addVaryHeaders(invocation);
        String contextPath = request.getContextPath();
        if (this.exemptedPaths.contains(contextPath)) {
            return invocation.invoke();
        }
        if (this.resourceIsolationPolicy.isRequestAllowed(request)) {
            return invocation.invoke();
        }
        LOG.warn("Fetch metadata rejected cross-origin request to: {}", (Object)contextPath);
        return SC_FORBIDDEN;
    }

    private void addVaryHeaders(ActionInvocation invocation) {
        HttpServletResponse response = invocation.getInvocationContext().getServletResponse();
        if (LOG.isDebugEnabled() && response.containsHeader("Vary")) {
            LOG.debug("HTTP response already has header: {} set, the old value will be overwritten (replaced)", (Object)"Vary");
        }
        response.setHeader("Vary", VARY_HEADER_VALUE);
    }
}

