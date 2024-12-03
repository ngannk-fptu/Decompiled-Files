/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerInterceptor;

public class ConversionServiceExposingInterceptor
implements HandlerInterceptor {
    private final ConversionService conversionService;

    public ConversionServiceExposingInterceptor(ConversionService conversionService) {
        Assert.notNull((Object)conversionService, "The ConversionService may not be null");
        this.conversionService = conversionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        request.setAttribute(ConversionService.class.getName(), (Object)this.conversionService);
        return true;
    }
}

