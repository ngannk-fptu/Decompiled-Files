/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.handler;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.util.WebUtils;

public class SimpleMappingExceptionResolver
extends AbstractHandlerExceptionResolver {
    public static final String DEFAULT_EXCEPTION_ATTRIBUTE = "exception";
    @Nullable
    private Properties exceptionMappings;
    @Nullable
    private Class<?>[] excludedExceptions;
    @Nullable
    private String defaultErrorView;
    @Nullable
    private Integer defaultStatusCode;
    private Map<String, Integer> statusCodes = new HashMap<String, Integer>();
    @Nullable
    private String exceptionAttribute = "exception";

    public void setExceptionMappings(Properties mappings) {
        this.exceptionMappings = mappings;
    }

    public void setExcludedExceptions(Class<?> ... excludedExceptions) {
        this.excludedExceptions = excludedExceptions;
    }

    public void setDefaultErrorView(String defaultErrorView) {
        this.defaultErrorView = defaultErrorView;
    }

    public void setStatusCodes(Properties statusCodes) {
        Enumeration<?> enumeration = statusCodes.propertyNames();
        while (enumeration.hasMoreElements()) {
            String viewName = (String)enumeration.nextElement();
            Integer statusCode = Integer.valueOf(statusCodes.getProperty(viewName));
            this.statusCodes.put(viewName, statusCode);
        }
    }

    public void addStatusCode(String viewName, int statusCode) {
        this.statusCodes.put(viewName, statusCode);
    }

    public Map<String, Integer> getStatusCodesAsMap() {
        return Collections.unmodifiableMap(this.statusCodes);
    }

    public void setDefaultStatusCode(int defaultStatusCode) {
        this.defaultStatusCode = defaultStatusCode;
    }

    public void setExceptionAttribute(@Nullable String exceptionAttribute) {
        this.exceptionAttribute = exceptionAttribute;
    }

    @Override
    @Nullable
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
        String viewName = this.determineViewName(ex, request);
        if (viewName != null) {
            Integer statusCode = this.determineStatusCode(request, viewName);
            if (statusCode != null) {
                this.applyStatusCodeIfPossible(request, response, statusCode);
            }
            return this.getModelAndView(viewName, ex, request);
        }
        return null;
    }

    @Nullable
    protected String determineViewName(Exception ex, HttpServletRequest request) {
        String viewName = null;
        if (this.excludedExceptions != null) {
            for (Class<?> excludedEx : this.excludedExceptions) {
                if (!excludedEx.equals(ex.getClass())) continue;
                return null;
            }
        }
        if (this.exceptionMappings != null) {
            viewName = this.findMatchingViewName(this.exceptionMappings, ex);
        }
        if (viewName == null && this.defaultErrorView != null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Resolving to default view '" + this.defaultErrorView + "'"));
            }
            viewName = this.defaultErrorView;
        }
        return viewName;
    }

    @Nullable
    protected String findMatchingViewName(Properties exceptionMappings, Exception ex) {
        String viewName = null;
        String dominantMapping = null;
        int deepest = Integer.MAX_VALUE;
        Enumeration<?> names = exceptionMappings.propertyNames();
        while (names.hasMoreElements()) {
            String exceptionMapping = (String)names.nextElement();
            int depth = this.getDepth(exceptionMapping, ex);
            if (depth < 0 || depth >= deepest && (depth != deepest || dominantMapping == null || exceptionMapping.length() <= dominantMapping.length())) continue;
            deepest = depth;
            dominantMapping = exceptionMapping;
            viewName = exceptionMappings.getProperty(exceptionMapping);
        }
        if (viewName != null && this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Resolving to view '" + viewName + "' based on mapping [" + dominantMapping + "]"));
        }
        return viewName;
    }

    protected int getDepth(String exceptionMapping, Exception ex) {
        return this.getDepth(exceptionMapping, ex.getClass(), 0);
    }

    private int getDepth(String exceptionMapping, Class<?> exceptionClass, int depth) {
        if (exceptionClass.getName().contains(exceptionMapping)) {
            return depth;
        }
        if (exceptionClass == Throwable.class) {
            return -1;
        }
        return this.getDepth(exceptionMapping, exceptionClass.getSuperclass(), depth + 1);
    }

    @Nullable
    protected Integer determineStatusCode(HttpServletRequest request, String viewName) {
        if (this.statusCodes.containsKey(viewName)) {
            return this.statusCodes.get(viewName);
        }
        return this.defaultStatusCode;
    }

    protected void applyStatusCodeIfPossible(HttpServletRequest request, HttpServletResponse response, int statusCode) {
        if (!WebUtils.isIncludeRequest((ServletRequest)request)) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Applying HTTP status " + statusCode));
            }
            response.setStatus(statusCode);
            request.setAttribute("javax.servlet.error.status_code", (Object)statusCode);
        }
    }

    protected ModelAndView getModelAndView(String viewName, Exception ex, HttpServletRequest request) {
        return this.getModelAndView(viewName, ex);
    }

    protected ModelAndView getModelAndView(String viewName, Exception ex) {
        ModelAndView mv = new ModelAndView(viewName);
        if (this.exceptionAttribute != null) {
            mv.addObject(this.exceptionAttribute, ex);
        }
        return mv;
    }
}

