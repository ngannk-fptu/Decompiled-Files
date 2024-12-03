/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.labs.botkiller;

import com.atlassian.labs.botkiller.BotKiller;
import com.atlassian.sal.api.user.UserManager;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotKillerFilter
implements Filter {
    private final BotKiller botKiller;
    private static final Logger log = LoggerFactory.getLogger(BotKillerFilter.class);

    public BotKillerFilter(UserManager userManager) {
        this.botKiller = new BotKiller(userManager);
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
        if (httpServletRequest.getAttribute(BotKillerFilter.class.getName()) != null) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        httpServletRequest.setAttribute(BotKillerFilter.class.getName(), (Object)Boolean.TRUE);
        filterChain.doFilter(servletRequest, servletResponse);
        this.botKiller.processRequest(httpServletRequest);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("The Atlassian BotKiller plugin has been started.  The hunt is afoot!");
    }

    public void destroy() {
        log.info("The Atlassian BotKiller plugin has stopped hunting.");
    }
}

