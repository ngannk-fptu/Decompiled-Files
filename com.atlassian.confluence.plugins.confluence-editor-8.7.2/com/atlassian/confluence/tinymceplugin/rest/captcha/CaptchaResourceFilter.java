/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.CaptchaManager
 *  com.sun.jersey.spi.container.ContainerRequest
 *  com.sun.jersey.spi.container.ContainerRequestFilter
 *  com.sun.jersey.spi.container.ContainerResponseFilter
 *  com.sun.jersey.spi.container.ResourceFilter
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.tinymceplugin.rest.captcha;

import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.confluence.tinymceplugin.rest.captcha.CaptchaCheckFailedException;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import javax.servlet.http.HttpServletRequest;

public class CaptchaResourceFilter
implements ResourceFilter,
ContainerRequestFilter {
    public static final String CAPTCHA_HEADER_ID = "X-Atlassian-Captcha-Id";
    public static final String CAPTCHA_HEADER_RESPONSE = "X-Atlassian-Captcha-Response";
    private final CaptchaManager captchaManager;

    public CaptchaResourceFilter(CaptchaManager captchaManager) {
        this.captchaManager = captchaManager;
    }

    public ContainerRequest filter(ContainerRequest request) {
        this.filter(request.getHeaderValue(CAPTCHA_HEADER_ID), request.getHeaderValue(CAPTCHA_HEADER_RESPONSE));
        return request;
    }

    public void filter(HttpServletRequest req) {
        this.filter(req.getHeader(CAPTCHA_HEADER_ID), req.getHeader(CAPTCHA_HEADER_RESPONSE));
    }

    public void filter(String captchaId, String captchaResponse) throws CaptchaCheckFailedException {
        if (this.captchaManager.isCaptchaEnabled() && !this.captchaManager.validateCaptcha(captchaId, captchaResponse)) {
            throw new CaptchaCheckFailedException();
        }
    }

    public ContainerRequestFilter getRequestFilter() {
        return this;
    }

    public ContainerResponseFilter getResponseFilter() {
        return null;
    }
}

