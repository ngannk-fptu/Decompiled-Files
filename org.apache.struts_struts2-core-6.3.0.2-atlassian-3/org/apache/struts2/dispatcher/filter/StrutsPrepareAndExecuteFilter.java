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
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.ExecuteOperations;
import org.apache.struts2.dispatcher.InitOperations;
import org.apache.struts2.dispatcher.PrepareOperations;
import org.apache.struts2.dispatcher.filter.FilterHostConfig;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

public class StrutsPrepareAndExecuteFilter
implements StrutsStatics,
Filter {
    private static final Logger LOG = LogManager.getLogger(StrutsPrepareAndExecuteFilter.class);
    protected PrepareOperations prepare;
    protected ExecuteOperations execute;
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
            init.initStaticContentLoader(config, dispatcher);
            this.prepare = this.createPrepareOperations(dispatcher);
            this.execute = this.createExecuteOperations(dispatcher);
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

    protected ExecuteOperations createExecuteOperations(Dispatcher dispatcher) {
        return new ExecuteOperations(dispatcher);
    }

    protected void postInit(Dispatcher dispatcher, FilterConfig filterConfig) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
        try {
            this.prepare.trackRecursion(request);
            String uri = RequestUtils.getUri(request);
            if (this.prepare.isUrlExcluded(request, this.excludedPatterns)) {
                LOG.trace("Request: {} is excluded from handling by Struts, passing request to other filters", (Object)uri);
                chain.doFilter((ServletRequest)request, (ServletResponse)response);
            } else {
                this.tryHandleRequest(chain, request, response, uri);
            }
        }
        finally {
            this.prepare.cleanupRequest(request);
        }
    }

    private void tryHandleRequest(FilterChain chain, HttpServletRequest request, HttpServletResponse response, String uri) throws IOException, ServletException {
        LOG.trace("Checking if: {} is a static resource", (Object)uri);
        boolean handled = this.execute.executeStaticResourceRequest(request, response);
        if (!handled) {
            LOG.trace("Uri: {} is not a static resource, assuming action", (Object)uri);
            this.handleRequest(chain, request, response, uri);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleRequest(FilterChain chain, HttpServletRequest request, HttpServletResponse response, String uri) throws ServletException, IOException {
        this.prepare.setEncodingAndLocale(request, response);
        this.prepare.createActionContext(request, response);
        this.prepare.assignDispatcherToThread();
        HttpServletRequest wrappedRequest = this.prepare.wrapRequest(request);
        try {
            ActionMapping mapping = this.prepare.findActionMapping(wrappedRequest, response, true);
            if (mapping == null) {
                LOG.trace("Cannot find mapping for: {}, passing to other filters", (Object)uri);
                chain.doFilter((ServletRequest)request, (ServletResponse)response);
            } else {
                LOG.trace("Found mapping: {} for: {}", (Object)mapping, (Object)uri);
                this.execute.executeAction(wrappedRequest, response, mapping);
            }
        }
        finally {
            this.prepare.cleanupWrappedRequest(wrappedRequest);
        }
    }

    public void destroy() {
        this.prepare.cleanupDispatcher();
    }
}

