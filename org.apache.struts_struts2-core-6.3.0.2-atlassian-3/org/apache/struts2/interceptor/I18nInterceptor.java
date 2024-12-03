/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.apache.commons.lang3.LocaleUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.message.Message
 *  org.apache.logging.log4j.message.ParameterizedMessage
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.LocaleProviderFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.TextParseUtil;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;

public class I18nInterceptor
extends AbstractInterceptor {
    private static final Logger LOG = LogManager.getLogger(I18nInterceptor.class);
    public static final String DEFAULT_SESSION_ATTRIBUTE = "WW_TRANS_I18N_LOCALE";
    public static final String DEFAULT_PARAMETER = "request_locale";
    public static final String DEFAULT_REQUEST_ONLY_PARAMETER = "request_only_locale";
    public static final String DEFAULT_COOKIE_ATTRIBUTE = "WW_TRANS_I18N_LOCALE";
    public static final String DEFAULT_COOKIE_PARAMETER = "request_cookie_locale";
    protected String parameterName = "request_locale";
    protected String requestOnlyParameterName = "request_only_locale";
    protected String attributeName = "WW_TRANS_I18N_LOCALE";
    protected String requestCookieParameterName = "request_cookie_locale";
    protected Storage storage = Storage.SESSION;
    protected LocaleProviderFactory localeProviderFactory;
    private Set<Locale> supportedLocale = Collections.emptySet();

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public void setRequestOnlyParameterName(String requestOnlyParameterName) {
        this.requestOnlyParameterName = requestOnlyParameterName;
    }

    public void setRequestCookieParameterName(String requestCookieParameterName) {
        this.requestCookieParameterName = requestCookieParameterName;
    }

    public void setLocaleStorage(String storageName) {
        if (storageName == null || "".equals(storageName)) {
            this.storage = Storage.ACCEPT_LANGUAGE;
        } else {
            try {
                this.storage = Storage.valueOf(storageName.toUpperCase());
            }
            catch (IllegalArgumentException e) {
                LOG.warn((Message)new ParameterizedMessage("Wrong storage name [{}] was defined, falling back to {}", (Object)storageName, (Object)Storage.SESSION), (Throwable)e);
                this.storage = Storage.SESSION;
            }
        }
    }

    public void setSupportedLocale(String supportedLocale) {
        this.supportedLocale = TextParseUtil.commaDelimitedStringToSet(supportedLocale).stream().map(Locale::new).collect(Collectors.toSet());
    }

    @Inject
    public void setLocaleProviderFactory(LocaleProviderFactory localeProviderFactory) {
        this.localeProviderFactory = localeProviderFactory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        LOG.debug("Intercept '{}/{}'", (Object)invocation.getProxy().getNamespace(), (Object)invocation.getProxy().getActionName());
        LocaleHandler localeHandler = this.getLocaleHandler(invocation);
        Locale locale = localeHandler.find();
        if (locale == null) {
            locale = localeHandler.read(invocation);
        }
        if (localeHandler.shouldStore()) {
            locale = localeHandler.store(invocation, locale);
        }
        this.useLocale(invocation, locale);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Before action invocation Locale={}", invocation.getStack().findValue("locale"));
        }
        try {
            String string = invocation.invoke();
            return string;
        }
        finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("After action invocation Locale={}", invocation.getStack().findValue("locale"));
            }
        }
    }

    protected LocaleHandler getLocaleHandler(ActionInvocation invocation) {
        RequestLocaleHandler localeHandler = this.storage == Storage.COOKIE ? new CookieLocaleHandler(invocation) : (this.storage == Storage.SESSION ? new SessionLocaleHandler(invocation) : (this.storage == Storage.REQUEST ? new RequestLocaleHandler(invocation) : new AcceptLanguageLocaleHandler(invocation)));
        LOG.debug("Using LocaleFinder implementation {}", (Object)localeHandler.getClass().getName());
        return localeHandler;
    }

    protected Locale getLocaleFromParam(Object requestedLocale) {
        String localeStr;
        LocaleProvider localeProvider = this.localeProviderFactory.createLocaleProvider();
        Locale locale = null;
        if (requestedLocale != null && (locale = requestedLocale instanceof Locale ? (Locale)requestedLocale : (localeProvider.isValidLocaleString(localeStr = requestedLocale.toString()) ? LocaleUtils.toLocale((String)localeStr) : localeProvider.getLocale())) != null) {
            LOG.debug("Found locale: {}", (Object)locale);
        }
        if (locale != null && !localeProvider.isValidLocale(locale)) {
            Locale defaultLocale = localeProvider.getLocale();
            LOG.debug("Provided locale {} isn't valid, fallback to default locale {}", (Object)locale, (Object)defaultLocale);
            locale = defaultLocale;
        }
        return locale;
    }

    protected Parameter findLocaleParameter(ActionInvocation invocation, String parameterName) {
        HttpParameters params = invocation.getInvocationContext().getParameters();
        Parameter requestedLocale = params.get(parameterName);
        params.remove(parameterName);
        if (requestedLocale.isDefined()) {
            LOG.debug("Requested locale: {}", (Object)requestedLocale.getValue());
        }
        return requestedLocale;
    }

    protected void useLocale(ActionInvocation invocation, Locale locale) {
        invocation.getInvocationContext().withLocale(locale);
    }

    protected class CookieLocaleHandler
    extends AcceptLanguageLocaleHandler {
        protected CookieLocaleHandler(ActionInvocation invocation) {
            super(invocation);
        }

        @Override
        public Locale find() {
            Locale requestOnlySessionLocale = super.find();
            if (requestOnlySessionLocale != null) {
                this.shouldStore = false;
                return requestOnlySessionLocale;
            }
            LOG.debug("Searching locale in request under parameter {}", (Object)I18nInterceptor.this.requestCookieParameterName);
            Parameter requestedLocale = I18nInterceptor.this.findLocaleParameter(this.actionInvocation, I18nInterceptor.this.requestCookieParameterName);
            if (requestedLocale.isDefined()) {
                return I18nInterceptor.this.getLocaleFromParam(requestedLocale.getValue());
            }
            return null;
        }

        @Override
        public Locale store(ActionInvocation invocation, Locale locale) {
            HttpServletResponse response = ServletActionContext.getResponse();
            Cookie cookie = new Cookie(I18nInterceptor.this.attributeName, locale.toString());
            cookie.setMaxAge(1209600);
            response.addCookie(cookie);
            return locale;
        }

        @Override
        public Locale read(ActionInvocation invocation) {
            Locale locale = null;
            Cookie[] cookies = ServletActionContext.getRequest().getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (!I18nInterceptor.this.attributeName.equals(cookie.getName())) continue;
                    locale = I18nInterceptor.this.getLocaleFromParam(cookie.getValue());
                }
            }
            if (locale == null) {
                LOG.debug("No Locale defined in cookie, fetching from current request and it won't be stored!");
                this.shouldStore = false;
                locale = super.read(invocation);
            } else {
                LOG.debug("Found stored Locale {} in cookie, using it!", locale);
            }
            return locale;
        }
    }

    protected class SessionLocaleHandler
    extends AcceptLanguageLocaleHandler {
        protected SessionLocaleHandler(ActionInvocation invocation) {
            super(invocation);
        }

        @Override
        public Locale find() {
            Locale requestOnlyLocale = super.find();
            if (requestOnlyLocale != null) {
                LOG.debug("Found locale under request only param, it won't be stored in session!");
                this.shouldStore = false;
                return requestOnlyLocale;
            }
            LOG.debug("Searching locale in request under parameter {}", (Object)I18nInterceptor.this.parameterName);
            Parameter requestedLocale = I18nInterceptor.this.findLocaleParameter(this.actionInvocation, I18nInterceptor.this.parameterName);
            if (requestedLocale.isDefined()) {
                return I18nInterceptor.this.getLocaleFromParam(requestedLocale.getValue());
            }
            return null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Locale store(ActionInvocation invocation, Locale locale) {
            Map<String, Object> session = invocation.getInvocationContext().getSession();
            if (session != null) {
                String sessionId = ServletActionContext.getRequest().getSession().getId();
                String string = sessionId.intern();
                synchronized (string) {
                    session.put(I18nInterceptor.this.attributeName, locale);
                }
            }
            return locale;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Locale read(ActionInvocation invocation) {
            Locale locale = null;
            LOG.debug("Checks session for saved locale");
            HttpSession session = ServletActionContext.getRequest().getSession(false);
            if (session != null) {
                String sessionId = session.getId();
                String string = sessionId.intern();
                synchronized (string) {
                    Object sessionLocale = invocation.getInvocationContext().getSession().get(I18nInterceptor.this.attributeName);
                    if (sessionLocale instanceof Locale) {
                        locale = (Locale)sessionLocale;
                        LOG.debug("Applied session locale: {}", (Object)locale);
                    }
                }
            }
            if (locale == null) {
                LOG.debug("No Locale defined in session, fetching from current request and it won't be stored in session!");
                this.shouldStore = false;
                locale = super.read(invocation);
            } else {
                LOG.debug("Found stored Locale {} in session, using it!", locale);
            }
            return locale;
        }
    }

    protected class AcceptLanguageLocaleHandler
    extends RequestLocaleHandler {
        protected AcceptLanguageLocaleHandler(ActionInvocation invocation) {
            super(invocation);
        }

        @Override
        public Locale find() {
            if (I18nInterceptor.this.supportedLocale.size() > 0) {
                Enumeration locales = this.actionInvocation.getInvocationContext().getServletRequest().getLocales();
                while (locales.hasMoreElements()) {
                    Locale locale = (Locale)locales.nextElement();
                    if (!I18nInterceptor.this.supportedLocale.contains(locale)) continue;
                    return locale;
                }
            }
            return super.find();
        }
    }

    protected class RequestLocaleHandler
    implements LocaleHandler {
        protected ActionInvocation actionInvocation;
        protected boolean shouldStore = true;

        protected RequestLocaleHandler(ActionInvocation invocation) {
            this.actionInvocation = invocation;
        }

        @Override
        public Locale find() {
            LOG.debug("Searching locale in request under parameter {}", (Object)I18nInterceptor.this.requestOnlyParameterName);
            Parameter requestedLocale = I18nInterceptor.this.findLocaleParameter(this.actionInvocation, I18nInterceptor.this.requestOnlyParameterName);
            if (requestedLocale.isDefined()) {
                return I18nInterceptor.this.getLocaleFromParam(requestedLocale.getValue());
            }
            return null;
        }

        @Override
        public Locale store(ActionInvocation invocation, Locale locale) {
            return locale;
        }

        @Override
        public Locale read(ActionInvocation invocation) {
            LOG.debug("Searching current Invocation context");
            Locale locale = invocation.getInvocationContext().getLocale();
            if (locale != null) {
                LOG.debug("Applied invocation context locale: {}", (Object)locale);
            }
            return locale;
        }

        @Override
        public boolean shouldStore() {
            return this.shouldStore;
        }
    }

    protected static interface LocaleHandler {
        public Locale find();

        public Locale read(ActionInvocation var1);

        public Locale store(ActionInvocation var1, Locale var2);

        public boolean shouldStore();
    }

    protected static enum Storage {
        COOKIE,
        SESSION,
        REQUEST,
        ACCEPT_LANGUAGE;

    }
}

