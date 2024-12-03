/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.concurrent.LazyReference$InitializationException
 *  com.opensymphony.module.sitemesh.Config
 *  com.opensymphony.module.sitemesh.Factory
 *  com.opensymphony.sitemesh.ContentProcessor
 *  com.opensymphony.sitemesh.DecoratorSelector
 *  com.opensymphony.sitemesh.webapp.SiteMeshFilter
 *  com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.confluence.impl.profiling.DecoratorTimings;
import com.atlassian.confluence.util.profiling.ConfluenceDecoratorSelector;
import com.atlassian.util.concurrent.LazyReference;
import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Factory;
import com.opensymphony.sitemesh.ContentProcessor;
import com.opensymphony.sitemesh.DecoratorSelector;
import com.opensymphony.sitemesh.webapp.SiteMeshFilter;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfilingSiteMeshFilter
extends SiteMeshFilter {
    private static final Logger LOG = LoggerFactory.getLogger(ProfilingSiteMeshFilter.class);
    private static final String ALREADY_APPLIED_KEY = "com.opensymphony.sitemesh.APPLIED_ONCE";
    private Factory smFactory;

    public void init(FilterConfig filterConfig) {
        super.init(filterConfig);
        this.smFactory = Factory.getInstance((Config)new Config(filterConfig));
    }

    public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain) throws IOException, ServletException {
        rq.setAttribute(ALREADY_APPLIED_KEY, (Object)false);
        Runnable timingsPublisher = DecoratorTimings.createTimingsPublisherAndAttach(rq);
        try {
            super.doFilter(rq, rs, chain);
            timingsPublisher.run();
        }
        catch (LazyReference.InitializationException | IllegalStateException e) {
            LOG.error("Application context has not been initialized: {}", (Object)e.getMessage());
            LOG.debug("Application context has not been initialized", e);
            chain.doFilter(rq, rs);
        }
    }

    @Deprecated(since="8.0", forRemoval=true)
    public static synchronized void ensureFactorySetup(ServletConfig ignored) {
    }

    @Deprecated(since="8.0", forRemoval=true)
    public static void setupFactory(ServletConfig ignored) {
    }

    protected DecoratorSelector initDecoratorSelector(SiteMeshWebAppContext webAppContext) {
        return new ConfluenceDecoratorSelector(this.smFactory.getDecoratorMapper(), webAppContext.getRequest().getDispatcherType());
    }

    protected ContentProcessor initContentProcessor(SiteMeshWebAppContext webAppContext) {
        return super.initContentProcessor(webAppContext);
    }
}

