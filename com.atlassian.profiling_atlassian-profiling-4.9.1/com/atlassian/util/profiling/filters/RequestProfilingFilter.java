/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.util.profiling.filters;

import com.atlassian.annotations.Internal;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

@Internal
public class RequestProfilingFilter
implements Filter {
    private static final String ACTIVATE_PARAM = "activate.param";
    private String activateParameterName;

    public void destroy() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        block28: {
            try {
                if (this.activateParameterName == null || request.getParameter(this.activateParameterName) == null) {
                    chain.doFilter(request, response);
                    break block28;
                }
                String timerName = ((HttpServletRequest)request).getRequestURI();
                try (Ticker ignored = Timers.getConfiguration().enableForThread();
                     Ticker ignored2 = Timers.start(timerName);){
                    chain.doFilter(request, response);
                }
            }
            finally {
                Timers.onRequestEnd();
            }
        }
    }

    public void init(FilterConfig filterConfig) {
        String param;
        if (filterConfig != null && (param = filterConfig.getInitParameter(ACTIVATE_PARAM)) != null && !param.trim().isEmpty()) {
            this.activateParameterName = param.trim();
        }
    }
}

