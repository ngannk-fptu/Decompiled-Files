/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.velocity.ContextUtils
 *  com.atlassian.confluence.velocity.context.OutputMimeTypeAwareContext
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.Supplier
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  com.atlassian.velocity.htmlsafe.HtmlFragment
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Throwables
 *  com.google.common.util.concurrent.UncheckedExecutionException
 *  com.opensymphony.module.sitemesh.Decorator
 *  com.opensymphony.module.sitemesh.HTMLPage
 *  com.opensymphony.module.sitemesh.Page
 *  com.opensymphony.module.sitemesh.RequestConstants
 *  com.opensymphony.sitemesh.Content
 *  com.opensymphony.sitemesh.compatability.Content2HTMLPage
 *  com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext
 *  com.opensymphony.sitemesh.webapp.decorator.BaseWebAppDecorator
 *  com.opensymphony.xwork2.ActionContext
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.struts2.ServletActionContext
 *  org.apache.struts2.views.velocity.VelocityManager
 *  org.apache.velocity.VelocityContext
 *  org.apache.velocity.context.Context
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionException
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.event.events.analytics.HttpRequestStats;
import com.atlassian.confluence.impl.profiling.DecoratorTimings;
import com.atlassian.confluence.internal.spaces.SpaceManagerInternal;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.setup.sitemesh.SitemeshContextItemProvider;
import com.atlassian.confluence.setup.sitemesh.SitemeshPageBodyRenderable;
import com.atlassian.confluence.setup.sitemesh.SitemeshPageHeadRenderable;
import com.atlassian.confluence.setup.struts.OutputAwareStrutsVelocityContext;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.themes.Theme;
import com.atlassian.confluence.themes.ThemeContext;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.confluence.util.profiling.DecoratorActionFactory;
import com.atlassian.confluence.util.profiling.VelocitySitemeshPage;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.confluence.velocity.ContextUtils;
import com.atlassian.confluence.velocity.context.OutputMimeTypeAwareContext;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.atlassian.velocity.htmlsafe.HtmlFragment;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.RequestConstants;
import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.compatability.Content2HTMLPage;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;
import com.opensymphony.sitemesh.webapp.decorator.BaseWebAppDecorator;
import com.opensymphony.xwork2.ActionContext;
import java.io.IOException;
import java.io.Writer;
import java.util.Optional;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.views.velocity.VelocityManager;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;

