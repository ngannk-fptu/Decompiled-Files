/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.web.bind.support.WebArgumentResolver
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.context.request.RequestAttributes
 *  org.springframework.web.context.request.RequestContextHolder
 *  org.springframework.web.context.request.ServletRequestAttributes
 *  org.springframework.web.context.request.ServletWebRequest
 *  org.springframework.web.method.annotation.AbstractWebArgumentResolverAdapter
 */
package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.annotation.AbstractWebArgumentResolverAdapter;

public class ServletWebArgumentResolverAdapter
extends AbstractWebArgumentResolverAdapter {
    public ServletWebArgumentResolverAdapter(WebArgumentResolver adaptee) {
        super(adaptee);
    }

    protected NativeWebRequest getWebRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Assert.state((boolean)(requestAttributes instanceof ServletRequestAttributes), (String)"No ServletRequestAttributes");
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)requestAttributes;
        return new ServletWebRequest(servletRequestAttributes.getRequest());
    }
}

