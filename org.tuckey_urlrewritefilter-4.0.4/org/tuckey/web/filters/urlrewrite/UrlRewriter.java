/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.tuckey.web.filters.urlrewrite;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tuckey.web.filters.urlrewrite.CatchElem;
import org.tuckey.web.filters.urlrewrite.Conf;
import org.tuckey.web.filters.urlrewrite.OutboundRule;
import org.tuckey.web.filters.urlrewrite.RewrittenOutboundUrl;
import org.tuckey.web.filters.urlrewrite.RewrittenUrl;
import org.tuckey.web.filters.urlrewrite.RuleChain;
import org.tuckey.web.filters.urlrewrite.utils.Log;
import org.tuckey.web.filters.urlrewrite.utils.StringUtils;
import org.tuckey.web.filters.urlrewrite.utils.URLDecoder;

public class UrlRewriter {
    private static Log log = Log.getLog(UrlRewriter.class);
    private Conf conf;

    public UrlRewriter(Conf conf) {
        this.conf = conf;
    }

    public RewrittenUrl processRequest(HttpServletRequest hsRequest, HttpServletResponse hsResponse) throws IOException, ServletException, InvocationTargetException {
        RuleChain chain = this.getNewChain(hsRequest, null);
        if (chain == null) {
            return null;
        }
        chain.process((ServletRequest)hsRequest, (ServletResponse)hsResponse);
        return chain.getFinalRewrittenRequest();
    }

    public boolean processRequest(HttpServletRequest hsRequest, HttpServletResponse hsResponse, FilterChain parentChain) throws IOException, ServletException {
        RuleChain chain = this.getNewChain(hsRequest, parentChain);
        if (chain == null) {
            return false;
        }
        chain.doRules((ServletRequest)hsRequest, (ServletResponse)hsResponse);
        return chain.isResponseHandled();
    }

    public String getPathWithinApplication(HttpServletRequest request) {
        String contextPath;
        String decodedRequestUri;
        String requestUri = request.getRequestURI();
        if (requestUri == null) {
            requestUri = "";
        }
        String path = StringUtils.startsWithIgnoreCase(decodedRequestUri = this.decodeRequestString(request, requestUri), contextPath = this.getContextPath(request)) && !this.conf.isUseContext() ? decodedRequestUri.substring(contextPath.length()) : (!StringUtils.startsWithIgnoreCase(decodedRequestUri, contextPath) && this.conf.isUseContext() ? contextPath + decodedRequestUri : decodedRequestUri);
        return StringUtils.isBlank(path) ? "/" : path;
    }

