/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  javax.servlet.jsp.jstl.core.Config
 *  javax.servlet.jsp.jstl.fmt.LocalizationContext
 */
package org.springframework.web.servlet.support;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceResourceBundle;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.support.RequestContextUtils;

public abstract class JstlUtils {
    public static MessageSource getJstlAwareMessageSource(@Nullable ServletContext servletContext, MessageSource messageSource) {
        String jstlInitParam;
        if (servletContext != null && (jstlInitParam = servletContext.getInitParameter("javax.servlet.jsp.jstl.fmt.localizationContext")) != null) {
            ResourceBundleMessageSource jstlBundleWrapper = new ResourceBundleMessageSource();
            jstlBundleWrapper.setBasename(jstlInitParam);
            jstlBundleWrapper.setParentMessageSource(messageSource);
            return jstlBundleWrapper;
        }
        return messageSource;
    }

    public static void exposeLocalizationContext(HttpServletRequest request, @Nullable MessageSource messageSource) {
        Locale jstlLocale = RequestContextUtils.getLocale(request);
        Config.set((ServletRequest)request, (String)"javax.servlet.jsp.jstl.fmt.locale", (Object)jstlLocale);
        TimeZone timeZone = RequestContextUtils.getTimeZone(request);
        if (timeZone != null) {
            Config.set((ServletRequest)request, (String)"javax.servlet.jsp.jstl.fmt.timeZone", (Object)timeZone);
        }
        if (messageSource != null) {
            SpringLocalizationContext jstlContext = new SpringLocalizationContext(messageSource, request);
            Config.set((ServletRequest)request, (String)"javax.servlet.jsp.jstl.fmt.localizationContext", (Object)((Object)jstlContext));
        }
    }

    public static void exposeLocalizationContext(RequestContext requestContext) {
        Config.set((ServletRequest)requestContext.getRequest(), (String)"javax.servlet.jsp.jstl.fmt.locale", (Object)requestContext.getLocale());
        TimeZone timeZone = requestContext.getTimeZone();
        if (timeZone != null) {
            Config.set((ServletRequest)requestContext.getRequest(), (String)"javax.servlet.jsp.jstl.fmt.timeZone", (Object)timeZone);
        }
        MessageSource messageSource = JstlUtils.getJstlAwareMessageSource(requestContext.getServletContext(), requestContext.getMessageSource());
        SpringLocalizationContext jstlContext = new SpringLocalizationContext(messageSource, requestContext.getRequest());
        Config.set((ServletRequest)requestContext.getRequest(), (String)"javax.servlet.jsp.jstl.fmt.localizationContext", (Object)((Object)jstlContext));
    }

    private static class SpringLocalizationContext
    extends LocalizationContext {
        private final MessageSource messageSource;
        private final HttpServletRequest request;

        public SpringLocalizationContext(MessageSource messageSource, HttpServletRequest request) {
            this.messageSource = messageSource;
            this.request = request;
        }

        public ResourceBundle getResourceBundle() {
            Object lcObject;
            HttpSession session = this.request.getSession(false);
            if (session != null && (lcObject = Config.get((HttpSession)session, (String)"javax.servlet.jsp.jstl.fmt.localizationContext")) instanceof LocalizationContext) {
                ResourceBundle lcBundle = ((LocalizationContext)lcObject).getResourceBundle();
                return new MessageSourceResourceBundle(this.messageSource, this.getLocale(), lcBundle);
            }
            return new MessageSourceResourceBundle(this.messageSource, this.getLocale());
        }

        public Locale getLocale() {
            Object localeObject;
            HttpSession session = this.request.getSession(false);
            if (session != null && (localeObject = Config.get((HttpSession)session, (String)"javax.servlet.jsp.jstl.fmt.locale")) instanceof Locale) {
                return (Locale)localeObject;
            }
            return RequestContextUtils.getLocale(this.request);
        }
    }
}

