/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.tuckey.web.filters.urlrewrite;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tuckey.web.filters.urlrewrite.RewrittenUrl;
import org.tuckey.web.filters.urlrewrite.Rule;
import org.tuckey.web.filters.urlrewrite.RuleBase;
import org.tuckey.web.filters.urlrewrite.RuleChain;
import org.tuckey.web.filters.urlrewrite.RuleExecutionOutput;
import org.tuckey.web.filters.urlrewrite.utils.Log;
import org.tuckey.web.filters.urlrewrite.utils.StringUtils;

public class NormalRule
extends RuleBase
implements Rule {
    private static Log log = Log.getLog(NormalRule.class);
    public short toType = 1;
    public static final short TO_TYPE_REDIRECT = 0;
    public static final short TO_TYPE_FORWARD = 1;
    public static final short TO_TYPE_PERMANENT_REDIRECT = 2;
    public static final short TO_TYPE_TEMPORARY_REDIRECT = 3;
    public static final short TO_TYPE_PRE_INCLUDE = 4;
    public static final short TO_TYPE_POST_INCLUDE = 5;
    public static final short TO_TYPE_PROXY = 6;
    private boolean encodeToUrl = false;
    private boolean queryStringAppend = false;
    private String toContextStr = null;
    private ServletContext toServletContext = null;

    public RewrittenUrl matches(String url, HttpServletRequest hsRequest, HttpServletResponse hsResponse, RuleChain chain) throws IOException, ServletException, InvocationTargetException {
        RuleExecutionOutput ruleExecutionOutput = super.matchesBase(url, hsRequest, hsResponse, chain);
        if (ruleExecutionOutput == null || !ruleExecutionOutput.isRuleMatched()) {
            return null;
        }
        if (this.queryStringAppend && hsRequest.getQueryString() != null) {
            String target = ruleExecutionOutput.getReplacedUrl();
            ruleExecutionOutput.setReplacedUrl(target + "&" + hsRequest.getQueryString());
        }
        if (this.toServletContext != null) {
            ruleExecutionOutput.setReplacedUrlContext(this.toServletContext);
        }
        return RuleExecutionOutput.getRewritenUrl(this.toType, this.encodeToUrl, ruleExecutionOutput);
    }

    public RewrittenUrl matches(String url, HttpServletRequest hsRequest, HttpServletResponse hsResponse) throws IOException, ServletException, InvocationTargetException {
        return this.matches(url, hsRequest, hsResponse, null);
    }

    public boolean initialise(ServletContext context) {
        boolean ok = super.initialise(context);
        this.initialised = true;
        if (!ok) {
            log.debug("failed to load rule");
        } else {
            log.debug("loaded rule " + this.getDisplayName() + " (" + this.from + ", " + this.to + " " + this.toType + ")");
        }
        if (!StringUtils.isBlank(this.toContextStr)) {
            log.debug("looking for context " + this.toContextStr);
            if (context == null) {
                this.addError("unable to look for context as current context null");
            } else {
                this.toServletContext = context.getContext("/" + this.toContextStr);
                if (this.toServletContext == null) {
                    this.addError("could not get servlet context " + this.toContextStr);
                } else {
                    log.debug("got context ok");
                }
            }
        }
        if (this.errors.size() > 0) {
            ok = false;
        }
        this.valid = ok;
        return ok;
    }

    public void setToType(String toTypeStr) {
        if ("redirect".equals(toTypeStr)) {
            this.toType = 0;
        } else if ("permanent-redirect".equals(toTypeStr)) {
            this.toType = (short)2;
        } else if ("temporary-redirect".equals(toTypeStr)) {
            this.toType = (short)3;
        } else if ("pre-include".equals(toTypeStr)) {
            this.toType = (short)4;
        } else if ("post-include".equals(toTypeStr)) {
            this.toType = (short)5;
        } else if ("forward".equals(toTypeStr) || "passthrough".equals(toTypeStr) || StringUtils.isBlank(toTypeStr)) {
            this.toType = 1;
        } else if ("proxy".equals(toTypeStr)) {
            this.toType = (short)6;
        } else {
            this.addError("type (" + toTypeStr + ") is not valid");
        }
    }

    public String getToType() {
        if (this.toType == 0) {
            return "redirect";
        }
        if (this.toType == 2) {
            return "permanent-redirect";
        }
        if (this.toType == 3) {
            return "temporary-redirect";
        }
        if (this.toType == 4) {
            return "pre-include";
        }
        if (this.toType == 5) {
            return "post-include";
        }
        if (this.toType == 6) {
            return "proxy";
        }
        return "forward";
    }

    protected void addError(String s) {
        log.error("Rule " + this.getDisplayName() + " had error: " + s);
        super.addError(s);
    }

    public String getDisplayName() {
        if (this.name != null) {
            return this.name + " (rule " + this.id + ")";
        }
        return "Rule " + this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getFullDisplayName() {
        return this.getDisplayName() + " (" + this.from + ", " + this.to + " " + this.toType + ")";
    }

    public boolean isEncodeToUrl() {
        return this.encodeToUrl;
    }

    public void setEncodeToUrl(boolean encodeToUrl) {
        this.encodeToUrl = encodeToUrl;
    }

    public String getToContextStr() {
        return this.toContextStr;
    }

    public void setToContextStr(String toContextStr) {
        this.toContextStr = toContextStr;
    }

    public ServletContext getToServletContext() {
        return this.toServletContext;
    }

    public void setQueryStringAppend(String value) {
        this.queryStringAppend = "true".equalsIgnoreCase(value);
    }
}