public class ConfluenceSitemeshDecorator
extends BaseWebAppDecorator {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceSitemeshDecorator.class);
    public static final String HTTP_REQUEST_ATTR_KEY_WEB_INTERFACE_CONTEXT = "atlas.webInterfaceContext";
    public static final boolean BUFFER_RESPONSE = Boolean.getBoolean("confluence.sitemesh.response.buffer");
    protected static final ErrorHandlingStrategy ERROR_SENDING_STRATEGY = new ErrorSendingErrorHandlingStrategy();
    protected static final ErrorHandlingStrategy ERROR_THROWING_STRATEGY = new ErrorThrowingErrorHandlingStrategy();
    public static final String CONFLUENCE_SITEMESH_DECORATOR_ATTRIBUTE = "com.atlassian.confluence.util.profiling.ConfluenceSitemeshDecorator";
    static final String SPACE_ID_KEY = "meta.spaceid";
    private final ResponseWritingStrategy writingStrategy;
    private final ThemeManager themeManager;
    private final Decorator decorator;
    private final SpaceManagerInternal spaceManager;
    private final VelocityManager velocityManager;
    private final Supplier<PlatformTransactionManager> platformTransactionManagerSupplier = new LazyComponentReference("transactionManager");

    ConfluenceSitemeshDecorator(ThemeManager themeManager, SpaceManagerInternal spaceManager, Decorator decorator, VelocityManager velocityManager) {
        this(themeManager, spaceManager, decorator, ERROR_SENDING_STRATEGY, velocityManager);
    }

    ConfluenceSitemeshDecorator(ThemeManager themeManager, SpaceManagerInternal spaceManager, Decorator decorator, ErrorHandlingStrategy errorHandlingStrategy, VelocityManager velocityManager) {
        this.themeManager = themeManager;
        this.decorator = decorator;
        this.writingStrategy = BUFFER_RESPONSE ? new StaticBufferedResponseWritingStrategy(errorHandlingStrategy) : new StreamingResponseWritingStrategy(errorHandlingStrategy);
        this.spaceManager = spaceManager;
        this.velocityManager = velocityManager;
    }

    private PlatformTransactionManager getPTManager() {
        return (PlatformTransactionManager)this.platformTransactionManagerSupplier.get();
    }

    protected void render(Content content, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, SiteMeshWebAppContext siteMeshWebAppContext) throws IOException, ServletException {
        request.setAttribute(CONFLUENCE_SITEMESH_DECORATOR_ATTRIBUTE, (Object)true);
        String profileName = "SiteMesh: applyDecorator: " + this.decorator.getName() + " (" + this.decorator.getPage() + ")";
        try (DecoratorTimings.DecoratorTimer timer = DecoratorTimings.newDecoratorTimer(this.getDecorator(), request);
             Ticker ignored = Timers.start((String)profileName);){
            if (ContainerManager.isContainerSetup()) {
                new TransactionTemplate(this.getPTManager()).execute(status -> {
                    try {
                        this.renderInternal(content, request, response);
                        return null;
                    }
                    catch (Exception ex) {
                        Throwables.throwIfInstanceOf((Throwable)ex, RuntimeException.class);
                        throw new UncheckedExecutionException((Throwable)ex);
                    }
                });
            } else {
                this.renderInternal(content, request, response);
            }
        }
        catch (UncheckedExecutionException e) {
            Throwables.throwIfInstanceOf((Throwable)e.getCause(), IOException.class);
            Throwables.throwIfInstanceOf((Throwable)e.getCause(), ServletException.class);
            throw e;
        }
        catch (TransactionException ex) {
            log.warn("TransactionException prevented transaction from committing whilst rendering the decorator, the cause is likely a previously logged exception: {}\nCause: {}", (Object)ex.getMessage(), (Object)(ex.getCause() != null ? ex.getCause().getMessage() : ""));
            log.debug("Full Details: ", (Throwable)ex);
        }
    }

    protected void renderInternal(Content content, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Content2HTMLPage page = new Content2HTMLPage(content, request);
        this.applyDecorator((Page)page, this.decorator, request, response);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void applyDecorator(Page page, Decorator decorator, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Preconditions.checkState((ActionContext.getContext() != null ? 1 : 0) != 0, (Object)"Struts has not been initialized on this thread");
        HttpRequestStats.elapse("sitemeshDecoratorApplyStarted");
        HttpServletRequest originalRequest = ServletActionContext.getRequest();
        HttpServletResponse originalResponse = ServletActionContext.getResponse();
        ServletActionContext.setRequest((HttpServletRequest)request);
        ServletActionContext.setResponse((HttpServletResponse)response);
        try {
            this.applyDecoratorUsingVelocity(request, page, response, decorator);
        }
        finally {
            ServletActionContext.setRequest((HttpServletRequest)originalRequest);
            ServletActionContext.setResponse((HttpServletResponse)originalResponse);
            HttpRequestStats.elapse("sitemeshDecoratorApplyFinished");
        }
    }

    @VisibleForTesting
    Optional<Space> findSpaceForDecorating(@Nullable WebInterfaceContext webInterfaceContext, Page page) {
        Space space = Optional.ofNullable(webInterfaceContext).map(WebInterfaceContext::getSpace).orElse(null);
        if (space == null && this.spaceManager != null) {
            String spaceId = page.getProperty(SPACE_ID_KEY);
            space = Optional.ofNullable(spaceId).map(spaceIdString -> {
                try {
                    return Long.parseLong(spaceIdString);
                }
                catch (NumberFormatException nfe) {
                    log.warn("Invalid spaceId", (Throwable)nfe);
                    return null;
                }
            }).map(this.spaceManager::getSpace).orElse(null);
        }
        return Optional.ofNullable(space);
    }

    private Context getSitemeshContext(HttpServletRequest request, HttpServletResponse response) {
        Context context = this.velocityManager.createContext(ActionContext.getContext().getValueStack(), request, response);
        VelocityContext sitemeshContext = new VelocityContext(SitemeshContextItemProvider.getProvider(request).getContextMap());
        ContextUtils.putAll((Context)context, (Context)sitemeshContext);
        return context;
    }

    private void applyDecoratorUsingVelocity(HttpServletRequest request, Page page, HttpServletResponse response, Decorator decorator) throws ServletException, IOException {
        String spaceKey;
        Context context;
        request.setAttribute(RequestConstants.PAGE, (Object)page);
        if (!response.isCommitted()) {
            response.setHeader("X-Accel-Buffering", "no");
        }
        WebInterfaceContext webInterfaceContext = (WebInterfaceContext)request.getAttribute(HTTP_REQUEST_ATTR_KEY_WEB_INTERFACE_CONTEXT);
        Optional<Space> space = this.findSpaceForDecorating(webInterfaceContext, page);
        ConfluenceActionSupport action = DecoratorActionFactory.createAction(webInterfaceContext, space.orElse(null));
        if (space.isPresent() && !ThemeContext.hasThemeContext((ServletRequest)request)) {
            ThemeContext.set((ServletRequest)request, space.get(), this.getActiveTheme(space.get().getKey()), this.getGlobalTheme());
        }
        if (!(context = this.getSitemeshContext(request, response)).containsKey((Object)"staticResourceUrlPrefix")) {
            context.put("staticResourceUrlPrefix", (Object)request.getContextPath());
        }
        context.put("action", (Object)action);
        context.put("helper", (Object)action.getHelper());
        context.put("sitemeshPage", (Object)page);
        context.put("title", (Object)page.getTitle());
        context.put("username", (Object)page.getProperty("page.username"));
        if (space.isPresent()) {
            context.put("space", (Object)space.get());
            spaceKey = space.get().getKey();
        } else {
            spaceKey = page.getProperty("page.spacekey");
        }
        context.put("spaceKey", (Object)spaceKey);
        context.put("theme", (Object)this.getActiveTheme(spaceKey));
        context.put("body", (Object)new SitemeshPageBodyRenderable(page));
        context.put("decorator", (Object)decorator);
        if (page instanceof HTMLPage) {
            HTMLPage htmlPage = (HTMLPage)page;
            context.put("head", (Object)new SitemeshPageHeadRenderable(htmlPage));
            context.put("sitemeshPage", (Object)new VelocitySitemeshPage((HTMLPage)page));
            context.put("title", (Object)new HtmlFragment((Object)page.getTitle()));
            if (context instanceof OutputMimeTypeAwareContext) {
                OutputAwareStrutsVelocityContext outputAwareContext = (OutputAwareStrutsVelocityContext)context;
                outputAwareContext.setOutputMimeType("text/html");
            }
        }
        this.writingStrategy.renderToResponse(decorator, context, response);
        request.removeAttribute(RequestConstants.PAGE);
    }

    private Theme getActiveTheme(String spaceKey) {
        return this.themeManager == null ? null : this.themeManager.getSpaceTheme(spaceKey);
    }

    private Theme getGlobalTheme() {
        return this.themeManager == null ? null : this.themeManager.getGlobalTheme();
    }

    protected Decorator getDecorator() {
        return this.decorator;
    }

    private static final class StreamingResponseWritingStrategy
    implements ResponseWritingStrategy {
        private final ErrorHandlingStrategy errorHandlingStrategy;

        private StreamingResponseWritingStrategy(ErrorHandlingStrategy errorHandlingStrategy) {
            this.errorHandlingStrategy = errorHandlingStrategy;
        }

        @Override
        public void renderToResponse(Decorator decorator, Context context, HttpServletResponse response) throws ServletException, IOException {
            try {
                VelocityUtils.renderTemplateWithoutSwallowingErrors(decorator.getPage(), context, (Writer)response.getWriter());
            }
            catch (Exception e) {
                if (response.isCommitted()) {
                    response.getWriter().printf("Error occurred during template rendering. Contact your administrator for assistance.", new Object[0]);
                    log.error("Error occurred rendering template: " + decorator.getPage(), (Throwable)e);
                }
                this.errorHandlingStrategy.handleException(e, response);
            }
        }
    }

    private static final class StaticBufferedResponseWritingStrategy
    implements ResponseWritingStrategy {
        private final ErrorHandlingStrategy errorHandlingStrategy;

        private StaticBufferedResponseWritingStrategy(ErrorHandlingStrategy errorHandlingStrategy) {
            this.errorHandlingStrategy = errorHandlingStrategy;
        }

        @Override
        public void renderToResponse(Decorator decorator, Context context, HttpServletResponse response) throws ServletException, IOException {
            try {
                String template = VelocityUtils.getRenderedTemplateWithoutSwallowingErrors(decorator.getPage(), context);
                response.getWriter().write(template);
            }
            catch (Exception e) {
                this.errorHandlingStrategy.handleException(e, response);
            }
        }
    }

    private static interface ResponseWritingStrategy {
        public void renderToResponse(Decorator var1, Context var2, HttpServletResponse var3) throws ServletException, IOException;
    }

    private static final class ErrorThrowingErrorHandlingStrategy
    implements ErrorHandlingStrategy {
        private ErrorThrowingErrorHandlingStrategy() {
        }

        @Override
        public void handleException(Exception e, HttpServletResponse response) throws ServletException, IOException {
            throw new ServletException((Throwable)e);
        }
    }

    private static final class ErrorSendingErrorHandlingStrategy
    implements ErrorHandlingStrategy {
        private ErrorSendingErrorHandlingStrategy() {
        }

        @Override
        public void handleException(Exception e, HttpServletResponse response) throws ServletException, IOException {
            response.sendError(500);
            throw new ServletException((Throwable)e);
        }
    }

    protected static interface ErrorHandlingStrategy {
        public void handleException(Exception var1, HttpServletResponse var2) throws ServletException, IOException;
    }
}