    public String getContextPath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        if ("/".equals(contextPath)) {
            contextPath = "";
        }
        return this.decodeRequestString(request, contextPath);
    }

    public String decodeRequestString(HttpServletRequest request, String source) {
        block7: {
            String enc;
            block6: {
                if (this.conf.isDecodeUsingEncodingHeader() && (enc = request.getCharacterEncoding()) != null) {
                    try {
                        return URLDecoder.decodeURL(source, enc);
                    }
                    catch (URISyntaxException ex) {
                        if (!log.isWarnEnabled()) break block6;
                        log.warn("Could not decode: " + source + " (header encoding: '" + enc + "'); exception: " + ex.getMessage());
                    }
                }
            }
            if (this.conf.isDecodeUsingCustomCharsetRequired() && (enc = this.conf.getDecodeUsing()) != null) {
                try {
                    return URLDecoder.decodeURL(source, enc);
                }
                catch (URISyntaxException ex) {
                    if (!log.isWarnEnabled()) break block7;
                    log.warn("Could not decode: " + source + " (encoding: '" + enc + "') using default encoding; exception: " + ex.getMessage());
                }
            }
        }
        return source;
    }

    private RuleChain getNewChain(HttpServletRequest hsRequest, FilterChain parentChain) {
        String query;
        String originalUrl = this.getPathWithinApplication(hsRequest);
        if (originalUrl == null) {
            log.debug("unable to fetch request uri from request.  This shouldn't happen, it may indicate that the web application server has a bug or that the request was not pased correctly.");
            return null;
        }
        if (log.isDebugEnabled()) {
            log.debug("processing request for " + originalUrl);
        }
        if (originalUrl != null && originalUrl.indexOf("?") == -1 && this.conf.isUseQueryString() && (query = hsRequest.getQueryString()) != null && (query = query.trim()).length() > 0) {
            originalUrl = originalUrl + "?" + query;
            log.debug("query string added");
        }
        if (!this.conf.isOk()) {
            log.debug("configuration is not ok.  not rewriting request.");
            return null;
        }
        List rules = this.conf.getRules();
        if (rules.size() == 0) {
            log.debug("there are no rules setup.  not rewriting request.");
            return null;
        }
        return new RuleChain(this, originalUrl, parentChain);
    }

    public RewrittenUrl handleInvocationTargetException(HttpServletRequest hsRequest, HttpServletResponse hsResponse, InvocationTargetException e) throws ServletException, IOException {
        Throwable originalThrowable = this.getOriginalException(e);
        if (log.isDebugEnabled()) {
            log.debug("attampting to find catch for exception " + originalThrowable.getClass().getName());
        }
        List catchElems = this.conf.getCatchElems();
        for (int i = 0; i < catchElems.size(); ++i) {
            CatchElem catchElem = (CatchElem)catchElems.get(i);
            if (!catchElem.matches(originalThrowable)) continue;
            try {
                return catchElem.execute(hsRequest, hsResponse, originalThrowable);
            }
            catch (InvocationTargetException invocationExceptionInner) {
                originalThrowable = this.getOriginalException(invocationExceptionInner);
                log.warn("had exception processing catch, trying the rest of the catches with " + originalThrowable.getClass().getName());
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("exception unhandled", e);
        }
        if (originalThrowable instanceof Error) {
            throw (Error)originalThrowable;
        }
        if (originalThrowable instanceof RuntimeException) {
            throw (RuntimeException)originalThrowable;
        }
        if (originalThrowable instanceof ServletException) {
            throw (ServletException)originalThrowable;
        }
        if (originalThrowable instanceof IOException) {
            throw (IOException)originalThrowable;
        }
        throw new ServletException(originalThrowable);
    }

    private Throwable getOriginalException(InvocationTargetException e) throws ServletException {
        Throwable originalThrowable = e.getTargetException();
        if (originalThrowable == null && (originalThrowable = e.getCause()) == null) {
            throw new ServletException((Throwable)e);
        }
        if (originalThrowable instanceof ServletException) {
            ServletException se = (ServletException)originalThrowable;
            for (int i = 0; i < 5 && se.getCause() instanceof ServletException; ++i) {
                se = (ServletException)se.getCause();
            }
            if (se.getCause() instanceof InvocationTargetException) {
                return this.getOriginalException((InvocationTargetException)se.getCause());
            }
            throw se;
        }
        return originalThrowable;
    }

    public Conf getConf() {
        return this.conf;
    }

    protected RewrittenOutboundUrl processEncodeURL(HttpServletResponse hsResponse, HttpServletRequest hsRequest, boolean encodeUrlHasBeenRun, String outboundUrl) {
        if (log.isDebugEnabled()) {
            log.debug("processing outbound url for " + outboundUrl);
        }
        if (outboundUrl == null) {
            return new RewrittenOutboundUrl(null, true);
        }
        boolean finalEncodeOutboundUrl = true;
        String finalToUrl = outboundUrl;
        List outboundRules = this.conf.getOutboundRules();
        try {
            for (int i = 0; i < outboundRules.size(); ++i) {
                RewrittenOutboundUrl rewrittenUrl;
                OutboundRule outboundRule = (OutboundRule)outboundRules.get(i);
                if (!encodeUrlHasBeenRun && outboundRule.isEncodeFirst() || encodeUrlHasBeenRun && !outboundRule.isEncodeFirst() || (rewrittenUrl = outboundRule.execute(finalToUrl, hsRequest, hsResponse)) == null) continue;
                if (log.isDebugEnabled()) {
                    log.debug("\"" + outboundRule.getDisplayName() + "\" matched");
                }
                finalToUrl = rewrittenUrl.getTarget();
                finalEncodeOutboundUrl = rewrittenUrl.isEncode();
                if (!outboundRule.isLast()) continue;
                log.debug("rule is last");
                break;
            }
        }
        catch (InvocationTargetException e) {
            try {
                this.handleInvocationTargetException(hsRequest, hsResponse, e);
            }
            catch (ServletException e1) {
                log.error(e1);
            }
            catch (IOException e1) {
                log.error(e1);
            }
        }
        return new RewrittenOutboundUrl(finalToUrl, finalEncodeOutboundUrl);
    }

    public void destroy() {
        this.conf.destroy();
    }
}

