/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.Part
 */
package org.springframework.web.bind.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartRequest;

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
            MultipartRequest multipartRequest = ((NativeWebRequest)request).getNativeRequest(MultipartRequest.class);
            if (multipartRequest != null) {
                this.bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
            } else if (this.isMultipartRequest(request) && (servletRequest = ((NativeWebRequest)request).getNativeRequest(HttpServletRequest.class)) != null) {
                this.bindParts(servletRequest, mpvs);
            }
        }
        this.doBind(mpvs);
    }

    private boolean isMultipartRequest(WebRequest request) {
        String contentType = request.getHeader("Content-Type");
        return StringUtils.startsWithIgnoreCase(contentType, "multipart/");
    }

    private void bindParts(HttpServletRequest request, MutablePropertyValues mpvs) {
        try {
            LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
            for (Part part : request.getParts()) {
                map.add(part.getName(), part);
            }
            map.forEach((key, values) -> {
                if (values.size() == 1) {
                    Part part = (Part)values.get(0);
                    if (this.isBindEmptyMultipartFiles() || part.getSize() > 0L) {
                        mpvs.add((String)key, part);
                    }
                } else {
                    mpvs.add((String)key, values);
                }
            });
        }
        catch (Exception ex) {
            throw new MultipartException("Failed to get request parts", ex);
        }
    }

    public void closeNoCatch() throws BindException {
        if (this.getBindingResult().hasErrors()) {
            throw new BindException(this.getBindingResult());
        }
    }
}

