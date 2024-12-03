/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.uri.rules;

import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.server.impl.uri.rules.BaseRule;
import com.sun.jersey.server.probes.UriRuleProbeProvider;
import com.sun.jersey.spi.uri.rules.UriRule;
import com.sun.jersey.spi.uri.rules.UriRuleContext;
import java.util.Iterator;

public final class ResourceObjectRule
extends BaseRule {
    private final Object resourceObject;

    public ResourceObjectRule(UriTemplate template, Object resourceObject) {
        super(template);
        this.resourceObject = resourceObject;
    }

    @Override
    public boolean accept(CharSequence path, Object resource, UriRuleContext context) {
        this.pushMatch(context);
        if (context.isTracingEnabled()) {
            context.trace(String.format("accept resource: \"%s\" -> @Path(\"%s\") %s", context.getUriInfo().getMatchedURIs().get(0), this.getTemplate().getTemplate(), ReflectionHelper.objectToString(this.resourceObject)));
        }
        context.pushResource(this.resourceObject);
        UriRuleProbeProvider.ruleAccept(ResourceObjectRule.class.getSimpleName(), path, this.resourceObject);
        Iterator<UriRule> matches = context.getRules(this.resourceObject.getClass()).match(path, context);
        while (matches.hasNext()) {
            if (!matches.next().accept(path, this.resourceObject, context)) continue;
            return true;
        }
        return false;
    }
}

