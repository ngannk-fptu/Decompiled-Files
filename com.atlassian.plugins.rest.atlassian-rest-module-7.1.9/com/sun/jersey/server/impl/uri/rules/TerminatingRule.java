/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.uri.rules;

import com.sun.jersey.server.probes.UriRuleProbeProvider;
import com.sun.jersey.spi.uri.rules.UriRule;
import com.sun.jersey.spi.uri.rules.UriRuleContext;
import javax.ws.rs.WebApplicationException;

public class TerminatingRule
implements UriRule {
    @Override
    public final boolean accept(CharSequence path, Object resource, UriRuleContext context) {
        UriRuleProbeProvider.ruleAccept(TerminatingRule.class.getSimpleName(), path, resource);
        if (context.isTracingEnabled()) {
            context.trace("accept termination (matching failure): \"" + path + "\"");
        }
        if (context.getResponse().isResponseSet()) {
            throw new WebApplicationException(context.getResponse().getResponse());
        }
        return false;
    }
}

