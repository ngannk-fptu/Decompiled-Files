/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.uri.rules;

import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.spi.uri.rules.UriRule;
import com.sun.jersey.spi.uri.rules.UriRuleContext;

public abstract class BaseRule
implements UriRule {
    private final UriTemplate template;

    public BaseRule(UriTemplate template) {
        assert (template != null);
        this.template = template;
    }

    protected final void pushMatch(UriRuleContext context) {
        context.pushMatch(this.template, this.template.getTemplateVariables());
    }

    protected final UriTemplate getTemplate() {
        return this.template;
    }
}

