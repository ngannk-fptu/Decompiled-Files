/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.whitelist;

import com.atlassian.plugins.whitelist.ImmutableWhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistType;
import java.util.Optional;
import java.util.function.Function;

public class ImmutableWhitelistRuleBuilder {
    private Integer id;
    private String expression;
    private WhitelistType type;
    private boolean allowInbound;
    private boolean authenticationRequired;
    public static final Function<WhitelistRule, WhitelistRule> COPY = input -> Optional.ofNullable(input).map(i -> ImmutableWhitelistRule.builder().copyOf((WhitelistRule)i).build()).orElse(null);

    public Integer getId() {
        return this.id;
    }

    public String getExpression() {
        return this.expression;
    }

    public WhitelistType getType() {
        return this.type;
    }

    public boolean isAllowInbound() {
        return this.allowInbound;
    }

    public boolean isAuthenticationRequired() {
        return this.authenticationRequired;
    }

    public ImmutableWhitelistRuleBuilder copyOf(WhitelistRule template) {
        return this.id(template.getId()).expression(template.getExpression()).type(template.getType()).allowInbound(template.isAllowInbound()).authenticationRequired(template.isAuthenticationRequired());
    }

    public ImmutableWhitelistRuleBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ImmutableWhitelistRuleBuilder expression(String expression) {
        this.expression = expression;
        return this;
    }

    public ImmutableWhitelistRuleBuilder type(WhitelistType type) {
        this.type = type;
        return this;
    }

    public ImmutableWhitelistRuleBuilder allowInbound(boolean allowInbound) {
        this.allowInbound = allowInbound;
        return this;
    }

    public ImmutableWhitelistRuleBuilder authenticationRequired(boolean authenticationRequired) {
        this.authenticationRequired = authenticationRequired;
        return this;
    }

    public ImmutableWhitelistRule build() {
        return new ImmutableWhitelistRule(this);
    }
}

