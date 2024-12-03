/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.bind;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.support.StandardServletPartUtils;
import org.springframework.web.util.WebUtils;

public class ServletRequestDataBinder
extends WebDataBinder {
    public ServletRequestDataBinder(@Nullable Object target) {
        super(target);
    }

    public ServletRequestDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }

    public void bind(ServletRequest request) {
        HttpServletRequest httpServletRequest;
        ServletRequestParameterPropertyValues mpvs = new ServletRequestParameterPropertyValues(request);
        MultipartRequest multipartRequest = WebUtils.getNativeRequest(request, MultipartRequest.class);
        if (multipartRequest != null) {
            this.bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
        } else if (StringUtils.startsWithIgnoreCase(request.getContentType(), "multipart/form-data") && (httpServletRequest = WebUtils.getNativeRequest(request, HttpServletRequest.class)) != null && HttpMethod.POST.matches(httpServletRequest.getMethod())) {
            StandardServletPartUtils.bindParts(httpServletRequest, mpvs, this.isBindEmptyMultipartFiles());
        }
        this.addBindValues(mpvs, request);
        this.doBind(mpvs);
    }

    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
    }

    public void closeNoCatch() throws ServletRequestBindingException {
        if (this.getBindingResult().hasErrors()) {
            throw new ServletRequestBindingException("Errors binding onto object '" + this.getBindingResult().getObjectName() + "'", new BindException(this.getBindingResult()));
        }
    }
}

