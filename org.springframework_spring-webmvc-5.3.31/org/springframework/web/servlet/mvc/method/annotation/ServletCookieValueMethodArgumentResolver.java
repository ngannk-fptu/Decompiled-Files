/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.core.MethodParameter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.method.annotation.AbstractCookieValueMethodArgumentResolver
 *  org.springframework.web.util.UrlPathHelper
 *  org.springframework.web.util.WebUtils
 */
package org.springframework.web.servlet.mvc.method.annotation;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractCookieValueMethodArgumentResolver;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

public class ServletCookieValueMethodArgumentResolver
extends AbstractCookieValueMethodArgumentResolver {
    private UrlPathHelper urlPathHelper = UrlPathHelper.defaultInstance;

    public ServletCookieValueMethodArgumentResolver(@Nullable ConfigurableBeanFactory beanFactory) {
        super(beanFactory);
    }

    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }

    @Nullable
    protected Object resolveName(String cookieName, MethodParameter parameter, NativeWebRequest webRequest) throws Exception {
        HttpServletRequest servletRequest = (HttpServletRequest)webRequest.getNativeRequest(HttpServletRequest.class);
        Assert.state((servletRequest != null ? 1 : 0) != 0, (String)"No HttpServletRequest");
        Cookie cookieValue = WebUtils.getCookie((HttpServletRequest)servletRequest, (String)cookieName);
        if (Cookie.class.isAssignableFrom(parameter.getNestedParameterType())) {
            return cookieValue;
        }
        if (cookieValue != null) {
            return this.urlPathHelper.decodeRequestString(servletRequest, cookieValue.getValue());
        }
        return null;
    }
}

