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
import java.util.HashMap;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tuckey.web.filters.urlrewrite.RewrittenUrl;
import org.tuckey.web.filters.urlrewrite.Rule;
import org.tuckey.web.filters.urlrewrite.UrlRewriteWrappedRequest;
import org.tuckey.web.filters.urlrewrite.UrlRewriteWrappedResponse;
import org.tuckey.web.filters.urlrewrite.UrlRewriter;
import org.tuckey.web.filters.urlrewrite.utils.Log;

public class RuleChain
implements FilterChain {
    private static Log log = Log.getLog(UrlRewriter.class);
    private int ruleIdxToRun = 0;
    private RewrittenUrl finalRewrittenRequest = null;
    private String finalToUrl;
    private List rules;
    private boolean requestRewritten;
    private boolean rewriteHandled = false;
    private boolean responseHandled;
    private FilterChain parentChain;
    private UrlRewriter urlRewriter;

    public RuleChain(UrlRewriter urlRewriter, String originalUrl, FilterChain parentChain) {
        this.finalToUrl = originalUrl;
        this.urlRewriter = urlRewriter;
        this.rules = urlRewriter.getConf().getRules();
        this.parentChain = parentChain;
    }

    private void doRuleProcessing(HttpServletRequest hsRequest, HttpServletResponse hsResponse) throws IOException, ServletException, InvocationTargetException {
        int currentIdx = this.ruleIdxToRun++;
        Rule rule = (Rule)this.rules.get(currentIdx);
        RewrittenUrl rewrittenUrl = rule.matches(this.finalToUrl, hsRequest, hsResponse, this);
        if (rule.isFilter()) {
            this.dontProcessAnyMoreRules();
        }
        if (rewrittenUrl != null) {
            log.trace("got a rewritten url");
            this.finalRewrittenRequest = rewrittenUrl;
            this.finalToUrl = rewrittenUrl.getTarget();
            if (rule.isLast()) {
                log.debug("rule is last");
                this.dontProcessAnyMoreRules();
            }
        }
    }

    private void dontProcessAnyMoreRules() {
        this.ruleIdxToRun = this.rules.size();
    }

    public RewrittenUrl getFinalRewrittenRequest() {
        return this.finalRewrittenRequest;
    }

    public boolean isResponseHandled() {
        return this.responseHandled;
    }

    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        try {
            this.process(request, response);
            this.handleRewrite(request, response);
        }
        catch (InvocationTargetException e) {
            this.handleExcep(request, response, e);
        }
    }

    private void handleExcep(ServletRequest request, ServletResponse response, InvocationTargetException e) throws IOException, ServletException {
        this.dontProcessAnyMoreRules();
        this.finalRewrittenRequest = this.urlRewriter.handleInvocationTargetException((HttpServletRequest)request, (HttpServletResponse)response, e);
        this.handleRewrite(request, response);
    }

    public void process(ServletRequest request, ServletResponse response) throws IOException, ServletException, InvocationTargetException {
        while (this.ruleIdxToRun < this.rules.size()) {
            this.doRuleProcessing((HttpServletRequest)request, (HttpServletResponse)response);
        }
    }

    public void doRules(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        try {
            this.process(request, response);
            this.handleRewrite(request, response);
        }
        catch (InvocationTargetException e) {
            this.handleExcep(request, response, e);
        }
        catch (ServletException e) {
            if (e.getCause() instanceof InvocationTargetException) {
                this.handleExcep(request, response, (InvocationTargetException)e.getCause());
            }
            throw e;
        }
    }

    private void handleRewrite(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if (this.rewriteHandled) {
            return;
        }
        this.rewriteHandled = true;
        if (response instanceof UrlRewriteWrappedResponse && request instanceof HttpServletRequest) {
            HashMap overiddenRequestParameters = ((UrlRewriteWrappedResponse)response).getOverridenRequestParameters();
            String overiddenMethod = ((UrlRewriteWrappedResponse)response).getOverridenMethod();
            if (overiddenRequestParameters != null || overiddenMethod != null) {
                request = new UrlRewriteWrappedRequest((HttpServletRequest)request, overiddenRequestParameters, overiddenMethod);
            }
        }
        if (this.finalRewrittenRequest != null) {
            this.responseHandled = true;
            this.requestRewritten = this.finalRewrittenRequest.doRewrite((HttpServletRequest)request, (HttpServletResponse)response, this.parentChain);
        }
        if (!this.requestRewritten) {
            this.responseHandled = true;
            this.parentChain.doFilter(request, response);
        }
    }
}

