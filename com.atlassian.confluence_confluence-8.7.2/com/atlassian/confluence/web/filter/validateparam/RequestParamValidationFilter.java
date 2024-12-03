/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  com.atlassian.spring.container.ContainerManager
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.web.filter.validateparam;

import com.atlassian.confluence.web.filter.validateparam.InvalidParameterEncodingException;
import com.atlassian.confluence.web.filter.validateparam.RequestParamCleaningWhitelistStrategy;
import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.spring.container.ContainerManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestParamValidationFilter
extends AbstractHttpFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestParamValidationFilter.class);
    private static final String ALREADY_FILTERED = RequestParamValidationFilter.class.getName() + "_already_filtered";
    private String whitelistStrategyBeanName;
    private RequestParamCleaningWhitelistStrategy whitelistStrategy;

    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        this.whitelistStrategyBeanName = filterConfig.getInitParameter("whitelistStrategy");
    }

    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (req.getAttribute(ALREADY_FILTERED) != null) {
            chain.doFilter((ServletRequest)req, (ServletResponse)res);
            return;
        }
        req.setAttribute(ALREADY_FILTERED, (Object)Boolean.TRUE);
        try {
            Map parameterMap = req.getParameterMap();
            this.validateParams(req.getServletPath(), parameterMap);
            chain.doFilter((ServletRequest)req, (ServletResponse)res);
        }
        catch (UnsupportedEncodingException e) {
            res.sendError(400, "Invalid parameter encoding");
        }
    }

    private void validateParams(String path, Map<String, String[]> rawParams) throws UnsupportedEncodingException {
        if (rawParams != null) {
            for (Map.Entry<String, String[]> e : rawParams.entrySet()) {
                this.validateParam(path, e.getKey(), e.getValue());
            }
        }
    }

    private void validateParam(String path, String paramName, String[] rawParamValues) throws UnsupportedEncodingException {
        RequestParamCleaningWhitelistStrategy whitelistStrategy = this.getWhitelistStrategy(path, paramName);
        if (whitelistStrategy != null && whitelistStrategy.isWhiteListed(path, paramName)) {
            return;
        }
        if (rawParamValues != null) {
            for (String rawParamValue : rawParamValues) {
                if (rawParamValue == null || !rawParamValue.contains("\u0000")) continue;
                log.warn("Potential attempt by user to send null data in parameter. [path={};paramName={}]", (Object)path, (Object)paramName);
                throw new InvalidParameterEncodingException(paramName, rawParamValue);
            }
        }
    }

    private RequestParamCleaningWhitelistStrategy getWhitelistStrategy(String path, String paramName) {
        if (this.whitelistStrategyBeanName != null && this.whitelistStrategy == null) {
            if (ContainerManager.isContainerSetup()) {
                this.whitelistStrategy = (RequestParamCleaningWhitelistStrategy)ContainerManager.getComponent((String)this.whitelistStrategyBeanName);
            } else {
                log.debug("Spring container is not ready - no white listing being used. [path={};paramName={}]", (Object)path, (Object)paramName);
            }
        }
        return this.whitelistStrategy;
    }
}

