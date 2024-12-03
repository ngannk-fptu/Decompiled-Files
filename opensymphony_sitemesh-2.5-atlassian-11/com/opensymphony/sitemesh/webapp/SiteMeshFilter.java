/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.opensymphony.sitemesh.webapp;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Factory;
import com.opensymphony.module.sitemesh.RequestConstants;
import com.opensymphony.module.sitemesh.scalability.ScalabilitySupport;
import com.opensymphony.module.sitemesh.scalability.ScalabilitySupportConfiguration;
import com.opensymphony.module.sitemesh.scalability.outputlength.MaxOutputLengthExceeded;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorage;
import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.ContentProcessor;
import com.opensymphony.sitemesh.Decorator;
import com.opensymphony.sitemesh.DecoratorSelector;
import com.opensymphony.sitemesh.compatability.DecoratorMapper2DecoratorSelector;
import com.opensymphony.sitemesh.compatability.PageParser2ContentProcessor;
import com.opensymphony.sitemesh.webapp.ContainerTweaks;
import com.opensymphony.sitemesh.webapp.ContentBufferingResponse;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SiteMeshFilter
implements Filter {
    private FilterConfig filterConfig;
    private ContainerTweaks containerTweaks;
    private ScalabilitySupportConfiguration scalabilitySupportConfiguration;
    private static final String ALREADY_APPLIED_KEY = "com.opensymphony.sitemesh.APPLIED_ONCE";

    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        this.containerTweaks = new ContainerTweaks();
        this.scalabilitySupportConfiguration = new ScalabilitySupportConfiguration(filterConfig);
    }

    public void destroy() {
        this.filterConfig = null;
        this.containerTweaks = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain) throws IOException, ServletException {
        block18: {
            HttpServletRequest request = (HttpServletRequest)rq;
            HttpServletResponse response = (HttpServletResponse)rs;
            ServletContext servletContext = this.filterConfig.getServletContext();
            SiteMeshWebAppContext webAppContext = new SiteMeshWebAppContext(request, response, servletContext);
            ContentProcessor contentProcessor = this.initContentProcessor(webAppContext);
            DecoratorSelector decoratorSelector = this.initDecoratorSelector(webAppContext);
            if (this.filterAlreadyAppliedForRequest(request)) {
                chain.doFilter((ServletRequest)request, (ServletResponse)response);
                return;
            }
            if (!contentProcessor.handles(webAppContext)) {
                chain.doFilter((ServletRequest)request, (ServletResponse)response);
                return;
            }
            if (this.containerTweaks.shouldAutoCreateSession()) {
                request.getSession(true);
            }
            ScalabilitySupport scalabilitySupport = this.scalabilitySupportConfiguration.getScalabilitySupport(request);
            try {
                Content content = this.obtainContent(contentProcessor, webAppContext, scalabilitySupport, request, response, chain);
                if (content == null) {
                    return;
                }
                Decorator decorator = decoratorSelector.selectDecorator(content, webAppContext);
                decorator.render(content, webAppContext);
            }
            catch (MaxOutputLengthExceeded exceeded) {
                this.handleMaximumExceeded(scalabilitySupport, request, response, servletContext, exceeded);
            }
            catch (IllegalStateException e) {
                if (!this.containerTweaks.shouldIgnoreIllegalStateExceptionOnErrorPage()) {
                    throw e;
                }
            }
            catch (RuntimeException e) {
                if (this.containerTweaks.shouldLogUnhandledExceptions()) {
                    servletContext.log("Unhandled exception occurred whilst decorating page", (Throwable)e);
                }
                throw e;
            }
            catch (ServletException e) {
                request.setAttribute(ALREADY_APPLIED_KEY, null);
                if (e.getCause() instanceof MaxOutputLengthExceeded) {
                    this.handleMaximumExceeded(scalabilitySupport, request, response, servletContext, (MaxOutputLengthExceeded)e.getCause());
                    break block18;
                }
                throw e;
            }
            finally {
                this.cleanupSecondaryStorage(scalabilitySupport.getSecondaryStorage(), servletContext);
            }
        }
    }

    private void handleMaximumExceeded(ScalabilitySupport scalabilitySupport, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, MaxOutputLengthExceeded exceeded) throws IOException {
        request.setAttribute(RequestConstants.MAXIMUM_OUTPUT_EXCEEDED_LENGTH, (Object)exceeded.getMaxOutputLength());
        if (scalabilitySupport.isMaxOutputLengthExceededThrown()) {
            throw exceeded;
        }
        servletContext.log("Exceeded the maximum SiteMesh page output size", (Throwable)exceeded);
        response.sendError(exceeded.getMaximumOutputExceededHttpCode(), exceeded.getMessage());
    }

    private void cleanupSecondaryStorage(SecondaryStorage secondaryStorage, ServletContext servletContext) {
        try {
            secondaryStorage.cleanUp();
        }
        catch (Exception e) {
            servletContext.log("Unable to clean up secondary storage properly.  Ignoring exception ", (Throwable)e);
        }
    }

    protected ContentProcessor initContentProcessor(SiteMeshWebAppContext webAppContext) {
        Factory factory = Factory.getInstance(new Config(this.filterConfig));
        factory.refresh();
        return new PageParser2ContentProcessor(factory);
    }

    protected DecoratorSelector initDecoratorSelector(SiteMeshWebAppContext webAppContext) {
        Factory factory = Factory.getInstance(new Config(this.filterConfig));
        factory.refresh();
        return new DecoratorMapper2DecoratorSelector(factory.getDecoratorMapper());
    }

    private Content obtainContent(ContentProcessor contentProcessor, SiteMeshWebAppContext webAppContext, ScalabilitySupport scalabilitySupport, HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        ContentBufferingResponse contentBufferingResponse = new ContentBufferingResponse(response, request, contentProcessor, webAppContext, scalabilitySupport);
        chain.doFilter((ServletRequest)request, (ServletResponse)contentBufferingResponse);
        webAppContext.setUsingStream(contentBufferingResponse.isUsingStream());
        return contentBufferingResponse.getContent();
    }

    private boolean filterAlreadyAppliedForRequest(HttpServletRequest request) {
        if (request.getAttribute(ALREADY_APPLIED_KEY) == Boolean.TRUE) {
            return true;
        }
        request.setAttribute(ALREADY_APPLIED_KEY, (Object)Boolean.TRUE);
        return false;
    }
}

