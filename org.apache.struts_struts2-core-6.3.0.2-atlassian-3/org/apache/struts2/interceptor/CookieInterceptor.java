/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.security.AcceptedPatternsChecker;
import com.opensymphony.xwork2.security.ExcludedPatternsChecker;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.Cookie;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.CookiesAware;

public class CookieInterceptor
extends AbstractInterceptor {
    private static final long serialVersionUID = 4153142432948747305L;
    private static final Logger LOG = LogManager.getLogger(CookieInterceptor.class);
    private static final String ACCEPTED_PATTERN = "[a-zA-Z0-9\\.\\]\\[_'\\s]+";
    private Set<String> cookiesNameSet = Collections.emptySet();
    private Set<String> cookiesValueSet = Collections.emptySet();
    private ExcludedPatternsChecker excludedPatternsChecker;
    private AcceptedPatternsChecker acceptedPatternsChecker;

    @Inject
    public void setExcludedPatternsChecker(ExcludedPatternsChecker excludedPatternsChecker) {
        this.excludedPatternsChecker = excludedPatternsChecker;
    }

    @Inject
    public void setAcceptedPatternsChecker(AcceptedPatternsChecker acceptedPatternsChecker) {
        this.acceptedPatternsChecker = acceptedPatternsChecker;
        this.acceptedPatternsChecker.setAcceptedPatterns(ACCEPTED_PATTERN);
    }

    public void setCookiesName(String cookiesName) {
        if (cookiesName != null) {
            this.cookiesNameSet = TextParseUtil.commaDelimitedStringToSet(cookiesName);
        }
    }

    public void setCookiesValue(String cookiesValue) {
        if (cookiesValue != null) {
            this.cookiesValueSet = TextParseUtil.commaDelimitedStringToSet(cookiesValue);
        }
    }

    public void setAcceptCookieNames(String commaDelimitedPattern) {
        this.acceptedPatternsChecker.setAcceptedPatterns(commaDelimitedPattern);
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        LOG.debug("start interception");
        LinkedHashMap<String, String> cookiesMap = new LinkedHashMap<String, String>();
        Cookie[] cookies = ServletActionContext.getRequest().getCookies();
        if (cookies != null) {
            ValueStack stack = ActionContext.getContext().getValueStack();
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                String value = cookie.getValue();
                if (this.isAcceptableName(name)) {
                    if (this.cookiesNameSet.contains("*")) {
                        LOG.debug("Contains cookie name [*] in configured cookies name set, cookie with name [{}] with value [{}] will be injected", (Object)name, (Object)value);
                        this.populateCookieValueIntoStack(name, value, cookiesMap, stack);
                        continue;
                    }
                    if (!this.cookiesNameSet.contains(cookie.getName())) continue;
                    this.populateCookieValueIntoStack(name, value, cookiesMap, stack);
                    continue;
                }
                LOG.warn("Cookie name [{}] with value [{}] was rejected!", (Object)name, (Object)value);
            }
        }
        this.injectIntoCookiesAwareAction(invocation.getAction(), cookiesMap);
        return invocation.invoke();
    }

    protected boolean isAcceptableName(String name) {
        return !this.isExcluded(name) && this.isAccepted(name);
    }

    protected boolean isAccepted(String name) {
        AcceptedPatternsChecker.IsAccepted accepted = this.acceptedPatternsChecker.isAccepted(name);
        if (accepted.isAccepted()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Cookie [{}] matches acceptedPattern [{}]", (Object)name, (Object)accepted.getAcceptedPattern());
            }
            return true;
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("Cookie [{}] doesn't match acceptedPattern [{}]", (Object)name, (Object)accepted.getAcceptedPattern());
        }
        return false;
    }

    protected boolean isExcluded(String name) {
        ExcludedPatternsChecker.IsExcluded excluded = this.excludedPatternsChecker.isExcluded(name);
        if (excluded.isExcluded()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Cookie [{}] matches excludedPattern [{}]", (Object)name, (Object)excluded.getExcludedPattern());
            }
            return true;
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("Cookie [{}] doesn't match excludedPattern [{}]", (Object)name, (Object)excluded.getExcludedPattern());
        }
        return false;
    }

    protected void populateCookieValueIntoStack(String cookieName, String cookieValue, Map<String, String> cookiesMap, ValueStack stack) {
        if (this.cookiesValueSet.isEmpty() || this.cookiesValueSet.contains("*")) {
            if (LOG.isDebugEnabled()) {
                if (this.cookiesValueSet.isEmpty()) {
                    LOG.debug("no cookie value is configured, cookie with name [{}] with value [{}] will be injected", (Object)cookieName, (Object)cookieValue);
                } else if (this.cookiesValueSet.contains("*")) {
                    LOG.debug("interceptor is configured to accept any value, cookie with name [{}] with value [{}] will be injected", (Object)cookieName, (Object)cookieValue);
                }
            }
            cookiesMap.put(cookieName, cookieValue);
            stack.setValue(cookieName, cookieValue);
        } else if (this.cookiesValueSet.contains(cookieValue)) {
            LOG.debug("both configured cookie name and value matched, cookie [{}] with value [{}] will be injected", (Object)cookieName, (Object)cookieValue);
            cookiesMap.put(cookieName, cookieValue);
            stack.setValue(cookieName, cookieValue);
        }
    }

    protected void injectIntoCookiesAwareAction(Object action, Map<String, String> cookiesMap) {
        if (action instanceof CookiesAware) {
            LOG.debug("Action [{}] implements CookiesAware, injecting cookies map [{}]", action, cookiesMap);
            ((CookiesAware)action).setCookiesMap(cookiesMap);
        }
        if (action instanceof org.apache.struts2.action.CookiesAware) {
            LOG.debug("Action [{}] implements CookiesAware, injecting cookies map [{}]", action, cookiesMap);
            ((org.apache.struts2.action.CookiesAware)action).withCookies(cookiesMap);
        }
    }
}

