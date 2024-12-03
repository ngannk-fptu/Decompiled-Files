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
 *  org.apache.struts2.dispatcher.Dispatcher
 *  org.apache.struts2.dispatcher.HostConfig
 *  org.apache.struts2.dispatcher.InitOperations
 *  org.apache.struts2.dispatcher.filter.StrutsPrepareFilter
 */
package com.atlassian.confluence.impl.struts;

import com.atlassian.confluence.impl.struts.ConfluenceStrutsDispatcher;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.HostConfig;
import org.apache.struts2.dispatcher.InitOperations;
import org.apache.struts2.dispatcher.filter.StrutsPrepareFilter;

public class ConfluenceStrutsPrepareFilter
extends StrutsPrepareFilter {
    protected InitOperations createInitOperations() {
        return new InitOperations(){

            public Dispatcher initDispatcher(HostConfig filterConfig) {
                return ConfluenceStrutsDispatcher.get();
            }
        };
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        if (this.prepare.isUrlExcluded(request, this.excludedPatterns)) {
            HttpServletResponse response = (HttpServletResponse)res;
            this.prepare.createActionContext(request, response);
        }
        super.doFilter(req, res, chain);
    }
}

