/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.GenericFilter
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.threads.ScheduledThreadPoolExecutor
 */
package org.apache.catalina.filters;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.util.TimeBucketCounter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.threads.ScheduledThreadPoolExecutor;

public class RateLimitFilter
extends GenericFilter {
    private static final long serialVersionUID = 1L;
    public static final int DEFAULT_BUCKET_DURATION = 60;
    public static final int DEFAULT_BUCKET_REQUESTS = 300;
    public static final boolean DEFAULT_ENFORCE = true;
    public static final int DEFAULT_STATUS_CODE = 429;
    public static final String DEFAULT_STATUS_MESSAGE = "Too many requests";
    public static final String RATE_LIMIT_ATTRIBUTE_COUNT = "org.apache.catalina.filters.RateLimitFilter.Count";
    public static final String PARAM_BUCKET_DURATION = "bucketDuration";
    public static final String PARAM_BUCKET_REQUESTS = "bucketRequests";
    public static final String PARAM_ENFORCE = "enforce";
    public static final String PARAM_STATUS_CODE = "statusCode";
    public static final String PARAM_STATUS_MESSAGE = "statusMessage";
    transient TimeBucketCounter bucketCounter;
    private int actualRequests;
    private int bucketRequests = 300;
    private int bucketDuration = 60;
    private boolean enforce = true;
    private int statusCode = 429;
    private String statusMessage = "Too many requests";
    private transient Log log = LogFactory.getLog(RateLimitFilter.class);
    private static final StringManager sm = StringManager.getManager(RateLimitFilter.class);

    public int getActualRequests() {
        return this.actualRequests;
    }

    public int getActualDurationInSeconds() {
        return this.bucketCounter.getActualDuration() / 1000;
    }

    public void init() throws ServletException {
        ScheduledExecutorService executorService;
        FilterConfig config = this.getFilterConfig();
        String param = config.getInitParameter(PARAM_BUCKET_DURATION);
        if (param != null) {
            this.bucketDuration = Integer.parseInt(param);
        }
        if ((param = config.getInitParameter(PARAM_BUCKET_REQUESTS)) != null) {
            this.bucketRequests = Integer.parseInt(param);
        }
        if ((param = config.getInitParameter(PARAM_ENFORCE)) != null) {
            this.enforce = Boolean.parseBoolean(param);
        }
        if ((param = config.getInitParameter(PARAM_STATUS_CODE)) != null) {
            this.statusCode = Integer.parseInt(param);
        }
        if ((param = config.getInitParameter(PARAM_STATUS_MESSAGE)) != null) {
            this.statusMessage = param;
        }
        if ((executorService = (ScheduledExecutorService)this.getServletContext().getAttribute(ScheduledThreadPoolExecutor.class.getName())) == null) {
            executorService = new java.util.concurrent.ScheduledThreadPoolExecutor(1);
        }
        this.bucketCounter = new TimeBucketCounter(this.bucketDuration, executorService);
        this.actualRequests = (int)Math.round(this.bucketCounter.getRatio() * (double)this.bucketRequests);
        this.log.info((Object)sm.getString("rateLimitFilter.initialized", new Object[]{super.getFilterName(), this.bucketRequests, this.bucketDuration, this.getActualRequests(), this.getActualDurationInSeconds(), (!this.enforce ? "Not " : "") + "enforcing"}));
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String ipAddr = request.getRemoteAddr();
        int reqCount = this.bucketCounter.increment(ipAddr);
        request.setAttribute(RATE_LIMIT_ATTRIBUTE_COUNT, (Object)reqCount);
        if (this.enforce && reqCount > this.actualRequests) {
            ((HttpServletResponse)response).sendError(this.statusCode, this.statusMessage);
            this.log.warn((Object)sm.getString("rateLimitFilter.maxRequestsExceeded", new Object[]{super.getFilterName(), reqCount, ipAddr, this.getActualRequests(), this.getActualDurationInSeconds()}));
            return;
        }
        chain.doFilter(request, response);
    }

    public void destroy() {
        this.bucketCounter.destroy();
        super.destroy();
    }
}

