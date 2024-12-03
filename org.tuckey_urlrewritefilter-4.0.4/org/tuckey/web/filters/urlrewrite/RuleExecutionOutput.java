/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.tuckey.web.filters.urlrewrite;

import javax.servlet.ServletContext;
import org.tuckey.web.filters.urlrewrite.NormalRewrittenUrl;
import org.tuckey.web.filters.urlrewrite.RewrittenUrl;
import org.tuckey.web.filters.urlrewrite.extend.RewriteMatch;
import org.tuckey.web.filters.urlrewrite.utils.Log;

public class RuleExecutionOutput {
    private static Log log = Log.getLog(RuleExecutionOutput.class);
    private String replacedUrl;
    private ServletContext replacedUrlContext = null;
    private boolean ruleMatched = false;
    private boolean stopFilterMatch = false;
    private boolean noSubstitution = false;
    private RewriteMatch rewriteMatch;

    public static RewrittenUrl getRewritenUrl(short toType, boolean encodeToUrl, RuleExecutionOutput ruleExecutionOutput) {
        NormalRewrittenUrl rewrittenRequest = new NormalRewrittenUrl(ruleExecutionOutput);
        String toUrl = ruleExecutionOutput.getReplacedUrl();
        if (ruleExecutionOutput.isNoSubstitution()) {
            if (log.isDebugEnabled()) {
                log.debug("needs no substitution");
            }
        } else if (toType == 0) {
            if (log.isDebugEnabled()) {
                log.debug("needs to be redirected to " + toUrl);
            }
            rewrittenRequest.setRedirect(true);
        } else if (toType == 2) {
            if (log.isDebugEnabled()) {
                log.debug("needs to be permanentely redirected to " + toUrl);
            }
            rewrittenRequest.setPermanentRedirect(true);
        } else if (toType == 3) {
            if (log.isDebugEnabled()) {
                log.debug("needs to be temporarily redirected to " + toUrl);
            }
            rewrittenRequest.setTemporaryRedirect(true);
        } else if (toType == 4) {
            if (log.isDebugEnabled()) {
                log.debug(toUrl + " needs to be pre included");
            }
            rewrittenRequest.setPreInclude(true);
        } else if (toType == 5) {
            if (log.isDebugEnabled()) {
                log.debug(toUrl + " needs to be post included");
            }
            rewrittenRequest.setPostInclude(true);
        } else if (toType == 1) {
            if (log.isDebugEnabled()) {
                log.debug("needs to be forwarded to " + toUrl);
            }
            rewrittenRequest.setForward(true);
        } else if (toType == 6) {
            if (log.isDebugEnabled()) {
                log.debug("needs to be proxied from " + toUrl);
            }
            rewrittenRequest.setProxy(true);
        }
        if (encodeToUrl) {
            rewrittenRequest.setEncode(true);
        } else {
            rewrittenRequest.setEncode(false);
        }
        return rewrittenRequest;
    }

    public RuleExecutionOutput(String replacedUrl, boolean ruleMatched, RewriteMatch lastRunMatch) {
        this.replacedUrl = replacedUrl;
        this.ruleMatched = ruleMatched;
        this.rewriteMatch = lastRunMatch;
    }

    public String getReplacedUrl() {
        return this.replacedUrl;
    }

    public boolean isRuleMatched() {
        return this.ruleMatched;
    }

    public boolean isStopFilterMatch() {
        return this.stopFilterMatch;
    }

    public void setStopFilterMatch(boolean stopFilterMatch) {
        this.stopFilterMatch = stopFilterMatch;
    }

    public void setReplacedUrl(String replacedUrl) {
        this.replacedUrl = replacedUrl;
    }

    public RewriteMatch getRewriteMatch() {
        return this.rewriteMatch;
    }

    public ServletContext getReplacedUrlContext() {
        return this.replacedUrlContext;
    }

    public void setReplacedUrlContext(ServletContext replacedUrlContext) {
        this.replacedUrlContext = replacedUrlContext;
    }

    public boolean isNoSubstitution() {
        return this.noSubstitution;
    }

    public void setNoSubstitution(boolean noSubstitution) {
        this.noSubstitution = noSubstitution;
    }
}

