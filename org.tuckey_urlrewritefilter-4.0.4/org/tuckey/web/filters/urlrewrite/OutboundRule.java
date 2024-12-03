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
import org.tuckey.web.filters.urlrewrite.RewrittenOutboundUrl;
import org.tuckey.web.filters.urlrewrite.RuleBase;
import org.tuckey.web.filters.urlrewrite.RuleExecutionOutput;
import org.tuckey.web.filters.urlrewrite.utils.Log;

public class OutboundRule
extends RuleBase {
    private static final Log log = Log.getLog(OutboundRule.class);
    private boolean encodeFirst;
    private boolean encodeToUrl = true;

    public RewrittenOutboundUrl execute(String url, HttpServletRequest hsRequest, HttpServletResponse hsResponse) throws InvocationTargetException {
        RuleExecutionOutput ruleRuleExecutionOutput;
        try {
            ruleRuleExecutionOutput = super.matchesBase(url, hsRequest, hsResponse, null);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (ServletException e) {
            throw new RuntimeException(e);
        }
        if (ruleRuleExecutionOutput == null || !ruleRuleExecutionOutput.isRuleMatched()) {
            return null;
        }
        return new RewrittenOutboundUrl(ruleRuleExecutionOutput.getReplacedUrl(), this.encodeToUrl);
    }

    public boolean initialise(ServletContext servletContext) {
        boolean ok = super.initialise(servletContext);
        if (ok) {
            String displayName = this.getDisplayName();
            log.debug("loaded outbound rule " + displayName + " (" + this.from + ", " + this.to + ')');
        } else {
            log.debug("failed to load outbound rule");
        }
        if (this.errors.size() > 0) {
            ok = false;
        }
        this.valid = ok;
        return ok;
    }

    protected void addError(String s) {
        String displayName = this.getDisplayName();
        log.error("Outbound Rule " + displayName + " had error: " + s);
        super.addError(s);
    }

    public String getDisplayName() {
        if (this.name != null) {
            return this.name + " (outbound rule " + this.id + ')';
        }
        return "Outbound Rule " + this.id;
    }

    public boolean isEncodeFirst() {
        return this.encodeFirst;
    }

    public boolean isEncodeToUrl() {
        return this.encodeToUrl;
    }

    public void setEncodeFirst(boolean encodeFirst) {
        this.encodeFirst = encodeFirst;
    }

    public void setEncodeToUrl(boolean encodeToUrl) {
        this.encodeToUrl = encodeToUrl;
    }
}

