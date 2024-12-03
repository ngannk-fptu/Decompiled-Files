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
 */
package org.springframework.web.filter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class HttpPutFormContentFilter
extends OncePerRequestFilter {
    private FormHttpMessageConverter formConverter = new AllEncompassingFormHttpMessageConverter();

    public void setFormConverter(FormHttpMessageConverter converter) {
        Assert.notNull((Object)converter, "FormHttpMessageConverter is required.");
        this.formConverter = converter;
    }

    public FormHttpMessageConverter getFormConverter() {
        return this.formConverter;
    }

    public void setCharset(Charset charset) {
        this.formConverter.setCharset(charset);
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ServletServerHttpRequest inputMessage;
        Object formParameters;
        if (("PUT".equals(request.getMethod()) || "PATCH".equals(request.getMethod())) && this.isFormContentType(request) && !(formParameters = this.formConverter.read((Class)null, (HttpInputMessage)(inputMessage = new ServletServerHttpRequest(request){

            @Override
            public InputStream getBody() throws IOException {
                return request.getInputStream();
            }
        }))).isEmpty()) {
            HttpPutFormContentRequestWrapper wrapper = new HttpPutFormContentRequestWrapper(request, (MultiValueMap<String, String>)formParameters);
            filterChain.doFilter((ServletRequest)wrapper, (ServletResponse)response);
            return;
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }

    private boolean isFormContentType(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType != null) {
            try {
                MediaType mediaType = MediaType.parseMediaType(contentType);
                return MediaType.APPLICATION_FORM_URLENCODED.includes(mediaType);
            }
            catch (IllegalArgumentException ex) {
                return false;
            }
        }
        return false;
    }

    private static class HttpPutFormContentRequestWrapper
    extends HttpServletRequestWrapper {
        private MultiValueMap<String, String> formParameters;

        public HttpPutFormContentRequestWrapper(HttpServletRequest request, MultiValueMap<String, String> parameters) {
            super(request);
            this.formParameters = parameters;
        }

        @Nullable
        public String getParameter(String name) {
            String queryStringValue = super.getParameter(name);
            String formValue = this.formParameters.getFirst(name);
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
            LinkedHashSet<Object> names = new LinkedHashSet<Object>();
            names.addAll(Collections.list(super.getParameterNames()));
            names.addAll(this.formParameters.keySet());
            return Collections.enumeration(names);
        }

        @Nullable
        public String[] getParameterValues(String name) {
            String[] parameterValues = super.getParameterValues(name);
            List formParam = (List)this.formParameters.get(name);
            if (formParam == null) {
                return parameterValues;
            }
            if (parameterValues == null || this.getQueryString() == null) {
                return StringUtils.toStringArray(formParam);
            }
            ArrayList<String> result = new ArrayList<String>(parameterValues.length + formParam.size());
            result.addAll(Arrays.asList(parameterValues));
            result.addAll(formParam);
            return StringUtils.toStringArray(result);
        }
    }
}

