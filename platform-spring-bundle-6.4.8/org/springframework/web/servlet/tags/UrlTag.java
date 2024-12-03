/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.jsp.JspException
 */
package org.springframework.web.servlet.tags;

import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.RequestDataValueProcessor;
import org.springframework.web.servlet.tags.HtmlEscapingAwareTag;
import org.springframework.web.servlet.tags.Param;
import org.springframework.web.servlet.tags.ParamAware;
import org.springframework.web.util.JavaScriptUtils;
import org.springframework.web.util.TagUtils;
import org.springframework.web.util.UriUtils;

public class UrlTag
extends HtmlEscapingAwareTag
implements ParamAware {
    private static final String URL_TEMPLATE_DELIMITER_PREFIX = "{";
    private static final String URL_TEMPLATE_DELIMITER_SUFFIX = "}";
    private static final String URL_TYPE_ABSOLUTE = "://";
    private List<Param> params = Collections.emptyList();
    private Set<String> templateParams = Collections.emptySet();
    @Nullable
    private UrlType type;
    @Nullable
    private String value;
    @Nullable
    private String context;
    @Nullable
    private String var;
    private int scope = 1;
    private boolean javaScriptEscape = false;

    public void setValue(String value) {
        if (value.contains(URL_TYPE_ABSOLUTE)) {
            this.type = UrlType.ABSOLUTE;
            this.value = value;
        } else if (value.startsWith("/")) {
            this.type = UrlType.CONTEXT_RELATIVE;
            this.value = value;
        } else {
            this.type = UrlType.RELATIVE;
            this.value = value;
        }
    }

    public void setContext(String context) {
        this.context = context.startsWith("/") ? context : "/" + context;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setScope(String scope) {
        this.scope = TagUtils.getScope(scope);
    }

    public void setJavaScriptEscape(boolean javaScriptEscape) throws JspException {
        this.javaScriptEscape = javaScriptEscape;
    }

    @Override
    public void addParam(Param param) {
        this.params.add(param);
    }

    @Override
    public int doStartTagInternal() throws JspException {
        this.params = new ArrayList<Param>();
        this.templateParams = new HashSet<String>();
        return 1;
    }

    public int doEndTag() throws JspException {
        String url = this.createUrl();
        RequestDataValueProcessor processor = this.getRequestContext().getRequestDataValueProcessor();
        ServletRequest request = this.pageContext.getRequest();
        if (processor != null && request instanceof HttpServletRequest) {
            url = processor.processUrl((HttpServletRequest)request, url);
        }
        if (this.var == null) {
            try {
                this.pageContext.getOut().print(url);
            }
            catch (IOException ex) {
                throw new JspException((Throwable)ex);
            }
        } else {
            this.pageContext.setAttribute(this.var, (Object)url, this.scope);
        }
        return 6;
    }

    String createUrl() throws JspException {
        Assert.state(this.value != null, "No value set");
        HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse)this.pageContext.getResponse();
        StringBuilder url = new StringBuilder();
        if (this.type == UrlType.CONTEXT_RELATIVE) {
            if (this.context == null) {
                url.append(request.getContextPath());
            } else if (this.context.endsWith("/")) {
                url.append(this.context, 0, this.context.length() - 1);
            } else {
                url.append(this.context);
            }
        }
        if (this.type != UrlType.RELATIVE && this.type != UrlType.ABSOLUTE && !this.value.startsWith("/")) {
            url.append('/');
        }
        url.append(this.replaceUriTemplateParams(this.value, this.params, this.templateParams));
        url.append(this.createQueryString(this.params, this.templateParams, url.indexOf("?") == -1));
        String urlStr = url.toString();
        if (this.type != UrlType.ABSOLUTE) {
            urlStr = response.encodeURL(urlStr);
        }
        urlStr = this.htmlEscape(urlStr);
        urlStr = this.javaScriptEscape ? JavaScriptUtils.javaScriptEscape(urlStr) : urlStr;
        return urlStr;
    }

    protected String createQueryString(List<Param> params, Set<String> usedParams, boolean includeQueryStringDelimiter) throws JspException {
        String encoding = this.pageContext.getResponse().getCharacterEncoding();
        StringBuilder qs = new StringBuilder();
        for (Param param : params) {
            if (usedParams.contains(param.getName()) || !StringUtils.hasLength(param.getName())) continue;
            if (includeQueryStringDelimiter && qs.length() == 0) {
                qs.append('?');
            } else {
                qs.append('&');
            }
            try {
                qs.append(UriUtils.encodeQueryParam(param.getName(), encoding));
                if (param.getValue() == null) continue;
                qs.append('=');
                qs.append(UriUtils.encodeQueryParam(param.getValue(), encoding));
            }
            catch (UnsupportedCharsetException ex) {
                throw new JspException((Throwable)ex);
            }
        }
        return qs.toString();
    }

    protected String replaceUriTemplateParams(String uri, List<Param> params, Set<String> usedParams) throws JspException {
        String encoding = this.pageContext.getResponse().getCharacterEncoding();
        for (Param param : params) {
            String value;
            String template = URL_TEMPLATE_DELIMITER_PREFIX + param.getName() + URL_TEMPLATE_DELIMITER_SUFFIX;
            if (uri.contains(template)) {
                usedParams.add(param.getName());
                value = param.getValue();
                try {
                    uri = StringUtils.replace(uri, template, value != null ? UriUtils.encodePath(value, encoding) : "");
                    continue;
                }
                catch (UnsupportedCharsetException ex) {
                    throw new JspException((Throwable)ex);
                }
            }
            template = "{/" + param.getName() + URL_TEMPLATE_DELIMITER_SUFFIX;
            if (!uri.contains(template)) continue;
            usedParams.add(param.getName());
            value = param.getValue();
            try {
                uri = StringUtils.replace(uri, template, value != null ? UriUtils.encodePathSegment(value, encoding) : "");
            }
            catch (UnsupportedCharsetException ex) {
                throw new JspException((Throwable)ex);
            }
        }
        return uri;
    }

    private static enum UrlType {
        CONTEXT_RELATIVE,
        RELATIVE,
        ABSOLUTE;

    }
}

