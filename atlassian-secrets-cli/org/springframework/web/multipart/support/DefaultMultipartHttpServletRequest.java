/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.multipart.support;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest;

public class DefaultMultipartHttpServletRequest
extends AbstractMultipartHttpServletRequest {
    private static final String CONTENT_TYPE = "Content-Type";
    @Nullable
    private Map<String, String[]> multipartParameters;
    @Nullable
    private Map<String, String> multipartParameterContentTypes;

    public DefaultMultipartHttpServletRequest(HttpServletRequest request, MultiValueMap<String, MultipartFile> mpFiles, Map<String, String[]> mpParams, Map<String, String> mpParamContentTypes) {
        super(request);
        this.setMultipartFiles(mpFiles);
        this.setMultipartParameters(mpParams);
        this.setMultipartParameterContentTypes(mpParamContentTypes);
    }

    public DefaultMultipartHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    @Nullable
    public String getParameter(String name) {
        String[] values = this.getMultipartParameters().get(name);
        if (values != null) {
            return values.length > 0 ? values[0] : null;
        }
        return super.getParameter(name);
    }

    public String[] getParameterValues(String name) {
        String[] values = this.getMultipartParameters().get(name);
        if (values != null) {
            return values;
        }
        return super.getParameterValues(name);
    }

    public Enumeration<String> getParameterNames() {
        Map<String, String[]> multipartParameters = this.getMultipartParameters();
        if (multipartParameters.isEmpty()) {
            return super.getParameterNames();
        }
        LinkedHashSet paramNames = new LinkedHashSet();
        Enumeration paramEnum = super.getParameterNames();
        while (paramEnum.hasMoreElements()) {
            paramNames.add(paramEnum.nextElement());
        }
        paramNames.addAll(multipartParameters.keySet());
        return Collections.enumeration(paramNames);
    }

    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> multipartParameters = this.getMultipartParameters();
        if (multipartParameters.isEmpty()) {
            return super.getParameterMap();
        }
        LinkedHashMap<String, String[]> paramMap = new LinkedHashMap<String, String[]>();
        paramMap.putAll(super.getParameterMap());
        paramMap.putAll(multipartParameters);
        return paramMap;
    }

    @Override
    public String getMultipartContentType(String paramOrFileName) {
        MultipartFile file = this.getFile(paramOrFileName);
        if (file != null) {
            return file.getContentType();
        }
        return this.getMultipartParameterContentTypes().get(paramOrFileName);
    }

    @Override
    public HttpHeaders getMultipartHeaders(String paramOrFileName) {
        String contentType = this.getMultipartContentType(paramOrFileName);
        if (contentType != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(CONTENT_TYPE, contentType);
            return headers;
        }
        return null;
    }

    protected final void setMultipartParameters(Map<String, String[]> multipartParameters) {
        this.multipartParameters = multipartParameters;
    }

    protected Map<String, String[]> getMultipartParameters() {
        if (this.multipartParameters == null) {
            this.initializeMultipart();
        }
        return this.multipartParameters;
    }

    protected final void setMultipartParameterContentTypes(Map<String, String> multipartParameterContentTypes) {
        this.multipartParameterContentTypes = multipartParameterContentTypes;
    }

    protected Map<String, String> getMultipartParameterContentTypes() {
        if (this.multipartParameterContentTypes == null) {
            this.initializeMultipart();
        }
        return this.multipartParameterContentTypes;
    }
}

