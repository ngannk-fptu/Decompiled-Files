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
import org.apache.struts2.dispatcher.ExecuteOperations;
import org.apache.struts2.dispatcher.InitOperations;
import org.apache.struts2.dispatcher.PrepareOperations;
import org.apache.struts2.dispatcher.filter.FilterHostConfig;
import org.apache.struts2.dispatcher.filter.StrutsPrepareFilter;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

public class StrutsExecuteFilter
implements StrutsStatics,
Filter {
    protected PrepareOperations prepare;
    protected ExecuteOperations execute;
    protected FilterConfig filterConfig;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    protected synchronized void lazyInit() {
        if (this.execute == null) {
            InitOperations init = this.createInitOperations();
            Dispatcher dispatcher = init.findDispatcherOnThread();
            init.initStaticContentLoader(new FilterHostConfig(this.filterConfig), dispatcher);
            this.prepare = this.createPrepareOperations(dispatcher);
            this.execute = this.createExecuteOperations(dispatcher);
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

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        ActionMapping mapping;
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
        if (this.excludeUrl(request)) {
            chain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        if (this.execute == null) {
            this.lazyInit();
        }
        if ((mapping = this.prepare.findActionMapping(request, response)) != null) {
            this.execute.executeAction(request, response, mapping);
        } else if (!this.execute.executeStaticResourceRequest(request, response)) {
            chain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
    }

    private boolean excludeUrl(HttpServletRequest request) {
        return Boolean.TRUE.equals(request.getAttribute(StrutsPrepareFilter.REQUEST_EXCLUDED_FROM_ACTION_MAPPING));
    }

    public void destroy() {
        this.prepare = null;
        this.execute = null;
        this.filterConfig = null;
    }
}

