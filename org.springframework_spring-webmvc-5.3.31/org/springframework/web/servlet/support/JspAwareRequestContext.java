/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.jsp.PageContext
 *  javax.servlet.jsp.jstl.core.Config
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.servlet.support;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.core.Config;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.support.RequestContext;

public class JspAwareRequestContext
extends RequestContext {
    private PageContext pageContext;

    public JspAwareRequestContext(PageContext pageContext) {
        this(pageContext, null);
    }

    public JspAwareRequestContext(PageContext pageContext, @Nullable Map<String, Object> model) {
        super((HttpServletRequest)pageContext.getRequest(), (HttpServletResponse)pageContext.getResponse(), pageContext.getServletContext(), model);
        this.pageContext = pageContext;
    }

    protected final PageContext getPageContext() {
        return this.pageContext;
    }

    @Override
    protected Locale getFallbackLocale() {
        Locale locale;
        if (jstlPresent && (locale = JstlPageLocaleResolver.getJstlLocale(this.getPageContext())) != null) {
            return locale;
        }
        return this.getRequest().getLocale();
    }

    @Override
    protected TimeZone getFallbackTimeZone() {
        TimeZone timeZone;
        if (jstlPresent && (timeZone = JstlPageLocaleResolver.getJstlTimeZone(this.getPageContext())) != null) {
            return timeZone;
        }
        return null;
    }

    private static class JstlPageLocaleResolver {
        private JstlPageLocaleResolver() {
        }

        @Nullable
        public static Locale getJstlLocale(PageContext pageContext) {
            Object localeObject = Config.find((PageContext)pageContext, (String)"javax.servlet.jsp.jstl.fmt.locale");
            return localeObject instanceof Locale ? (Locale)localeObject : null;
        }

        @Nullable
        public static TimeZone getJstlTimeZone(PageContext pageContext) {
            Object timeZoneObject = Config.find((PageContext)pageContext, (String)"javax.servlet.jsp.jstl.fmt.timeZone");
            return timeZoneObject instanceof TimeZone ? (TimeZone)timeZoneObject : null;
        }
    }
}

