/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.view;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.SmartView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.support.RequestDataValueProcessor;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

public class RedirectView
extends AbstractUrlBasedView
implements SmartView {
    private static final Pattern URI_TEMPLATE_VARIABLE_PATTERN = Pattern.compile("\\{([^/]+?)\\}");
    private boolean contextRelative = false;
    private boolean http10Compatible = true;
    private boolean exposeModelAttributes = true;
    @Nullable
    private String encodingScheme;
    @Nullable
    private HttpStatus statusCode;
    private boolean expandUriTemplateVariables = true;
    private boolean propagateQueryParams = false;
    @Nullable
    private String[] hosts;

    public RedirectView() {
        this.setExposePathVariables(false);
    }

    public RedirectView(String url) {
        super(url);
        this.setExposePathVariables(false);
    }

    public RedirectView(String url, boolean contextRelative) {
        super(url);
        this.contextRelative = contextRelative;
        this.setExposePathVariables(false);
    }

    public RedirectView(String url, boolean contextRelative, boolean http10Compatible) {
        super(url);
        this.contextRelative = contextRelative;
        this.http10Compatible = http10Compatible;
        this.setExposePathVariables(false);
    }

    public RedirectView(String url, boolean contextRelative, boolean http10Compatible, boolean exposeModelAttributes) {
        super(url);
        this.contextRelative = contextRelative;
        this.http10Compatible = http10Compatible;
        this.exposeModelAttributes = exposeModelAttributes;
        this.setExposePathVariables(false);
    }

    public void setContextRelative(boolean contextRelative) {
        this.contextRelative = contextRelative;
    }

    public void setHttp10Compatible(boolean http10Compatible) {
        this.http10Compatible = http10Compatible;
    }

    public void setExposeModelAttributes(boolean exposeModelAttributes) {
        this.exposeModelAttributes = exposeModelAttributes;
    }

    public void setEncodingScheme(String encodingScheme) {
        this.encodingScheme = encodingScheme;
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    public void setExpandUriTemplateVariables(boolean expandUriTemplateVariables) {
        this.expandUriTemplateVariables = expandUriTemplateVariables;
    }

    public void setPropagateQueryParams(boolean propagateQueryParams) {
        this.propagateQueryParams = propagateQueryParams;
    }

    public boolean isPropagateQueryProperties() {
        return this.propagateQueryParams;
    }

    public void setHosts(String ... hosts) {
        this.hosts = hosts;
    }

    @Nullable
    public String[] getHosts() {
        return this.hosts;
    }

    @Override
    public boolean isRedirectView() {
        return true;
    }

    @Override
    protected boolean isContextRequired() {
        return false;
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String targetUrl = this.createTargetUrl(model, request);
        targetUrl = this.updateTargetUrl(targetUrl, model, request, response);
        RequestContextUtils.saveOutputFlashMap(targetUrl, request, response);
        this.sendRedirect(request, response, targetUrl, this.http10Compatible);
    }

    protected final String createTargetUrl(Map<String, Object> model, HttpServletRequest request) throws UnsupportedEncodingException {
        StringBuilder targetUrl = new StringBuilder();
        String url = this.getUrl();
        Assert.state(url != null, "'url' not set");
        if (this.contextRelative && this.getUrl().startsWith("/")) {
            targetUrl.append(this.getContextPath(request));
        }
        targetUrl.append(this.getUrl());
        String enc = this.encodingScheme;
        if (enc == null) {
            enc = request.getCharacterEncoding();
        }
        if (enc == null) {
            enc = "ISO-8859-1";
        }
        if (this.expandUriTemplateVariables && StringUtils.hasText(targetUrl)) {
            Map<String, String> variables = this.getCurrentRequestUriVariables(request);
            targetUrl = this.replaceUriTemplateVariables(targetUrl.toString(), model, variables, enc);
        }
        if (this.isPropagateQueryProperties()) {
            this.appendCurrentQueryParams(targetUrl, request);
        }
        if (this.exposeModelAttributes) {
            this.appendQueryProperties(targetUrl, model, enc);
        }
        return targetUrl.toString();
    }

    private String getContextPath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        while (contextPath.startsWith("//")) {
            contextPath = contextPath.substring(1);
        }
        return contextPath;
    }

    protected StringBuilder replaceUriTemplateVariables(String targetUrl, Map<String, Object> model, Map<String, String> currentUriVariables, String encodingScheme) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        Matcher matcher = URI_TEMPLATE_VARIABLE_PATTERN.matcher(targetUrl);
        int endLastMatch = 0;
        while (matcher.find()) {
            Object value;
            String name = matcher.group(1);
            Object object = value = model.containsKey(name) ? model.remove(name) : currentUriVariables.get(name);
            if (value == null) {
                throw new IllegalArgumentException("Model has no value for key '" + name + "'");
            }
            result.append(targetUrl, endLastMatch, matcher.start());
            result.append(UriUtils.encodePathSegment(value.toString(), encodingScheme));
            endLastMatch = matcher.end();
        }
        result.append(targetUrl.substring(endLastMatch));
        return result;
    }

    private Map<String, String> getCurrentRequestUriVariables(HttpServletRequest request) {
        String name = HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
        Map<String, String> uriVars = (Map<String, String>)request.getAttribute(name);
        return uriVars != null ? uriVars : Collections.emptyMap();
    }

    protected void appendCurrentQueryParams(StringBuilder targetUrl, HttpServletRequest request) {
        String query = request.getQueryString();
        if (StringUtils.hasText(query)) {
            String fragment = null;
            int anchorIndex = targetUrl.indexOf("#");
            if (anchorIndex > -1) {
                fragment = targetUrl.substring(anchorIndex);
                targetUrl.delete(anchorIndex, targetUrl.length());
            }
            if (targetUrl.toString().indexOf(63) < 0) {
                targetUrl.append('?').append(query);
            } else {
                targetUrl.append('&').append(query);
            }
            if (fragment != null) {
                targetUrl.append(fragment);
            }
        }
    }

    protected void appendQueryProperties(StringBuilder targetUrl, Map<String, Object> model, String encodingScheme) throws UnsupportedEncodingException {
        String fragment = null;
        int anchorIndex = targetUrl.indexOf("#");
        if (anchorIndex > -1) {
            fragment = targetUrl.substring(anchorIndex);
            targetUrl.delete(anchorIndex, targetUrl.length());
        }
        boolean first = targetUrl.toString().indexOf(63) < 0;
        for (Map.Entry<String, Object> entry : this.queryProperties(model).entrySet()) {
            Object rawValue = entry.getValue();
            Collection<Object> values = rawValue != null && rawValue.getClass().isArray() ? CollectionUtils.arrayToList(rawValue) : (rawValue instanceof Collection ? (Set<Object>)rawValue : Collections.singleton(rawValue));
            for (Object value : values) {
                if (first) {
                    targetUrl.append('?');
                    first = false;
                } else {
                    targetUrl.append('&');
                }
                String encodedKey = this.urlEncode(entry.getKey(), encodingScheme);
                String encodedValue = value != null ? this.urlEncode(value.toString(), encodingScheme) : "";
                targetUrl.append(encodedKey).append('=').append(encodedValue);
            }
        }
        if (fragment != null) {
            targetUrl.append(fragment);
        }
    }

    protected Map<String, Object> queryProperties(Map<String, Object> model) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        model.forEach((name, value) -> {
            if (this.isEligibleProperty((String)name, value)) {
                result.put((String)name, value);
            }
        });
        return result;
    }

    protected boolean isEligibleProperty(String key, @Nullable Object value) {
        if (value == null) {
            return false;
        }
        if (this.isEligibleValue(value)) {
            return true;
        }
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            if (length == 0) {
                return false;
            }
            for (int i2 = 0; i2 < length; ++i2) {
                Object element = Array.get(value, i2);
                if (this.isEligibleValue(element)) continue;
                return false;
            }
            return true;
        }
        if (value instanceof Collection) {
            Collection coll = (Collection)value;
            if (coll.isEmpty()) {
                return false;
            }
            for (Object element : coll) {
                if (this.isEligibleValue(element)) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    protected boolean isEligibleValue(@Nullable Object value) {
        return value != null && BeanUtils.isSimpleValueType(value.getClass());
    }

    protected String urlEncode(String input, String encodingScheme) throws UnsupportedEncodingException {
        return URLEncoder.encode(input, encodingScheme);
    }

    protected String updateTargetUrl(String targetUrl, Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
        WebApplicationContext wac = this.getWebApplicationContext();
        if (wac == null) {
            wac = RequestContextUtils.findWebApplicationContext(request, this.getServletContext());
        }
        if (wac != null && wac.containsBean("requestDataValueProcessor")) {
            RequestDataValueProcessor processor = wac.getBean("requestDataValueProcessor", RequestDataValueProcessor.class);
            return processor.processUrl(request, targetUrl);
        }
        return targetUrl;
    }

    protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String targetUrl, boolean http10Compatible) throws IOException {
        String encodedURL;
        String string = encodedURL = this.isRemoteHost(targetUrl) ? targetUrl : response.encodeRedirectURL(targetUrl);
        if (http10Compatible) {
            HttpStatus attributeStatusCode = (HttpStatus)((Object)request.getAttribute(View.RESPONSE_STATUS_ATTRIBUTE));
            if (this.statusCode != null) {
                response.setStatus(this.statusCode.value());
                response.setHeader("Location", encodedURL);
            } else if (attributeStatusCode != null) {
                response.setStatus(attributeStatusCode.value());
                response.setHeader("Location", encodedURL);
            } else {
                response.sendRedirect(encodedURL);
            }
        } else {
            HttpStatus statusCode = this.getHttp11StatusCode(request, response, targetUrl);
            response.setStatus(statusCode.value());
            response.setHeader("Location", encodedURL);
        }
    }

    protected boolean isRemoteHost(String targetUrl) {
        if (ObjectUtils.isEmpty(this.getHosts())) {
            return false;
        }
        String targetHost = UriComponentsBuilder.fromUriString(targetUrl).build().getHost();
        if (!StringUtils.hasLength(targetHost)) {
            return false;
        }
        for (String host : this.getHosts()) {
            if (!targetHost.equals(host)) continue;
            return false;
        }
        return true;
    }

    protected HttpStatus getHttp11StatusCode(HttpServletRequest request, HttpServletResponse response, String targetUrl) {
        if (this.statusCode != null) {
            return this.statusCode;
        }
        HttpStatus attributeStatusCode = (HttpStatus)((Object)request.getAttribute(View.RESPONSE_STATUS_ATTRIBUTE));
        if (attributeStatusCode != null) {
            return attributeStatusCode;
        }
        return HttpStatus.SEE_OTHER;
    }
}

