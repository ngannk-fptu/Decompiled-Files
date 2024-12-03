/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.dispatcher.filter;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.InitOperations;
import org.apache.struts2.dispatcher.PrepareOperations;
import org.apache.struts2.dispatcher.filter.FilterHostConfig;

public class StrutsPrepareFilter
implements StrutsStatics,
Filter {
    protected static final String REQUEST_EXCLUDED_FROM_ACTION_MAPPING = StrutsPrepareFilter.class.getName() + ".REQUEST_EXCLUDED_FROM_ACTION_MAPPING";
    protected PrepareOperations prepare;
    protected List<Pattern> excludedPatterns;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        InitOperations init = this.createInitOperations();
        Dispatcher dispatcher = null;
        try {
            FilterHostConfig config = new FilterHostConfig(filterConfig);
            dispatcher = init.initDispatcher(config);
            this.prepare = this.createPrepareOperations(dispatcher);
            this.excludedPatterns = init.buildExcludedPatternsList(dispatcher);
            this.postInit(dispatcher, filterConfig);
        }
        finally {
            if (dispatcher != null) {
                dispatcher.cleanUpAfterInit();
            }
            init.cleanup();
        }
    }

    protected InitOperations createInitOperations() {
        return new InitOperations();
    }

    protected PrepareOperations createPrepareOperations(Dispatcher dispatcher) {
        return new PrepareOperations(dispatcher);
    }

    protected void postInit(Dispatcher dispatcher, FilterConfig filterConfig) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
        boolean didWrap = false;
        try {
            this.prepare.trackRecursion(request);
            if (this.prepare.isUrlExcluded(request, this.excludedPatterns)) {
                request.setAttribute(REQUEST_EXCLUDED_FROM_ACTION_MAPPING, (Object)true);
            } else {
                request.setAttribute(REQUEST_EXCLUDED_FROM_ACTION_MAPPING, (Object)false);
                this.prepare.setEncodingAndLocale(request, response);
                this.prepare.createActionContext(request, response);
                this.prepare.assignDispatcherToThread();
                request = this.prepare.wrapRequest(request);
                didWrap = true;
                this.prepare.findActionMapping(request, response, true);
            }
            chain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
        finally {
            if (didWrap) {
                this.prepare.cleanupWrappedRequest(request);
            }
            this.prepare.cleanupRequest(request);
        }
    }

    public void destroy() {
        this.prepare.cleanupDispatcher();
    }
}

