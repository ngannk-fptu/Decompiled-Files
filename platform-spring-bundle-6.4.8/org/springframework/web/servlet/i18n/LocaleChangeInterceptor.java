/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.servlet.i18n;

import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

public class LocaleChangeInterceptor
implements HandlerInterceptor {
    public static final String DEFAULT_PARAM_NAME = "locale";
    protected final Log logger = LogFactory.getLog(this.getClass());
    private String paramName = "locale";
    @Nullable
    private String[] httpMethods;
    private boolean ignoreInvalidLocale = false;

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return this.paramName;
    }

    public void setHttpMethods(String ... httpMethods) {
        this.httpMethods = httpMethods;
    }

    @Nullable
    public String[] getHttpMethods() {
        return this.httpMethods;
    }

    public void setIgnoreInvalidLocale(boolean ignoreInvalidLocale) {
        this.ignoreInvalidLocale = ignoreInvalidLocale;
    }

    public boolean isIgnoreInvalidLocale() {
        return this.ignoreInvalidLocale;
    }

    @Deprecated
    public void setLanguageTagCompliant(boolean languageTagCompliant) {
        if (!languageTagCompliant) {
            throw new IllegalArgumentException("LocaleChangeInterceptor always accepts BCP 47 language tags");
        }
    }

    @Deprecated
    public boolean isLanguageTagCompliant() {
        return true;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
        String newLocale = request.getParameter(this.getParamName());
        if (newLocale != null && this.checkHttpMethod(request.getMethod())) {
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
            if (localeResolver == null) {
                throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
            }
            try {
                localeResolver.setLocale(request, response, this.parseLocaleValue(newLocale));
            }
            catch (IllegalArgumentException ex) {
                if (this.isIgnoreInvalidLocale()) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug((Object)("Ignoring invalid locale value [" + newLocale + "]: " + ex.getMessage()));
                    }
                }
                throw ex;
            }
        }
        return true;
    }

    private boolean checkHttpMethod(String currentMethod) {
        Object[] configuredMethods = this.getHttpMethods();
        if (ObjectUtils.isEmpty(configuredMethods)) {
            return true;
        }
        for (Object configuredMethod : configuredMethods) {
            if (!((String)configuredMethod).equalsIgnoreCase(currentMethod)) continue;
            return true;
        }
        return false;
    }

    @Nullable
    protected Locale parseLocaleValue(String localeValue) {
        return StringUtils.parseLocale(localeValue);
    }
}

