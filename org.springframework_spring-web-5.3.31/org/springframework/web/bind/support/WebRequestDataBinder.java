/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.beans.MutablePropertyValues
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 *  org.springframework.validation.BindException
 */
package org.springframework.web.bind.support;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.support.StandardServletPartUtils;

public class WebRequestDataBinder
extends WebDataBinder {
    public WebRequestDataBinder(@Nullable Object target) {
        super(target);
    }

    public WebRequestDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }

    public void bind(WebRequest request) {
        MutablePropertyValues mpvs = new MutablePropertyValues(request.getParameterMap());
        if (request instanceof NativeWebRequest) {
            HttpServletRequest servletRequest;
            NativeWebRequest nativeRequest = (NativeWebRequest)request;
            MultipartRequest multipartRequest = nativeRequest.getNativeRequest(MultipartRequest.class);
            if (multipartRequest != null) {
                this.bindMultipart((Map<String, List<MultipartFile>>)multipartRequest.getMultiFileMap(), mpvs);
            } else if (StringUtils.startsWithIgnoreCase((String)request.getHeader("Content-Type"), (String)"multipart/form-data") && (servletRequest = nativeRequest.getNativeRequest(HttpServletRequest.class)) != null && HttpMethod.POST.matches(servletRequest.getMethod())) {
                StandardServletPartUtils.bindParts(servletRequest, mpvs, this.isBindEmptyMultipartFiles());
            }
        }
        this.doBind(mpvs);
    }

    public void closeNoCatch() throws BindException {
        if (this.getBindingResult().hasErrors()) {
            throw new BindException(this.getBindingResult());
        }
    }
}

