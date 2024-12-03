/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.uri.rules;

import com.sun.jersey.api.core.HttpResponseContext;
import com.sun.jersey.server.probes.UriRuleProbeProvider;
import com.sun.jersey.spi.uri.rules.UriRule;
import com.sun.jersey.spi.uri.rules.UriRuleContext;
import java.util.regex.MatchResult;
import javax.ws.rs.core.Response;

public class RightHandPathRule
implements UriRule {
    private final boolean redirect;
    private final boolean patternEndsInSlash;
    private final UriRule rule;

    public RightHandPathRule(boolean redirect, boolean patternEndsInSlash, UriRule rule) {
        assert (rule != null);
        this.redirect = redirect;
        this.patternEndsInSlash = patternEndsInSlash;
        this.rule = rule;
    }

    @Override
    public final boolean accept(CharSequence path, Object resource, UriRuleContext context) {
        UriRuleProbeProvider.ruleAccept(RightHandPathRule.class.getSimpleName(), path, resource);
        String rhpath = this.getRightHandPath(context.getMatchResult());
        if (rhpath.length() == 0) {
            if (this.patternEndsInSlash && this.redirect) {
                if (context.isTracingEnabled()) {
                    context.trace(String.format("accept right hand path redirect: \"%s\" to \"%s/\"", path, path));
                }
                return this.redirect(context);
            }
            context.pushRightHandPathLength(0);
        } else if (rhpath.length() == 1) {
            if (!this.patternEndsInSlash && this.redirect) {
                return false;
            }
            rhpath = "";
            context.pushRightHandPathLength(0);
        } else if (this.patternEndsInSlash) {
            context.pushRightHandPathLength(rhpath.length() - 1);
        } else {
            context.pushRightHandPathLength(rhpath.length());
        }
        if (context.isTracingEnabled()) {
            CharSequence lhpath = path.subSequence(0, path.length() - rhpath.length());
            context.trace(String.format("accept right hand path %s: \"%s\" -> \"%s\" : \"%s\"", context.getMatchResult(), path, lhpath, rhpath));
        }
        return this.rule.accept(rhpath, resource, context);
    }

    private String getRightHandPath(MatchResult mr) {
        String rhp = mr.groupCount() > 0 ? mr.group(mr.groupCount()) : "";
        return rhp != null ? rhp : "";
    }

    private boolean redirect(UriRuleContext context) {
        HttpResponseContext response = context.getResponse();
        response.setResponse(Response.temporaryRedirect(context.getUriInfo().getRequestUriBuilder().path("/").build(new Object[0])).build());
        return true;
    }
}

