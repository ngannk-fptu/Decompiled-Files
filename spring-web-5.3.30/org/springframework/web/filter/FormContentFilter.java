/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.filter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class FormContentFilter
extends OncePerRequestFilter {
    private static final List<String> HTTP_METHODS = Arrays.asList("PUT", "PATCH", "DELETE");
    private FormHttpMessageConverter formConverter = new AllEncompassingFormHttpMessageConverter();

    public void setFormConverter(FormHttpMessageConverter converter) {
        Assert.notNull((Object)converter, (String)"FormHttpMessageConverter is required");
        this.formConverter = converter;
    }

    public void setCharset(Charset charset) {
        this.formConverter.setCharset(charset);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        MultiValueMap<String, String> params = this.parseIfNecessary(request);
        if (!CollectionUtils.isEmpty(params)) {
            filterChain.doFilter((ServletRequest)new FormContentRequestWrapper(request, params), (ServletResponse)response);
        } else {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
    }

    @Nullable
    private MultiValueMap<String, String> parseIfNecessary(final HttpServletRequest request) throws IOException {
        if (!this.shouldParse(request)) {
            return null;
        }
        ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(request){

            @Override
            public InputStream getBody() throws IOException {
                return request.getInputStream();
            }
        };
        return this.formConverter.read((Class<? extends MultiValueMap<String, ?>>)null, (HttpInputMessage)inputMessage);
    }

    private boolean shouldParse(HttpServletRequest request) {
        String contentType = request.getContentType();
        String method = request.getMethod();
        if (StringUtils.hasLength((String)contentType) && HTTP_METHODS.contains(method)) {
            try {
                MediaType mediaType = MediaType.parseMediaType(contentType);
                return MediaType.APPLICATION_FORM_URLENCODED.includes(mediaType);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        return false;
    }

    private static class FormContentRequestWrapper
    extends HttpServletRequestWrapper {
        private MultiValueMap<String, String> formParams;

        public FormContentRequestWrapper(HttpServletRequest request, MultiValueMap<String, String> params) {
            super(request);
            this.formParams = params;
        }

        @Nullable
        public String getParameter(String name) {
            String queryStringValue = super.getParameter(name);
            String formValue = (String)this.formParams.getFirst((Object)name);
            return queryStringValue != null ? queryStringValue : formValue;
        }

        public Map<String, String[]> getParameterMap() {
            LinkedHashMap<String, String[]> result = new LinkedHashMap<String, String[]>();
            Enumeration<String> names = this.getParameterNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                result.put(name, this.getParameterValues(name));
            }
            return result;
        }

        public Enumeration<String> getParameterNames() {
            LinkedHashSet names = new LinkedHashSet();
            names.addAll(Collections.list(super.getParameterNames()));
            names.addAll(this.formParams.keySet());
            return Collections.enumeration(names);
        }

        @Nullable
        public String[] getParameterValues(String name) {
            String[] parameterValues = super.getParameterValues(name);
            List formParam = (List)this.formParams.get((Object)name);
            if (formParam == null) {
                return parameterValues;
            }
            if (parameterValues == null || this.getQueryString() == null) {
                return StringUtils.toStringArray((Collection)formParam);
            }
            ArrayList<String> result = new ArrayList<String>(parameterValues.length + formParam.size());
            result.addAll(Arrays.asList(parameterValues));
            result.addAll(formParam);
            return StringUtils.toStringArray(result);
        }
    }
}

