/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.servlet.handler;

import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

public abstract class AbstractHandlerExceptionResolver
implements HandlerExceptionResolver,
Ordered {
    private static final String HEADER_CACHE_CONTROL = "Cache-Control";
    protected final Log logger = LogFactory.getLog(this.getClass());
    private int order = Integer.MAX_VALUE;
    @Nullable
    private Set<?> mappedHandlers;
    @Nullable
    private Class<?>[] mappedHandlerClasses;
    @Nullable
    private Log warnLogger;
    private boolean preventResponseCaching = false;

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setMappedHandlers(Set<?> mappedHandlers) {
        this.mappedHandlers = mappedHandlers;
    }

    public void setMappedHandlerClasses(Class<?> ... mappedHandlerClasses) {
        this.mappedHandlerClasses = mappedHandlerClasses;
    }

    public void setWarnLogCategory(String loggerName) {
        this.warnLogger = StringUtils.hasLength(loggerName) ? LogFactory.getLog((String)loggerName) : null;
    }

    public void setPreventResponseCaching(boolean preventResponseCaching) {
        this.preventResponseCaching = preventResponseCaching;
    }

    @Override
    @Nullable
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
        if (this.shouldApplyTo(request, handler)) {
            this.prepareResponse(ex, response);
            ModelAndView result = this.doResolveException(request, response, handler, ex);
            if (result != null) {
                if (this.logger.isDebugEnabled() && (this.warnLogger == null || !this.warnLogger.isWarnEnabled())) {
                    this.logger.debug((Object)(this.buildLogMessage(ex, request) + (result.isEmpty() ? "" : " to " + result)));
                }
                this.logException(ex, request);
            }
            return result;
        }
        return null;
    }

    protected boolean shouldApplyTo(HttpServletRequest request, @Nullable Object handler) {
        if (handler != null) {
            if (this.mappedHandlers != null && this.mappedHandlers.contains(handler)) {
                return true;
            }
            if (this.mappedHandlerClasses != null) {
                for (Class<?> handlerClass : this.mappedHandlerClasses) {
                    if (!handlerClass.isInstance(handler)) continue;
                    return true;
                }
            }
        }
        return !this.hasHandlerMappings();
    }

    protected boolean hasHandlerMappings() {
        return this.mappedHandlers != null || this.mappedHandlerClasses != null;
    }

    protected void logException(Exception ex, HttpServletRequest request) {
        if (this.warnLogger != null && this.warnLogger.isWarnEnabled()) {
            this.warnLogger.warn((Object)this.buildLogMessage(ex, request));
        }
    }

    protected String buildLogMessage(Exception ex, HttpServletRequest request) {
        return "Resolved [" + LogFormatUtils.formatValue(ex, -1, true) + "]";
    }

    protected void prepareResponse(Exception ex, HttpServletResponse response) {
        if (this.preventResponseCaching) {
            this.preventCaching(response);
        }
    }

    protected void preventCaching(HttpServletResponse response) {
        response.addHeader(HEADER_CACHE_CONTROL, "no-store");
    }

    @Nullable
    protected abstract ModelAndView doResolveException(HttpServletRequest var1, HttpServletResponse var2, @Nullable Object var3, Exception var4);
}

