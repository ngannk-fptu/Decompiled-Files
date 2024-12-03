/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.tuckey.web.filters.urlrewrite;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tuckey.web.filters.urlrewrite.RequestProxy;
import org.tuckey.web.filters.urlrewrite.RewrittenUrl;
import org.tuckey.web.filters.urlrewrite.RuleExecutionOutput;
import org.tuckey.web.filters.urlrewrite.extend.RewriteMatch;
import org.tuckey.web.filters.urlrewrite.utils.Log;

public class NormalRewrittenUrl
implements RewrittenUrl {
    private static Log log = Log.getLog(RewrittenUrl.class);
    private boolean forward = false;
    private boolean redirect = false;
    private boolean permanentRedirect = false;
    private boolean temporaryRedirect = false;
    private boolean preInclude = false;
    private boolean postInclude = false;
    private boolean proxy = false;
    private String target;
    private boolean encode;
    private boolean stopFilterChain = false;
    private boolean noSubstitution = false;
    private RewriteMatch rewriteMatch;
    private ServletContext targetContext = null;

    public NormalRewrittenUrl(RuleExecutionOutput ruleExecutionOutput) {
        this.target = ruleExecutionOutput.getReplacedUrl();
        this.targetContext = ruleExecutionOutput.getReplacedUrlContext();
        this.stopFilterChain = ruleExecutionOutput.isStopFilterMatch();
        this.rewriteMatch = ruleExecutionOutput.getRewriteMatch();
        this.noSubstitution = ruleExecutionOutput.isNoSubstitution();
    }

    protected NormalRewrittenUrl(String target) {
        this.target = target;
    }

    public String getTarget() {
        return this.target;
    }

    public boolean isForward() {
        return this.forward;
    }

    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    public boolean isRedirect() {
        return this.redirect;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }

    public void setPermanentRedirect(boolean permanentRedirect) {
        this.permanentRedirect = permanentRedirect;
    }

    public boolean isPermanentRedirect() {
        return this.permanentRedirect;
    }

    public void setTemporaryRedirect(boolean temporaryRedirect) {
        this.temporaryRedirect = temporaryRedirect;
    }

    public boolean isTemporaryRedirect() {
        return this.temporaryRedirect;
    }

    public void setEncode(boolean b) {
        this.encode = b;
    }

    public boolean isEncode() {
        return this.encode;
    }

    public boolean isPreInclude() {
        return this.preInclude;
    }

    public void setPreInclude(boolean preInclude) {
        this.preInclude = preInclude;
    }

    public boolean isPostInclude() {
        return this.postInclude;
    }

    public void setPostInclude(boolean postInclude) {
        this.postInclude = postInclude;
    }

    public boolean isStopFilterChain() {
        return this.stopFilterChain;
    }

    public void setStopFilterChain(boolean stopFilterChain) {
        this.stopFilterChain = stopFilterChain;
    }

    public boolean isProxy() {
        return this.proxy;
    }

    public void setProxy(boolean proxy) {
        this.proxy = proxy;
    }

    public boolean doRewrite(HttpServletRequest hsRequest, HttpServletResponse hsResponse, FilterChain chain) throws IOException, ServletException {
        boolean requestRewritten = false;
        String target = this.getTarget();
        if (log.isTraceEnabled()) {
            log.trace("doRewrite called");
        }
        if (this.rewriteMatch != null) {
            this.rewriteMatch.execute(hsRequest, hsResponse);
        }
        if (this.stopFilterChain) {
            log.trace("stopping filter chain");
            requestRewritten = true;
        } else if (this.isNoSubstitution()) {
            log.trace("no substitution");
            requestRewritten = false;
        } else if (this.isForward()) {
            if (hsResponse.isCommitted()) {
                log.error("response is comitted cannot forward to " + target + " (check you haven't done anything to the response (ie, written to it) before here)");
            } else {
                RequestDispatcher rq = this.getRequestDispatcher(hsRequest, target, this.targetContext);
                rq.forward((ServletRequest)hsRequest, (ServletResponse)hsResponse);
                if (log.isTraceEnabled()) {
                    log.trace("forwarded to " + target);
                }
            }
            requestRewritten = true;
        } else if (this.isPreInclude()) {
            RequestDispatcher rq = this.getRequestDispatcher(hsRequest, target, this.targetContext);
            rq.include((ServletRequest)hsRequest, (ServletResponse)hsResponse);
            chain.doFilter((ServletRequest)hsRequest, (ServletResponse)hsResponse);
            requestRewritten = true;
            if (log.isTraceEnabled()) {
                log.trace("preinclded " + target);
            }
        } else if (this.isPostInclude()) {
            RequestDispatcher rq = this.getRequestDispatcher(hsRequest, target, this.targetContext);
            chain.doFilter((ServletRequest)hsRequest, (ServletResponse)hsResponse);
            rq.include((ServletRequest)hsRequest, (ServletResponse)hsResponse);
            requestRewritten = true;
            if (log.isTraceEnabled()) {
                log.trace("postinclded " + target);
            }
        } else if (this.isRedirect()) {
            if (hsResponse.isCommitted()) {
                log.error("response is comitted cannot redirect to " + target + " (check you haven't done anything to the response (ie, written to it) before here)");
            } else {
                if (this.isEncode()) {
                    target = hsResponse.encodeRedirectURL(target);
                }
                hsResponse.sendRedirect(target);
                if (log.isTraceEnabled()) {
                    log.trace("redirected to " + target);
                }
            }
            requestRewritten = true;
        } else if (this.isTemporaryRedirect()) {
            if (hsResponse.isCommitted()) {
                log.error("response is comitted cannot temporary redirect to " + target + " (check you haven't done anything to the response (ie, written to it) before here)");
            } else {
                if (this.isEncode()) {
                    target = hsResponse.encodeRedirectURL(target);
                }
                hsResponse.setStatus(302);
                hsResponse.setHeader("Location", target);
                if (log.isTraceEnabled()) {
                    log.trace("temporarily redirected to " + target);
                }
            }
            requestRewritten = true;
        } else if (this.isPermanentRedirect()) {
            if (hsResponse.isCommitted()) {
                log.error("response is comitted cannot permanent redirect " + target + " (check you haven't done anything to the response (ie, written to it) before here)");
            } else {
                if (this.isEncode()) {
                    target = hsResponse.encodeRedirectURL(target);
                }
                hsResponse.setStatus(301);
                hsResponse.setHeader("Location", target);
                if (log.isTraceEnabled()) {
                    log.trace("permanently redirected to " + target);
                }
            }
            requestRewritten = true;
        } else if (this.isProxy()) {
            if (hsResponse.isCommitted()) {
                log.error("response is committed. cannot proxy " + target + ". Check that you havn't written to the response before.");
            } else {
                RequestProxy.execute(target, hsRequest, hsResponse);
                if (log.isTraceEnabled()) {
                    log.trace("Proxied request to " + target);
                }
            }
            requestRewritten = true;
        }
        return requestRewritten;
    }

    private RequestDispatcher getRequestDispatcher(HttpServletRequest hsRequest, String toUrl, ServletContext targetContext) throws ServletException {
        RequestDispatcher rq;
        RequestDispatcher requestDispatcher = rq = targetContext != null ? targetContext.getRequestDispatcher(this.target) : hsRequest.getRequestDispatcher(toUrl);
        if (rq == null) {
            throw new ServletException("unable to get request dispatcher for " + toUrl);
        }
        return rq;
    }

    public ServletContext getTargetContext() {
        return this.targetContext;
    }

    public void setTargetContext(ServletContext targetContext) {
        this.targetContext = targetContext;
    }

    public boolean isNoSubstitution() {
        return this.noSubstitution;
    }

    public void setNoSubstitution(boolean noSubstitution) {
        this.noSubstitution = noSubstitution;
    }
}

